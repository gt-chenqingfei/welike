package com.pekingese.pagestack.framework.util

import com.pekingese.pagestack.framework.page.BasePage
import com.pekingese.pagestack.framework.page.PageName

class PageNameUtil {

    fun <T : Any> getPageName(page: Class<T>): String {
        page.annotations.firstOrNull()?.let {
            if (it is PageName) {
                return it.value
            }
        }
        return ""
    }
}