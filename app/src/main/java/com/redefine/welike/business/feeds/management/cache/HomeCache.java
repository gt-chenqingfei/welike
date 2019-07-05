package com.redefine.welike.business.feeds.management.cache;

//import android.text.TextUtils;
//
//import com.alibaba.fastjson.JSONArray;
//import com.redefine.welike.base.DBStore;
//import com.redefine.welike.base.GlobalConfig;
//import com.redefine.welike.business.feeds.management.FeedsStatusObserver;
//import com.redefine.welike.business.feeds.management.bean.ForwardPost;
//import com.redefine.welike.business.feeds.management.bean.LinkPost;
//import com.redefine.welike.business.feeds.management.bean.PicInfo;
//import com.redefine.welike.business.feeds.management.bean.PicPost;
//import com.redefine.welike.business.feeds.management.bean.PostBase;
//import com.redefine.welike.business.feeds.management.bean.TextPost;
//import com.redefine.welike.business.feeds.management.bean.VideoPost;
//import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
//import com.redefine.welike.business.feeds.management.parser.RichTextContentParser;
//import com.redefine.welike.base.profile.AccountManager;
//import com.redefine.welike.base.profile.bean.Account;
//import com.redefine.welike.base.dao.welike.DaoSession;
//import com.redefine.welike.base.dao.welike.HomePost;
//import com.redefine.welike.base.dao.welike.HomePostDao;
//import com.welike.richtext.RichItem;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Locale;
//import java.util.Set;

/**
 * Created by liubin on 2018/1/23.
 */

