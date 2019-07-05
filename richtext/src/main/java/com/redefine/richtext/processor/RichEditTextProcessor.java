package com.redefine.richtext.processor;

import android.support.annotation.NonNull;
import android.support.v4.text.util.LinkifyCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.redefine.richtext.R;
import com.redefine.richtext.RichContent;
import com.redefine.richtext.RichEditText;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.block.Block;
import com.redefine.richtext.block.BlockFactory;
import com.redefine.richtext.constant.RichConstant;
import com.redefine.richtext.copy.RichTextClipboardManager;
import com.redefine.richtext.emoji.bean.EmojiBean;
import com.redefine.richtext.helper.RichTextHelper;
import com.redefine.richtext.loader.ArticleLoader;
import com.redefine.richtext.loader.EmojiLoader;
import com.redefine.richtext.loader.InnerImageLoader;
import com.redefine.richtext.loader.LinkLoader;
import com.redefine.richtext.loader.MentionLoader;
import com.redefine.richtext.loader.SuperTopicLoader;
import com.redefine.richtext.loader.TopicLoader;
import com.redefine.richtext.loader.UnSupportLoader;
import com.redefine.richtext.span.EmojiSpan;
import com.redefine.richtext.span.RichSpan;
import com.redefine.richtext.util.CollectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MR on 2018/1/23.
 */

public class RichEditTextProcessor implements IRichEditTextProcessor, View.OnKeyListener, TextWatcher {

    private final RichEditText mRichEditText;
    private final LinkLoader mLinkLoader;
    private final MentionLoader mMentionLoader;
    private final TopicLoader mTopicLoader;
    private final SuperTopicLoader mSuperTopicLoader;
    private final EmojiLoader mEmojiLoader;
    private final InnerImageLoader mInnerImageLoader;
    private final UnSupportLoader mUnSupportLoader;
    private final ArticleLoader mArticleLoader;
    private int mLastSelStart;
    private int mLastSelEnd;
    private static final Pattern LINK_PATTERN = Pattern.compile("(http|https)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
    private OnInputRichFlagListener mRichFlagInputListener;
    private boolean isCallMetionList;
    private boolean isCallTopicList;

    public RichEditTextProcessor(RichEditText editText) {
        mRichEditText = editText;
        mLinkLoader = new LinkLoader();
        mMentionLoader = new MentionLoader();
        mTopicLoader = new TopicLoader();
        mSuperTopicLoader = new SuperTopicLoader();
        mEmojiLoader = new EmojiLoader();
        mInnerImageLoader = new InnerImageLoader();
        mUnSupportLoader = new UnSupportLoader();
        mRichEditText.setOnKeyListener(this);
        mArticleLoader = new ArticleLoader();
        mRichEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
        mRichEditText.addTextChangedListener(this);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
            int startPos = mRichEditText.getSelectionStart();
            RichSpan span = getRichSpanOnSectionEnd(0, startPos);
            Spannable spannable = mRichEditText.getText();
            if (span != null && startPos == mRichEditText.getSelectionEnd()) {
                // 光标前面是富文本标签,
                mRichEditText.requestFocus();
                mRichEditText.clearFocus();
                mRichEditText.setSelection(spannable.getSpanStart(span), spannable.getSpanEnd(span));
                mRichEditText.requestFocus();
                return true;
            }
        }
        return false;
    }

    /**
     * 从selectionStart往右查找最近的富文本
     *
     * @return
     */
    public RichSpan getRichSpanOnSectionStart(int start, int end) {
        Spannable spannable = mRichEditText.getText();
        RichSpan[] spans = spannable.getSpans(start, end, RichSpan.class);
        if (CollectionUtil.isEmpty(spans)) {
            // 只取被标记span
            return null;
        }
        for (RichSpan span : spans) {
            if (start >= spannable.getSpanStart(span) && start < spannable.getSpanEnd(span)) {
                return span;
            }
        }
        return null;
    }

