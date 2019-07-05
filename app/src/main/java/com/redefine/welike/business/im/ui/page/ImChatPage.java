package com.redefine.welike.business.im.ui.page;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.dialog.CommonConfirmDialog;
import com.redefine.commonui.dialog.MenuItem;
import com.redefine.commonui.dialog.MenuItemIdConstant;
import com.redefine.commonui.dialog.OnMenuItemClickListener;
import com.redefine.commonui.dialog.SimpleMenuDialog;
import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.adapter.HeaderRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.util.ViewUtil;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.InputMethodUtil;
import com.redefine.im.room.MESSAGE;
import com.redefine.im.room.MessageSession;
import com.redefine.im.room.SESSION;
import com.redefine.multimedia.photoselector.activity.PhotoSelectorActivity;
import com.redefine.multimedia.photoselector.config.ImagePickConfig;
import com.redefine.multimedia.photoselector.constant.ImagePickConstant;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.photoselector.entity.MimeType;
import com.redefine.richtext.RichEditText;
import com.redefine.richtext.copy.RichTextClipboardManager;
import com.redefine.richtext.emoji.EmojiPanel;
import com.redefine.richtext.emoji.bean.EmojiBean;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.CommonRequestCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.io.WeLikeFileManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.business.im.IMHelper;
import com.redefine.welike.business.im.model.CardInfo;
import com.redefine.welike.business.im.ui.adapter.ChatAdapter;
import com.redefine.welike.business.im.ui.constant.ImConstant;
import com.redefine.welike.business.im.ui.holder.BaseChatCardViewHolder;
import com.redefine.welike.business.im.ui.holder.ChatTextViewHolder;
import com.redefine.welike.business.im.ui.vm.ImChatViewModel;
import com.redefine.welike.business.im.ui.vm.SessionListViewModel;
import com.redefine.welike.business.user.management.BlockUserManager;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelRelativeLayout;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import top.zibin.luban.Luban;
import top.zibin.luban.WrapSizeFile;

import static com.redefine.welike.business.im.ui.constant.ImConstant.IM_SESSION_CUSTOMER;
import static com.redefine.welike.business.im.ui.constant.ImConstant.IM_SESSION_KRY;

/**
 * Created by nianguowang on 2018/6/8
 */
