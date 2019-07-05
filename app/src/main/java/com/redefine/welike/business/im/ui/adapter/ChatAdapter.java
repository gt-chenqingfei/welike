package com.redefine.welike.business.im.ui.adapter;

import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.util.DiffUtil;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreHeaderAdapter;
import com.redefine.commonui.loadmore.bean.BaseFooterBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.im.Constants;
import com.redefine.im.room.MESSAGE;
import com.redefine.im.room.SESSION;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.im.IMHelper;
import com.redefine.welike.business.im.model.CardInfo;
import com.redefine.welike.business.im.ui.ChatMessage;
import com.redefine.welike.business.im.ui.holder.BaseChatCardViewHolder;
import com.redefine.welike.business.im.ui.holder.ChatAudioViewHolder;
import com.redefine.welike.business.im.ui.holder.ChatCardAppViewHolder;
import com.redefine.welike.business.im.ui.holder.ChatCardLinkViewHolder;
import com.redefine.welike.business.im.ui.holder.ChatCardPostViewHolder;
import com.redefine.welike.business.im.ui.holder.ChatCardProfileViewHolder;
import com.redefine.welike.business.im.ui.holder.ChatCardTopicViewHolder;
import com.redefine.welike.business.im.ui.holder.ChatPicViewHolder;
import com.redefine.welike.business.im.ui.holder.ChatSystemViewHolder;
import com.redefine.welike.business.im.ui.holder.ChatTextViewHolder;
import com.redefine.welike.business.im.ui.holder.ChatUnknowViewHolder;
import com.redefine.welike.business.im.ui.holder.ChatVideoViewHolder;
import com.redefine.welike.business.im.ui.holder.ImChatLoadMoreViewHolder;
import com.redefine.welike.business.im.ui.holder.SectionViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liwenbo on 2018/2/6.
 */

public class ChatAdapter extends LoadMoreHeaderAdapter<ChatMessage, BaseFooterBean> {

    private static final int MINUTE_15 = 60 * 15 * 1000;
    public final List<ChatMessage> mData = new ArrayList<>();

    public static final int MINE_MESSAGE_TYPE_TEXT = 1;
    public static final int MINE_MESSAGE_TYPE_PIC = 2;
    public static final int MINE_MESSAGE_TYPE_AUDIO = 3;
    public static final int MINE_MESSAGE_TYPE_VIDEO = 4;
    public static final int MINE_MESSAGE_TYPE_UNKNOW = 5;
    public static final int OTHER_MESSAGE_TYPE_TEXT = 6;
    public static final int OTHER_MESSAGE_TYPE_PIC = 7;
    public static final int OTHER_MESSAGE_TYPE_AUDIO = 8;
    public static final int OTHER_MESSAGE_TYPE_VIDEO = 9;
    public static final int OTHER_MESSAGE_TYPE_UNKNOW = 10;
    public static final int MESSAGE_TYPE_TIME = 11;
    public static final int MESSAGE_TYPE_SYSTEM = 12;
    public static final int MINE_MESSAGE_TYPE_CARD_POST = 13;
    public static final int MINE_MESSAGE_TYPE_CARD_TOPIC = 14;
    public static final int MINE_MESSAGE_TYPE_CARD_PROFILE = 15;
    public static final int MINE_MESSAGE_TYPE_CARD_APP = 16;
    public static final int MINE_MESSAGE_TYPE_CARD_LINK = 17;
    public static final int OTHER_MESSAGE_TYPE_CARD_POST = 18;
    public static final int OTHER_MESSAGE_TYPE_CARD_TOPIC = 19;
    public static final int OTHER_MESSAGE_TYPE_CARD_PROFILE = 20;
    public static final int OTHER_MESSAGE_TYPE_CARD_APP = 21;
    public static final int OTHER_MESSAGE_TYPE_CARD_LINK = 22;

    private final String mUid;
    private final SESSION mSession;
    private OnItemClickListener mOnItemClickListener;
    private BaseChatCardViewHolder.OnChatCardClickListener mCardClickListener;

    public ChatAdapter(SESSION session) {
        mUid = AccountManager.getInstance().getAccount().getUid();
        mSession = session;
    }

    public void setData(final List<MESSAGE> messages) {
        if(com.redefine.foundation.utils.CollectionUtil.isEmpty(messages)) {
            return;
        }
        Collections.sort(messages, new Comparator<MESSAGE>() {
            @Override
            public int compare(MESSAGE o1, MESSAGE o2) {
                if (o1 == null || o2 == null) {
                    return 0;
                }
                long l = o1.getTime() - o2.getTime();
                if (l == 0) {
                    return 0;
                }
                return l > 0 ? 1 : -1;
            }
        });
        final List<ChatMessage> chatMessages = convertChatMessages(messages);
        List<ChatMessage> newMessages = insertTime(chatMessages);
        List<ChatMessage> temp = new ArrayList<>(newMessages);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return mData.size();
            }

