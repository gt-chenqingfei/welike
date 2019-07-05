package com.redefine.welike.business.publisher.viewmodel

import android.text.SpannableStringBuilder
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.richtext.RichContent
import com.redefine.richtext.RichItem
import com.redefine.richtext.block.BlockFactory

/**
 * @author qingfei.chen
 * @date 2019/1/18
 * Copyright (C) 2018 redefine , Inc.
 */
object RichContentHelper {

    fun buildForwardRichContent(uid: String, nickName: String, text: String,
                                richItem: List<RichItem>?): RichContent {
        val block = BlockFactory.createMention(nickName, uid)

        val result = SpannableStringBuilder()
        result.append("//")
        block.richItem.index = result.length
        result.append(block.blockText).append(": ")
        val length = result.length
        result.append(text)
        val content = RichContent()
        val richItemList: MutableList<RichItem>
        if (CollectionUtil.isEmpty(richItem)) {
            richItemList = ArrayList()
            richItemList.add(block.richItem)
            content.richItemList = richItemList
        } else {
            // 需要矫正comment的富文本
            richItemList = ArrayList()
            richItemList.add(block.richItem)
            var temp: RichItem
            // 需要深拷贝，因为修改了index
            richItem?.let {
                for (item in richItem) {
                    temp = item.copyRichItem()
                    temp.index = item.index + length
                    richItemList.add(temp)
                }
            }
        }
        content.text = result.toString()
        content.summary = content.text
        content.richItemList = richItemList
        return content
    }

    fun buildRichContent(text: String, richItem: List<RichItem>?): RichContent {

        val result = SpannableStringBuilder()
        val length = result.length
        result.append(text)
        val content = RichContent()
        val richItemList: MutableList<RichItem> = ArrayList()
        if (CollectionUtil.isEmpty(richItem)) {
            content.richItemList = richItemList
        } else {
            var temp: RichItem
            richItem?.let {
                for (item in richItem) {
                    temp = item.copyRichItem()
                    temp.index = item.index + length
                    richItemList.add(temp)
                }
            }
        }
        content.text = result.toString()
        content.summary = content.text
        content.richItemList = richItemList
        return content
    }
}