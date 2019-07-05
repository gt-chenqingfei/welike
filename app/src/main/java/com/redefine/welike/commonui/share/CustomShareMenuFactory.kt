package com.redefine.welike.commonui.share

import android.content.Context
import com.redefine.commonui.share.*
import com.redefine.commonui.share.sharemedel.ShareModel
import com.redefine.commonui.share.sharemedel.SharePackageModel
import com.redefine.welike.R
import com.redefine.welike.base.resource.ResourceTool

/**
 * Created by nianguowang on 2019/1/11
 */
enum class ShareMenu {
    BLOCK, UNBLOCK, DELETE, UNLIKE, REPORT, TOP, UN_TOP
}

class CustomShareMenuFactory {
    companion object {

        fun createMenu(shareMenu: ShareMenu, listener: (ShareMenu)->Unit): SharePackageModel {
            return when(shareMenu) {
                ShareMenu.BLOCK     -> createBlockMenu(listener)
                ShareMenu.UNBLOCK   -> createUnBlockMenu(listener)
                ShareMenu.DELETE    -> createDeleteMenu(listener)
                ShareMenu.UNLIKE    -> createUnlikeMenu(listener)
                ShareMenu.REPORT    -> createReportMenu(listener)
                ShareMenu.TOP       -> createTopMenu(listener)
                ShareMenu.UN_TOP    -> createUnTopMenu(listener)
            }
        }

        fun createEmptyMenu(): SharePackageModel {
            return SharePackageModel("", com.redefine.commonui.R.color.white, SharePackageFactory.SharePackage.EMPTY, EmptyMenuShareManager(SharePackageFactory.SharePackage.EMPTY));
        }

        private fun createBlockMenu(listenerInvoker: (ShareMenu)->Unit): SharePackageModel {
            return SharePackageModel(ResourceTool.getString("block"),
                    R.drawable.block_share_icon, SharePackageFactory.SharePackage.MENU1,
                    object :DefaultShareMenuManager(SharePackageFactory.SharePackage.MENU1){
                        override fun shareTo(context: Context?, shareModel: ShareModel?, listener: CommonListener<String>?) {
                            super.shareTo(context, shareModel, listener)
                            listenerInvoker.invoke(ShareMenu.BLOCK)
                        }
                    })
        }

        private fun createUnBlockMenu(listenerInvoker: (ShareMenu)->Unit): SharePackageModel {
            return SharePackageModel(ResourceTool.getString("unblock"),
                    R.drawable.block_share_icon, SharePackageFactory.SharePackage.MENU1,
                    object :DefaultShareMenuManager(SharePackageFactory.SharePackage.MENU1) {
                        override fun shareTo(context: Context?, shareModel: ShareModel?, listener: CommonListener<String>?) {
                            super.shareTo(context, shareModel, listener)
                            listenerInvoker.invoke(ShareMenu.UNBLOCK)
                        }
                    })
        }

        private fun createDeleteMenu(listenerInvoker: (ShareMenu)->Unit): SharePackageModel {
            return SharePackageModel(ResourceTool.getString("feed_delete_confirm"),
                    R.drawable.delete_share_icon, SharePackageFactory.SharePackage.MENU2,
                    object : DefaultShareMenuManager(SharePackageFactory.SharePackage.MENU2) {
                        override fun shareTo(context: Context?, shareModel: ShareModel?, listener: CommonListener<String>?) {
                            super.shareTo(context, shareModel, listener)
                            listenerInvoker.invoke(ShareMenu.DELETE)
                        }
                    })
        }

        private fun createUnlikeMenu(listenerInvoker: (ShareMenu)->Unit): SharePackageModel {
            return SharePackageModel(ResourceTool.getString("feed_unlike"),
                    R.drawable.like_share_icon, SharePackageFactory.SharePackage.MENU3,
                    object :DefaultShareMenuManager(SharePackageFactory.SharePackage.MENU3) {
                        override fun shareTo(context: Context?, shareModel: ShareModel?, listener: CommonListener<String>?) {
                            super.shareTo(context, shareModel, listener)
                            listenerInvoker.invoke(ShareMenu.UNLIKE)
                        }
                    })
        }

        private fun createReportMenu(listenerInvoker: (ShareMenu)->Unit): SharePackageModel {
            return SharePackageModel(ResourceTool.getString("report"),
                    R.drawable.report_share_icon, SharePackageFactory.SharePackage.MENU4,
                    object :DefaultShareMenuManager(SharePackageFactory.SharePackage.MENU4) {
                        override fun shareTo(context: Context?, shareModel: ShareModel?, listener: CommonListener<String>?) {
                            super.shareTo(context, shareModel, listener)
                            listenerInvoker.invoke(ShareMenu.REPORT)
                        }
                    })
        }

        private fun createTopMenu(listenerInvoker: (ShareMenu)->Unit): SharePackageModel {
            return SharePackageModel(ResourceTool.getString("feed_top"),
                    R.drawable.top_share_icon, SharePackageFactory.SharePackage.MENU6,
                    object :DefaultShareMenuManager(SharePackageFactory.SharePackage.MENU6) {
                        override fun shareTo(context: Context?, shareModel: ShareModel?, listener: CommonListener<String>?) {
                            super.shareTo(context, shareModel, listener)
                            listenerInvoker.invoke(ShareMenu.TOP)
                        }
                    })
        }

        private fun createUnTopMenu(listenerInvoker: (ShareMenu)->Unit): SharePackageModel {
            return SharePackageModel(ResourceTool.getString("un_feed_top"),
                    R.drawable.top_share_icon, SharePackageFactory.SharePackage.MENU6,
                    object :DefaultShareMenuManager(SharePackageFactory.SharePackage.MENU6) {
                        override fun shareTo(context: Context?, shareModel: ShareModel?, listener: CommonListener<String>?) {
                            super.shareTo(context, shareModel, listener)
                            listenerInvoker.invoke(ShareMenu.UN_TOP)
                        }
                    })
        }

    }
}