            @Override
            public int getNewListSize() {
                return messages.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return mData.get(oldItemPosition).getMessage().getMid().equals(chatMessages.get(newItemPosition).getMessage().getMid());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return mData.get(oldItemPosition) == chatMessages.get(newItemPosition);
            }
        });
        diffResult.dispatchUpdatesTo(new AdapterListUpdateCallback(this));

        mData.clear();
        mData.addAll(temp);
    }

    /**
     * 这里的TYPE有点乱，一共有四个类型的type：
     * 1，MESSAGE的type，定义在{@link Constants}中；
     * 2，MESSAGE中Card的type，定义在{@link CardInfo}中；
     * 3，MESSAGE中Card的subType，定义在{@link CardInfo}中；
     * 4，ChatMessage中的type，定义在@{@link ChatMessage}中；
     * 这个方法就是将前三个type拍平成第四个type，方便UI渲染。
     * @param messages 数据库中存的Message
     * @return
     */
    private List<ChatMessage> convertChatMessages(List<MESSAGE> messages) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        Map<String, String> headerMap = new HashMap<>();
        for (int i = messages.size() - 1; i >= 0; i--) {
            MESSAGE message = messages.get(i);
            ChatMessage chatMessage = new ChatMessage(message, ChatMessage.MESSAGE_TYPE_UNKNOWN);
            chatMessages.add(0, chatMessage);
            if(message.getType() == Constants.MESSAGE_TYPE_CARD) {
                String header = headerMap.get(message.getSenderUid());
                if(header == null) {
                    headerMap.put(message.getSenderUid(), message.getSenderHead());
                } else {
                    message.setSenderHead(header);
                }
                if(!TextUtils.isEmpty(message.getText())) {
                    CardInfo cardInfo = IMHelper.INSTANCE.parser(message.getText());
                    chatMessage.setCardInfo(cardInfo);
                    switch (cardInfo.getType()) {
                        case CardInfo.TYPE_POST:
                            if (cardInfo.getSubType() == CardInfo.SUB_TYPE_IMAGE
                                    || cardInfo.getSubType() == CardInfo.SUB_TYPE_TEXT
                                    || cardInfo.getSubType() == CardInfo.SUB_TYPE_VOTE
                                    || cardInfo.getSubType() == CardInfo.SUB_TYPE_VIDEO) {
                                chatMessage.setType(ChatMessage.MESSAGE_TYPE_CARD_POST);
                            } else {
                                chatMessage.setType(ChatMessage.MESSAGE_TYPE_UNKNOWN);
                            }
                            break;
                        case CardInfo.TYPE_TOPIC:
                            chatMessage.setType(ChatMessage.MESSAGE_TYPE_CARD_TOPIC);
                            break;
                        case CardInfo.TYPE_PROFILE:
                            chatMessage.setType(ChatMessage.MESSAGE_TYPE_CARD_PROFILE);
                            break;
                        case CardInfo.TYPE_SHAREAPP:
                            chatMessage.setType(ChatMessage.MESSAGE_TYPE_CARD_APP);
                            break;
                        case CardInfo.TYPE_CUSTOM:
                            chatMessage.setType(ChatMessage.MESSAGE_TYPE_CARD_LINK);
                            break;
                        default:
                            chatMessage.setType(ChatMessage.MESSAGE_TYPE_UNKNOWN);
                            break;
                    }
                } else {
                    chatMessage.setType(ChatMessage.MESSAGE_TYPE_UNKNOWN);
                }
            } else if (message.getType() == Constants.MESSAGE_TYPE_TXT
                    || message.getType() == Constants.MESSAGE_TYPE_PIC
                    || message.getType() == Constants.MESSAGE_TYPE_AUDIO
                    || message.getType() == Constants.MESSAGE_TYPE_VIDEO) {
                String header = headerMap.get(message.getSenderUid());
                if(header == null) {
                    headerMap.put(message.getSenderUid(), message.getSenderHead());
                } else {
                    message.setSenderHead(header);
                }
                chatMessage.setType(message.getType());
            } else if (message.getType() == Constants.MESSAGE_TYPE_TIME
                    || message.getType() == Constants.MESSAGE_TYPE_SYSTEM) {
                chatMessage.setType(message.getType());
            }
        }
        return chatMessages;
    }

    private List<ChatMessage> insertTime(List<ChatMessage> messages) {
        if(CollectionUtil.isEmpty(messages)) {
            return messages;
        }
        List<ChatMessage> result = new ArrayList<>();
        ChatMessage preMessage = null;
        for (int i = 0; i < messages.size(); i++) {
            ChatMessage curMessage = messages.get(i);
            if(preMessage == null) {
                ChatMessage timeMessage = new ChatMessage(curMessage.getMessage(), Constants.MESSAGE_TYPE_TIME);
                result.add(timeMessage);
            } else {
                long preMessageTime = preMessage.getMessage().getTime();
                long curMessageTime = curMessage.getMessage().getTime();

                if(Math.abs(curMessageTime - preMessageTime) > MINUTE_15) {
                    ChatMessage timeMessage = new ChatMessage(curMessage.getMessage(), Constants.MESSAGE_TYPE_TIME);
                    result.add(timeMessage);
                }
            }
            result.add(curMessage);
            preMessage = curMessage;
        }
        return result;
    }

    @Override
    protected BaseRecyclerViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return new ImChatLoadMoreViewHolder(mInflater.inflate(R.layout.im_chat_load_more_layout, parent, false));
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder viewHolder;
        switch (viewType) {
            case MINE_MESSAGE_TYPE_TEXT:
                viewHolder = new ChatTextViewHolder(mSession, mInflater.inflate(R.layout.im_mine_chat_text_item, parent, false));
                break;
            case MINE_MESSAGE_TYPE_PIC:
                viewHolder = new ChatPicViewHolder(mSession, mInflater.inflate(R.layout.im_mine_chat_pic_item, parent, false));
                break;
            case MINE_MESSAGE_TYPE_AUDIO:
                viewHolder = new ChatAudioViewHolder(mSession, mInflater.inflate(R.layout.im_mine_chat_audio_item, parent, false));
                break;
            case MINE_MESSAGE_TYPE_VIDEO:
                viewHolder = new ChatVideoViewHolder(mSession, mInflater.inflate(R.layout.im_mine_chat_video_item, parent, false));
                break;
            case MINE_MESSAGE_TYPE_UNKNOW:
                viewHolder = new ChatUnknowViewHolder(mSession, mInflater.inflate(R.layout.im_mine_chat_unknow_item, parent, false));
                break;
            case MINE_MESSAGE_TYPE_CARD_APP:
                viewHolder = new ChatCardAppViewHolder(mSession, mInflater.inflate(R.layout.im_mine_chat_common_card_item, parent, false), mCardClickListener);
                break;
            case MINE_MESSAGE_TYPE_CARD_LINK:
                viewHolder = new ChatCardLinkViewHolder(mSession, mInflater.inflate(R.layout.im_mine_chat_card_link_item, parent, false), mCardClickListener);
                break;
            case MINE_MESSAGE_TYPE_CARD_PROFILE:
                viewHolder = new ChatCardProfileViewHolder(mSession, mInflater.inflate(R.layout.im_mine_chat_card_profile_item, parent, false), mCardClickListener);
                break;
            case MINE_MESSAGE_TYPE_CARD_TOPIC:
                viewHolder = new ChatCardTopicViewHolder(mSession, mInflater.inflate(R.layout.im_mine_chat_card_topic_item, parent,false), mCardClickListener);
                break;
            case MINE_MESSAGE_TYPE_CARD_POST:
                viewHolder = new ChatCardPostViewHolder(mSession, mInflater.inflate(R.layout.im_mine_chat_common_card_item, parent, false), mCardClickListener);
                break;
            case OTHER_MESSAGE_TYPE_TEXT:
                viewHolder = new ChatTextViewHolder(mSession, mInflater.inflate(R.layout.im_other_chat_text_item, parent, false));
                break;
            case OTHER_MESSAGE_TYPE_PIC:
                viewHolder = new ChatPicViewHolder(mSession, mInflater.inflate(R.layout.im_other_chat_pic_item, parent, false));
                break;
            case OTHER_MESSAGE_TYPE_AUDIO:
                viewHolder = new ChatAudioViewHolder(mSession, mInflater.inflate(R.layout.im_other_chat_audio_item, parent, false));
                break;
            case OTHER_MESSAGE_TYPE_VIDEO:
                viewHolder = new ChatVideoViewHolder(mSession, mInflater.inflate(R.layout.im_other_chat_video_item, parent, false));
                break;
            case OTHER_MESSAGE_TYPE_CARD_APP:
                viewHolder = new ChatCardAppViewHolder(mSession, mInflater.inflate(R.layout.im_other_chat_common_card_item, parent, false), mCardClickListener);
                break;
            case OTHER_MESSAGE_TYPE_CARD_LINK:
                viewHolder = new ChatCardLinkViewHolder(mSession, mInflater.inflate(R.layout.im_other_chat_card_link_item, parent, false), mCardClickListener);
                break;
            case OTHER_MESSAGE_TYPE_CARD_PROFILE:
                viewHolder = new ChatCardProfileViewHolder(mSession, mInflater.inflate(R.layout.im_other_chat_card_profile_item, parent, false), mCardClickListener);
                break;
            case OTHER_MESSAGE_TYPE_CARD_TOPIC:
                viewHolder = new ChatCardTopicViewHolder(mSession, mInflater.inflate(R.layout.im_other_chat_card_topic_item, parent,false), mCardClickListener);
                break;
            case OTHER_MESSAGE_TYPE_CARD_POST:
                viewHolder = new ChatCardPostViewHolder(mSession, mInflater.inflate(R.layout.im_other_chat_common_card_item, parent, false), mCardClickListener);
                break;
            case MESSAGE_TYPE_TIME:
                viewHolder = new SectionViewHolder(mInflater.inflate(R.layout.im_time_section_item, parent, false));
                break;
            case MESSAGE_TYPE_SYSTEM:
                viewHolder = new ChatSystemViewHolder(mInflater.inflate(R.layout.im_chat_system_item, parent, false));
                break;
            case OTHER_MESSAGE_TYPE_UNKNOW:
            default:
                viewHolder = new ChatUnknowViewHolder(mSession, mInflater.inflate(R.layout.im_other_chat_unknow_item, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    protected void onBindItemViewHolder(final BaseRecyclerViewHolder holder, final int position) {
        holder.bindViews(this, mData.get(position));
        if (holder instanceof ChatTextViewHolder) {
            ((ChatTextViewHolder) holder).mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder, mData.get(position));
                }
            });
            ((ChatTextViewHolder) holder).mTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemClick(holder, mData.get(position));
                    return true;
                }
            });
        }
    }

    @Override
    protected int getRealItemViewType(int position) {
        int type = mData.get(position).getType();
        boolean isMine = TextUtils.equals(mUid, mData.get(position).getMessage().getSenderUid());
        switch (type) {
            case ChatMessage.MESSAGE_TYPE_TIME:
                return MESSAGE_TYPE_TIME;
            case ChatMessage.MESSAGE_TYPE_TXT:
                return isMine ? MINE_MESSAGE_TYPE_TEXT : OTHER_MESSAGE_TYPE_TEXT;
            case ChatMessage.MESSAGE_TYPE_PIC:
                return isMine ? MINE_MESSAGE_TYPE_PIC : OTHER_MESSAGE_TYPE_PIC;
            case ChatMessage.MESSAGE_TYPE_AUDIO:
                return isMine ? MINE_MESSAGE_TYPE_AUDIO : OTHER_MESSAGE_TYPE_AUDIO;
            case ChatMessage.MESSAGE_TYPE_VIDEO:
                return isMine ? MINE_MESSAGE_TYPE_VIDEO : OTHER_MESSAGE_TYPE_VIDEO;
            case ChatMessage.MESSAGE_TYPE_CARD_POST:
                return isMine ? MINE_MESSAGE_TYPE_CARD_POST : OTHER_MESSAGE_TYPE_CARD_POST;
            case ChatMessage.MESSAGE_TYPE_CARD_TOPIC:
                return isMine ? MINE_MESSAGE_TYPE_CARD_TOPIC : OTHER_MESSAGE_TYPE_CARD_TOPIC;
            case ChatMessage.MESSAGE_TYPE_CARD_PROFILE:
                return isMine ? MINE_MESSAGE_TYPE_CARD_PROFILE : OTHER_MESSAGE_TYPE_CARD_PROFILE;
            case ChatMessage.MESSAGE_TYPE_CARD_APP:
                return isMine ? MINE_MESSAGE_TYPE_CARD_APP : OTHER_MESSAGE_TYPE_CARD_APP;
            case ChatMessage.MESSAGE_TYPE_CARD_LINK:
                return isMine ? MINE_MESSAGE_TYPE_CARD_LINK : OTHER_MESSAGE_TYPE_CARD_LINK;
            case ChatMessage.MESSAGE_TYPE_SYSTEM:
                return MESSAGE_TYPE_SYSTEM;
            case ChatMessage.MESSAGE_TYPE_UNKNOWN:
            default:
                return isMine ? MINE_MESSAGE_TYPE_UNKNOW : OTHER_MESSAGE_TYPE_UNKNOW;
        }
    }

    @Override
    public int getRealItemCount() {
        return mData.size();
    }

    @Override
    public ChatMessage getRealItem(int position) {
        return mData.get(position);
    }

    public void setOnChatClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnChatCardClickLisgener(BaseChatCardViewHolder.OnChatCardClickListener listener) {
        mCardClickListener = listener;
    }
}
