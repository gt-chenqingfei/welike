package com.redefine.welike.business.publisher.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.fresco.loader.VideoCoverUrlLoader
import com.redefine.foundation.utils.CommonHelper
import com.redefine.foundation.utils.InputMethodUtil
import com.redefine.multimedia.photoselector.activity.PhotoSelectorActivity
import com.redefine.multimedia.photoselector.config.ImagePickConfig
import com.redefine.multimedia.photoselector.constant.ImagePickConstant
import com.redefine.multimedia.photoselector.entity.Item
import com.redefine.multimedia.photoselector.entity.MimeType
import com.redefine.richtext.RichItem
import com.redefine.richtext.block.BlockFactory
import com.redefine.richtext.emoji.EmojiPanel
import com.redefine.richtext.emoji.bean.EmojiBean
import com.redefine.welike.R
import com.redefine.welike.base.constant.CommonRequestCode
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.publisher.management.PublishConfig
import com.redefine.welike.business.publisher.management.draft.ArticleDraft
import com.redefine.welike.business.publisher.management.draft.PicItemDraft
import com.redefine.welike.business.publisher.management.draft.VideoAttachmentDraft
import com.redefine.welike.business.publisher.management.draft.VideoItemDraft
import com.redefine.welike.business.publisher.ui.view.AddTitleLinkDialog
import com.redefine.welike.commonui.photoselector.PhotoSelector
import com.redefine.welike.kext.translate
import kotlinx.android.synthetic.main.publish_article_post.*

/**
 *
 * Name: PublishArticleActivity
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-16 16:21
 *
 */

class PublishArticleActivity : BaseActivity() {

    private var contentLength: Int = 0

    private var innerImageCount: Int = 0

    private var innerGiffyCount: Int = 0

    private lateinit var mArticleDraft: ArticleDraft

