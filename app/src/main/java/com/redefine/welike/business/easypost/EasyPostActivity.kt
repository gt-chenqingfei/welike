package com.redefine.welike.business.easypost

import android.Manifest
import android.animation.Animator
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.dialog.CommonConfirmDialog
import com.redefine.commonui.widget.LoadingDlg
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.foundation.utils.InputMethodUtil
import com.redefine.im.threadUIDelay
import com.redefine.multimedia.photoselector.constant.ImagePickConstant
import com.redefine.multimedia.photoselector.entity.Item
import com.redefine.richtext.emoji.EmojiPanel
import com.redefine.richtext.emoji.bean.EmojiBean
import com.redefine.welike.R
import com.redefine.welike.base.constant.CommonRequestCode.EDITOR_POLL_CHOOSE_PIC_CODE
import com.redefine.welike.base.constant.PermissionRequestCode
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.easypost.api.bean.PostStatus
import com.redefine.welike.business.easypost.management.EasyPost
import com.redefine.welike.business.easypost.management.EasyPostManager
import com.redefine.welike.business.easypost.management.EasyPostViewModel
import com.redefine.welike.business.easypost.ui.ImageFragment
import com.redefine.welike.business.easypost.ui.adapter.CategoryViewAdapter
import com.redefine.welike.business.easypost.ui.adapter.ImagePagerAdapter
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.ui.activity.PostStatusStater
import com.redefine.welike.commonui.event.commonenums.BooleanValue
import com.redefine.welike.commonui.event.model.PostStatusModel
import com.redefine.welike.commonui.event.model.PublishEventModel
import com.redefine.welike.commonui.photoselector.PhotoSelector
import com.redefine.welike.kext.showOrHideKeyboard
import com.redefine.welike.kext.translate
import com.redefine.welike.statistical.EventConstants
import com.redefine.welike.statistical.EventLog1
import com.redefine.welike.statistical.manager.PostStatusEventManager
import com.welike.emoji.EmoticonsKeyboardUtils
import com.welike.emoji.SoftKeyboardSizeWatchLayout
import kotlinx.android.synthetic.main.easypost_activity.*
import pub.devrel.easypermissions.EasyPermissions

