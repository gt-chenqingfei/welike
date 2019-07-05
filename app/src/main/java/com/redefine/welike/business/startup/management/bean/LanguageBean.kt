package com.redefine.welike.business.startup.management.bean

import android.support.annotation.DrawableRes

/**
 * Created by honglin on 2018/7/10.
 */
open class LanguageBean(@DrawableRes var iconDrawable: Int, @DrawableRes var iconActive: Int, @DrawableRes var iconUnActive: Int, var shortName: String, var checked: Boolean, var isReady: Boolean)


