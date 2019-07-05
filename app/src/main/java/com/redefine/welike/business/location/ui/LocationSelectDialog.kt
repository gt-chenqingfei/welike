package com.redefine.welike.business.location.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.im.w
import com.redefine.welike.R
import com.redefine.welike.base.constant.PermissionRequestCode
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.location.management.bean.Location
import com.redefine.welike.business.location.management.bean.PoiInfo
import com.redefine.welike.business.location.ui.adapter.LocationPoiAdapter
import com.redefine.welike.business.location.ui.contract.ILocationSelectContract
import com.redefine.welike.business.location.ui.presenter.LocationSelectPresenter
import com.redefine.welike.business.publisher.ui.component.OnSearchBarListener
import kotlinx.android.synthetic.main.dialog_location_select_layout.*
import pub.devrel.easypermissions.EasyPermissions

/**
 * @author qingfei.chen
 * @date 2018/12/1
 * Copyright (C) 2018 redefine , Inc.
 */
class LocationSelectDialog(val context: AppCompatActivity, val listener: OnLocationSelectedListener)
    : BottomSheetDialog(context, R.style.BottomSheetEdit), EasyPermissions.PermissionCallbacks, ILocationSelectContract.ILocationSelectView, OnSearchBarListener {

    companion object {
        fun showDialog(context: AppCompatActivity, listener: OnLocationSelectedListener) {
            val bottomSheet = LocationSelectDialog(context, listener)
            val view = View.inflate(context, R.layout.dialog_location_select_layout, null)
            bottomSheet.setContentView(view)
            val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
            view.layoutParams.height = ScreenUtils.getScreenHeight(context)
            bottomSheetBehavior.peekHeight = ScreenUtils.getScreenHeight(context) - ScreenUtils.dip2Px(160f)
            bottomSheet.show()
        }
    }

    private val REQUEST_CHECK_SETTINGS = 1

    private val mAdapter: LocationPoiAdapter by lazy { LocationPoiAdapter() }
    private var mCurrentLocation: Location? = null
    private val mPresenter: ILocationSelectContract.ILocationSelectPresenter by lazy { LocationSelectPresenter(context.applicationContext, this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val designBottomSheet = window.findViewById<View>(R.id.design_bottom_sheet)
        designBottomSheet.setBackgroundResource(R.drawable.shape_bottom_sheet_dialog_drag_bg)
        location_select_recycler_view.layoutManager = LinearLayoutManager(context)
        location_select_recycler_view.adapter = mAdapter
        location_select_recycler_view.addOnScrollListener(EndlessRecyclerOnScrollListener(object : ILoadMoreDelegate {
            override fun canLoadMore(): Boolean = mAdapter.canLoadMore()

            override fun onLoadMore() {
                mAdapter.onLoadMore()
                mPresenter.onLoadMore()
            }
        }))


        mAdapter.setOnItemClickListener { _, t ->
            if (t is PoiInfo) {
                listener.onLocationSelected(t.location)
                this.cancel()
            }
        }
        mAdapter.setRetryLoadMoreListener {
            if (mAdapter.canLoadMore()) {
                mAdapter.onLoadMore()
                mPresenter.onLoadMore()
            }
        }
        showLoading()
        if (EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            getLocation()
        } else {
            EasyPermissions.requestPermissions(context,
                    ResourceTool.getString("location_permission"),
                    PermissionRequestCode.LOCATION_PERMISSION_REQUEST, Manifest.permission.ACCESS_FINE_LOCATION)
        }

        common_empty_view.showEmptyImageText(R.drawable.ic_common_empty, ResourceTool.getString("no_location_found"))
        common_empty_view.setBgResource(R.color.white)
        common_error_view.setOnErrorViewClickListener { mPresenter.onClickError() }
        search_bar.setOnSearchBarListener(this)
        val bottomSheetBehavior = BottomSheetBehavior.from(location_select_root_view.parent as View)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        drag_view.visibility = View.INVISIBLE
                        search_bar.setSearchBarShadow(true)
                        designBottomSheet.setBackgroundColor(context.resources.getColor(R.color.white))
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        drag_view.visibility = View.VISIBLE
                        search_bar.setSearchBarShadow(false)
                        designBottomSheet.setBackgroundResource(R.drawable.shape_bottom_sheet_dialog_drag_bg)
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> cancel()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        location_select_root_view.getViewTreeObserver().addOnGlobalLayoutListener {
            val rect = Rect()
            location_select_root_view.getWindowVisibleDisplayFrame(rect)
            val height = location_select_root_view.getRootView().getHeight()
            val heightDefere = height - rect.bottom
            if (heightDefere > 200) {

                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mPresenter.onTextChange(s.toString().trim { it <= ' ' })
    }

    override fun onCancelClick() {
        this.cancel()
    }


    @SuppressLint("MissingPermission")
    fun getLocation() {
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation.addOnSuccessListener {
            if (it != null) {
                w("Location 1")
                mPresenter.doLocation(it)
            } else {
                w("Location 2")
                val locationRequest = LocationRequest().apply {
                    interval = 10000
                    fastestInterval = 5000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                val settingClient: SettingsClient = LocationServices.getSettingsClient(context)
                val task: Task<LocationSettingsResponse> = settingClient.checkLocationSettings(builder.build())
                task.addOnSuccessListener {
                    w("Location 3")
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            w("Location 4")
                            locationResult ?: return
                            locationResult.locations.firstOrNull().let {
                                if (it != null) {
                                    mPresenter.doLocation(it)
                                } else {
                                    showErrorView()
                                }
                            }
                            client.removeLocationUpdates(this)
                        }
                    }
                    client.requestLocationUpdates(locationRequest, locationCallback, null)
                }

                task.addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        w("Location 5")
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            exception.startResolutionForResult(context,
                                    REQUEST_CHECK_SETTINGS)
                        } catch (sendEx: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        mPresenter.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        getLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Toast.makeText(context, ResourceTool.getString("common_permissions_denied"), Toast.LENGTH_SHORT).show()
        showEmptyView()
    }

    override fun showLoading() {
        common_loading_view.visibility = View.VISIBLE
        common_empty_view.visibility = View.INVISIBLE
        common_error_view.visibility = View.INVISIBLE
    }

    override fun showErrorView() {
        common_loading_view.visibility = View.INVISIBLE
        common_empty_view.visibility = View.INVISIBLE
        common_error_view.visibility = View.VISIBLE
    }

    override fun showEmptyView() {
        common_loading_view.visibility = View.INVISIBLE
        common_empty_view.visibility = View.VISIBLE
        common_error_view.visibility = View.INVISIBLE
    }

    override fun showContent() {
        common_loading_view.visibility = View.INVISIBLE
        common_empty_view.visibility = View.INVISIBLE
        common_error_view.visibility = View.INVISIBLE
    }

    override fun stopLoadmore() {
        mAdapter.goneLoadMore()
        mAdapter.addNewData(null)
    }

    override fun showNew(data: MutableList<PoiInfo>?) {
        data?.let {
            if (it.isNotEmpty()) {
                mAdapter.addNewData(data)
            }
            mAdapter.finishLoadMore()
            mAdapter.goneLoadMore()
        }
    }

    override fun loadError() {
        mAdapter.loadError()
    }

    override fun onCreate(rootView: View?, savedInstanceState: Bundle?) {
    }

    override fun showLoadMore(data: MutableList<PoiInfo>?) {
        mAdapter.addHisData(data)
    }

    override fun showNoMore() {
        mAdapter.noMore()
    }

    override fun finishLoadMore() {
        mAdapter.finishLoadMore()
    }

}

interface OnLocationSelectedListener {
    fun onLocationSelected(location: Location)
}