@Route(path = RouteConstant.IM_CHAT_ROUTE_PATH)
public class ImChatPage extends BaseActivity implements View.OnClickListener, EmojiPanel.OnEmojiItemClickListener,
        ILoadMoreDelegate, TextWatcher, View.OnTouchListener, KPSwitchConflictUtil.SwitchClickListener, BlockUserManager.BlockUserCallback {

    private SESSION mSession;
    private boolean mIsCustomer;
    private String from;
    private RecyclerView mRecyclerView;
    private View mPicBtn;
    private View mEmojiBtn;
    private RichEditText mEditText;
    private EmojiPanel mEmojiPanel;
    private KPSwitchPanelRelativeLayout mChatContainer;
    private AppCompatImageView mSendBtn;
    private View mBackBtn;
    private TextView mTitleView;
    private SimpleDraweeView mUserHeader;
    private View mMoreBtn;
    private View mBottomLayout;
    private LoadingDlg mLoadingDlg;
    private SimpleMenuDialog mDialog;

    private int mPageState;
    private ChatAdapter mAdapter;
    private ImChatViewModel mChatViewModel;
    private SessionListViewModel mSessionListViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_chat_layout);
        parseBundle();

        mChatViewModel = ViewModelProviders.of(this).get(ImChatViewModel.class);
        mSessionListViewModel = ViewModelProviders.of(this).get(SessionListViewModel.class);
        mAdapter = new ChatAdapter(mSession);
        //TODO 暂时不分页加载
        mAdapter.noMore();
        mAdapter.hideHeader();
        mAdapter.setOnChatClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final RecyclerView.ViewHolder viewHolder, Object t) {
                if (viewHolder instanceof ChatTextViewHolder) {
                    List<MenuItem> list = new ArrayList<>();
                    list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_COPY, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "copy")));

                    mDialog = SimpleMenuDialog.show(viewHolder.itemView.getContext(), list, new OnMenuItemClickListener() {
                        @Override
                        public void onMenuClick(MenuItem menuItem) {
                            if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_COPY) {
                                RichTextClipboardManager.getInstance().copy(viewHolder.itemView.getContext(), ((ChatTextViewHolder) viewHolder).mTextView.getText());
                                Toast.makeText(viewHolder.itemView.getContext(), ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "copy_to_clipboard"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
        mAdapter.setOnChatCardClickLisgener(new BaseChatCardViewHolder.OnChatCardClickListener() {
            @Override
            public void onChatCardClick(View v, CardInfo cardInfo) {
                InputMethodUtil.hideInputMethod(mEditText);
                new DefaultUrlRedirectHandler(v.getContext(), DefaultUrlRedirectHandler.FROM_IM).onUrlRedirect(cardInfo.getForwardUrl());
            }
        });
        initView();
    }

    private void parseBundle() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            finish();
            return;
        }
        mSession = extras.getParcelable(IM_SESSION_KRY);
        from = extras.getString(RouteConstant.ROUTE_KEY_ENTRY_TYPE, "");
        mIsCustomer = extras.getBoolean(IM_SESSION_CUSTOMER, false);
    }

    private void initView() {

        mRecyclerView = findViewById(R.id.im_session_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        if (mRecyclerView.getItemAnimator() != null) {
            mRecyclerView.getItemAnimator().setChangeDuration(0);
            mRecyclerView.getItemAnimator().setAddDuration(0);
            mRecyclerView.getItemAnimator().setMoveDuration(0);
            mRecyclerView.getItemAnimator().setRemoveDuration(0);
        }
        mPicBtn = findViewById(R.id.im_chat_pic_btn);
        mEmojiBtn = findViewById(R.id.emoji_btn_switch);
        mEditText = findViewById(R.id.chat_editText);
        mSendBtn = findViewById(R.id.send_btn);
        mEmojiPanel = findViewById(R.id.chat_emoji_panel);
        mChatContainer = findViewById(R.id.chat_bottom_container);
        mChatContainer.setIgnoreRecommendHeight(true);
        mBackBtn = findViewById(R.id.common_back_btn);
        mTitleView = findViewById(R.id.common_title_view);
        mUserHeader = findViewById(R.id.chat_title_user_header);
        mMoreBtn = findViewById(R.id.common_more_btn);
        mBottomLayout = findViewById(R.id.im_chat_bottom_layout);

        mRecyclerView.addOnScrollListener(new HeaderRecyclerOnScrollListener(this));

        mRecyclerView.setOnTouchListener(this);

        mTitleView.setText(mSession.getSessionNice());
        findViewById(R.id.chat_user_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodUtil.hideInputMethod(mEditText);
                UserHostPage.launch(true, mSession.getSessionUid());
            }
        });
        HeadUrlLoader.getInstance().loadHeaderUrl2(mUserHeader, mSession.getSessionHead());

        mEditText.setHint(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "chat_edit_text_hint"));

        mEditText.addTextChangedListener(this);
        KeyboardUtil.attach(this, mChatContainer, new KeyboardUtil.OnKeyboardShowingListener() {
            @Override
            public void onKeyboardShowing(boolean isShowing) {
                if (mRecyclerView.getAdapter() != null && isShowing) {
                    mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                }


                mEmojiBtn.setSelected(mChatContainer.getVisibility() == View.VISIBLE);
//                mEmojiBtn.setSelected(isShowing);
            }
        });
        mMoreBtn.setVisibility(mIsCustomer ? View.GONE : View.VISIBLE);
        KPSwitchConflictUtil.attach(mChatContainer, mEmojiBtn, mEditText, this);
        mPicBtn.setOnClickListener(this);
        mEmojiPanel.setOnEmojiItemClickListener(this);
        mSendBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mMoreBtn.setOnClickListener(this);
        mSendBtn.setVisibility(View.GONE);

        int visableChat = mSession.getVisableChat() == null ? 0 : mSession.getVisableChat();
        mBottomLayout.setVisibility(visableChat != 0 ? View.VISIBLE : View.GONE);
        BlockUserManager.getInstance().register(this);

        mRecyclerView.setAdapter(mAdapter);
        mChatViewModel.loadMessages(mSession.getSid(), new ImChatViewModel.OnChatLiveDataLoadListener() {
            @Override
            public void onChatLiveDataLoad(LiveData<List<MESSAGE>> messageLiveData) {
                messageLiveData.observe(ImChatPage.this, new Observer<List<MESSAGE>>() {
                    @Override
                    public void onChanged(@Nullable final List<MESSAGE> messages) {
                        mAdapter.setData(messages);
                        scrollToPosition(mAdapter.getItemCount() - 1, false);
                    }
                });
            }
        });
        mSessionListViewModel.loadSessions(new SessionListViewModel.OnSessionLoadListener() {
            @Override
            public void onSessionLoaded(LiveData<List<MessageSession>> sessionLiveData) {
                sessionLiveData.observe(ImChatPage.this, new Observer<List<MessageSession>>() {
                    @Override
                    public void onChanged(@Nullable List<MessageSession> sessions) {
                        updateTitleHeader(sessions);
                    }
                });
            }
        });

        IMHelper.INSTANCE.setCurrentSession(mSession.getSid());
    }

    private void updateTitleHeader(List<MessageSession> sessions) {
        for (MessageSession session : sessions) {
            if(TextUtils.equals(session.getSSid(), mSession.getSid())) {
                mSession.setSessionHead(session.getSHead());
                mSession.setSessionNice(session.getSName());
                HeadUrlLoader.getInstance().loadHeaderUrl2(mUserHeader, mSession.getSessionHead());
                break;
            }
        }
    }

    @Override
    public void onClick(final View v) {
        if (v == mPicBtn) {
            InputMethodUtil.hideInputMethod(mEditText);
            clickSelectPic(v);
        } else if (v == mSendBtn) {
            onSendTextMessage(mEditText.getEditableText().toString());
            mEditText.setText("");
        } else if (v == mBackBtn) {
            finish();
        } else if (v == mMoreBtn) {
            // do show menu
            List<MenuItem> list = new ArrayList<>();
            list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_FEED_BACK, ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "im_feed_back_text")));
            MenuItem blockItem = new MenuItem(MenuItemIdConstant.MENU_ITEM_BLOCK, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "block") + GlobalConfig.AT + mSession.getSessionNice());
            list.add(blockItem);
            SimpleMenuDialog.show(v.getContext(), list, new OnMenuItemClickListener() {
                @Override
                public void onMenuClick(MenuItem menuItem) {
                    if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_FEED_BACK) {
                        IMHelper.INSTANCE.getCustomerSession(new Function1<SESSION, Unit>() {
                            @Override
                            public Unit invoke(final SESSION session) {
                                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                    @Override
                                    public void run() {
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable(ImConstant.IM_SESSION_KRY, session);
                                        bundle.putBoolean(ImConstant.IM_SESSION_CUSTOMER, true);
                                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CHAT_EVENT, bundle));
                                    }
                                });
                                return null;
                            }
                        }, new Function1<Integer, Unit>() {
                            @Override
                            public Unit invoke(Integer integer) {
                                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mMoreBtn.getContext(), ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "create_chat_failed"), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return null;
                            }
                        });
                    } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_BLOCK) {
                        doBlockUser(v.getContext(), mSession.getSessionNice(), mSession.getSessionUid());
                    }

                }
            });
        }
    }

    public void clickSelectPic(View view) {
        if (view.getContext() instanceof Activity) {
//            ImPhotoSelector.selectPicture(((Activity) view.getContext()), null);
            Activity activity = (Activity) view.getContext();
            Intent intent = new Intent();
            intent.setClass(activity, PhotoSelectorActivity.class);
            intent.putExtra(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, ImagePickConfig.get()
                    .setCapture(true).setIsCutPhoto(false)
                    .setMaxImageSelectable(1).setShowSingleMediaType(true)
                    .setMimeTypeSet(MimeType.ofImage()));
            activity.startActivityForResult(intent, CommonRequestCode.CHAT_SELECT_PIC_CODE);
        }
    }

    private void doBlockUser(final Context context, String nickName, final String uid) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        if (context instanceof Activity) {
            String title = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "block_user_confirm_title");

            CommonConfirmDialog.showCancelDialog(context, String.format(title, GlobalConfig.AT + nickName), new CommonConfirmDialog.IConfirmDialogListener() {
                @Override
                public void onClickCancel() {

                }

                @Override
                public void onClickConfirm() {
                    mLoadingDlg = new LoadingDlg((Activity) context);
                    mLoadingDlg.show();
                    BlockUserManager.getInstance().block(uid);
                    EventLog1.BlockUser.report1(uid, EventConstants.FEED_PAGE_IM_MESSAGE, null, null, null, null);
                }
            });
        }
    }

    public void onSendTextMessage(final String s) {
        IMHelper.INSTANCE.sendText(mSession, s, from);
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_SEND_IM);
    }

    private void onSendPicMessage(final Item item) {
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                final WrapSizeFile file;
                try {
                    file = Luban.with(ImChatPage.this)
                            .setTargetDir(WeLikeFileManager.getTempCacheDir().getAbsolutePath())
                            .setCompressQuality(80)
                            .ignoreBy(0).get(item.filePath);
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            doRealPicMessageSend(file);
                        }
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_SEND_IM);
    }

    private void doRealPicMessageSend(final WrapSizeFile localMedia) {
        IMHelper.INSTANCE.sendImage(mSession, localMedia.file.getAbsolutePath(), from);
    }

    private void scrollToPosition(int i, boolean b) {
        if (b) {
            mRecyclerView.smoothScrollToPosition(i);
        } else {
            mRecyclerView.scrollToPosition(i);
        }

    }

    private boolean isOnTopPosition() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        return position == 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 图片选择结果回调
