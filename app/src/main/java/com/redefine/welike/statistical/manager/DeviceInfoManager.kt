package com.redefine.welike.statistical.manager

import android.content.Context
import android.os.Bundle
import com.redefine.welike.base.util.MemoryExt
import com.redefine.welike.statistical.EventLog1

/**
 * @author redefine honlin
 * @Date on 2019/3/5
 * @Description
 */

object DeviceInfoManager {

    fun log(context: Context) {

        val bundle = Bundle()
        bundle.putString(EventLog1.DeviceStatus.ACTION, EventLog1.DeviceStatus.ACTION_DEVICE)
        bundle.putString(EventLog1.DeviceStatus.MEMORY_FREE, MemoryExt.getMemoryFree(context))
        bundle.putString(EventLog1.DeviceStatus.MEMORY_TOTAL, MemoryExt.getTotalMemory(context))
        bundle.putString(EventLog1.DeviceStatus.ROM_FREE, MemoryExt.getRomAvailSpace(context))
        bundle.putString(EventLog1.DeviceStatus.ROM_TOTAL, MemoryExt.getRomTotalSpace(context))
        EventLog1.DeviceStatus.report1(bundle)

    }

}