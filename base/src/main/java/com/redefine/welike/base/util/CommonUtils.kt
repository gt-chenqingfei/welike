package com.redefine.welike.base.util

import com.redefine.foundation.utils.ASCIIUtils
import com.redefine.foundation.utils.WelikeConstants
import com.redefine.welike.base.GlobalConfig

/**
 * @author redefine honlin
 * @Date on 2018/12/5
 * @Description
 */

class ParamsProvoder {

    companion object {
        val paramter1 = ASCIIUtils.getParams(WelikeConstants.params, -1)

        val paramter2 = ASCIIUtils.getParams(GlobalConfig.params, 1)

        val params = paramter1 + paramter2

    }

}