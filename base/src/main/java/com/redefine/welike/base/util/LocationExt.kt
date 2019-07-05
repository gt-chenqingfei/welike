package com.redefine.welike.base.util

import android.Manifest
import android.content.Context
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import pub.devrel.easypermissions.EasyPermissions

/**
 * @author redefine honlin
 * @Date on 2019/3/4
 * @Description
 *
 * 获取地理位置
 */


object LocationExt {

    var lng: Double = 0.toDouble()
    var lat: Double = 0.toDouble()

    fun initOrRefreshLocation(context: Context) {
        if (EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            try {
                val client = LocationServices.getFusedLocationProviderClient(context)

                client.lastLocation.addOnSuccessListener {
                    if (it != null && !DateTimeUtil.isOver5Min(System.currentTimeMillis(), it.time)) {
                        Log.e("honlin", "" + it.latitude+"")
                        Log.e("honlin", "" + it.longitude+"")

                        lng = it.longitude
                        lat = it.latitude

                    } else {
                        val locationRequest = LocationRequest().apply {
                            interval = 10000
                            fastestInterval = 5000
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        }
                        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                        val settingClient: SettingsClient = LocationServices.getSettingsClient(context)
                        val task: Task<LocationSettingsResponse> = settingClient.checkLocationSettings(builder.build())
                        task.addOnSuccessListener {
                            val locationCallback = object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult?) {
                                    locationResult ?: return
                                    locationResult.locations.firstOrNull().let { location ->
                                        if (location != null) {
                                            lng = location.longitude
                                            lat = location.latitude
                                        } else {
                                        }
                                    }
                                    client.removeLocationUpdates(this)
                                }
                            }
                            client.requestLocationUpdates(locationRequest, locationCallback, null)
                        }
                    }
                }
            } catch (e: Exception) {
                //do nothing
            }
        }
    }


}