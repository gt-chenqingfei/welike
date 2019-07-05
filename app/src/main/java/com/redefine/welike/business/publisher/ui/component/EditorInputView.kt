package com.redefine.welike.business.publisher.ui.component

import android.arch.lifecycle.Observer
import android.content.Context
import android.support.annotation.ColorRes
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.widget.TextView
import com.redefine.richtext.RichContent
import com.redefine.richtext.RichEditText
import com.redefine.richtext.block.BlockFactory
import com.redefine.richtext.emoji.bean.EmojiBean
import com.redefine.welike.R
import com.redefine.welike.business.publisher.api.bean.CommonTopic
import com.redefine.welike.business.publisher.api.bean.SuperTopic
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.bean.DraftBase
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean
import com.redefine.welike.business.publisher.management.draft.ArticleDraft
import com.redefine.welike.business.publisher.ui.component.base.BaseLinearContainer
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import com.redefine.welike.business.publisher.viewmodel.AbsPublishViewModel
import kotlinx.android.synthetic.main.layout_publish_post_editor_input.view.*

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */

@LayoutResource(layout = R.layout.layout_publish_post_editor_input)
class EditorInputView(context: Context?, attrs: AttributeSet?) : BaseLinearContainer(context, attrs)
        , TextWatcher {
    lateinit var mTvLimitWarn: TextView
    lateinit var mViewModel: AbsPublishViewModel<*>

    fun getRichEditText():RichEditText{
        return et_publish_editor
    }

    override fun onCreateView() {
        et_publish_editor.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
        val textLength = et_publish_editor.richProcessor.textLength
        if (textLength == 0) {
            mTvLimitWarn.text = ""
        } else {
            when {
                textLength > mViewModel.getInputTextMaxOverLimit() ->
                    updateLimitWarnText(mViewModel.getInputTextMaxOverLimit() - textLength, R.color.common_limit_over_color)
                textLength > mViewModel.getInputTextMaxWarnLimit() ->
                    updateLimitWarnText(textLength, R.color.main)
                else ->
                    updateLimitWarnText(textLength, R.color.common_limit_color)
            }
        }

        mViewModel.afterTextChanged(et_publish_editor.richProcessor.textLength,
                et_publish_editor.text,
                et_publish_editor.richProcessor.topicCount,
                et_publish_editor.hasFocus(),
                et_publish_editor.richProcessor.superTopicCount
        )
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    fun <T : DraftBase> attach(tvLimit: TextView, viewModel: AbsPublishViewModel<T>) {
        mTvLimitWarn = tvLimit
        mViewModel = viewModel
        viewModel.mMentionLiveData.observe(activityContext, Observer { mention ->
            mention?.let {
                if (it.withOutFlag) {
                    insertMentionWithOutFlag(it.uid, it.mention)
                } else {
                    insertUser(it.mention, it.uid)
                }
            }
        })

        viewModel.mLinkLiveData.observe(activityContext, Observer { link ->
            link?.let {
                insertLink(it)
            }
        })

        viewModel.mTopicLiveData.observe(activityContext, Observer { topic ->
            topic?.let {
                if (it.withOutFlag) {
                    insertTopicWithOutFlag(it.bean)
                } else {
                    if (it.index == -1) {
                        insertTopic(it.bean)
                    } else {
                        insertTopicAtIndex(it.index, it.bean)
                    }
                }
            }
        })

        viewModel.mSuperTopicLiveData.observe(activityContext, Observer {
            insertSuperTopic(it)
        })

        viewModel.mCommonTopicLiveData.observe(activityContext, Observer {
            insertCommonTopicWithOutFlag(it)
        })

        viewModel.mEmotionLiveData.observe(activityContext, Observer {
            if (it == null) {
                delete()
            } else {
                insertEmotion(it)
            }
        })

        viewModel.mRichContentLiveData.observe(activityContext, Observer {
            insertRichContent(it)
        })

        viewModel.mArticleLiveData.observe(activityContext, Observer {
            insertArticle(it)
        })

        viewModel.mContentLiveData.observe(activityContext, Observer {
            insertString(it)
        })
    }


    private fun updateLimitWarnText(textLength: Int, @ColorRes colorId: Int) {
        val spannableStringBuilder = SpannableStringBuilder((textLength).toString())
        val span = ForegroundColorSpan(mContext.resources.getColor(colorId))
        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mTvLimitWarn.text = spannableStringBuilder
    }

    private fun insertUser(nickName: String?, uid: String?) {

        et_publish_editor.richProcessor.insertMention(BlockFactory
                .createMention(nickName, uid))
    }

    private fun insertString(hashTail: String?) {
        hashTail?.let {
            et_publish_editor.richProcessor.insertSpannable(hashTail)
        }
    }

    private fun insertLink(url: String) {
        et_publish_editor.richProcessor.insertLink(BlockFactory
                .createLink(activityContext.getString(R.string.web_links), "", url))
        insertString(" ")
    }

    private fun insertTopic(bean: TopicSearchSugBean.TopicBean?) {
        if (bean?.id == null || bean.name == null) {
            return
        }
        PublisherEventManager.INSTANCE.addTopic_id(bean.name)
        et_publish_editor.richProcessor.insertTopic(BlockFactory
                .createTopic(bean.name, bean.id))
    }

    private fun insertTopicAtIndex(index: Int, bean: TopicSearchSugBean.TopicBean) {
        PublisherEventManager.INSTANCE.addTopic_id(bean.name)
        et_publish_editor.richProcessor.insertTopic(index, BlockFactory
                .createTopic(bean.name, bean.id))
    }

    /**
     * @param mention like @ABC
     */
    private fun insertMentionWithOutFlag(userId: String?, mention: String?) {
        et_publish_editor.richProcessor.insertMention(BlockFactory
                .createMentionWithOutFlag(userId, mention))
    }

    private fun insertTopicWithOutFlag(topic: TopicSearchSugBean.TopicBean?) {
        if (topic?.id == null || topic.name == null) {
            return
        }
        PublisherEventManager.INSTANCE.addTopic_id(topic.name)
        et_publish_editor.richProcessor.insertTopic(BlockFactory
                .createTopicWithOutFlag(topic.name, topic.id))
    }

    private fun insertSuperTopic(topic: SuperTopic?) {
        topic?.let {
            et_publish_editor.richProcessor.insertSuperTopic(BlockFactory.createSuperTopic(topic.id, topic.name, topic.labelId))
        }
    }

    private fun insertCommonTopicWithOutFlag(topic: CommonTopic?) {
        topic?.let {
            et_publish_editor.richProcessor.insertTopic(BlockFactory.createTopicWithOutFlag(topic.id, topic.name, topic.labelId))
        }
    }

    private fun insertEmotion(emojiBean: EmojiBean) {

        val view = activityContext.currentFocus
        if (view != null && view is RichEditText) {
            view.richProcessor.insertEmoji(emojiBean)
        } else {
            et_publish_editor.richProcessor.insertEmoji(emojiBean)
        }
        PublisherEventManager.INSTANCE.addEmoji_num()
        PublishAnalyticsManager.getInstance().obtainCurrentModel().addEmojiNum()
    }

    private fun insertRichContent(richContent: RichContent?) {
        richContent?.let {
            et_publish_editor.richProcessor.insertRichText(richContent.text, richContent.richItemList)
        }
    }

    private fun delete() {
        et_publish_editor.richProcessor.delete()
        val view = activityContext.currentFocus
        if (view != null && view is RichEditText) {
            view.richProcessor.delete()
        } else {
            et_publish_editor.richProcessor.delete()
        }

        PublisherEventManager.INSTANCE.removeEmoji_num()
    }

    private fun insertArticle(articleDraft: ArticleDraft?) {
        articleDraft?.let {
            et_publish_editor.richProcessor.insertArticle(BlockFactory.createArticle(activityContext, articleDraft.mPostName))
        }
    }

}