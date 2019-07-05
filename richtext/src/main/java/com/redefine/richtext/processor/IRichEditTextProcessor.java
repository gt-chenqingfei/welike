package com.redefine.richtext.processor;

import com.redefine.richtext.RichContent;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.block.Block;
import com.redefine.richtext.emoji.bean.EmojiBean;

import java.util.List;

/**
 * Created by MR on 2018/1/23.
 */

public interface IRichEditTextProcessor {
    void onSelectionChanged(int selStart, int selEnd);

    boolean onTextContextMenuItem(int id);

    RichContent getRichContent(boolean isNeedFilterLineFlag, int summaryLimit, int contentLimit);

    void insertMention(Block block);

    void insertLink(Block link);

    void insertTopic(Block topic);

    void insertEmoji(EmojiBean emojiBean);

    void insertSpannable(CharSequence spannable);

    void insertRichText(String content, List<RichItem> richItemList);

    void setOnInputMentionListener(RichEditTextProcessor.OnInputRichFlagListener listener);

    void delete();

    int getTextLength();

//    void setFilterEnterChar(boolean isFilter);

    void insertMention(Block mention, boolean isAddSpace);

    void insertImage(Block image);

    int getInnerImagePicCount();

    int getInnerImageGiffyCount();

    int getBlankCount();

    int getTopicCount();

    int getSuperTopicCount();

    void insertTopic(int index, Block topic);

    void insertSuperTopic(Block topic);

    void setSupportInnerImage(boolean b);

    void insertArticle(Block article);

    void setRoundRectLink(boolean b);
}
