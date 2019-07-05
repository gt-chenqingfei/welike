package com.redefine.multimedia.player.request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhichuan.huang on 2017/5/9.
 * <br/><br/>
 * email: 670534823@qq.com
 * <br/><br/>
 * phone: 15914375424
 * <br/><br/>
 * desc:
 */
public class YoutubeVideoInfo {

    public String check_type;

    public String id;

    public String title;

    public String author;

    public long duration;

    public String url;

    public int view_count;

    public List<YoutubeVideoItem> files = new ArrayList<YoutubeVideoItem>();
}
