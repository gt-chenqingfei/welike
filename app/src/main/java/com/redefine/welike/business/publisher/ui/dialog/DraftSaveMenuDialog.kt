package com.redefine.welike.business.publisher.ui.dialog

import android.content.Context
import com.redefine.commonui.dialog.MenuItem
import com.redefine.commonui.dialog.OnMenuItemClickListener
import com.redefine.commonui.dialog.SimpleMenuDialog
import com.redefine.welike.R
import java.util.ArrayList

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */
class DraftSaveMenuDialog(val listener: OnMenuItemClickListener, context: Context) : SimpleMenuDialog(context) {

    companion object {
        private var ID_BASE = 0x00000000
        val MENU_ITEM_DISCARD = ID_BASE++
        val MENU_ITEM_CANCEL = ID_BASE++
        val MENU_ITEM_SAVE = ID_BASE++
    }

    fun showDialog() {
        val list = ArrayList<MenuItem>()
        list.add(MenuItem(MENU_ITEM_SAVE, context.getString(R.string.editor_discard_save)))
        list.add(MenuItem(MENU_ITEM_CANCEL, context.getString(R.string.editor_discard_cancel)))
        list.add(MenuItem(MENU_ITEM_DISCARD, context.getString(R.string.editor_discard_discard)))
        val deleteDialog = SimpleMenuDialog(context)
        deleteDialog.setCanceledOnTouchOutside(true)
        deleteDialog.setMenuItems(list, listener)
        deleteDialog.show()
    }
}