    /**
     * 从selectionEnd往左查找最近的富文本
     *
     * @return
     */
    public RichSpan getRichSpanOnSectionEnd(int start, int end) {
        Spannable spannable = mRichEditText.getText();
        RichSpan[] spans = spannable.getSpans(start, end, RichSpan.class);
        if (CollectionUtil.isEmpty(spans)) {
            // 只取被标记span
            return null;
        }
        for (RichSpan span : spans) {
            if (end > spannable.getSpanStart(span) && end <= spannable.getSpanEnd(span)) {
                return span;
            }
        }
        return null;
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        try {
            checkSpanOnSectionChange();
            doSelectionChange(selStart, selEnd);
        } catch (Exception e) {
            // do nothing
        }
    }

    private void checkSpanOnSectionChange() {
        Editable editor = mRichEditText.getText();
        EmojiSpan[] emojiSpans = editor.getSpans(0, editor.length(), EmojiSpan.class);

        if (!CollectionUtil.isEmpty(emojiSpans)) {
            for (EmojiSpan emojiSpan : emojiSpans) {

                CharSequence charSequence = editor.subSequence(editor.getSpanStart(emojiSpan), editor.getSpanEnd(emojiSpan));
                // check 显示
                if (emojiSpan.getEmojiBean() == null) {
                    editor.removeSpan(emojiSpan);
                }
                if (!TextUtils.equals(charSequence, emojiSpan.getEmojiBean().emoji)) {
                    mRichEditText.getEditableText().delete(editor.getSpanStart(emojiSpan), editor.getSpanEnd(emojiSpan));
                    editor.removeSpan(emojiSpan);
                }
            }
        }

        RichSpan[] spans = editor.getSpans(0, editor.length(), RichSpan.class);
        if (CollectionUtil.isEmpty(spans)) {
            return;
        }
        Block block;
        for (RichSpan richClickableSpan : spans) {
            block = richClickableSpan.getBlock();
            if (block == null) {
                editor.removeSpan(richClickableSpan);
                continue;
            }
            CharSequence charSequence = editor.subSequence(editor.getSpanStart(richClickableSpan), editor.getSpanEnd(richClickableSpan));
            // check 显示
            if (!TextUtils.equals(charSequence, block.blockText)) {
                mRichEditText.getEditableText().delete(editor.getSpanStart(richClickableSpan), editor.getSpanEnd(richClickableSpan));
                editor.removeSpan(richClickableSpan);
            }
        }
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        int min = 0;
        int max = mRichEditText.getText().length();

        if (mRichEditText.isFocused()) {
            final int selStart = mRichEditText.getSelectionStart();
            final int selEnd = mRichEditText.getSelectionEnd();

            min = Math.max(0, Math.min(selStart, selEnd));
            max = Math.max(0, Math.max(selStart, selEnd));
        }

        switch (id) {
            case android.R.id.cut:
                CharSequence cutContent = mRichEditText.getText().subSequence(min, max);
                RichTextClipboardManager.getInstance().copy(mRichEditText.getContext(), cutContent);
                return false;
            case android.R.id.paste:
                CharSequence pasteContent = RichTextClipboardManager.getInstance().parse(mRichEditText.getContext());
                CharSequence realParse = preParse(pasteContent);
                mRichEditText.getText().replace(min, max, realParse);
                return true;
            case android.R.id.copy:
                CharSequence copyContent = mRichEditText.getText().subSequence(min, max);
                RichTextClipboardManager.getInstance().copy(mRichEditText.getContext(), copyContent);
                return false;
        }
        return false;
    }

    private CharSequence preParse(CharSequence copyContent) {
        SpannableStringBuilder s = new SpannableStringBuilder(copyContent);
        RichSpan[] spans = s.getSpans(0, s.length(), RichSpan.class);
        EmojiSpan[] emojiSpans = s.getSpans(0, s.length(), EmojiSpan.class);
        if (!CollectionUtil.isEmpty(spans)) {
            for (RichSpan span : spans) {
                replaceSpan(s, span);
                s.removeSpan(span);
            }
        }
        if (!CollectionUtil.isEmpty(emojiSpans)) {
            for (EmojiSpan span : emojiSpans) {
                s.removeSpan(span);
            }
        }
        return mEmojiLoader.parseEmojiContent(mRichEditText.getContext(), s);
    }

