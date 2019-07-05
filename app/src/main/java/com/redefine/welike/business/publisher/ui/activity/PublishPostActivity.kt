package com.redefine.welike.business.publisher.ui.activity

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import com.redefine.foundation.framework.Event
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.foundation.utils.CommonHelper
import com.redefine.foundation.utils.InputMethodUtil
import com.redefine.im.threadUIDelay
import com.redefine.multimedia.photoselector.constant.ImagePickConstant
import com.redefine.multimedia.photoselector.entity.Item
import com.redefine.multimedia.photoselector.entity.MimeType
import com.redefine.multimedia.photoselector.model.SelectedItemCollection
import com.redefine.multimedia.recorder.activity.VideoRecorderActivity
import com.redefine.multimedia.recorder.constant.VideoRecorderConstant
import com.redefine.multimedia.snapshot.PhotoSnapShotActivity
import com.redefine.richtext.RichContent
import com.redefine.richtext.emoji.EmojiPanel
import com.redefine.richtext.processor.RichEditTextProcessor
import com.redefine.welike.R
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.constant.CommonRequestCode
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.constant.PermissionRequestCode
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.base.track.AFGAEventManager
import com.redefine.welike.base.track.TrackerConstant
import com.redefine.welike.business.feeds.ui.constant.FeedConstant
import com.redefine.welike.business.location.management.bean.Location
import com.redefine.welike.business.location.ui.LocationSelectDialog
import com.redefine.welike.business.location.ui.OnLocationSelectedListener
import com.redefine.welike.business.location.ui.constant.LocationConstant
import com.redefine.welike.business.publisher.management.FeedPoster
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.bean.DraftCategory
import com.redefine.welike.business.publisher.management.bean.DraftPost
import com.redefine.welike.business.publisher.management.bean.PostMenuState
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean
import com.redefine.welike.business.publisher.ui.bean.BottomBehaviorItem
import com.redefine.welike.business.publisher.ui.component.BottomBehaviorView
import com.redefine.welike.business.publisher.ui.component.EditorInputView
import com.redefine.welike.business.publisher.ui.component.OnItemClickListener
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import com.redefine.welike.business.publisher.viewmodel.PublishPostViewModel
import com.redefine.welike.business.startup.management.HalfLoginManager
import com.redefine.welike.business.topic.ui.constant.TopicConstant
import com.redefine.welike.commonui.photoselector.PhotoSelector
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.EventLog1
import com.redefine.welike.statistical.bean.RegisterAndLoginModel
import kotlinx.android.synthetic.main.activity_publish_post.*
import kotlinx.android.synthetic.main.layout_publish_post_editor_container.*
import kotlinx.android.synthetic.main.layout_publish_post_editor_input.*
import kotlinx.android.synthetic.main.layout_publish_post_location.*
import kotlinx.android.synthetic.main.toolbar_publish_common.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

/**
 * @author qingfei.chen
 * @date ${DATE}
 * Copyright (C) 2018 redefine , Inc.
 */