    private var titleLength: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.publish_article_post)
        mArticleDraft = ArticleDraft(CommonHelper.generateUUID())
        common_back_btn.setOnClickListener {
            finish()
        }

        common_title_view.text = "article".translate(ResourceTool.ResourceFileEnum.PUBLISH)

        publish_title_text.text = "title".translate(ResourceTool.ResourceFileEnum.PUBLISH)

        publish_content_text.text = "content".translate(ResourceTool.ResourceFileEnum.PUBLISH)

        publish_content_edit.hint = "publish_content_hint".translate(ResourceTool.ResourceFileEnum.PUBLISH)
        publish_title_edit.hint = "publish_title_hint".translate(ResourceTool.ResourceFileEnum.PUBLISH)

        publish_title_edit.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                publish_title_text.setTextColor(ContextCompat.getColor(v.context, R.color.main))
                publish_title_div.setBackgroundColor(ContextCompat.getColor(v.context, R.color.main))
            } else {
                publish_title_text.setTextColor(ContextCompat.getColor(v.context, R.color.color_31))
                publish_title_div.setBackgroundColor(ContextCompat.getColor(v.context, R.color.common_div_color))
            }
        }

        publish_content_edit.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                publish_content_text.setTextColor(ContextCompat.getColor(v.context, R.color.main))
            } else {
                publish_content_text.setTextColor(ContextCompat.getColor(v.context, R.color.color_31))
            }
        }

        publish_title_edit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                titleLength = s?.toString()?.trim()?.length ?: 0
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        publish_content_edit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                contentLength = publish_content_edit.richProcessor.textLength
                innerImageCount = publish_content_edit.richProcessor.innerImagePicCount
                innerGiffyCount = publish_content_edit.richProcessor.innerImageGiffyCount
                onContentTextChange()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        publish_bottom_emoji_container.setIgnoreRecommendHeight(true)
        KeyboardUtil.attach(this, publish_bottom_emoji_container) { isShowing ->
            if (isShowing) {
                publish_emoji.isSelected = false
                publish_emoji.setImageResource(R.drawable.editor_selector_emoji)
            }
        }
        KPSwitchConflictUtil.attach(publish_bottom_emoji_container, publish_emoji, publish_content_edit) { _: View, b: Boolean ->
            when (b) {
                true -> {
                    publish_emoji.isSelected = true
                    publish_content_edit.clearFocus()
                }
                false -> {
                    publish_emoji.isSelected = false
                    publish_content_edit.requestFocus()
                }
            }
        }
        publish_next.text = "next".translate(ResourceTool.ResourceFileEnum.PUBLISH)

        editor_photo.setOnClickListener {
            when {
                mArticleDraft.videoAttachmentDraft != null -> {
                    val intent = Intent()
                    intent.setClass(this, PhotoSelectorActivity::class.java)
                    intent.putExtra(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, ImagePickConfig.get().setMimeTypeSet(MimeType.ofVideo())
                            .setShowSingleMediaType(true).setIsCutPhoto(false).setCapture(true).setCountable(true))
                    this.startActivityForResult(intent, CommonRequestCode.EDITOR_ARTICLE_CHOOSE_PIC_CODE)
                }
                innerImageCount != 0 || innerGiffyCount != 0 -> {
                    val intent = Intent()
                    intent.setClass(this, PhotoSelectorActivity::class.java)
                    intent.putExtra(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, ImagePickConfig.get().setMimeTypeSet(MimeType.ofImage())
                            .setShowSingleMediaType(true).setIsCutPhoto(false).setCapture(true).setCountable(true))
                    this.startActivityForResult(intent, CommonRequestCode.EDITOR_ARTICLE_CHOOSE_PIC_CODE)
                }
                else -> {
                    val intent = Intent()
                    intent.setClass(this, PhotoSelectorActivity::class.java)
                    intent.putExtra(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, ImagePickConfig.get().setMimeTypeSet(MimeType.ofAll())
                            .setShowSingleMediaType(false).setIsCutPhoto(false).setCapture(true).setCountable(true))
                    this.startActivityForResult(intent, CommonRequestCode.EDITOR_ARTICLE_CHOOSE_PIC_CODE)
                }
            }
        }

        publish_link.setOnClickListener {
            AddTitleLinkDialog.show(this) { url, name ->
                url?.let { u ->
                    if (publish_content_edit.text.isNotEmpty() && !TextUtils.equals(publish_content_edit.text.last() + "", "\n")) {
                        publish_content_edit.richProcessor.insertSpannable("\n")
                    }
                    publish_content_edit.richProcessor.insertLink(BlockFactory.createLink(resources.getString(R.string.web_links), name, u))
                    publish_content_edit.richProcessor.insertSpannable(" \n")
                    publish_content_edit.requestFocus()
                }
            }
        }

        editor_emoji_layout.setOnEmojiItemClickListener(object : EmojiPanel.OnEmojiItemClickListener {
            override fun onEmojiClick(emojiBean: EmojiBean?) {
                publish_content_edit.richProcessor.insertEmoji(emojiBean)
                publish_content_edit.requestFocus()
            }

            override fun onEmojiDelClick() {
                publish_content_edit.richProcessor.delete()
                publish_content_edit.requestFocus()
            }

        })
        publish_title_edit.requestFocus()
        publish_next.setOnClickListener {
            if (canNext()) {
                doNext()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CommonRequestCode.EDITOR_ARTICLE_CHOOSE_PIC_CODE -> {
                    // 图片选择结果回调
                    val items: ArrayList<Item> = data!!.getParcelableArrayListExtra(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS)
                    items.forEach {
                        if (it.isImage) {
                            val picItemDraft = PicItemDraft(it.filePath)
                            picItemDraft.fileSize = it.size
                            picItemDraft.height = it.height
                            picItemDraft.width = it.width
                            picItemDraft.mimeType = it.mimeType
                            publish_content_edit.richProcessor.insertImage(
                                    BlockFactory.createInnerImageBlock(this
                                            , RichItem.RICH_SUB_TYPE_PIC
                                            , it.width, it.height, it.filePath, it.size, it.mimeType))
                        } else {
                            val videoItemDraft = VideoItemDraft(it.filePath, isVideoFromRecorder = false)
                            videoItemDraft.fileSize = it.size
                            videoItemDraft.height = it.height
                            videoItemDraft.width = it.width
                            videoItemDraft.mimeType = it.mimeType
                            mArticleDraft.videoAttachmentDraft = VideoAttachmentDraft(videoItemDraft)
                            updateVideoCover()
                        }
                    }
                    publish_content_edit.requestFocus()
                }
                CommonRequestCode.PUBLISH_ARTICLE_PREVIEW -> {
                    finish()
                }
            }
        }
    }

    override fun finish() {
        InputMethodUtil.hideInputMethod(this)
        super.finish()
    }

    private fun doNext() {
        val title = publish_title_edit.text.trim().toString()
        val result = publish_content_edit.richProcessor.getRichContent(false
                , PublishConfig.MAX_ARTICLE_SUMMARY_LENGTH, PublishConfig.MAX_ARTICLE_CONTENT_LENGTH)
        if (TextUtils.isEmpty(result.summary) && mArticleDraft.videoAttachmentDraft != null) {
            result.summary = "share_video".translate(ResourceTool.ResourceFileEnum.PUBLISH)
            result.text = "share_video".translate(ResourceTool.ResourceFileEnum.PUBLISH)
        }
        mArticleDraft.setRichContent(title, result)
        PublishArticlePreviewActivity.launchArticlePreviewActivity(this, mArticleDraft)
    }

    private fun canNext(): Boolean {
        if (titleLength == 0) {
            Toast.makeText(this, "publish_title_is_empty".translate(ResourceTool.ResourceFileEnum.PUBLISH), Toast.LENGTH_SHORT).show()
            return false
        }
        if (titleLength > PublishConfig.MAX_TITLE_LENGTH) {
            Toast.makeText(this, "publish_title_too_long".translate(ResourceTool.ResourceFileEnum.PUBLISH), Toast.LENGTH_SHORT).show()
            return false
        }
        if (innerImageCount > PublishConfig.MAX_POST_PIC_COUNT) {
            Toast.makeText(this, "publish_pic_too_much".translate(ResourceTool.ResourceFileEnum.PUBLISH), Toast.LENGTH_SHORT).show()
            return false
        }
        if (mArticleDraft.videoAttachmentDraft != null &&
                (innerGiffyCount != 0 || innerImageCount != 0)) {
            Toast.makeText(this, "publish_cant_mix_video_pic".translate(ResourceTool.ResourceFileEnum.PUBLISH), Toast.LENGTH_SHORT).show()
            return false
        }
        if (isPublishEmpty()) {
            Toast.makeText(this, "publish_content_is_empty".translate(ResourceTool.ResourceFileEnum.PUBLISH), Toast.LENGTH_SHORT).show()
            return false
        }
        return true

    }

    private fun isPublishEmpty(): Boolean {
        return titleLength == 0 || (contentLength == 0 && mArticleDraft.videoAttachmentDraft == null)
    }

    /**
     * 输入内容发生变化
     */
    private fun onContentTextChange() {
        if (contentLength == 0) {
            publish_text_length.visibility = View.GONE
            publish_text_length.text = ""
        } else {
            publish_text_length.visibility = View.VISIBLE
            when {
                contentLength > PublishConfig.MAX_ARTICLE_CONTENT_LENGTH -> {
                    val spannableStringBuilder = SpannableStringBuilder((PublishConfig.MAX_ARTICLE_CONTENT_LENGTH - contentLength).toString())
                    val span = ForegroundColorSpan(ContextCompat.getColor(this, R.color.common_limit_over_color))
                    spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    publish_text_length.text = spannableStringBuilder
                }
                contentLength > PublishConfig.MAX_ARTICLE_SUMMARY_LENGTH -> {
                    val spannableStringBuilder = SpannableStringBuilder(contentLength.toString())
                    val span = ForegroundColorSpan(ContextCompat.getColor(this, R.color.main))
                    spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    publish_text_length.text = spannableStringBuilder
                }
                else -> {
                    val spannableStringBuilder = SpannableStringBuilder(contentLength.toString())
                    val span = ForegroundColorSpan(ContextCompat.getColor(this, R.color.common_limit_color))
                    spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    publish_text_length.text = spannableStringBuilder
                }
            }

        }
    }

    /**
     * 更新视频封面
     */
    private fun updateVideoCover() {
        if (mArticleDraft.videoAttachmentDraft?.videoItemDraft != null) {
            publish_video_container.visibility = View.VISIBLE
            val height = mArticleDraft.videoAttachmentDraft?.videoItemDraft?.height
            val width = mArticleDraft.videoAttachmentDraft?.videoItemDraft?.width
            val filePath = mArticleDraft.videoAttachmentDraft?.videoItemDraft?.localFileName
            VideoCoverUrlLoader.getInstance().loadVideoThumbFile(publish_video_container, publish_video_thumb, filePath, width
                    ?: 0, height ?: 0)
            publish_video_play.setOnClickListener {
                PhotoSelector.launchPlayPostVideo(this, filePath!!)
            }
            editor_video_delete_btn.setOnClickListener {
                mArticleDraft.videoAttachmentDraft = null
                updateVideoCover()
            }
        } else {
            publish_video_container.visibility = View.GONE
        }
    }
}