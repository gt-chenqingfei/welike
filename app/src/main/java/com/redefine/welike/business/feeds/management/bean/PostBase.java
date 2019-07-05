package com.redefine.welike.business.feeds.management.bean;

import com.redefine.richtext.RichItem;
import com.redefine.welike.business.location.management.bean.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2018/1/6.
 */

public class PostBase implements Serializable {
    public static final int POST_TYPE_TEXT = 1;
    public static final int POST_TYPE_PIC = 2;
    public static final int POST_TYPE_VIDEO = 3;
    public static final int POST_TYPE_LINK = 4;

    public static final int POST_TYPE_POLL = 6;
    public static final int POST_TYPE_INTEREST = 7;
    public static final int POST_TYPE_FOLLOW = 8;
    public static final int POST_TYPE_ART = 9;

    public static final int POST_TYPE_AD = 10;
    public static final int POST_TYPE_ACTIVE = 11;

    public static final int POST_TYPE_GP_SCORE = 20;

    public static final int POST_TYPE_FORWARD = 5;


    private static final long serialVersionUID = -1518868400871481372L;

    protected String pid;
    protected String uid;
    protected int type;
    protected long time;
    protected String headUrl;
    protected String nickName;
    protected boolean following;
    protected String from;
    protected String text;
    protected String summary;
    protected Location location;
    protected long likeCount;
    protected long readCount;
    protected long shareCount;
    protected boolean like;
    protected long superLikeExp;
    protected boolean deleted;


    protected boolean hot;
    protected boolean isRealHot;
    protected boolean block;
    protected int commentCount;
    protected int forwardCount;

    protected int vip;
    protected int curLevel;
    protected List<RichItem> richItemList;
    protected TopicCardInfo topicCardInfo;
    protected boolean isShowTopicCard;
    //区分post来源，operation表示运营池，new表示投稿池，统计用。
    protected String strategy;
    protected String operationType;
    protected String language;
    public String origin;
    protected String[] tags;
    protected String sequenceId;
    private ArticleInfo articleInfo;


    //record share buttom lifecycle
    protected boolean isShowWhatsApp = false;

    protected boolean isRePost = true;

    private boolean isHotPoll = false;

    private int position = 0;

    private boolean isTop = false;

    //activity
    private ActiveAttachment activeAttachment;


    //new
    private String shareSource;
    //万能参数
    private String reclogs;

    public ActiveAttachment getActiveAttachment() {
        return activeAttachment;
    }

    public void setActiveAttachment(ActiveAttachment activeAttachment) {
        this.activeAttachment = activeAttachment;
    }

    //HEADER
    private HeaderAttachment headerAttachment;
    //AD
    private AdAttachment adAttachment;

    public HeaderAttachment getHeaderAttachment() {
        return headerAttachment;
    }

    public void setHeaderAttachment(HeaderAttachment headerAttachment) {
        this.headerAttachment = headerAttachment;
    }

    public AdAttachment getAdAttachment() {
        return adAttachment;
    }

    public void setAdAttachment(AdAttachment adAttachment) {
        this.adAttachment = adAttachment;
    }

    private List<PollParticipants> pollParticipants = new ArrayList<>();


    public ArrayList<UserHonor> userhonors = new ArrayList<>();


    public String getReclogs() {
        return reclogs;
    }

    public void setReclogs(String reclogs) {
        this.reclogs = reclogs;
    }

    public boolean isRePost() {
        return isRePost;
    }

    public void setRePost(boolean rePost) {
        isRePost = rePost;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean isTop) {
        this.isTop = isTop;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    public boolean isRealHot() {
        return isRealHot;
    }

    public void setRealHot(boolean realHot) {
        isRealHot = realHot;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getShareSource() {
        return shareSource;
    }

    public void setShareSource(String shareSource) {
        this.shareSource = shareSource;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public long getSuperLikeExp() {
        return superLikeExp;
    }

    public void setSuperLikeExp(long superLikeExp) {
        this.superLikeExp = superLikeExp;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getForwardCount() {
        return forwardCount;
    }

    public void setForwardCount(int forwardCount) {
        this.forwardCount = forwardCount;
    }

    public List<RichItem> getRichItemList() {
        return richItemList;
    }

    public void setRichItemList(List<RichItem> richItemList) {
        this.richItemList = richItemList;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getVip() {
        return vip;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public TopicCardInfo getTopicCardInfo() {
        return topicCardInfo;
    }

    public void setTopicCardInfo(TopicCardInfo topicCardInfo) {
        this.topicCardInfo = topicCardInfo;
    }

    public boolean isShowTopicCard() {
        return isShowTopicCard;
    }

    public void setShowTopicCard(boolean showTopicCard) {
        isShowTopicCard = showTopicCard;
    }

    public long getReadCount() {
        return readCount;
    }

    public void setReadCount(long readCount) {
        this.readCount = readCount;
    }

    public long getShareCount() {
        return shareCount;
    }

    public void setShareCount(long shareCount) {
        this.shareCount = shareCount;
    }

    public int getCurLevel() {
        return curLevel;
    }

    public void setCurLevel(int curLevel) {
        this.curLevel = curLevel;
    }

    public boolean isShowWhatsApp() {
        return isShowWhatsApp;
    }

    public void setShowWhatsApp(boolean showWhatsApp) {
        isShowWhatsApp = showWhatsApp;
    }

    public boolean isHotPoll() {
        return isHotPoll;
    }

    public void setHotPoll(boolean hotPoll) {
        isHotPoll = hotPoll;
    }

    public List<PollParticipants> getPollParticipants() {
        return pollParticipants;
    }

    public void setPollParticipants(List<PollParticipants> pollParticipants) {
        this.pollParticipants = pollParticipants;
    }

    public ArticleInfo getArticleInfo() {
        return articleInfo;
    }

    public void setArticleInfo(ArticleInfo articleInfo) {
        this.articleInfo = articleInfo;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }
}