    private void replaceSpan(SpannableStringBuilder s, RichSpan span) {
        Block b = span.getBlock();
        if (b.richItem == null) {
            return;
        }
        if (TextUtils.isEmpty(b.blockText)) {
            return;
        }
        int start = s.getSpanStart(span);
        int end = s.getSpanEnd(span);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (b.richItem.isAtItem()) {
            spannableStringBuilder.append(mMentionLoader.parseRichContent(b, false));
        } else if (b.richItem.isTopicItem()) {
            spannableStringBuilder.append(mTopicLoader.parseRichContent(b, false));
        } else if (b.richItem.isSuperTopicItem()) {
            spannableStringBuilder.append(mSuperTopicLoader.parseRichContent(mRichEditText.getContext(), b));
        } else if (b.richItem.isLinkItem()) {
            spannableStringBuilder.append(mLinkLoader.parseRichContent(mRichEditText.getContext(), b));
        } else if (b.richItem.isInnerImageItem()) {
            spannableStringBuilder.append(mInnerImageLoader.parseRichContent(mRichEditText.getContext(), b));
        } else if (b.richItem.isArticleItem()) {
            spannableStringBuilder.append(mArticleLoader.parseRichContent(mRichEditText.getContext(), b));
        } else {
            //暂不支持的显示不支持文案
            spannableStringBuilder.append(mUnSupportLoader.parseRichContent(mRichEditText.getContext(), b));
        }
        s.replace(start, end, spannableStringBuilder);
    }

    @Override
    public RichContent getRichContent(boolean isNeedFilterLineFlag, int summaryLimit, int contentLimit) {
        // 首先解析未标记的url
        SpannableStringBuilder editor = RichTextFilter.filter(mRichEditText);
        if (isNeedFilterLineFlag) {
            editor = RichTextFilter.filterLineFlag(editor);
        }

        LinkifyCompat.addLinks(editor, LINK_PATTERN, "");
        URLSpan[] urlSpans = editor.getSpans(0, editor.length(), URLSpan.class);
        RichSpan[] richSpans = editor.getSpans(0, editor.length(), RichSpan.class);

        // 防止存在span嵌套的bug
        if (!CollectionUtil.isEmpty(richSpans) && !CollectionUtil.isEmpty(urlSpans)) {
            int richStart = 0;
            int richEnd = 0;

            int urlStart = 0;
            int urlEnd = 0;
            for (RichSpan richSpan : richSpans) {
                richStart = editor.getSpanStart(richSpan);
                richEnd = richStart + richSpan.getBlock().blockText.length();
                for (URLSpan span : urlSpans) {
                    urlStart = editor.getSpanStart(span);
                    urlEnd = editor.getSpanEnd(span);
                    if (urlStart >= richStart && urlEnd <= richEnd) {
                        editor.removeSpan(span);
                    }
                }
            }
        }
        urlSpans = editor.getSpans(0, editor.length(), URLSpan.class);

        if (!CollectionUtil.isEmpty(urlSpans)) {
            // 替换Spanable的url
            int start = 0;
            int end = 0;
            Block block;
            for (URLSpan span : urlSpans) {
                start = editor.getSpanStart(span);
                end = editor.getSpanEnd(span);
                RichSpan[] spans = editor.getSpans(start, end, RichSpan.class);
                if (!CollectionUtil.isEmpty(spans)) {
                    continue;
                }
                block = BlockFactory.getBlockByUrlSpan(mRichEditText.getResources().getString(R.string.web_links), editor, span);
                editor.removeSpan(span);
                editor.replace(start, end, mLinkLoader.parseRichContent(mRichEditText.getContext(), block));
            }
        }
        // 进行富文本的统一标记
        RichSpan[] spans = editor.getSpans(0, editor.length(), RichSpan.class);
        RichContent richContent = new RichContent();
        richContent.text = editor.toString();

        richContent.richItemList = new ArrayList<RichItem>();
        RichItem item;
        for (RichSpan richClickableSpan : spans) {
            item = richClickableSpan.getRichItem();
            if (item == null) {
                continue;
            }
            item.index = editor.getSpanStart(richClickableSpan);
            item.source = TextUtils.isEmpty(item.source)? "": item.source.trim();
            item.display = TextUtils.isEmpty(item.display)? "": item.display.trim();
            richContent.richItemList.add(item);
        }
        if (richContent.richItemList.size() > 1) {
            // 两个以上才进行排序
            Collections.sort(richContent.richItemList);
        }

        if (getTextLength() > contentLimit) {
            CharSequence spannable = getSummary(editor, contentLimit);
            richContent.text = spannable.toString();
        }

        if (summaryLimit > 0) {
            CharSequence spannable = getSummary(editor, summaryLimit);
            richContent.summary = spannable.toString();
        } else {
            richContent.summary = richContent.text;
        }


        return richContent;
    }

