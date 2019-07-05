package com.redefine.welike.business.feeds.management.cache;

//import android.text.TextUtils;
//
//import com.alibaba.fastjson.JSONArray;
//import com.redefine.welike.base.DBStore;
//import com.redefine.welike.base.GlobalConfig;
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
//import com.redefine.welike.business.user.management.bean.User;
//import com.redefine.welike.base.dao.welike.DaoSession;
//import com.redefine.welike.base.dao.welike.HotPost;
//import com.redefine.welike.base.dao.welike.HotPostDao;
//import com.welike.richtext.RichItem;
//
//import org.greenrobot.greendao.query.LazyList;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;

/**
 * Created by liubin on 2018/1/23.
 */

//public class HotCache {
//    private DaoSession daoSession;
//    private HotPostDao postDao;
//    private HotCacheCallback listener;
//
//    public interface HotCacheCallback {
//
//        void onHotCacheInsertCompleted(List<PostBase> posts, String newCursor);
//
//    }
//
//    private static class HotCacheHolder {
//        public static HotCache instance = new HotCache();
//    }
//
//    private HotCache() {
//        daoSession = DBStore.getInstance().getDaoSession();
//        if (daoSession != null) {
//            postDao = daoSession.getHotPostDao();
//        }
//    }
//
//    public static HotCache getInstance() { return HotCacheHolder.instance; }
//
//    public void setListener(HotCacheCallback listener) {
//        this.listener = listener;
//    }
//
//    public void insert(List<PostBase> posts, String newCursor) {
//        List<HotPost> dbPosts = new ArrayList<>();
//        long now = new Date().getTime();
//        int index = 0;
//        for (PostBase post : posts) {
//            HotPost dbPost = parsePost(post,now + index);
//            dbPosts.add(dbPost);
//            index++;
//        }
//        runInsert(dbPosts, posts, newCursor);
//    }
//
//    public synchronized List<PostBase> listMoreByPid(String pid, int countOnePage) {
//        if (postDao == null) return null;
//
//        List<PostBase> posts = new ArrayList<>();
//        Set<String> uids = new HashSet<>();
//        try {
//            HotPost cursorPost = postDao.queryBuilder().where(HotPostDao.Properties.Pid.eq(pid)).build().unique();
//            if (cursorPost != null) {
//                List<HotPost> dbPosts = postDao.queryBuilder().where(HotPostDao.Properties.InsertTime.le(cursorPost.getInsertTime())).orderDesc(HotPostDao.Properties.InsertTime).limit(countOnePage + 1).build().list();
//                if (dbPosts != null && dbPosts.size() > 0) {
//                    boolean begin = false;
//                    for (HotPost dbPost : dbPosts) {
//                        if (begin) {
//                            PostBase post = hotPostToPost(dbPost);
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
//            List<HotPost> dbPosts = postDao.queryBuilder().orderDesc(HotPostDao.Properties.InsertTime).limit(countOnePage).build().list();
//            for (HotPost dbPost : dbPosts) {
//                PostBase post = hotPostToPost(dbPost);
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
//        if (postDao == null) return;
//        try {
//            postDao.deleteAll();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public synchronized void markLikeState(String pid, boolean isLike) {
//        if (postDao == null) return;
//
//        HotPost post = postDao.queryBuilder().where(HotPostDao.Properties.Pid.eq(pid)).build().unique();
//        if (post != null) {
//            if (isLike) {
//                post.setLikeValue(1);
//                post.setLikeCount(post.getLikeCount() + 1);
//            } else {
//                post.setLikeValue(0);
//                long newCount = post.getLikeCount() - 1;
//                if (newCount < 0) newCount = 0;
//                post.setLikeCount(newCount);
//            }
//            postDao.update(post);
//        }
//    }
//
//    public void updatePostsStatus(final User user) {
//        if (daoSession != null) {
//            daoSession.runInTx(new Runnable() {
//
//                @Override
//                public void run() {
//                    runUpdatePostsStatus(user);
//                }
//
//            });
//        }
//    }
//
//    public void removePosts(final String pid) {
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
////    private void broadcast(List<PostBase> posts, String newCursor) {
////        synchronized (listenerRefList) {
////            for (WeakReference<?> callbackRef : listenerRefList) {
////                Object l = callbackRef.get();
////                if (l != null && l instanceof HotCacheCallback) {
////                    HotCacheCallback listener = (HotCacheCallback)l;
////                    listener.onHotCacheInsertCompleted(posts, newCursor);
////                }
////            }
////        }
////    }
//
//    private synchronized void runUpdatePostsStatus(User user) {
//        LazyList<HotPost> dbPosts = null;
//        try {
//            dbPosts = postDao.queryBuilder().where(HotPostDao.Properties.Uid.eq(user.getUid())).build().listLazy();
//            for (HotPost post : dbPosts) {
//                post.setNick(user.getNickName());
//                post.setHead(user.getHeadUrl());
//                postDao.update(post);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (dbPosts != null) {
//                dbPosts.close();
//            }
//        }
//
//        LazyList<HotPost> dbChildPosts = null;
//        try {
//            dbChildPosts = postDao.queryBuilder().where(HotPostDao.Properties.ForwardUid.eq(user.getUid())).build().listLazy();
//            for (HotPost post : dbChildPosts) {
//                post.setForwardNick(user.getNickName());
//                postDao.update(post);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (dbChildPosts != null) {
//                dbChildPosts.close();
//            }
//        }
//    }
//
//    private void runInsert(final List<HotPost> dbPosts, final List<PostBase> posts, final String newCursor) {
//        daoSession.runInTx(new Runnable() {
//
//            @Override
//            public void run() {
//                List<PostBase> insertedPosts = new ArrayList<>();
//                int allCount = insertPosts(insertedPosts, posts, dbPosts);
//                if (allCount > GlobalConfig.HOT_POSTS_MAX_CACHE_COUNT) {
//                    int removeCount = allCount - GlobalConfig.HOT_POSTS_MAX_CACHE_COUNT;
//                    try {
//                        removeOldestPosts(removeCount);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if (listener != null) {
//                    listener.onHotCacheInsertCompleted(posts, newCursor);
//                }
//            }
//
//        });
//    }
//
//    private synchronized int insertPosts(List<PostBase> insertedPosts, final List<PostBase> posts, final List<HotPost> dbPosts) {
//        for (HotPost dbPost : dbPosts) {
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
//        postDao.queryBuilder().orderAsc(HotPostDao.Properties.InsertTime).limit(count).buildDelete().executeDeleteWithoutDetachingEntities();
//    }
//
//    private synchronized void runRemovePost(String pid) {
//        if (postDao == null) return;
//
//        try {
//            List<HotPost> posts = postDao.queryBuilder().where(HotPostDao.Properties.ForwardPid.eq(pid)).build().list();
//            for (HotPost p : posts) {
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
//    private HotPost parsePost(PostBase postBase, long insertTime) {
//        HotPost dbPost = new HotPost();
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
//            PostBase childPost = forwardPost.getRootPost();
//            parseChildPost(childPost, dbPost);
//        }
//        return dbPost;
//    }
//
//    private void parseChildPost(PostBase childPost, HotPost dbPost) {
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
//            dbPost.setForwardVideoThumb(videoPost.getCoverUrl());
//        } else if (childPost.getType() == PostBase.POST_TYPE_LINK) {
//            LinkPost linkPost = (LinkPost)childPost;
//            dbPost.setForwardLink(linkPost.getLinkUrl());
//            dbPost.setForwardLinkTitle(linkPost.getLinkTitle());
//            dbPost.setForwardLinkImg(linkPost.getLinkThumbUrl());
//            dbPost.setForwardLinkText(linkPost.getLinkText());
//        }
//    }
//
//    private PostBase hotPostToPost(HotPost hotPost) {
//
//        byte type = hotPost.getType();
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
//        post.setPid(hotPost.getPid());
//        post.setUid(hotPost.getUid());
//        if (account != null) {
//            if (TextUtils.equals(post.getUid(), account.getUid())) {
//                isSelf = true;
//            }
//        }
//        post.setType(type);
//        post.setTime(hotPost.getTime());
//        if (isSelf) {
//            post.setHeadUrl(account.getHeadUrl());
//            post.setNickName(account.getNickName());
//        } else {
//            post.setHeadUrl(hotPost.getHead());
//            post.setNickName(hotPost.getNick());
//        }
//        post.setFrom(hotPost.getFrom());
//        post.setText(hotPost.getText());
//        post.setSummary(hotPost.getSummary());
//        String richJSONString = hotPost.getRich();
//        if (!TextUtils.isEmpty(richJSONString)) {
//            List<RichItem> richItemList = RichTextContentParser.parserRichJSON(richJSONString);
//            if (richItemList != null) {
//                post.setRichItemList(richItemList);
//            }
//        }
//        post.setLikeCount(hotPost.getLikeCount());
//        if (hotPost.getLikeValue() == 0) {
//            post.setLike(false);
//        } else {
//            post.setLike(true);
//        }
//        post.setCommentCount(hotPost.getCommentCount());
//        post.setForwardCount(hotPost.getForwardCount());
//
//        if (type == PostBase.POST_TYPE_PIC) {
//            PicPost picPost = (PicPost)post;
//
//            String pic1 = hotPost.getPic1();
//            if (!TextUtils.isEmpty(pic1)) {
//                String pic1Large = hotPost.getPic1Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic1);
//                p.setPicUrl(pic1Large);
//                picPost.addPicInfo(p);
//            }
//            String pic2 = hotPost.getPic2();
//            if (!TextUtils.isEmpty(pic2)) {
//                String pic2Large = hotPost.getPic2Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic2);
//                p.setPicUrl(pic2Large);
//                picPost.addPicInfo(p);
//            }
//            String pic3 = hotPost.getPic3();
//            if (!TextUtils.isEmpty(pic3)) {
//                String pic3Large = hotPost.getPic3Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic3);
//                p.setPicUrl(pic3Large);
//                picPost.addPicInfo(p);
//            }
//            String pic4 = hotPost.getPic4();
//            if (!TextUtils.isEmpty(pic4)) {
//                String pic4Large = hotPost.getPic4Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic4);
//                p.setPicUrl(pic4Large);
//                picPost.addPicInfo(p);
//            }
//            String pic5 = hotPost.getPic5();
//            if (!TextUtils.isEmpty(pic5)) {
//                String pic5Large = hotPost.getPic5Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic5);
//                p.setPicUrl(pic5Large);
//                picPost.addPicInfo(p);
//            }
//            String pic6 = hotPost.getPic6();
//            if (!TextUtils.isEmpty(pic6)) {
//                String pic6Large = hotPost.getPic6Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic6);
//                p.setPicUrl(pic6Large);
//                picPost.addPicInfo(p);
//            }
//            String pic7 = hotPost.getPic7();
//            if (!TextUtils.isEmpty(pic7)) {
//                String pic7Large = hotPost.getPic7Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic7);
//                p.setPicUrl(pic7Large);
//                picPost.addPicInfo(p);
//            }
//            String pic8 = hotPost.getPic8();
//            if (!TextUtils.isEmpty(pic8)) {
//                String pic8Large = hotPost.getPic8Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic8);
//                p.setPicUrl(pic8Large);
//                picPost.addPicInfo(p);
//            }
//            String pic9 = hotPost.getPic9();
//            if (!TextUtils.isEmpty(pic9)) {
//                String pic9Large = hotPost.getPic9Large();
//                PicInfo p = new PicInfo();
//                p.setThumbUrl(pic9);
//                p.setPicUrl(pic9Large);
//                picPost.addPicInfo(p);
//            }
//        } else if (type == PostBase.POST_TYPE_VIDEO) {
//            VideoPost videoPost = (VideoPost)post;
//            videoPost.setVideoUrl(hotPost.getVideo());
//            videoPost.setCoverUrl(hotPost.getVideoThumb());
//        } else if (type == PostBase.POST_TYPE_LINK) {
//            LinkPost linkPost = (LinkPost)post;
//            linkPost.setLinkUrl(hotPost.getLink());
//            linkPost.setLinkText(hotPost.getLinkText());
//            linkPost.setLinkThumbUrl(hotPost.getLinkImg());
//            linkPost.setLinkTitle(hotPost.getLinkTitle());
//        } else if (type == PostBase.POST_TYPE_FORWARD) {
//            ForwardPost forwardPost = (ForwardPost)post;
//
//            byte childType = hotPost.getForwardType();
//            PostBase childPost = null;
//            if (childType == PostBase.POST_TYPE_TEXT) {
//                childPost = new TextPost();
//            } else if (childType == PostBase.POST_TYPE_PIC) {
//                childPost = new PicPost();
//            } else if (childType == PostBase.POST_TYPE_VIDEO) {
//                childPost = new VideoPost();
//            } else if (childType == PostBase.POST_TYPE_LINK) {
//                childPost = new LinkPost();
//            }
//            if (childPost == null) return null;
//
//            childPost.setPid(hotPost.getForwardPid());
//            childPost.setUid(hotPost.getForwardUid());
//            if (account != null) {
//                if (TextUtils.equals(childPost.getUid(), account.getUid())) {
//                    childIsSelf = true;
//                }
//            }
//            childPost.setType(childType);
//            if (childIsSelf) {
//                childPost.setNickName(account.getNickName());
//            } else {
//                childPost.setNickName(hotPost.getForwardNick());
//            }
//            childPost.setHeadUrl(hotPost.getForwardHead());
//            childPost.setText(hotPost.getForwardText());
//            childPost.setSummary(hotPost.getForwardSummary());
//            if (hotPost.getForwardLikeValue() == 0) {
//                childPost.setLike(false);
//            } else {
//                childPost.setLike(true);
//            }
//            childPost.setLikeCount(hotPost.getForwardLikeCount());
//            childPost.setCommentCount(hotPost.getForwardCommentCount());
//            childPost.setForwardCount(hotPost.getForwardForwardCount());
//            String childRichJSONString = hotPost.getForwardRich();
//            if (!TextUtils.isEmpty(childRichJSONString)) {
//                List<RichItem> childRichItemList = RichTextContentParser.parserRichJSON(childRichJSONString);
//                if (childRichItemList != null) {
//                    childPost.setRichItemList(childRichItemList);
//                }
//            }
//            if (childType == PostBase.POST_TYPE_PIC) {
//                PicPost childPicPost = (PicPost)childPost;
//
//                String pic1 = hotPost.getForwardPic1();
//                if (!TextUtils.isEmpty(pic1)) {
//                    String pic1Large = hotPost.getForwardPic1Large();
//                    PicInfo p = new PicInfo();
//                    p.setThumbUrl(pic1);
//                    p.setPicUrl(pic1Large);
//                    childPicPost.addPicInfo(p);
//                }
//                String pic2 = hotPost.getForwardPic2();
//                if (!TextUtils.isEmpty(pic2)) {
//                    String pic2Large = hotPost.getForwardPic2Large();
//                    PicInfo p = new PicInfo();
//                    p.setThumbUrl(pic2);
//                    p.setPicUrl(pic2Large);
//                    childPicPost.addPicInfo(p);
//                }
//                String pic3 = hotPost.getForwardPic3();
//                if (!TextUtils.isEmpty(pic3)) {
//                    String pic3Large = hotPost.getForwardPic3Large();
//                    PicInfo p = new PicInfo();
//                    p.setThumbUrl(pic3);
//                    p.setPicUrl(pic3Large);
//                    childPicPost.addPicInfo(p);
//                }
//                String pic4 = hotPost.getForwardPic4();
//                if (!TextUtils.isEmpty(pic4)) {
//                    String pic4Large = hotPost.getForwardPic4Large();
//                    PicInfo p = new PicInfo();
//                    p.setThumbUrl(pic4);
//                    p.setPicUrl(pic4Large);
//                    childPicPost.addPicInfo(p);
//                }
//                String pic5 = hotPost.getForwardPic5();
//                if (!TextUtils.isEmpty(pic5)) {
//                    String pic5Large = hotPost.getForwardPic5Large();
//                    PicInfo p = new PicInfo();
//                    p.setThumbUrl(pic5);
//                    p.setPicUrl(pic5Large);
//                    childPicPost.addPicInfo(p);
//                }
//                String pic6 = hotPost.getForwardPic6();
//                if (!TextUtils.isEmpty(pic6)) {
//                    String pic6Large = hotPost.getForwardPic6Large();
//                    PicInfo p = new PicInfo();
//                    p.setThumbUrl(pic6);
//                    p.setPicUrl(pic6Large);
//                    childPicPost.addPicInfo(p);
//                }
//                String pic7 = hotPost.getForwardPic7();
//                if (!TextUtils.isEmpty(pic7)) {
//                    String pic7Large = hotPost.getForwardPic7Large();
//                    PicInfo p = new PicInfo();
//                    p.setThumbUrl(pic7);
//                    p.setPicUrl(pic7Large);
//                    childPicPost.addPicInfo(p);
//                }
//                String pic8 = hotPost.getForwardPic8();
//                if (!TextUtils.isEmpty(pic8)) {
//                    String pic8Large = hotPost.getForwardPic8Large();
//                    PicInfo p = new PicInfo();
//                    p.setThumbUrl(pic8);
//                    p.setPicUrl(pic8Large);
//                    childPicPost.addPicInfo(p);
//                }
//                String pic9 = hotPost.getForwardPic9();
//                if (!TextUtils.isEmpty(pic9)) {
//                    String pic9Large = hotPost.getForwardPic9Large();
//                    PicInfo p = new PicInfo();
//                    p.setThumbUrl(pic9);
//                    p.setPicUrl(pic9Large);
//                    childPicPost.addPicInfo(p);
//                }
//            } else if (childType == PostBase.POST_TYPE_VIDEO) {
//                VideoPost childVideoPost = (VideoPost)childPost;
//                childVideoPost.setVideoUrl(hotPost.getForwardVideo());
//                childVideoPost.setCoverUrl(hotPost.getForwardVideoThumb());
//            } else if (childType == PostBase.POST_TYPE_LINK) {
//                LinkPost childLinkPost = (LinkPost)childPost;
//                childLinkPost.setLinkUrl(hotPost.getForwardLink());
//                childLinkPost.setLinkText(hotPost.getForwardLinkText());
//                childLinkPost.setLinkThumbUrl(hotPost.getForwardLinkImg());
//                childLinkPost.setLinkTitle(hotPost.getForwardLinkTitle());
//            }
//            forwardPost.setRootPost(childPost);
//        }
//
//        return post;
//    }
//
//}