//        List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
        if (data == null) {
            return;
        }
        if (requestCode == CommonRequestCode.CHAT_SELECT_PIC_CODE && resultCode == Activity.RESULT_OK) {
            final ArrayList<Item> items = data.getParcelableArrayListExtra(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS);
            if (!CollectionUtil.isEmpty(items)) {
                // 取出第1个判断是否是图片，视频和图片只能二选一，不必考虑图片和视频混合
                Item localMedia = items.get(0);
                if (localMedia.isImage()) {
                    // 图片类型
                    onSendPicMessage(localMedia);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InputMethodUtil.hideInputMethod(mEditText);
        BlockUserManager.getInstance().unregister(this);
        IMHelper.INSTANCE.setCurrentSession("");
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onEmojiClick(EmojiBean emojiBean) {
        mEditText.getRichProcessor().insertEmoji(emojiBean);
    }

    @Override
    public void onEmojiDelClick() {
        mEditText.getRichProcessor().delete();
    }

    @Override
    public boolean canLoadMore() {
        return ViewUtil.canScroll(mRecyclerView) && mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mAdapter.onLoadMore();
        //TODO load more
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            KPSwitchConflictUtil.hidePanelAndKeyboard(mChatContainer);
            mEmojiBtn.setSelected(mChatContainer.getVisibility() == View.VISIBLE);
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().trim().length() == 0) {
            mSendBtn.setVisibility(View.GONE);
            mEditText.setBackgroundDrawable(null);
        } else {
            mSendBtn.setVisibility(View.VISIBLE);
            int length = mEditText.getRichProcessor().getTextLength();
            if (length > GlobalConfig.FEED_MAX_LENGTH) {
                if (mSendBtn.isEnabled()) {
                    mEditText.setBackgroundResource(R.drawable.im_chat_message_error_bg);
                    mSendBtn.setEnabled(false);
                    mSendBtn.setImageResource(R.drawable.im_chat_send_btn_disable);
                    Toast.makeText(mEditText.getContext(), ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "im_content_text_over_limit"), Toast.LENGTH_SHORT).show();
                }
            } else {
                mEditText.setBackgroundResource(R.drawable.im_chat_message_bg);
                mSendBtn.setEnabled(true);
                mSendBtn.setImageResource(R.drawable.im_chat_send_btn);
            }
        }
    }

    @Override
    public void onClickSwitch(View v, boolean switchToPanel) {

        mEmojiBtn.setSelected(mChatContainer.getVisibility() == View.VISIBLE);

        if (switchToPanel) {
            mEditText.clearFocus();
        } else {
            mEditText.requestFocus();
        }
    }

    @Override
    public void onBlockCompleted(String uid, int errCode) {
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
    }

    @Override
    public void onUnBlockCompleted(String uid, int errCode) {
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            EventLog1.BlockUser.report2(uid, EventConstants.FEED_PAGE_IM_MESSAGE, null, null, null, null);
        }
    }
}
