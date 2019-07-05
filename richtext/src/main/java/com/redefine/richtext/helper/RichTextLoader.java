package com.redefine.richtext.helper;

import android.content.Context;
import android.support.v4.text.util.LinkifyCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.URLSpan;

import com.redefine.richtext.RichItem;
import com.redefine.richtext.block.Block;
import com.redefine.richtext.block.BlockFactory;
import com.redefine.richtext.loader.ArticleLoader;
import com.redefine.richtext.loader.EmojiLoader;
import com.redefine.richtext.loader.InnerImageLoader;
import com.redefine.richtext.loader.LessLoader;
import com.redefine.richtext.loader.LinkLoader;
import com.redefine.richtext.loader.MentionLoader;
import com.redefine.richtext.loader.MoreLoader;
import com.redefine.richtext.loader.SuperTopicLoader;
import com.redefine.richtext.loader.TopicLoader;
import com.redefine.richtext.loader.UnSupportLoader;
import com.redefine.richtext.span.OnRichItemClickListener;
import com.redefine.richtext.util.CollectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class RichTextLoader {

    private final MentionLoader mMentionLoader;
    private final TopicLoader mTopicLoader;
    private final SuperTopicLoader mSuperTopicLoader;
    private final LinkLoader mLinkLoader;
    private final ArticleLoader mArticleLoader;
    private final InnerImageLoader mInnerImageLoader;
    private final EmojiLoader mEmojiLoader;
    private final MoreLoader mMoreLoader;
    private final LessLoader mLessLoader;
    private final UnSupportLoader mUnSupportLoader;
    private final Context mContext;
    private static final Pattern LINK_PATTERN = Pattern.compile("(http|https)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");


    public RichTextLoader(Context context) {
        mContext = context;
        mMentionLoader = new MentionLoader();
        mTopicLoader = new TopicLoader();
        mSuperTopicLoader = new SuperTopicLoader();
        mLinkLoader = new LinkLoader();
        mArticleLoader = new ArticleLoader();
        mEmojiLoader = new EmojiLoader();
        mMoreLoader = new MoreLoader();
        mLessLoader=new LessLoader();
        mInnerImageLoader = new InnerImageLoader();
        mUnSupportLoader = new UnSupportLoader();
    }

    public Spannable parseRichContent(String text, List<RichItem> items, IRichItemFilter richItemFilter, ICharItemFilter charItemFilter) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableStringBuilder();
        }
        try {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (!CollectionUtil.isEmpty(items)) {
                // 解析RichItem字段
                List<Block> blockList = new ArrayList<>();
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
                            if (charItemFilter != null) {
                                String c = charItemFilter.filter(b.blockText);
                                if (c == null) {
                                    c = "";
                                }
                                spannableStringBuilder.append(c);
                            } else {
                                spannableStringBuilder.append(b.blockText);
                            }
                        } else {
                            if (richItemFilter != null) {
                                Spannable spannable = richItemFilter.filter(b.richItem);
                                if (spannable != null) {
                                    spannableStringBuilder.append(spannable);
                                    continue;
                                }
                            }
                            if (b.richItem.isAtItem()) {
                                spannableStringBuilder.append(mMentionLoader.parseRichContent(b, false));
                            } else if (b.richItem.isTopicItem()) {
                                spannableStringBuilder.append(mTopicLoader.parseRichContent(b, false));
                            } else if (b.richItem.isSuperTopicItem()) {
                                spannableStringBuilder.append(mSuperTopicLoader.parseRichContent(mContext, b));
                            } else if (b.richItem.isLinkItem()) {
                                spannableStringBuilder.append(mLinkLoader.parseRichContent(mContext, b));
                            } else if (b.richItem.isInnerImageItem()) {
                                spannableStringBuilder.append(mInnerImageLoader.parseRichContent(mContext, b));
                            } else if (b.richItem.isArticleItem()) {
                                spannableStringBuilder.append(mArticleLoader.parseRichContent(mContext, b));
                            } else {
                                //暂不支持的显示不支持文案
                                spannableStringBuilder.append(mUnSupportLoader.parseRichContent(mContext, b));
                            }

                        }
                    }
                }
            } else {
                spannableStringBuilder.append(text);
            }
            // 最后对emoji表情解析
            return mEmojiLoader.parseEmojiContent(mContext, spannableStringBuilder);
        } catch (Exception e) {
            e.printStackTrace();
            return new SpannableStringBuilder(text);
        }
    }

    public Spannable parseRichContent(String text, List<RichItem> items) {
        return parseRichContent(text, items, null, null);
    }

    public void setOnRichItemClickListener(OnRichItemClickListener listener) {
        mLinkLoader.setOnRichItemClickListener(listener);
        mTopicLoader.setOnRichItemClickListener(listener);
        mSuperTopicLoader.setOnRichItemClickListener(listener);
        mMentionLoader.setOnRichItemClickListener(listener);
        mMoreLoader.setOnRichItemClickListener(listener);
        mLessLoader.setOnRichItemClickListener(listener);
        mArticleLoader.setOnRichItemClickListener(listener);
        mInnerImageLoader.setOnRichItemClickListener(listener);
    }

    public Spannable parseCommonLinks(CharSequence spannable) {
        SpannableStringBuilder editor = new SpannableStringBuilder(spannable);
        LinkifyCompat.addLinks(editor, LINK_PATTERN, "");
        URLSpan[] urlSpans = editor.getSpans(0, editor.length(), URLSpan.class);
        if (!CollectionUtil.isEmpty(urlSpans)) {
            // 替换Spanable的url
            int start;
            int end;
            Block block;
            for (URLSpan span : urlSpans) {
                start = editor.getSpanStart(span);
                end = editor.getSpanEnd(span);
                block = BlockFactory.getBlockByUrlSpan(span.getURL(), editor, span);
                editor.removeSpan(span);
                editor.replace(start, end, mLinkLoader.parseRichContentWidthoutIcon(mContext, block));
            }
        }
        return editor;
    }

    public Spannable parseMore(String display) {
        return mMoreLoader.parseRichContent(BlockFactory.createMore(display));
    }
    public Spannable parseLess(String display) {
        return mLessLoader.parseRichContent(BlockFactory.createLess(display));
    }

    public void setRoundRectLink(boolean b) {
        mLinkLoader.setRoundRectLink(b);
    }

    public static interface IRichItemFilter {
        Spannable filter(RichItem richItem);
    }

    public static interface ICharItemFilter {
        String filter(String chars);
    }
}
