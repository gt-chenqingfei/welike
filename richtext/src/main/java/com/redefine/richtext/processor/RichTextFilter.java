package com.redefine.richtext.processor;

import android.text.Editable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.redefine.richtext.RichEditText;

import java.util.regex.Pattern;

/**
 * Created by liwenbo on 2018/3/15.
 */

public class RichTextFilter {
    public static final Pattern mLineSpacePattern = Pattern.compile("\\s*|\t|\r|\n");

    /**
     *
     * @param richEditText
     * @return
     */
    public static SpannableStringBuilder filter(RichEditText richEditText) {
        Editable e = richEditText.getText();
        Layout layout = richEditText.getLayout();
        if (layout == null) {
            return new SpannableStringBuilder(richEditText.getText());
        }
        LineState lineState = LineState.ONE_LINE;
        int lineCount = layout.getLineCount();
        int charStart;
        int charEnd;
        // 首先计算出有效行首和行尾
        int lineStart = 0;
        int lineEnd = lineCount - 1;
        CharSequence sequence;
        for (int i = 0; i < lineCount; i++) {
            // 计算行首有效行标
            charStart = layout.getLineStart(i);
            charEnd = layout.getLineEnd(i);
            sequence = e.subSequence(charStart, charEnd);
            if (!isNullString(sequence)) {
                lineStart = i;
                break;
            }
        }

        for (int i = lineCount - 1; i >= lineStart; i--) {
            // 计算行尾有效行标
            charStart = layout.getLineStart(i);
            charEnd = layout.getLineEnd(i);
            sequence = e.subSequence(charStart, charEnd);
            if (!isNullString(sequence)) {
                lineEnd = i;
                break;
            }
        }

        // 控制段首和段中空行处理

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = lineStart; i <= lineEnd; i++) {
            charStart = layout.getLineStart(i);
            charEnd = layout.getLineEnd(i);
            sequence = e.subSequence(charStart, charEnd);
            if (isNullString(sequence)) {
                if (lineState == LineState.ONE_LINE) {
                } else {
                    lineState = LineState.ONE_LINE;
                    stringBuilder.append(sequence);
                }
            } else {
                lineState = LineState.NONE;
                stringBuilder.append(sequence);
            }
        }
        // 去除段首空白字符
        int length = stringBuilder.length();
        charStart = 0;
        charEnd = length - 1;
        char c;
        for (int i = 0; i < length; i++) {
            c = stringBuilder.charAt(i);
            if (!isNullString(String.valueOf(c))) {
                charStart = i;
                break;
            }
        }

        for (int i = length - 1; i >= charStart; i--) {
            c = stringBuilder.charAt(i);
            if (!isNullString(String.valueOf(c))) {
                charEnd = i;
                break;
            }
        }
        // 去除段尾空白字符
        sequence = stringBuilder.subSequence(charStart, charEnd + 1);
        return new SpannableStringBuilder(sequence);
    }

    /**
     * 判断是否是全空白字符
     * @param sequence
     * @return
     */
    private static boolean isNullString(CharSequence sequence) {
        if (TextUtils.isEmpty(sequence)) {
            return true;
        }
        return mLineSpacePattern.matcher(sequence).matches();
    }

    /**
     * 过滤回车
     * @param editor
     * @return
     */
    public static SpannableStringBuilder filterLineFlag(Editable editor) {
        if (editor == null) {
            return new SpannableStringBuilder();
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int length = editor.length();
        char c;
        int index = 0;
        for (int i = 0; i < length; i++) {
            c = editor.charAt(i);
            if (c == '\n') {
                spannableStringBuilder.append(editor.subSequence(index, i)).append(" ");
                index = i + 1;
            }
        }
        if (index < length) {
            spannableStringBuilder.append(editor.subSequence(index, length));
        }
        return spannableStringBuilder;
    }

    public static enum LineState {
        NONE, ONE_LINE
    }
}