class EasyPostActivity : BaseActivity(), EmojiPanel.OnEmojiItemClickListener,
        ViewPager.OnPageChangeListener, TextWatcher,
        SoftKeyboardSizeWatchLayout.OnViewSizeChangeListener,
        EasyPermissions.PermissionCallbacks, CategoryViewAdapter.OnTabSelectedListener {


    private val adapterViewPager: ImagePagerAdapter by lazy { ImagePagerAdapter(manager = supportFragmentManager) }
    private lateinit var viewModel: EasyPostViewModel

    private val typefaceItems = listOf(Typeface.BOLD, Typeface.ITALIC, Typeface.NORMAL)
    private var typefaceCurrent = 0
    private var editorText: String? = null
    private var onEdit = false
    private val mLoadingDlg by lazy { LoadingDlg(this,true) }
    var eventModel: PostStatusModel? = null
    var draftId: String? = null
    var mPublishEventModel: PublishEventModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.easypost_activity)
        viewModel = ViewModelProviders.of(this).get(EasyPostViewModel::class.java)

        changeTextSize(etStatusContent?.text?.length)
        lavEasyPostTip.setAnimation("easy_post_tip.json")
        viewPagerEasyPost.adapter = adapterViewPager

        viewModel.init()
        viewModel.status.observe(this, android.arch.lifecycle.Observer {
            it?.let {

                common_loading_view.visibility = View.GONE
                viewPagerEasyPost.visibility = View.VISIBLE
                easyPostCategoryTabView.attach(viewModel.getPostStatusCategorys(), this)
            }
        })

        PostStatusEventManager.INSTANCE.report2()
        performListener()
        guideTipIfNeed()

        intent?.let {
            draftId = it.getStringExtra(PostStatusStater.EXTRA_DRAFT_ID)
            mPublishEventModel = PublishAnalyticsManager.getInstance().obtainEventModel(draftId)
            mPublishEventModel?.proxy?.report1()
            eventModel = it.getSerializableExtra(PostStatusStater.EXTRA_EVENT_MODEL)
                    as PostStatusModel
            eventModel?.let { EventLog1.PostStatus.report2(it.buttonFrom) }
        }

    }

    override fun onStop() {
        super.onStop()
        this.showOrHideKeyboard(tvEastPostSend, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPublishEventModel?.proxy?.report14()
    }

    override fun finish() {
        super.finish()
        adapterViewPager.tabs.forEach {
            it.abort()
        }
    }

    override fun onTabSelect(position: Int, data: PostStatus) {
        viewModel.setCurrent(data)
        easyPostThumbView.attach(data.picUrlList, viewPagerEasyPost)

        val text = viewModel.getRandomText()
        changeText(text)
        adapterViewPager.notifyDataSetChange(obtainFragments(data, text))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        when (requestCode) {
            PermissionRequestCode.POST_STATUS_IMAGE_SAVE_CODE -> {
                performSend()
            }
            PermissionRequestCode.IMAGE_PICK_PERMISSION_REQUEST_CODE -> {
                getCurrentFragment()?.copyView {
                    it?.let {
                        viewModel.saveBitmap(it, this)
                    }
                }
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Toast.makeText(applicationContext, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_permissions_denied"), Toast.LENGTH_SHORT).show()
    }


    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_UP && event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (easyPostEmojiContainer.visibility == View.VISIBLE) {
                easyPostEmojiContainer.visibility = View.GONE
                tvRandomText.visibility = View.VISIBLE
                easyPostThumbView.visibility = View.VISIBLE
                easyPostCategoryTabView.visibility = View.VISIBLE
                showEdit(false)
                return true
            }

            showConfirmDialog()
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        this.showOrHideKeyboard(tvEastPostSend, false)
        easyPostEmojiContainer.visibility = View.GONE

    }

    override fun onPageSelected(position: Int) {
        easyPostThumbView.smoothScrollToPosition(position)
        eventModel?.let {
            it.buttonType = EventLog1.PostStatus.ButtonType.SHUFFLE_IMAGE
            EventLog1.PostStatus.report3(it.buttonType)
        }
    }

    override fun onEmojiClick(emojiBean: EmojiBean?) {
        etStatusContent?.richProcessor?.insertEmoji(emojiBean)
    }

    override fun onEmojiDelClick() {
        etStatusContent?.richProcessor?.delete()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == EDITOR_POLL_CHOOSE_PIC_CODE) {
            if (data == null) {
                return
            }

            val items = data.getParcelableArrayListExtra<Item>(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS)
            if (!CollectionUtil.isEmpty(items)) {
                getCurrentFragment()?.updateImage(items[0].filePath)
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        editorText = etStatusContent.text.toString()
        changeTextSize(s?.length ?: 0)

        if (easyPostEmojiContainer.isSoftKeyboardPop && !onEdit) {
            eventModel?.let {
                it.buttonType = EventLog1.PostStatus.ButtonType.EDIT_TEXT
                EventLog1.PostStatus.report3(it.buttonType)
            }
            onEdit = true
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun onViewSizeChanged(isSoftKeyboardPop: Boolean) {
        if (isSoftKeyboardPop) {
            easyPostEmojiContainer.visibility = View.GONE
            tvRandomText.visibility = View.GONE
            showEdit(true)
            easyPostThumbView.visibility = View.GONE
            easyPostCategoryTabView.visibility = View.GONE
        } else {
            onEdit = false
            if (easyPostEmojiContainer.visibility == View.VISIBLE) {
                tvRandomText.visibility = View.GONE
                easyPostThumbView.visibility = View.GONE
                easyPostCategoryTabView.visibility = View.GONE
            } else {
                tvRandomText.visibility = View.VISIBLE
                easyPostThumbView.visibility = View.VISIBLE
                easyPostCategoryTabView.visibility = View.VISIBLE
                showEdit(false)
            }
        }
    }

    private fun obtainFragments(postStatus: PostStatus?, text: String): List<ImageFragment> {
        val fragments: MutableList<ImageFragment> = mutableListOf()

        if (postStatus == null) {
            return fragments
        }
        for (i in postStatus.picUrlList.indices) {
            val fragment = ImageFragment().also {
                it.post = EasyPost(text, postStatus.picUrlList[i])
                it.mEditText = etStatusContent
            }
            fragments.add(fragment)
        }

        if (fragments.isEmpty()) {
            val fragment = ImageFragment().also {
                it.post = EasyPost("", "")
                it.mEditText = etStatusContent
            }
            fragments.add(fragment)
        }

        return fragments
    }

    private fun getCurrentFragment(): ImageFragment? {
        return adapterViewPager.getItem(viewPagerEasyPost.currentItem)
    }

    private fun showConfirmDialog() {
        CommonConfirmDialog.showConfirmDialog(this@EasyPostActivity,
                "exit_tip".translate(ResourceTool.ResourceFileEnum.PUBLISH),
                "common_cancel".translate(),
                "common_confirm".translate(),
                object : CommonConfirmDialog.IConfirmDialogListener {
                    override fun onClickCancel() {
                    }

                    override fun onClickConfirm() {
                        InputMethodUtil.hideInputMethod(this@EasyPostActivity)
                        overridePendingTransition(R.anim.sliding_to_left_in, R.anim.sliding_right_out)
                        finish()
                    }
                })
    }

    private fun guideTipIfNeed() {
        if (EasyPostManager.checkTip()) {
            clEasyPostTip.visibility = View.VISIBLE
            lavEasyPostTip.playAnimation()
            lavEasyPostTip.repeatCount = 1
            lavEasyPostTip.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    clEasyPostTip.visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator?) {
                    clEasyPostTip.visibility = View.GONE
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
        }
    }

    private fun getTypeface(): Int {
        if (typefaceCurrent == typefaceItems.size) {
            typefaceCurrent = 0
        }
        val typeface = typefaceItems[typefaceCurrent]
        typefaceCurrent++
        return typeface
    }

    private fun changeTypeface() {
        var typeface = getTypeface()
        if (typeface == 0) {
            val content: String = etStatusContent?.text.toString()
            etStatusContent?.setText(content)
            return
        }
        var spannableString = SpannableString(etStatusContent?.text)
        spannableString.setSpan(StyleSpan(typeface), 0,
                etStatusContent!!.text.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        etStatusContent?.setText(spannableString)
    }

    private fun changeTextSize(len: Int?) {
        when (len) {
            in 1..10 -> etStatusContent?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 56f)
            in 11..15 -> etStatusContent?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50f)
            in 16..25 -> etStatusContent?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40f)
            in 26..35 -> etStatusContent?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 34f)
            in 36..60 -> etStatusContent?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28f)
            in 61..80 -> etStatusContent?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26f)
        }
    }

    private fun showEdit(value: Boolean) {
        etStatusContent?.let {
            if (value) {
                it.requestFocus()
                it.isCursorVisible = true
            } else {
                it.clearFocus()
                it.isCursorVisible = false
            }
        }
    }

    private fun attachKeyboard() {
        ivEmoji.setOnClickListener {
            EmoticonsKeyboardUtils.closeSoftKeyboard(etStatusContent)
            if (easyPostEmojiContainer.visibility === android.view.View.VISIBLE) {
                easyPostEmojiContainer.visibility = View.GONE
            } else {
                EventLog1.PostStatus.report7()
                threadUIDelay(100) {
                    tvRandomText.visibility = View.GONE
                    easyPostThumbView.visibility = View.GONE
                    easyPostCategoryTabView.visibility = View.GONE
                    easyPostEmojiContainer.visibility = View.VISIBLE
                    showEdit(true)
                }
            }
        }

        easyPostEmojiContainer.setOnViewSizeChangeListener(this)
    }

    private fun changeText(text: String) {
        editorText = text
        etStatusContent?.setText(text)
        showEdit(false)
    }

    private fun performDownloadListener() {
        ivEasyPostDownload.setOnClickListener {
            PostStatusEventManager.INSTANCE.report5()
            EventLog1.PostStatus.report5()
            if (EasyPermissions.hasPermissions(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                getCurrentFragment()?.copyView {
                    it?.let {
                        viewModel.saveBitmap(it, this)
                    } ?: mLoadingDlg.dismiss()
                }
            } else {
                EasyPermissions.requestPermissions(this,
                        getString(R.string.sd_write_permission),
                        PermissionRequestCode.IMAGE_PICK_PERMISSION_REQUEST_CODE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

        }
    }

    private fun performListener() {

        viewPagerEasyPost.addOnPageChangeListener(this)
        easyPostEmojiPanel.setOnEmojiItemClickListener(this)
        etStatusContent?.addTextChangedListener(this)
        attachKeyboard()

        etStatusContent?.setOnClickListener {
            showEdit(true)
        }

        tvRandomText.setOnClickListener {
            adapterViewPager.notifyDataSetChanged()
            changeText(viewModel.getRandomText())
            PostStatusEventManager.INSTANCE.setButtontype(EventConstants.POST_STATUS.BUTTON_TYPE_CHANGE_TEXT)
            PostStatusEventManager.INSTANCE.report3()
            eventModel?.let {
                it.buttonType = EventLog1.PostStatus.ButtonType.SHUFFLE_TEXT
                EventLog1.PostStatus.report3(it.buttonType)
            }
        }

        ivEasyPostBackBtn.setOnClickListener {
            showConfirmDialog()
        }

        ivEasyPostFontChange.setOnClickListener {
            changeTypeface()
        }

        ivEasyPostImageReplace.setOnClickListener {
            PostStatusEventManager.INSTANCE.report6()
            EventLog1.PostStatus.report6()
            PhotoSelector.launchPhotoSelectorForPoll1(this)
        }

        tvEastPostSend.setOnClickListener {

            if (EasyPermissions.hasPermissions(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                performSend()
            } else {
                EasyPermissions.requestPermissions(this,
                        getString(R.string.sd_write_permission),
                        PermissionRequestCode.POST_STATUS_IMAGE_SAVE_CODE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

        }

        ivEasyPostEditorBtn.setOnClickListener {
            this.showOrHideKeyboard(etStatusContent, true)
        }

        performDownloadListener()
    }


    private fun performSend() {
        adapterViewPager.notifyDataSetChanged()

        if (!mLoadingDlg.isShowing) {
            mLoadingDlg.show()
            mLoadingDlg.setOnCancelListener {
                getCurrentFragment()?.abort()
                tvEastPostSend.isEnabled = true
            }
        }
        tvEastPostSend.isEnabled = false

        getCurrentFragment()?.copyView { bitmap ->
            var topic = "#Post Status"
            if (viewModel.getCurrent().topic != null) {
                topic = viewModel.getCurrent().topic
            }

            bitmap?.let {
                viewModel.send(topic, bitmap, draftId)
            }
            mLoadingDlg.dismiss()

            PostStatusEventManager.INSTANCE.setCategoryId(viewModel.getCurrent().id.toString())
            PostStatusEventManager.INSTANCE.setCategoryName(viewModel.getCurrent().text)
            PostStatusEventManager.INSTANCE.report4()
            eventModel?.let { psm ->
                psm.categoryId = viewModel.getCurrent().id.toString()
                psm.categoryName = viewModel.getCurrent().text
                psm.text = PostStatusEventManager.INSTANCE.text
                psm.imageId = PostStatusEventManager.INSTANCE.imageid
                psm.textChanged = if (PostStatusEventManager.INSTANCE.textchanged == 1) BooleanValue.YES else BooleanValue.NO
                EventLog1.PostStatus.report4(psm.textChanged, psm.text, psm.imageId, psm.categoryId, psm.categoryName)
            }

            this.finish()
        }
    }
}





