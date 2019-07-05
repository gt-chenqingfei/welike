package com.redefine.welike.business.contact.view

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.enums.PageLoadMoreStatusEnum
import com.redefine.commonui.enums.PageStatusEnum
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate
import com.redefine.commonui.share.SharePackageFactory
import com.redefine.commonui.share.sharemedel.ShareModel
import com.redefine.commonui.widget.LoadingDlg
import com.redefine.welike.R
import com.redefine.welike.base.constant.PermissionRequestCode
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.base.profile.AccountManager

import com.redefine.welike.commonui.event.model.EventModel
import com.redefine.welike.commonui.share.ShareManagerWrapper
import com.redefine.welike.commonui.event.expose.ItemExposeManager
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory
import com.redefine.welike.kext.translate
import com.redefine.welike.statistical.EventLog1
import kotlinx.android.synthetic.main.contact_page.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@Route(path = RouteConstant.PATH_CONTACTS)
class ContactActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var viewModel: ContactViewModel

    var isAuth: Boolean = AccountManager.getInstance().isLoginComplete
    var from: EventLog1.AddFriend.ButtonFrom = EventLog1.AddFriend.ButtonFrom.OTHER
    val postViewTimeDelegate = ItemExposeManager(ItemExposeCallbackFactory.createContactFollowBtnCallback())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_page)

        viewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)
        //set views.
        findViewById<TextView>(R.id.tv_common_title).text = "contact_connect".translate()
        findViewById<View>(R.id.iv_common_next).visibility = View.GONE
        findViewById<ViewGroup>(R.id.title_layout).setBackgroundResource(R.color.white)
        friend_list.layoutManager = LinearLayoutManager(this)
        common_empty_view.showEmptyText("contact_empty".translate())
        common_empty_view.setBgResource(R.color.transparent)
        common_empty_view.setBackgroundColor(Color.TRANSPARENT)
        //set listener
        findViewById<View>(R.id.iv_common_back).setOnClickListener { onBackPressed() }
        common_error_view.setOnErrorViewClickListener { viewModel.refresh() }
        common_error_view.setErrorText("common_permissions_denied".translate())
        //set adapter.
        val adapter = ContactAdapter()
        adapter.setRetryLoadMoreListener {
            if (adapter.canLoadMore()) {
                viewModel.loadMore()
            }
        }
        //listen adapter's click event.
        adapter.followAll = viewModel::followAll
        adapter.onFollow = viewModel::follow
        adapter.invite = viewModel::invite
        adapter.onClickProfile = viewModel::showProfile
        adapter.onClickFaceToFace = viewModel::onClickFaceToFace
        //set recycler view.
        friend_list.adapter = adapter
        friend_list.addOnScrollListener(EndlessRecyclerOnScrollListener(object : ILoadMoreDelegate {
            override fun canLoadMore() = adapter.canLoadMore()
            override fun onLoadMore() = viewModel.loadMore()
        }))

        val mLoadingDlg = LoadingDlg(this@ContactActivity)
        viewModel.state.observe(this, Observer {
            it?.let {
                common_empty_view.visibility = View.INVISIBLE
                common_error_view.visibility = View.INVISIBLE
                mLoadingDlg.dismiss()
                when (it) {
                    PageStatusEnum.LOADING -> mLoadingDlg.show()
                    PageStatusEnum.ERROR -> common_error_view.visibility = View.VISIBLE
                    PageStatusEnum.EMPTY -> common_empty_view.visibility = View.VISIBLE
                    else -> {
                    }
                }
            }
        })
        viewModel.loading.observe(this, Observer {
            it?.let {
                when (it) {
                    PageLoadMoreStatusEnum.NONE -> adapter.clearFinishFlag()
                    PageLoadMoreStatusEnum.LOADING -> adapter.onLoadMore()
                    PageLoadMoreStatusEnum.NO_MORE -> adapter.noMore()
                    PageLoadMoreStatusEnum.LOAD_ERROR -> adapter.loadError()
                    PageLoadMoreStatusEnum.FINISH -> adapter.finishLoadMore()
                }
            }
        })
        viewModel.data.observe(this, Observer {
            it?.let {
                adapter.setData(it)
                if (it.size > 1) {
                    postViewTimeDelegate.onDataLoaded()
                }
            }
        })

//        viewModel.permissionRunner.observe(this, Observer {
//            it?.let {
//                if (it) {
//                    EasyPermissions.requestPermissions(this@ContactActivity, "read_contact_permission".translate()
//                            , (PermissionRequestCode.READ_CONTACT_PERMISSION), Manifest.permission.READ_CONTACTS)
//                }
//            }
//        })
//        viewModel.init()
        /**
         * 权限: 请求权限
         */
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            viewModel.init()
        } else {
            EasyPermissions.requestPermissions(this@ContactActivity, "read_contact_permission".translate()
                    , (PermissionRequestCode.READ_CONTACT_PERMISSION), Manifest.permission.READ_CONTACTS)
        }
        intent.getSerializableExtra("from")?.let {
            from = it as EventLog1.AddFriend.ButtonFrom
        }
        EventLog1.AddFriend.report2(from)
        postViewTimeDelegate.attach(friend_list, adapter, null)
    }

    override fun onResume() {
        super.onResume()
        postViewTimeDelegate.onAttach()
        postViewTimeDelegate.onShow()
        postViewTimeDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        postViewTimeDelegate.onDetach()
        postViewTimeDelegate.onHide()
        postViewTimeDelegate.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        postViewTimeDelegate.onDestroy()
    }

    /**
     * 权限: 接受权限请求结果
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this@ContactActivity)
    }

    /**
     * 权限: 获取成功
     */
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        viewModel.init()
        from.let {
            EventLog1.AddFriend.report1(EventLog1.AddFriend.Authorized.AUTHORIZED, from)
        }
    }

    /**
     * 权限: 获取失败
     */
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this@ContactActivity, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
        viewModel.denied()
        from?.let {
            EventLog1.AddFriend.report1(EventLog1.AddFriend.Authorized.UNAUTHORIZED, from)
        }
    }

    /**
     * 权限: App setting 返回
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            EasyPermissions.requestPermissions(this@ContactActivity, "read_contact_permission".translate()
                    , (PermissionRequestCode.READ_CONTACT_PERMISSION), Manifest.permission.READ_CONTACTS)
        }
    }
}