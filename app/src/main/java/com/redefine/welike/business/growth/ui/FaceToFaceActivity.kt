package com.redefine.welike.business.growth.ui

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.facebook.drawee.generic.RoundingParams
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.dialog.CommonConfirmDialog
import com.redefine.commonui.loadmore.adapter.KAdapter
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.welike.R
import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.constant.PermissionRequestCode
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.base.ext.setStatusBarJ
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.base.util.DateTimeUtil
import com.redefine.welike.business.growth.vm.FaceToFaceViewModel
import com.redefine.welike.business.user.management.FollowUserManager
import com.redefine.welike.business.user.management.bean.RecommendUser
import com.redefine.welike.business.user.ui.constant.UserConstant
import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract
import com.redefine.welike.common.VipUtil
import com.redefine.welike.commonui.event.expose.model.FollowEventModel
import com.redefine.welike.statistical.EventConstants
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.EventLog1
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.face_to_face_activity.*
import kotlinx.android.synthetic.main.face_to_face_big_item.view.*
import kotlinx.android.synthetic.main.face_to_face_small_item.view.*
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

/**
 *
 * Name: FaceToFaceActivity
 * Author: weber
 * Email:
 * Comment: //TODO
 * Date: 2019-01-14 12:00
 *
 */

@Route(path = RouteConstant.FACE_TO_FACE_PATH)
class FaceToFaceActivity : BaseActivity(), EasyPermissions.PermissionCallbacks, FollowUserManager.FollowUserCallback {

    private var mAdapter: KAdapter<RecommendUser>? = null
    private lateinit var mViewModel: FaceToFaceViewModel
    private var mLastLocation: Location? = null
    private var mTaskDispose: Disposable? = null
    private lateinit var mSlideFromRight: LayoutAnimationController
    private lateinit var mScaleIn: LayoutAnimationController
    private var mDialog: CommonConfirmDialog? = null
    private var isFirstShow = true
    private var mRefreshCount = 0