@LayoutResource(layout = R.layout.activity_publish_post, title = R.string.editor_post_title)
class PublishPostActivity : AbsPublishActivity<DraftPost, PublishPostViewModel>(), View.OnClickListener,
        OnItemClickListener,
        EmojiPanel.OnEmojiItemClickListener,
        RichEditTextProcessor.OnInputRichFlagListener,
        EasyPermissions.PermissionCallbacks {
    override fun getBottomBehaviorView(): BottomBehaviorView {
        return editor_bottom_behavior_view
    }

    override fun getScrollView(): View {
        return editor_scroll_view
    }

    override fun getEditorInputView(): EditorInputView {
        return et_publish_post_editor_component
    }

    override fun getViewModel(): PublishPostViewModel {
        val vm = ViewModelProviders.of(this).get(PublishPostViewModel::class.java)
        vm.init(DraftCategory.POST, mDraftId)
        return vm
    }

    override fun getRichContent(): RichContent {
        return et_publish_editor.richProcessor.getRichContent(false,
                GlobalConfig.SUMMARY_LIMIT,
                GlobalConfig.PUBLISH_POST_INPUT_TEXT_MAX_OVER_LIMIT)
    }

    override fun getEventLabel(): String {
        return "original"
    }

    override fun getLimitWarnView(): TextView {
        return editor_post_text_limit_warn
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let { intent ->

            if (intent.hasExtra(PublishPostStarter.EXTRA_FROM)) {
                mViewModel.from = intent.getStringExtra(PublishPostStarter.EXTRA_FROM)
            }

            val multiContent = intent.getStringExtra(FeedConstant.KEY_DATA)
            val version = intent.getStringExtra(FeedConstant.KEY_VERSION)
            if (!TextUtils.isEmpty(version) && TextUtils.isDigitsOnly(version) && version.toInt() > 0) {
                mViewModel.parsePublishPostContent(multiContent)
            } else {
                mViewModel.parseMultiContentFromPush(multiContent)
            }

            val hashTail = intent.getStringExtra(FeedConstant.KEY_HASH_TAG)
            val hashType = intent.getStringExtra(FeedConstant.KEY_HASH_TYPE)
            val hashID = intent.getStringExtra(FeedConstant.KEY_HASH_ID)
            mViewModel.parseTail(hashID, hashTail, hashType)

            intent.getSerializableExtra(LocationConstant.BUNDLE_KEY_LOCATION)?.let {
                val location = it as Location
                mViewModel.updateLocation(location)
            }

            intent.getSerializableExtra(TopicConstant.BUNDLE_KEY_TOPIC)?.let {
                val topic = it as TopicSearchSugBean.TopicBean
                mViewModel.updateTopic(topic, true)
                mPublishEventModel.setTopicSource(EventLog1.Publish.TopicSource.FROM_TOPIC)
            }
        }

        if (FeedPoster.getInstance().isPostLimitOver()) {
            this.finish()
            return
        }

        EventBus.getDefault().register(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun registerListener() {
        super.registerListener()
        editor_bottom_behavior_view.setOnItemClickListener(this)
        editor_location.setOnClickListener(this)
    }

    override fun registerObserve() {
        super.registerObserve()
        mViewModel.mMenuStateLiveData.observe(this, Observer {
            it?.let {
                performMenuState(it)
            }
        })

        mViewModel.mSuperTopicLiveData.observe(this, Observer {
            tv_title.setText(R.string.editor_topic_post_title)
        })

        mViewModel.mLoginStateLiveData.observe(this, Observer {
            InputMethodUtil.hideInputMethod(et_publish_post_editor_component)
            HalfLoginManager.getInstancce().showLoginDialog(this, RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.PUBLISH))
        })

        mViewModel.mScrollToBottomLiveData.observe(this, Observer {
            editor_scroll_view.fullScroll(ScrollView.FOCUS_DOWN)
        })
    }

    override fun onKeyboardShowing(isKeyboardShowing: Boolean) {
        super.onKeyboardShowing(isKeyboardShowing)
        if (isKeyboardShowing) {
            editor_container_bottom_shadow.visibility = View.VISIBLE
        } else {
            editor_container_bottom_shadow.visibility = View.INVISIBLE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        performLocationClick()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Toast.makeText(this, ResourceTool.getString("common_permissions_denied"), Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        if (v == editor_location) {
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                EasyPermissions.requestPermissions(this,
                        ResourceTool.getString("location_permission"),
                        PermissionRequestCode.LOCATION_PERMISSION_REQUEST, Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                performLocationClick()
            }
        }

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (!CollectionUtil.isEmpty(mViewModel.mPollLiveData.value)) {
            // 当前在投票状态
            return false
        }
        if (event?.action == MotionEvent.ACTION_UP
                || event?.action == MotionEvent.ACTION_CANCEL) {


            KeyboardUtil.showKeyboard(et_publish_editor)
            publish_add_topic_component.hideTip()
        }
        return false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return
            }
            if (requestCode == CommonRequestCode.EDITOR_CHOOSE_PIC_CODE || requestCode == CommonRequestCode.SHORT_CUT_IMAGE_PICK) {
                val items = data.getParcelableArrayListExtra<Item>(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS)
                mViewModel.updateVideoAndPhotos(items)
            } else if (requestCode == CommonRequestCode.EDITOR_POLL_CHOOSE_PIC_CODE) {
                val items = data.getParcelableArrayListExtra<Item>(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS)
                if (data.extras != null) {
                    val position = data.extras!!.getInt(PublishPostStarter.EXTRA_PUBLISH_POLL_POSITION, 0)
                    mViewModel.updatePollImage(items.firstOrNull(), position)
                }
            } else if (requestCode == CommonRequestCode.REQUEST_IMAGE_PICK_SNAPSHOT) {
                val filePath = data.getStringExtra(VideoRecorderConstant.MEDIA_RESULT_PATH)
                val uri = data.data
                val width = data.getIntExtra(VideoRecorderConstant.MEDIA_WIDTH, 0)
                val height = data.getIntExtra(VideoRecorderConstant.MEDIA_HEIGHT, 0)
                val item = Item(uri, filePath, MimeType.JPEG.toString(), File(filePath).length(), 0, width, height)
                item.isFromRecorder = true
                mViewModel.updatePhoto(item)
            } else if (requestCode == CommonRequestCode.REQUEST_IMAGE_PICK_VIDEO_RECORD) {
                val filePath = data.getStringExtra(VideoRecorderConstant.MEDIA_RESULT_PATH)
                val uri = data.data
                val width = data.getIntExtra(VideoRecorderConstant.MEDIA_WIDTH, 0)
                val height = data.getIntExtra(VideoRecorderConstant.MEDIA_HEIGHT, 0)
                val duration = data.getLongExtra(VideoRecorderConstant.MEDIA_DURATION, 0)
                val item = Item(uri, filePath, MimeType.MP4.toString(), File(filePath).length(), duration, width, height)
                item.isFromRecorder = true
                mViewModel.updateVideo(item)
            } else if (requestCode == CommonRequestCode.IMAGE_PICK_PREVIEW) {
                val items = data.getParcelableArrayListExtra<Item>(SelectedItemCollection.STATE_SELECTION)
                mViewModel.updateVideoAndPhotos(items)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == CommonRequestCode.REQUEST_IMAGE_PICK_SNAPSHOT
                    || requestCode == CommonRequestCode.REQUEST_IMAGE_PICK_VIDEO_RECORD
                    || requestCode == CommonRequestCode.SHORT_CUT_IMAGE_PICK
                    || requestCode == CommonRequestCode.SHORT_CUT_CHOICE_TOPIC_CODE) {
                InputMethodUtil.hideInputMethod(this)
            }
        }
    }

    override fun onTopicInput() {
        publish_add_topic_component.performTopicClick()
    }


    override fun onItemClick(item: BottomBehaviorItem?) {
        super.onItemClick(item)
        if (item == null) {
            return
        }

        publish_add_topic_component.hideTip()

        when (item.type) {
            BottomBehaviorItem.BEHAVIOR_ALUMB_ITEM_TYPE -> {
                performPhotoClick()
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_POSTERNAVI_PHOTO)
            }

            BottomBehaviorItem.BEHAVIOR_SNAPSHOT_ITEM_TYPE -> {
                performOpenCameraPhoto()
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_POSTERNAVI_CAMERA)
            }

            BottomBehaviorItem.BEHAVIOR_VIDEO_ITEM_TYPE -> {
                performCameraVideo()
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_POSTERNAVI_VIDEO)
            }

            BottomBehaviorItem.BEHAVIOR_POLL_ITEM_TYPE -> {
                performPollClick()
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_POSTERNAVI_POLL)
                mPublishEventModel.proxy.report16()
            }

            BottomBehaviorItem.BEHAVIOR_EASY_POST -> {
                performPostStatusClick()
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_POSTERNAVI_EASY_POST)
            }
        }
    }

    private fun performPollClick() {
        val topicBean = TopicSearchSugBean.TopicBean()
        topicBean.name = getString(R.string.poll)
        topicBean.id = CommonHelper.generateUUID()
        mViewModel.generateInitPoll(topicBean)
    }

    private fun performPhotoClick() {
        InputMethodUtil.hideInputMethod(currentFocus)
        val list: ArrayList<Item> = ArrayList()
        mViewModel.mPhotoLiveData.value?.let {
            list.addAll(it)
        }
        mViewModel.mVideoLiveData.value?.let {
            list.add(it)
        }

        PhotoSelector.launchPhotoSelectorForPublishPost(this, list)
        EventLog.Publish.report2(PublisherEventManager.INSTANCE.source,
                PublisherEventManager.INSTANCE.main_source,
                PublisherEventManager.INSTANCE.page_type)
        mPublishEventModel.proxy.report2()
        AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_POSTER_PHOTO)
    }

    private fun performOpenCameraPhoto() {
        val intent = Intent(this, PhotoSnapShotActivity::class.java)
        intent.putExtra(VideoRecorderConstant.MEDIA_TYPE, VideoRecorderConstant.TYPE_IMAGE)
        overridePendingTransition(com.redefine.multimedia.R.anim.sliding_right_in, com.redefine.multimedia.R.anim.sliding_to_left_out)
        startActivityForResult(intent, CommonRequestCode.REQUEST_IMAGE_PICK_SNAPSHOT)
        mPublishEventModel.proxy.report2()
    }

    private fun performCameraVideo() {
        val intent = Intent(this, VideoRecorderActivity::class.java)
        intent.putExtra(VideoRecorderConstant.MEDIA_TYPE, VideoRecorderConstant.TYPE_VIDEO)
        overridePendingTransition(com.redefine.multimedia.R.anim.sliding_right_in, com.redefine.multimedia.R.anim.sliding_to_left_out)
        startActivityForResult(intent, CommonRequestCode.REQUEST_IMAGE_PICK_VIDEO_RECORD)
        mPublishEventModel.proxy.report2()
    }

    private fun performPostStatusClick() {
        if (!AccountManager.getInstance().isLogin) {
            mViewModel.updateLoginState(false)
            return
        }
//        val bundle = Bundle()
//        bundle.putInt(ShortCutConstant.SHORT_CUT_ENTRANCE, ShortCutConstant.ShortCutEasyPost)
        PostStatusStater.startActivityFromEdit(this)
//        EasyPostActivity.launch(this, PostStatusModel(EventLog1.PostStatus.ButtonFrom.PUBLISHER_ENTRANCE))
        this.finish()
    }


    private fun performLocationClick() {
        InputMethodUtil.hideInputMethod(currentFocus)
        LocationSelectDialog.showDialog(this, object : OnLocationSelectedListener {
            override fun onLocationSelected(location: Location) {
                mViewModel.updateLocation(location)
                threadUIDelay(200) {
                    InputMethodUtil.showInputMethod(currentFocus)
                }
            }
        })

        PublishAnalyticsManager.getInstance().obtainEventModel(mViewModel.mDraft.draftId).proxy.report5()
    }

    private fun performMenuState(menuState: PostMenuState?) {
        menuState?.let {
            editor_bottom_behavior_view.setAllEnabled(true)
            if (!menuState.hasPoll) {
                when {
                    menuState.hasVideo -> {
                        editor_bottom_behavior_view.setVideoCameraStatusPollDisable()
                    }
                    menuState.hasPic -> {
                        editor_bottom_behavior_view.setVideoStatusPollDisable()
                    }
                }
            } else {//has poll item
                editor_bottom_behavior_view.setAlbumVideoCameraStatusPollDisable()
            }
            tv_btn_send.isEnabled = menuState.sendBtnEnable
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: Event) {
        if (event.id == EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN) {
            finish()
        }
    }

}