    private CharSequence getSummary(Editable editor, int summaryLimit) {
        if (editor.length() <= summaryLimit) {
            return editor;
        }
        RichSpan[] spans = editor.getSpans(0, editor.length(), RichSpan.class);
        EmojiSpan[] emojiSpans = editor.getSpans(0, editor.length(), EmojiSpan.class);
        List<Range> ranges = new ArrayList<Range>();
        Range range;
        if (!CollectionUtil.isEmpty(spans)) {
            for (RichSpan span : spans) {
                range = new Range();
                if ((span.getRichItem() == null)) {
                    continue;
                }
                range.type = Range.getRichItemType(span.getRichItem().type);
                range.start = editor.getSpanStart(span);
                range.end = editor.getSpanEnd(span);
                ranges.add(range);
            }
        }
        if (!CollectionUtil.isEmpty(emojiSpans)) {
            for (EmojiSpan emojiSpan : emojiSpans) {
                range = new Range();
                range.type = Range.EMOJI;
                range.start = editor.getSpanStart(emojiSpan);
                range.end = editor.getSpanEnd(emojiSpan);
                ranges.add(range);
            }
        }
        if (!CollectionUtil.isEmpty(ranges)) {
            if (ranges.size() > 1) {
                Collections.sort(ranges);
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            int lastIndex = 0;
            int currentLength = 0;
            int contentLength = editor.length();
            boolean isOverLimit = false;
            for (Range r : ranges) {
                if (lastIndex != r.start) {
                    //两个富文本之间有普通文本
                    if (isOverLimit(currentLength, new Range(lastIndex, r.start), summaryLimit)) {
                        int maxLength = Math.min(editor.length(), lastIndex + (summaryLimit - currentLength));
                        CharSequence sequence = editor.subSequence(lastIndex, maxLength);
                        currentLength += sequence.length();
                        spannableStringBuilder.append(sequence);
                        isOverLimit = true;
                        break;
                    } else {
                        int maxLength = Math.min(editor.length(), r.start);
                        CharSequence sequence = editor.subSequence(lastIndex, maxLength);
                        currentLength += sequence.length();
                        spannableStringBuilder.append(sequence);
                    }
                }
                if (isOverLimit(currentLength, r, summaryLimit)) {
                    // 富文本超出不保留
                    isOverLimit = true;
                    break;
                } else {
                    int maxStart = Math.min(editor.length(), r.start);
                    int maxEnd = Math.min(editor.length(), r.end);
                    CharSequence sequence = editor.subSequence(maxStart, maxEnd);
                    currentLength += RichTextHelper.getRichTextLength(r);
                    spannableStringBuilder.append(sequence);
                }
                lastIndex = r.end;
            }
            // 已经越界，则直接返回
            if (isOverLimit) {
                return spannableStringBuilder;
            }
            // 未越界则继续截取common字符
            if (isOverLimit(currentLength, new Range(lastIndex, contentLength), summaryLimit)) {
                int maxLength = Math.min(editor.length(), lastIndex + (summaryLimit - currentLength));
                CharSequence sequence = editor.subSequence(lastIndex, maxLength);
                spannableStringBuilder.append(sequence);
                return spannableStringBuilder;
            } else {
                // 一直都没越界，返回所有串
                return editor;
            }

        } else {
            int maxLength = Math.min(editor.length(), summaryLimit);
            return editor.subSequence(0, maxLength);
        }
    }

    private boolean isOverLimit(int current, Range range, int limit) {
        if (range.type == Range.COMMON) {
            int total = current + range.end - range.start;
            return total > limit;
        } else {
            int total = current + RichTextHelper.getRichTextLength(range);
            return total > limit;
        }
    }

    @Override
    public void insertMention(Block block) {
        insertMention(block, true);
    }

    @Override
    public void insertMention(Block block, boolean isAddSpace) {
        Editable editable = mRichEditText.getText();
        int start = mRichEditText.getSelectionStart();
        int end = mRichEditText.getSelectionEnd();
        start = Math.max(0, Math.min(start, end));
        end = Math.max(0, Math.max(start, end));
        if (start == 0) {
            Spannable spannable = mMentionLoader.parseRichContent(block, isAddSpace);
            insertSpannable(spannable);
            return;
        }
        CharSequence charSequence = editable.subSequence(start - 1, start);
        if (start == end && TextUtils.equals(charSequence, RichConstant.AT) && isCallMetionList) {
            isCallMetionList = false;
            Spannable spannable = mMentionLoader.parseRichContent(block, isAddSpace);
            insertSpannable(spannable, start - 1, start);
        } else {
            Spannable spannable = mMentionLoader.parseRichContent(block, isAddSpace);
            insertSpannable(spannable);
        }
    }

    @Override
    public void insertImage(Block image) {
        Spannable spannable =  mInnerImageLoader.parseRichContent(mRichEditText.getContext(), image);
        Editable editable = mRichEditText.getText();
        int start = mRichEditText.getSelectionStart();
        int end = mRichEditText.getSelectionEnd();
        start = Math.max(0, Math.min(start, end));
        end = Math.max(0, Math.max(start, end));
        editable.delete(start, end);
        editable.insert(start, spannable);
        start = mRichEditText.getSelectionStart();
        editable.insert(start, "\n");
    }

    @Override
    public int getTopicCount() {
        Editable editor = mRichEditText.getText();
        RichSpan[] spans = editor.getSpans(0, editor.length(), RichSpan.class);
        int topicLength = 0;
        for (RichSpan span : spans) {
            if (span.getRichItem() != null && (span.getRichItem().isTopicItem() || span.getRichItem().isSuperTopicItem())) {
                topicLength++;
            }
        }
        return topicLength;
    }

    @Override
    public int getSuperTopicCount() {
        Editable editor = mRichEditText.getText();
        RichSpan[] spans = editor.getSpans(0, editor.length(), RichSpan.class);
        int topicLength = 0;
        for (RichSpan span : spans) {
            if (span.getRichItem() != null && (span.getRichItem().isSuperTopicItem())) {
                topicLength++;
            }
        }
        return topicLength;
    }

    @Override
    public int getInnerImagePicCount() {
        Editable editor = mRichEditText.getText();
        RichSpan[] spans = editor.getSpans(0, editor.length(), RichSpan.class);
        int topicLength = 0;
        for (RichSpan span : spans) {
            if (span.getRichItem() != null && span.getRichItem().isInnerImageItem() && TextUtils.equals(span.getRichItem().classify, RichItem.RICH_SUB_TYPE_PIC)) {
                topicLength++;
            }
        }
        return topicLength;
    }

    @Override
    public int getInnerImageGiffyCount() {
        Editable editor = mRichEditText.getText();
        RichSpan[] spans = editor.getSpans(0, editor.length(), RichSpan.class);
        int topicLength = 0;
        for (RichSpan span : spans) {
            if (span.getRichItem() != null && span.getRichItem().isInnerImageItem() && TextUtils.equals(span.getRichItem().classify, RichItem.RICH_SUB_TYPE_EMOTION)) {
                topicLength++;
            }
        }
        return topicLength;
    }

    @Override
    public int getBlankCount() {
        Editable editor = mRichEditText.getText();
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(editor);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    @Override
    public void insertTopic(int index, Block block) {
        Spannable spannable = mTopicLoader.parseRichContent(block);
        mRichEditText.getText().insert(0, spannable);
    }

    @Override
    public void insertSuperTopic(Block topic) {
        Spannable spannable = mSuperTopicLoader.parseRichContent(mRichEditText.getContext(), topic);
        insertSpannable(spannable);
    }

    @Override
    public void setSupportInnerImage(boolean b) {
        mInnerImageLoader.enable(b);
    }

    @Override
    public void insertArticle(Block article) {
        Spannable spannable = mArticleLoader.parseRichContent(mRichEditText.getContext(), article);
        mRichEditText.getText().insert(0, spannable);
    }

    @Override
    public void setRoundRectLink(boolean b) {
        mLinkLoader.setRoundRectLink(b);
    }

    private void insertSpannable(Spannable spannable, int start, int end) {
        Editable editable = mRichEditText.getText();
        start = Math.max(0, Math.min(start, end));
        end = Math.max(0, Math.max(start, end));
        editable.replace(start, end, spannable);
    }

    @Override
    public void insertLink(Block block) {
        Spannable spannable = mLinkLoader.parseRichContent(mRichEditText.getContext(), block);
        insertSpannable(spannable);
    }

    @Override
    public void insertTopic(Block block) {
        Editable editable = mRichEditText.getText();
        int start = mRichEditText.getSelectionStart();
        int end = mRichEditText.getSelectionEnd();
        start = Math.max(0, Math.min(start, end));
        end = Math.max(0, Math.max(start, end));
        if (start == 0) {
            Spannable spannable = mTopicLoader.parseRichContent(block);
            insertSpannable(spannable);
            return;
        }
        CharSequence charSequence = editable.subSequence(start - 1, start);
        if (start == end && TextUtils.equals(charSequence, RichConstant.TOPIC) && isCallTopicList) {
            isCallTopicList = false;
            Spannable spannable = mTopicLoader.parseRichContent(block);
            insertSpannable(spannable, start - 1, start);
        } else {
            Spannable spannable = mTopicLoader.parseRichContent(block);
            insertSpannable(spannable);
        }
    }

    @Override
    public void insertEmoji(EmojiBean emojiBean) {
        Spannable spannable = mEmojiLoader.parseEmojiContent(mRichEditText.getContext(), emojiBean);
        insertSpannable(spannable);
    }

    @Override
    public void insertSpannable(CharSequence spannable) {
        Editable editable = mRichEditText.getText();
        int start = mRichEditText.getSelectionStart();
        int end = mRichEditText.getSelectionEnd();
        start = Math.max(0, Math.min(start, end));
        end = Math.max(0, Math.max(start, end));
        editable.replace(start, end, spannable);
    }

    @Override
    public void insertRichText(String text, List<RichItem> items) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        try {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(mRichEditText.getText());
            if (!CollectionUtil.isEmpty(items)) {
                // 解析RichItem字段
                List<Block> blockList = new ArrayList<Block>();
                int lastIndex = 0;
                Block block;
                int contentLength = text.length();
                if (items.size() > 1) {
                    // 两个以上才进行排序
                    Collections.sort(items);
                }
                for (RichItem item : items) {
                    // 解析当前富文本和下个
                    if (item.index < 0 || item.index > contentLength || (item.index + item.length) > contentLength) {
                        // 越界的丢掉
                        continue;
                    }
                    if (lastIndex != item.index) {
                        //两个富文本之间有普通文本
                        block = new Block();
                        block.blockText = text.substring(lastIndex, item.index);
                        blockList.add(block);
                    }
                    block = new Block();
                    block.blockText = text.substring(item.index, item.index + item.length);
                    block.richItem = item;
                    blockList.add(block);
                    lastIndex = item.index + item.length;
                }
                if (lastIndex < contentLength) {
                    block = new Block();
                    block.blockText = text.substring(lastIndex, contentLength);
                    blockList.add(block);
                }
                // 解析完成block字段，开始对每个富文本解析器做解析
                if (CollectionUtil.isEmpty(blockList)) {
                    spannableStringBuilder.append(text);
                } else {
                    for (Block b : blockList) {
                        if (TextUtils.isEmpty(b.blockText)) {
                            continue;
                        }
                        if (b.richItem == null) {
                            spannableStringBuilder.append(b.blockText);
                        } else {
                            if (b.richItem.isAtItem()) {
                                spannableStringBuilder.append(mMentionLoader.parseRichContent(b, false));
                            } else if (b.richItem.isTopicItem()) {
                                spannableStringBuilder.append(mTopicLoader.parseRichContent(b, false));
                            } else if (b.richItem.isSuperTopicItem()) {
                                spannableStringBuilder.append(mSuperTopicLoader.parseRichContent(mRichEditText.getContext(), b));
                            } else if (b.richItem.isLinkItem()) {
                                spannableStringBuilder.append(mLinkLoader.parseRichContent(mRichEditText.getContext(), b));
                            } else if (b.richItem.isArticleItem()) {
                                spannableStringBuilder.append(mArticleLoader.parseRichContent(mRichEditText.getContext(), b));
                            } else {
                                //暂不支持的显示不支持文案
                                spannableStringBuilder.append(mUnSupportLoader.parseRichContent(mRichEditText.getContext(), b));
                            }

                        }
                    }
                }
            } else {
                spannableStringBuilder.append(text);
            }
            // 最后对emoji表情解析
            Spannable spannable = mEmojiLoader.parseEmojiContent(mRichEditText.getContext(), spannableStringBuilder);

            mRichEditText.setText(spannable);
        } catch (Exception e) {
            // do nothing
            e.printStackTrace();
        }
    }

    private void doSelectionChange(int selStart, int selEnd) {
        selStart = Math.max(0, Math.min(selStart, selEnd));
        selEnd = Math.max(0, Math.max(selStart, selEnd));
        if (selStart == mLastSelStart && mLastSelEnd == selEnd) {
            return;
        }
        if (selStart == 0 && selEnd == 0) {
            mLastSelStart = selStart;
            mLastSelEnd = selEnd;
            return;
        }

        Spannable spannable = mRichEditText.getText();

        RichSpan leftSpan = getRichSpanOnSectionStart(selStart, selEnd);
        RichSpan rightSpan = getRichSpanOnSectionEnd(selStart, selEnd);
        int realStart = selStart;
        int realEnd = selEnd;

        if (leftSpan != null) {
            realStart = spannable.getSpanStart(leftSpan);
        }

        if (rightSpan != null) {
            realEnd = spannable.getSpanEnd(rightSpan);
        }
        mLastSelStart = realStart;
        mLastSelEnd = realEnd;
        if (realStart == selStart && realEnd == selEnd) {
            return;
        }
        mRichEditText.setSelection(realStart, realEnd);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count == 1) {
            CharSequence charSequence = s.subSequence(start, start + count);
            if (TextUtils.equals(charSequence, RichConstant.AT)) {
                if (mRichFlagInputListener != null) {
                    isCallMetionList = true;
                    mRichFlagInputListener.onMentionInput();
                }
            } else if (TextUtils.equals(charSequence, RichConstant.TOPIC)) {
                if (mRichFlagInputListener != null) {
                    isCallTopicList = true;
                    mRichFlagInputListener.onTopicInput();
                }
            } else {
                isCallMetionList = false;
                isCallTopicList = false;
            }
        } else {
            isCallMetionList = false;
            isCallTopicList = false;
        }
    }