    private val bigItemBinder = { view: View, param: RecommendUser, _: Int ->
        VipUtil.set(view.face_to_face_big_head, param.avatar, param.vip)
        view.face_to_face_big_head.avatar.hierarchy.roundingParams = RoundingParams.asCircle().setBorder(Color.WHITE, ScreenUtils.dip2Px(2F).toFloat())
        view.face_to_face_big_nick.text = param.name
        view.face_to_face_big_intro?.text = param.intro
        val mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(view.face_to_face_big_follow_btn, true, true)
        mFollowBtnPresenter.bindView(param.uid, param.following, param.follower, FollowEventModel("INVALID", null))
        mFollowBtnPresenter.setFollowBtnClickCallback {
            AccountManager.getInstance().account?.uid.let { uid ->
                val f = if (param.follower && param.following) {
                    3
                } else if (param.follower && !param.following) {
                    4
                } else if (!param.follower && param.following) {
                    1
                } else {
                    2
                }
                EventLog1.FaceToFace.report4(param.uid, uid, f)
            }
        }
        view.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(UserConstant.FROM_PAGE, EventConstants.PROFILE_FROM_PAGE_OTHER)
            bundle.putBoolean(UserConstant.IS_EXPANDED, true)
            bundle.putString(UserConstant.UID, param.uid)
            ARouter.getInstance().build(RouteConstant.PROFILE_ROUTE_PATH).with(bundle).navigation()
        }
    }

    private val smallItemBinder = { view: View, param: RecommendUser, _: Int ->
        VipUtil.set(view.face_to_face_small_head, param.avatar, param.vip)
        view.face_to_face_small_head.avatar.hierarchy.roundingParams = RoundingParams.asCircle().setBorder(Color.WHITE, ScreenUtils.dip2Px(2F).toFloat())
        view.face_to_face_small_nick.text = param.name
        val mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(view.face_to_face_small_follow_btn, true, true)
        mFollowBtnPresenter.bindView(param.uid, param.following, param.follower, FollowEventModel("INVALID", null))
        mFollowBtnPresenter.setFollowBtnClickCallback {
            AccountManager.getInstance().account?.uid.let { uid ->
                val f = if (param.follower && param.following) {
                    3
                } else if (param.follower && !param.following) {
                    4
                } else if (!param.follower && param.following) {
                    1
                } else {
                    2
                }
                EventLog1.FaceToFace.report4(param.uid, uid, f)
            }
        }
        view.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(UserConstant.FROM_PAGE, EventConstants.PROFILE_FROM_PAGE_OTHER)
            bundle.putBoolean(UserConstant.IS_EXPANDED, true)
            bundle.putString(UserConstant.UID, param.uid)
            ARouter.getInstance().build(RouteConstant.PROFILE_ROUTE_PATH).with(bundle).navigation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_to_face_activity)
        face_to_face_radar_lottie.setAnimation("radar.json")
        mViewModel = ViewModelProviders.of(this).get(FaceToFaceViewModel::class.java)
        mSlideFromRight = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_item_anim_from_right)
        mScaleIn = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_item_anim_scale_in)
        face_to_face_top_radar.setShowInnerCricle(true)
        face_to_face_top_radar.setStartColor(0x5cFFFFFF)
        face_to_face_close_btn.setOnClickListener {
            finish()
        }

        AccountManager.getInstance().account?.let {
            VipUtil.set(face_to_face_account_head, it.headUrl, it.vip)
            face_to_face_account_head.avatar.hierarchy.roundingParams = RoundingParams.asCircle().setBorder(Color.WHITE, ScreenUtils.dip2Px(2F).toFloat())
        }

        mViewModel.recommendUsers.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            val oldData = mAdapter?.getData()
            val isNewDataOverLimit = (it.size) > MAX_COUNT
            val isOldDataOverLimit = (oldData?.size ?: 0) > MAX_COUNT

            if (isNewDataOverLimit xor isOldDataOverLimit || mAdapter == null) {
                switchLayoutManager(isNewDataOverLimit)
            }

            if (isFirstShow) {
                if (isNewDataOverLimit) {
                    EventLog1.FaceToFace.report3(2)
                } else {
                    EventLog1.FaceToFace.report3(1)
                }
                isFirstShow = false
            }

            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return TextUtils.equals(oldData?.get(oldItemPosition)?.uid, it[newItemPosition].uid)
                }

                override fun getOldListSize(): Int {
                    return oldData?.size ?: 0
                }

                override fun getNewListSize(): Int {
                    return it.size
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldData?.get(oldItemPosition) == it[newItemPosition]
                }

            }).dispatchUpdatesTo(mAdapter)
            mAdapter?.setData(it.toMutableList())
        })
        startTask()
        FollowUserManager.getInstance().register(this)
    }

    private fun switchLayoutManager(isBig: Boolean) {
        if (isBig) {
            face_to_face_top_radar.visibility = View.VISIBLE
            face_to_face_account_head.visibility = View.INVISIBLE
            if (face_to_face_appbar_header.layoutParams is AppBarLayout.LayoutParams) {
                (face_to_face_appbar_header.layoutParams as AppBarLayout.LayoutParams).scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                face_to_face_appbar_header.requestLayout()
            }
            face_to_face_tips.visibility = View.INVISIBLE
            face_to_face_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            face_to_face_list.layoutAnimation = mSlideFromRight
            mAdapter = KAdapter(R.layout.face_to_face_big_item, bigItemBinder)

        } else {
            face_to_face_top_radar.visibility = View.INVISIBLE
            if (face_to_face_appbar_header.layoutParams is AppBarLayout.LayoutParams) {
                (face_to_face_appbar_header.layoutParams as AppBarLayout.LayoutParams).scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
                face_to_face_appbar_header.requestLayout()
            }
            face_to_face_tips.visibility = View.VISIBLE
            face_to_face_account_head.visibility = View.VISIBLE
            face_to_face_list.layoutManager = RadarLayoutManager()
            face_to_face_list.layoutAnimation = mScaleIn
            mAdapter = KAdapter(R.layout.face_to_face_small_item, smallItemBinder)
        }
        face_to_face_list.adapter = mAdapter
        face_to_face_list.startLayoutAnimation()
    }

    override fun onFollowCompleted(uid: String?, errCode: Int) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            refreshOnFollow(uid)
        } else {
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun onUnfollowCompleted(uid: String?, errCode: Int) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            refreshOnUnFollow(uid)
        } else {
            mAdapter?.notifyDataSetChanged()
        }
    }

    fun refreshOnFollow(uid: String?) {
        var hasFollowFeed = false
        val oldData = mAdapter?.getData() ?: return
        for (user in oldData) {
            if (TextUtils.equals(user.uid, uid)) {
                user.following = true
                hasFollowFeed = true
            }
        }
        if (hasFollowFeed) {
            mAdapter?.notifyDataSetChanged()
        }
    }

    fun refreshOnUnFollow(uid: String?) {
        var hasFollowFeed = false
        val oldData = mAdapter?.getData() ?: return
        for (user in oldData) {
            if (TextUtils.equals(user.uid, uid)) {
                user.following = false
                hasFollowFeed = true
            }
        }
        if (hasFollowFeed) {
            mAdapter?.notifyDataSetChanged()
        }
    }


    private fun startFindFriends() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
                && EasyPermissions.permissionPermanentlyDenied(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (mDialog == null) {
                mDialog = CommonConfirmDialog.showFullScreenConfirmDialog(this, getString(R.string.face_to_face_permission_title)
                        , getString(R.string.common_cancel)
                        , getString(R.string.authorization)
                        , object: CommonConfirmDialog.IConfirmDialogListener {
                    override fun onClickCancel() {
                        onPermissionsDenied(PermissionRequestCode.LOCATION_PERMISSION_REQUEST, arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION))
                    }

                    override fun onClickConfirm() {
                        gotoSettings()
                    }

                })
                mDialog?.setFullScreen(true)
                mDialog?.setCanceledOnTouchOutside(false)

            }
            mDialog?.show()
        } else {
            mDialog?.dismiss()
            if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestLocation()
            } else {
                EasyPermissions.requestPermissions(this,
                        getString(R.string.friend_face_to_face_location_permission),
                        PermissionRequestCode.LOCATION_PERMISSION_REQUEST, Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun gotoSettings() {
        try {
            val localIntent = Intent()
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            localIntent.data = Uri.fromParts("package", packageName, null)
            this.startActivity(localIntent)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        if (mLastLocation != null) {
            doLocationResult(mLastLocation)
            return
        }
        val client = LocationServices.getFusedLocationProviderClient(this)
        client.lastLocation.addOnSuccessListener {
            if (it != null && !DateTimeUtil.isOver5Min(System.currentTimeMillis(), it.time)) {
                doLocationResult(it)
            } else {
                val locationRequest = LocationRequest().apply {
                    interval = 10000
                    fastestInterval = 5000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                val settingClient: SettingsClient = LocationServices.getSettingsClient(this)
                val task: Task<LocationSettingsResponse> = settingClient.checkLocationSettings(builder.build())
                task.addOnSuccessListener {
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            locationResult ?: return
                            locationResult.locations.firstOrNull().let { location ->
                                if (location != null) {
                                    doLocationResult(location)
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
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            exception.startResolutionForResult(this,
                                    REQUEST_CHECK_SETTINGS)
                        } catch (sendEx: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    }
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        face_to_face_radar_lottie.playAnimation()
        startTask()
    }

    override fun onStop() {
        super.onStop()
        face_to_face_radar_lottie.pauseAnimation()
        stopTask()

    }

    override fun finish() {
        setStatusBarJ(this)
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mRefreshCount > 0) {
            EventLog1.FaceToFace.report5(mRefreshCount)
        }
        FollowUserManager.getInstance().unregister(this)
        stopTask()
        face_to_face_radar_lottie.cancelAnimation()
    }

    private fun startTask() {
        stopTask()
        mTaskDispose = Observable.interval(0, 5, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            startFindFriends()
        }
    }

    private fun stopTask() {
        mTaskDispose?.dispose()
        mTaskDispose = null
    }

    private fun doLocationResult(it: Location?) {
        it?.let {
            mLastLocation = it
            mRefreshCount++
            mViewModel.getRecommendUsers(it)
        }
    }

    private fun showErrorView() {

    }

    private fun showEmptyView() {
        EventLog1.FaceToFace.report2(2)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        EventLog1.FaceToFace.report2(1)
        requestLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Toast.makeText(this, getString(R.string.face_to_face_permissions_denied), Toast.LENGTH_SHORT).show()
        showEmptyView()
    }


    override fun isFullScreenActivity(): Boolean {
        return true
    }

    companion object {
        private const val REQUEST_CHECK_SETTINGS = 1
        private const val MAX_COUNT = 10

    }
}