//public class HomeCache {
//    private DaoSession daoSession;
//    private HomePostDao postDao;
//    private HomeCacheCallback listener;
//
//    public interface HomeCacheCallback {
//
//        void onHomeCacheInsertCompleted(List<PostBase> posts, boolean nextLoadFromNet, String newCursor);
//
//    }
//
//    private static class HomeCacheHolder {
//        public static HomeCache instance = new HomeCache();
//    }
//
//    private HomeCache() {
//        daoSession = DBStore.getInstance().getDaoSession();
//        if (daoSession != null) {
//            postDao = daoSession.getHomePostDao();
//        }
//    }
//
//    public static HomeCache getInstance() { return HomeCacheHolder.instance; }
//
//    public void setListener(HomeCacheCallback listener) {
//        this.listener = listener;
//    }
//
//    public void insert(List<PostBase> posts, String newCursor) {
//        if (postDao == null) return;
//
//        List<HomePost> dbPosts = new ArrayList<>();
//        long now = new Date().getTime();
//        int index = 0;
//        for (PostBase post : posts) {
//            HomePost dbPost = parsePost(post, now + index);
//            dbPosts.add(dbPost);
//            index++;
//        }
//        runInsert(dbPosts, posts, newCursor);
//    }
//
//    public synchronized List<PostBase> listBeforePid(String pid, int countOnePage) {
//        if (postDao == null) return null;
//
//        List<PostBase> posts = new ArrayList<>();
//        Set<String> uids = new HashSet<>();
//        try {
//            HomePost cursorPost = postDao.queryBuilder().where(HomePostDao.Properties.Pid.eq(pid)).build().unique();
//            if (cursorPost != null) {
//                List<HomePost> dbPosts =  postDao.queryBuilder().where(HomePostDao.Properties.Time.le(cursorPost.getTime())).orderDesc(HomePostDao.Properties.Time).limit(countOnePage + 1).build().list();
//                if (dbPosts != null && dbPosts.size() > 0) {
//                    boolean begin = false;
//                    for (HomePost dbPost : dbPosts) {
//                        if (begin) {
//                            PostBase post = homePostToPost(dbPost);
//                            if (post != null) {
//                                posts.add(post);
//                                uids.add(post.getUid());
//                                if (post instanceof ForwardPost) {
//                                    ForwardPost fpost = (ForwardPost)post;
//                                    PostBase rpost = fpost.getRootPost();
//                                    if (rpost != null && !fpost.isForwardDeleted()) {
//                                        uids.add(rpost.getUid());
//                                    }
//                                }
//                            }
//                        } else if (TextUtils.equals(dbPost.getPid(), pid)) {
//                            begin = true;
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        PostsDataSourceParser.updateFeedsFollowStatus(posts, uids);
//
//        return posts;
//    }
//
//    public synchronized List<PostBase> listNewPosts(int countOnePage) {
//        if (postDao == null) return null;
//
//        List<PostBase> posts = new ArrayList<>();
//        Set<String> uids = new HashSet<>();
//        try {
//            List<HomePost> dbPosts = postDao.queryBuilder().orderDesc(HomePostDao.Properties.Time).limit(countOnePage).build().list();
//            for (HomePost dbPost : dbPosts) {
//                PostBase post = homePostToPost(dbPost);
//                if (post != null) {
//                    posts.add(post);
//                    uids.add(post.getUid());
//                    if (post instanceof ForwardPost) {
//                        ForwardPost fpost = (ForwardPost)post;
//                        PostBase rpost = fpost.getRootPost();
//                        if (rpost != null && !fpost.isForwardDeleted()) {
//                            uids.add(rpost.getUid());
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        PostsDataSourceParser.updateFeedsFollowStatus(posts, uids);
//
//        return posts;
//    }
//
//    public synchronized void clearCache() {
//        if (postDao != null) {
//            try {
//                postDao.deleteAll();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public synchronized void markLikeState(String pid, boolean isLike) {
//        if (postDao == null) return;
//        HomePost post = postDao.queryBuilder().where(HomePostDao.Properties.Pid.eq(pid)).build().unique();
//        if (post != null) {
//            if (isLike) {
//                post.setLikeValue(1);
//            } else {
//                post.setLikeValue(0);
//            }
//            if (isLike) {
//                post.setLikeCount(post.getLikeCount() + 1);
//            } else {
//                long newCount = post.getLikeCount() - 1;
//                if (newCount < 0) newCount = 0;
//                post.setLikeCount(newCount);
//            }
//            postDao.update(post);
//        }
//    }
//
//    public void updatePostsStatus(final List<FeedsStatusObserver.FeedStatus> statusList) {
//        if (postDao == null) return;
//
//        final List<String> pids = new ArrayList<>();
//        for (FeedsStatusObserver.FeedStatus status : statusList) {
//            pids.add(status.getPid());
//        }
//        daoSession.runInTx(new Runnable() {
//
//            @Override
//            public void run() {
//                runUpdateStatusList(pids, statusList);
//            }
//
//        });
//
//    }
//
//    public synchronized void removePost(final String pid) {
//        if (daoSession == null) return;
//
//        daoSession.runInTx(new Runnable() {
//
//            @Override
//            public void run() {
//                runRemovePost(pid);
//            }
//
//        });
//    }
//
//    public static String generateCursor(PostBase post) {
//        String timeStr = String.valueOf(post.getTime());
//        String firstTime = timeStr.substring(0, timeStr.length() - 3);
//        String secTime = timeStr.substring(timeStr.length() - 3, timeStr.length());
//        return String.format(Locale.getDefault(), "%s:%s:%s", firstTime, secTime, post.getPid());
//    }
//
//    private void runInsert(final List<HomePost> dbPosts, final List<PostBase> posts, final String newCursor) {
//        daoSession.runInTx(new Runnable() {
//
//            @Override
//            public void run() {
//                List<PostBase> insertedPosts = new ArrayList<>();
//                int allCount = insertPosts(insertedPosts, posts, dbPosts);
//                if (allCount > GlobalConfig.HOME_POSTS_MAX_CACHE_COUNT) {
//                    int removeCount = allCount - GlobalConfig.HOME_POSTS_MAX_CACHE_COUNT;
//                    try {
//                        removeOldestPosts(removeCount);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if (insertedPosts.size() < posts.size()) {
//                    // 触发重叠，下次从本地数据库拉取
//                    if (listener != null) {
//                        listener.onHomeCacheInsertCompleted(posts, false, newCursor);
//                    }
//                } else {
//                    if (listener != null) {
//                        listener.onHomeCacheInsertCompleted(insertedPosts, true, newCursor);
//                    }
//                }
//            }
//
//        });
//    }
//
//    private synchronized void runUpdateStatusList(List<String> uids, List<FeedsStatusObserver.FeedStatus> statusList) {
//        try {
//            List<HomePost> posts = postDao.queryBuilder().where(HomePostDao.Properties.Pid.in(uids)).build().list();
//            if (posts != null && posts.size() > 0) {
//                for (HomePost p : posts) {
//                    FeedsStatusObserver.FeedStatus status = findFeedStatus(p.getPid(), statusList);
//                    if (status != null) {
//                        p.setLikeCount(status.getLikeCount());
//                        p.setCommentCount(status.getCommentCount());
//                        p.setForwardCount(status.getForwardCount());
//                        postDao.update(p);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private synchronized int insertPosts(List<PostBase> insertedPosts, final List<PostBase> posts, final List<HomePost> dbPosts) {
//        for (HomePost dbPost : dbPosts) {
//            try {
//                postDao.insert(dbPost);
//                PostBase p = findPost(dbPost.getPid(), posts);
//                if (p != null) {
//                    insertedPosts.add(p);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return (int)postDao.queryBuilder().buildCount().count();
//    }
//
//    private synchronized void removeOldestPosts(int count) {
//        if (postDao == null) return;
//        if (count <= 0) return;
//
//        postDao.queryBuilder().orderAsc(HomePostDao.Properties.Time).limit(count).buildDelete().executeDeleteWithoutDetachingEntities();
//    }
//
//    private synchronized void runRemovePost(String pid) {
//        if (postDao == null) return;
//
//        try {
//            postDao.queryBuilder().where(HomePostDao.Properties.Pid.eq(pid)).buildDelete().executeDeleteWithoutDetachingEntities();
//            List<HomePost> posts = postDao.queryBuilder().where(HomePostDao.Properties.ForwardPid.eq(pid)).build().list();
//            for (HomePost p : posts) {
//                p.setForwardDeleted(true);
//                postDao.update(p);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static PostBase findPost(String pid, final List<PostBase> posts) {
//        for (PostBase p : posts) {
//            if (TextUtils.equals(p.getPid(), pid)) {
//                return p;
//            }
//        }
//        return null;
//    }
//
//    private static FeedsStatusObserver.FeedStatus findFeedStatus(String pid, final List<FeedsStatusObserver.FeedStatus> statusList) {
//        for (FeedsStatusObserver.FeedStatus p : statusList) {
//            if (TextUtils.equals(p.getPid(), pid)) {
//                return p;
//            }
//        }
//        return null;
//    }
//
//    private HomePost parsePost(PostBase postBase, long insertTime) {
//        HomePost dbPost = new HomePost();
//        dbPost.setPid(postBase.getPid());
//        dbPost.setUid(postBase.getUid());
//        dbPost.setNick(postBase.getNickName());
//        dbPost.setHead(postBase.getHeadUrl());
//        dbPost.setTime(postBase.getTime());
//        dbPost.setInsertTime(insertTime);
//        dbPost.setFrom(postBase.getFrom());
//        dbPost.setText(postBase.getText());
//        dbPost.setSummary(postBase.getSummary());
//        if (postBase.getRichItemList() != null && postBase.getRichItemList().size() > 0) {
//            JSONArray jsonArr = RichTextContentParser.parserRichAttachments(postBase.getRichItemList());
//            if (jsonArr != null) {
//                dbPost.setRich(jsonArr.toJSONString());
//            }
//        }
//        dbPost.setLikeCount(postBase.getLikeCount());
//        if (postBase.isLike()) {
//            dbPost.setLikeValue(1);
//        } else {
//            dbPost.setLikeValue(0);
//        }
//        dbPost.setCommentCount(postBase.getCommentCount());
//        dbPost.setForwardCount(postBase.getForwardCount());
//        dbPost.setType((byte)postBase.getType());
//        if (postBase.getType() == PostBase.POST_TYPE_PIC) {
//            PicPost picPost = (PicPost)postBase;
//            int index = 0;
//            List<PicInfo> picList = picPost.listPicInfo();
//            for (PicInfo pic : picList) {
//                String url = pic.getPicUrl();
//                String thumbUrl = pic.getThumbUrl();
//                switch (index)
//                {
//                    case 0:
//                    {
//                        dbPost.setPic1(thumbUrl);
//                        dbPost.setPic1Large(url);
//                        break;
//                    }
//                    case 1:
//                    {
//                        dbPost.setPic2(thumbUrl);
//                        dbPost.setPic2Large(url);
//                        break;
//                    }
//                    case 2:
//                    {
//                        dbPost.setPic3(thumbUrl);
//                        dbPost.setPic3Large(url);
//                        break;
//                    }
//                    case 3:
//                    {
//                        dbPost.setPic4(thumbUrl);
//                        dbPost.setPic4Large(url);
//                        break;
//                    }
//                    case 4:
//                    {
//                        dbPost.setPic5(thumbUrl);
//                        dbPost.setPic5Large(url);
//                        break;
//                    }
//                    case 5:
//                    {
//                        dbPost.setPic6(thumbUrl);
//                        dbPost.setPic6Large(url);
//                        break;
//                    }
//                    case 6:
//                    {
//                        dbPost.setPic7(thumbUrl);
//                        dbPost.setPic7Large(url);
//                        break;
//                    }
//                    case 7:
//                    {
//                        dbPost.setPic8(thumbUrl);
//                        dbPost.setPic8Large(url);
//                        break;
//                    }
//                    case 8:
//                    {
//                        dbPost.setPic9(thumbUrl);
//                        dbPost.setPic9Large(url);
//                        break;
//                    }
//                    default:
//                        break;
//                }
//                index++;
//            }
//        } else if (postBase.getType() == PostBase.POST_TYPE_VIDEO) {
//            VideoPost videoPost = (VideoPost)postBase;
//            dbPost.setVideo(videoPost.getVideoUrl());
//            dbPost.setVideoThumb(videoPost.getCoverUrl());
//        } else if (postBase.getType() == PostBase.POST_TYPE_LINK) {
//            LinkPost linkPost = (LinkPost)postBase;
//            dbPost.setLink(linkPost.getLinkUrl());
//            dbPost.setLinkTitle(linkPost.getLinkTitle());
//            dbPost.setLinkImg(linkPost.getLinkThumbUrl());
//            dbPost.setLinkText(linkPost.getLinkText());
//        } else if (postBase.getType() == PostBase.POST_TYPE_FORWARD) {
//            ForwardPost forwardPost = (ForwardPost)postBase;
//            dbPost.setForwardDeleted(forwardPost.isForwardDeleted());
//            if (!dbPost.getForwardDeleted()) {
//                PostBase childPost = forwardPost.getRootPost();
//                parseChildPost(childPost, dbPost);
//            }
//        }
//        return dbPost;
//    }
//
//    private void parseChildPost(PostBase childPost, HomePost dbPost) {
//        if (childPost == null) return;
//
//        dbPost.setForwardPid(childPost.getPid());
//        dbPost.setForwardUid(childPost.getUid());
//        dbPost.setForwardNick(childPost.getNickName());
//        dbPost.setForwardHead(childPost.getHeadUrl());
//        dbPost.setForwardType((byte)childPost.getType());
//        dbPost.setForwardLikeCount(childPost.getLikeCount());
//        if (childPost.isLike()) {
//            dbPost.setForwardLikeValue(1);
//        } else {
//            dbPost.setForwardLikeValue(0);
//        }
//        dbPost.setForwardCommentCount(childPost.getCommentCount());
//        dbPost.setForwardForwardCount(childPost.getForwardCount());
//        dbPost.setForwardText(childPost.getText());
//        dbPost.setForwardSummary(childPost.getSummary());
//        if (childPost.getRichItemList() != null && childPost.getRichItemList().size() > 0) {
//            JSONArray jsonArr = RichTextContentParser.parserRichAttachments(childPost.getRichItemList());
//            if (jsonArr != null) {
//                dbPost.setForwardRich(jsonArr.toJSONString());
//            }
//        }
//        if (childPost.getType() == PostBase.POST_TYPE_PIC) {
//            PicPost picPost = (PicPost)childPost;
//            int index = 0;
//            List<PicInfo> picList = picPost.listPicInfo();
//            for (PicInfo pic : picList) {
//                String url = pic.getPicUrl();
//                String thumbUrl = pic.getThumbUrl();
//                switch (index)
//                {
//                    case 0:
//                    {
//                        dbPost.setForwardPic1(thumbUrl);
//                        dbPost.setForwardPic1Large(url);
//                        break;
//                    }
//                    case 1:
//                    {
//                        dbPost.setForwardPic2(thumbUrl);
//                        dbPost.setForwardPic2Large(url);
//                        break;
//                    }
//                    case 2:
//                    {
//                        dbPost.setForwardPic3(thumbUrl);
//                        dbPost.setForwardPic3Large(url);
//                        break;
//                    }
//                    case 3:
//                    {
//                        dbPost.setForwardPic4(thumbUrl);
//                        dbPost.setForwardPic4Large(url);
//                        break;
//                    }
//                    case 4:
//                    {
//                        dbPost.setForwardPic5(thumbUrl);
//                        dbPost.setForwardPic5Large(url);
//                        break;
//                    }
//                    case 5:
//                    {
//                        dbPost.setForwardPic6(thumbUrl);
//                        dbPost.setForwardPic6Large(url);
//                        break;
//                    }
//                    case 6:
//                    {
//                        dbPost.setForwardPic7(thumbUrl);
//                        dbPost.setForwardPic7Large(url);
//                        break;
//                    }
//                    case 7:
//                    {
//                        dbPost.setForwardPic8(thumbUrl);
//                        dbPost.setForwardPic8Large(url);
//                        break;
//                    }
//                    case 8:
//                    {
//                        dbPost.setForwardPic9(thumbUrl);
//                        dbPost.setForwardPic9Large(url);
//                        break;
//                    }
//                    default:
//                        break;
//                }
//                index++;
//            }
//        } else if (childPost.getType() == PostBase.POST_TYPE_VIDEO) {
//            VideoPost videoPost = (VideoPost)childPost;
//            dbPost.setForwardVideo(videoPost.getVideoUrl());
//            dbPost.setForwardVideoThumb(videoPost.getVideoUrl());
//        } else if (childPost.getType() == PostBase.POST_TYPE_LINK) {
//            LinkPost linkPost = (LinkPost)childPost;
//            dbPost.setForwardLink(linkPost.getLinkUrl());
//            dbPost.setForwardLinkTitle(linkPost.getLinkTitle());
//            dbPost.setForwardLinkImg(linkPost.getLinkThumbUrl());
//            dbPost.setForwardLinkText(linkPost.getLinkText());
//        }
//    }
//
//    private PostBase homePostToPost(HomePost homePost) {
//
//        byte type = homePost.getType();
//        PostBase post = null;
//        if (type == PostBase.POST_TYPE_TEXT) {
//            post = new TextPost();
//        } else if (type == PostBase.POST_TYPE_PIC) {
//            post = new PicPost();
//        } else if (type == PostBase.POST_TYPE_VIDEO) {
//            post = new VideoPost();
//        } else if (type == PostBase.POST_TYPE_LINK) {
//            post = new LinkPost();
//        } else if (type == PostBase.POST_TYPE_FORWARD) {
//            post = new ForwardPost();
//        }
//        if (post == null) return null;
//
//        boolean isSelf = false;
//        boolean childIsSelf = false;
//        Account account = AccountManager.getInstance().getAccount();
//
//        post.setPid(homePost.getPid());
//        post.setUid(homePost.getUid());
//        if (account != null) {
//            if (TextUtils.equals(post.getUid(), account.getUid())) {
//                isSelf = true;
//            }
//        }
//        post.setType(type);
//        post.setTime(homePost.getTime());
//        if (isSelf) {
//            post.setHeadUrl(account.getHeadUrl());
//            post.setNickName(account.getNickName());
//        } else {
//            post.setHeadUrl(homePost.getHead());
//            post.setNickName(homePost.getNick());
//        }
//        post.setFrom(homePost.getFrom());
//        post.setText(homePost.getText());
//        post.setSummary(homePost.getSummary());
//        post.setLikeCount(homePost.getLikeCount());
//        if (homePost.getLikeValue() == 0) {
//            post.setLike(false);
//        } else {
//            post.setLike(true);
//        }
//        post.setCommentCount(homePost.getCommentCount());
//        post.setForwardCount(homePost.getForwardCount());
//        String richJSONString = homePost.getRich();
//        if (!TextUtils.isEmpty(richJSONString)) {
//            List<RichItem> richItemList = RichTextContentParser.parserRichJSON(richJSONString);
//            if (richItemList != null) {
//                post.setRichItemList(richItemList);
//            }
//        }
//        if (type == PostBase.POST_TYPE_PIC) {
//            PicPost picPost = (PicPost)post;
//
//            String pic1 = homePost.getPic1();
//            if (!TextUtils.isEmpty(pic1)) {
//                String pic1Large = homePost.getPic1Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic1);
//                p.setPicUrl(pic1Large);
//                picPost.addPicInfo(p);
//            }
//            String pic2 = homePost.getPic2();
//            if (!TextUtils.isEmpty(pic2)) {
//                String pic2Large = homePost.getPic2Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic2);
//                p.setPicUrl(pic2Large);
//                picPost.addPicInfo(p);
//            }
//            String pic3 = homePost.getPic3();
//            if (!TextUtils.isEmpty(pic3)) {
//                String pic3Large = homePost.getPic3Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic3);
//                p.setPicUrl(pic3Large);
//                picPost.addPicInfo(p);
//            }
//            String pic4 = homePost.getPic4();
//            if (!TextUtils.isEmpty(pic4)) {
//                String pic4Large = homePost.getPic4Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic4);
//                p.setPicUrl(pic4Large);
//                picPost.addPicInfo(p);
//            }
//            String pic5 = homePost.getPic5();
//            if (!TextUtils.isEmpty(pic5)) {
//                String pic5Large = homePost.getPic5Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic5);
//                p.setPicUrl(pic5Large);
//                picPost.addPicInfo(p);
//            }
//            String pic6 = homePost.getPic6();
//            if (!TextUtils.isEmpty(pic6)) {
//                String pic6Large = homePost.getPic6Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic6);
//                p.setPicUrl(pic6Large);
//                picPost.addPicInfo(p);
//            }
//            String pic7 = homePost.getPic7();
//            if (!TextUtils.isEmpty(pic7)) {
//                String pic7Large = homePost.getPic7Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic7);
//                p.setPicUrl(pic7Large);
//                picPost.addPicInfo(p);
//            }
//            String pic8 = homePost.getPic8();
//            if (!TextUtils.isEmpty(pic8)) {
//                String pic8Large = homePost.getPic8Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic8);
//                p.setPicUrl(pic8Large);
//                picPost.addPicInfo(p);
//            }
//            String pic9 = homePost.getPic9();
//            if (!TextUtils.isEmpty(pic9)) {
//                String pic9Large = homePost.getPic9Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic9);
//                p.setPicUrl(pic9Large);
//                picPost.addPicInfo(p);
//            }
//        } else if (type == PostBase.POST_TYPE_VIDEO) {
//            VideoPost videoPost = (VideoPost)post;
//            videoPost.setVideoUrl(homePost.getVideo());
//            videoPost.setCoverUrl(homePost.getVideoThumb());
//        } else if (type == PostBase.POST_TYPE_LINK) {
//            LinkPost linkPost = (LinkPost)post;
//            linkPost.setLinkUrl(homePost.getLink());
//            linkPost.setLinkText(homePost.getLinkText());
//            linkPost.setLinkThumbUrl(homePost.getLinkImg());
//            linkPost.setLinkTitle(homePost.getLinkTitle());
//        } else if (type == PostBase.POST_TYPE_FORWARD) {
//            ForwardPost forwardPost = (ForwardPost)post;
//            forwardPost.setForwardDeleted(homePost.getForwardDeleted());
//            if (!forwardPost.isForwardDeleted()) {
//                byte childType = homePost.getForwardType();
//                PostBase childPost = null;
//                if (childType == PostBase.POST_TYPE_TEXT) {
//                    childPost = new TextPost();
//                } else if (childType == PostBase.POST_TYPE_PIC) {
//                    childPost = new PicPost();
//                } else if (childType == PostBase.POST_TYPE_VIDEO) {
//                    childPost = new VideoPost();
//                } else if (childType == PostBase.POST_TYPE_LINK) {
//                    childPost = new LinkPost();
//                }
//                if (childPost == null) return null;
//
//                childPost.setPid(homePost.getForwardPid());
//                childPost.setUid(homePost.getForwardUid());
//                if (account != null) {
//                    if (TextUtils.equals(childPost.getUid(), account.getUid())) {
//                        childIsSelf = true;
//                    }
//                }
//                childPost.setType(childType);
//                if (childIsSelf) {
//                    childPost.setNickName(account.getNickName());
//                } else {
//                    childPost.setNickName(homePost.getForwardNick());
//                }
//                childPost.setHeadUrl(homePost.getForwardHead());
//                childPost.setText(homePost.getForwardText());
//                childPost.setSummary(homePost.getForwardSummary());
//                if (homePost.getForwardLikeValue() == 0) {
//                    childPost.setLike(false);
//                } else {
//                    childPost.setLike(true);
//                }
//                childPost.setLikeCount(homePost.getForwardLikeCount());
//                childPost.setCommentCount(homePost.getForwardCommentCount());
//                childPost.setForwardCount(homePost.getForwardForwardCount());
//                String childRichJSONString = homePost.getForwardRich();
//                if (!TextUtils.isEmpty(childRichJSONString)) {
//                    List<RichItem> childRichItemList = RichTextContentParser.parserRichJSON(childRichJSONString);
//                    if (childRichItemList != null) {
//                        childPost.setRichItemList(childRichItemList);
//                    }
//                }
//                if (childType == PostBase.POST_TYPE_PIC) {
//                    PicPost childPicPost = (PicPost)childPost;
//
//                    String pic1 = homePost.getForwardPic1();
//                    if (!TextUtils.isEmpty(pic1)) {
//                        String pic1Large = homePost.getForwardPic1Large();
//                        PicInfo p = new PicInfo();
//                        p.setThumbUrl(pic1);
//                        p.setPicUrl(pic1Large);
//                        childPicPost.addPicInfo(p);
//                    }
//                    String pic2 = homePost.getForwardPic2();
//                    if (!TextUtils.isEmpty(pic2)) {
//                        String pic2Large = homePost.getForwardPic2Large();
//                        PicInfo p = new PicInfo();
//                        p.setThumbUrl(pic2);
//                        p.setPicUrl(pic2Large);
//                        childPicPost.addPicInfo(p);
//                    }
//                    String pic3 = homePost.getForwardPic3();
//                    if (!TextUtils.isEmpty(pic3)) {
//                        String pic3Large = homePost.getForwardPic3Large();
//                        PicInfo p = new PicInfo();
//                        p.setThumbUrl(pic3);
//                        p.setPicUrl(pic3Large);
//                        childPicPost.addPicInfo(p);
//                    }
//                    String pic4 = homePost.getForwardPic4();
//                    if (!TextUtils.isEmpty(pic4)) {
//                        String pic4Large = homePost.getForwardPic4Large();
//                        PicInfo p = new PicInfo();
//                        p.setThumbUrl(pic4);
//                        p.setPicUrl(pic4Large);
//                        childPicPost.addPicInfo(p);
//                    }
//                    String pic5 = homePost.getForwardPic5();
//                    if (!TextUtils.isEmpty(pic5)) {
//                        String pic5Large = homePost.getForwardPic5Large();
//                        PicInfo p = new PicInfo();
//                        p.setThumbUrl(pic5);
//                        p.setPicUrl(pic5Large);
//                        childPicPost.addPicInfo(p);
//                    }
//                    String pic6 = homePost.getForwardPic6();
//                    if (!TextUtils.isEmpty(pic6)) {
//                        String pic6Large = homePost.getForwardPic6Large();
//                        PicInfo p = new PicInfo();
//                        p.setThumbUrl(pic6);
//                        p.setPicUrl(pic6Large);
//                        childPicPost.addPicInfo(p);
//                    }
//                    String pic7 = homePost.getForwardPic7();
//                    if (!TextUtils.isEmpty(pic7)) {
//                        String pic7Large = homePost.getForwardPic7Large();
//                        PicInfo p = new PicInfo();
//                        p.setThumbUrl(pic7);
//                        p.setPicUrl(pic7Large);
//                        childPicPost.addPicInfo(p);
//                    }
//                    String pic8 = homePost.getForwardPic8();
//                    if (!TextUtils.isEmpty(pic8)) {
//                        String pic8Large = homePost.getForwardPic8Large();
//                        PicInfo p = new PicInfo();
//                        p.setThumbUrl(pic8);
//                        p.setPicUrl(pic8Large);
//                        childPicPost.addPicInfo(p);
//                    }
//                    String pic9 = homePost.getForwardPic9();
//                    if (!TextUtils.isEmpty(pic9)) {
//                        String pic9Large = homePost.getForwardPic9Large();
//                        PicInfo p = new PicInfo();
//                        p.setThumbUrl(pic9);
//                        p.setPicUrl(pic9Large);
//                        childPicPost.addPicInfo(p);
//                    }
//                } else if (childType == PostBase.POST_TYPE_VIDEO) {
//                    VideoPost childVideoPost = (VideoPost)childPost;
//                    childVideoPost.setVideoUrl(homePost.getForwardVideo());
//                    childVideoPost.setCoverUrl(homePost.getForwardVideoThumb());
//                } else if (childType == PostBase.POST_TYPE_LINK) {
//                    LinkPost childLinkPost = (LinkPost)childPost;
//                    childLinkPost.setLinkUrl(homePost.getForwardLink());
//                    childLinkPost.setLinkText(homePost.getForwardLinkText());
//                    childLinkPost.setLinkThumbUrl(homePost.getForwardLinkImg());
//                    childLinkPost.setLinkTitle(homePost.getForwardLinkTitle());
//                }
//                forwardPost.setRootPost(childPost);
//            }
//        }
//
//        return post;
//    }
//
//}