    public void setOnInputMentionListener(OnInputRichFlagListener listener) {
        mRichFlagInputListener = listener;
    }

    @Override
    public void delete() {
        if (!TextUtils.isEmpty(mRichEditText.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            mRichEditText.dispatchKeyEvent(event);
        }
    }

    @Override
    public int getTextLength() {
        return RichTextHelper.getRichTextLength(mRichEditText.getText());
    }

    public static interface OnInputRichFlagListener {
        void onMentionInput();

        void onTopicInput();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public static class Range implements Comparable<Range> {
        public static final int COMMON = 0;
        public static final int UNKONWN = -1;
        public static final int EMOJI = 1;
        public static final int LINK = 2;
        public static final int TOPIC = 3;
        public static final int SUPER_TOPIC = 3;
        public static final int MENTION = 4;
        public static final int INNER_IMAGE = 5;
        public static final int ARTICLE = 6;
        public int type; // 0
        public int start;
        public int end;

        public Range(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public Range() {

        }

        @Override
        public int compareTo(@NonNull Range o) {
            return start - o.start;
        }

        public static int getRichItemType(String type) {
            if (TextUtils.equals(type, RichItem.RICH_TYPE_TOPIC)) {
                return TOPIC;
            } else if (TextUtils.equals(type, RichItem.RICH_TYPE_SUPER_TOPIC)) {
                return SUPER_TOPIC;
            } else if (TextUtils.equals(type, RichItem.RICH_TYPE_LINK)) {
                return LINK;
            } else if (TextUtils.equals(type, RichItem.RICH_TYPE_MENTION)) {
                return MENTION;
            } else if (TextUtils.equals(type, RichItem.RICH_TYPE_INNER_IMAGE)) {
                return INNER_IMAGE;
            } else if (TextUtils.equals(type, RichItem.RICH_TYPE_ARTICLE)) {
                return ARTICLE;
            } else {
                return UNKONWN;
            }
        }
    }
}
