package com.redefine.welike.business.location.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate
import com.redefine.im.w
import com.redefine.welike.R
import com.redefine.welike.base.constant.CommonRequestCode
import com.redefine.welike.base.constant.PermissionRequestCode
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.location.management.bean.PoiInfo
import com.redefine.welike.business.location.ui.adapter.LocationPoiAdapter
import com.redefine.welike.business.location.ui.contract.ILocationSelectContract
import com.redefine.welike.business.location.ui.presenter.LocationSelectPresenter
import com.redefine.welike.business.publisher.ui.constant.EditorConstant
import com.redefine.welike.kext.translate
import kotlinx.android.synthetic.main.location_select_layout.*
import pub.devrel.easypermissions.EasyPermissions

class LocationSelectActivity : BaseActivity(), EasyPermissions.PermissionCallbacks, ILocationSelectContract.ILocationSelectView {

    private val REQUEST_CHECK_SETTINGS = 1

    private val mAdapter: LocationPoiAdapter by lazy { LocationPoiAdapter() }
    private var mCurrentLocation: Location? = null
    //    private val rootView: LocationSelectView by lazy { LocationSelectView() }
    private val mPresenter: ILocationSelectContract.ILocationSelectPresenter by lazy { LocationSelectPresenter(application, this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_select_layout)
//        rootView.onCreate(location_select_root_view, savedInstanceState)
        location_select_recycler_view.layoutManager = LinearLayoutManager(this@LocationSelectActivity)
        location_select_recycler_view.adapter = mAdapter
        location_select_recycler_view.addOnScrollListener(EndlessRecyclerOnScrollListener(object : ILoadMoreDelegate {
            override fun canLoadMore(): Boolean = mAdapter.canLoadMore()

            override fun onLoadMore() {
                mAdapter.onLoadMore()
                mPresenter.onLoadMore()
            }
        }))


//        rootView.setAdapter(mAdapter)
        mAdapter.setOnItemClickListener { _, t ->
            if (t is PoiInfo) {
                setResult(Activity.RESULT_OK, Intent().apply { putExtra(EditorConstant.KEY_POI_INFO, t) })
                finishActivity()
            }
        }
        mAdapter.setRetryLoadMoreListener {
            if (mAdapter.canLoadMore()) {
                mAdapter.onLoadMore()
                mPresenter.onLoadMore()
            }
        }
        showLoading()
        if (EasyPermissions.hasPermissions(this@LocationSelectActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            getLocation()
        } else {
            EasyPermissions.requestPermissions(this@LocationSelectActivity,
                    ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "location_permission"),
                    PermissionRequestCode.LOCATION_PERMISSION_REQUEST, Manifest.permission.ACCESS_FINE_LOCATION)
        }

        common_empty_view.showEmptyImageText(R.drawable.ic_common_empty, ResourceTool.getString(ResourceTool.ResourceFileEnum.LOCATION, "no_location_found"))
        common_empty_view.setBgResource(R.color.white)
        common_error_view.setOnErrorViewClickListener { mPresenter.onClickError() }
        common_title_view.text = "location_select_title".translate(ResourceTool.ResourceFileEnum.LOCATION)
        location_search_edit.apply {
            hint = "location_search_edit_text_hint".translate(ResourceTool.ResourceFileEnum.LOCATION)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    location_search_edit_clear.visibility = if (s.toString().isEmpty()) View.INVISIBLE else View.VISIBLE
                    mPresenter.onTextChange(s.toString().trim { it <= ' ' })
                }
            })
            setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    mPresenter.onTextChange(text.toString().trim())
                    true
                } else {
                    false
                }
            }
        }

        location_search_edit_clear.setOnClickListener {
            location_search_edit.setText("")
        }
        common_back_btn.setOnClickListener {
            finishActivity()
        }


    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        val client = LocationServices.getFusedLocationProviderClient(this)
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
                val settingClient: SettingsClient = LocationServices.getSettingsClient(this)
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
                            exception.startResolutionForResult(this@LocationSelectActivity,
                                    REQUEST_CHECK_SETTINGS)
                        } catch (sendEx: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    }
                }
            }
        }
    }

    fun createLocationRequest() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onBackPressed() {
        finishActivity()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mPresenter.onSaveInstanceState(outState)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        getLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Toast.makeText(this, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_permissions_denied"), Toast.LENGTH_SHORT).show()
        showEmptyView()
    }

    companion object {

        fun launch(context: Activity) {
            val intent = Intent(context, LocationSelectActivity::class.java)
            context.startActivityForResult(intent, CommonRequestCode.LOCATION_SELECT_REQUEST_CODE)
            context.overridePendingTransition(R.anim.sliding_right_in, R.anim.sliding_to_left_out)
        }
    }

    private fun finishActivity() {
        overridePendingTransition(R.anim.sliding_to_left_in, R.anim.sliding_right_out)
        finish()
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