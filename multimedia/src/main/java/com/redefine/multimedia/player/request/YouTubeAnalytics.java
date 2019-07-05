package com.redefine.multimedia.player.request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhichuan.huang on 2017/5/9.
 * <br/><br/>
 * email: 670534823@qq.com
 * <br/><br/>
 * phone: 15914375424
 * <br/><br/>
 * desc:
 */
public class YouTubeAnalytics {

    private static final String TAG = YouTubeAnalytics.class.getSimpleName();

    public static final String VID_MATE_YTB_FORMAT = "{\"formats\":[\n" +
            "{\"itag\":5,\"width\":320,\"height\":240,\"format\":\"FLV\",\"acodec\":\"MP3\",\"vcodec\":\"H.263\",\"quality\":\"240p\",\"title\":\"320x240(FLV 240p)\",\"canList\":false},\n" +
            "{\"itag\":17,\"width\":176,\"height\":144,\"format\":\"3GP\",\"acodec\":\"AAC\",\"vcodec\":\"MPEG-4\",\"quality\":\"144p\",\"title\":\"176x144(3GP 144p)\",\"canList\":true},\n" +
            "{\"itag\":18,\"width\":640,\"height\":360,\"format\":\"MP4\",\"acodec\":\"AAC\",\"vcodec\":\"MPEG-4\",\"quality\":\"360p\",\"title\":\"640x360(MP4 360p)\",\"canList\":true},\n" +
            "{\"itag\":22,\"width\":1280,\"height\":720,\"format\":\"MP4\",\"acodec\":\"AAC\",\"vcodec\":\"H.264\",\"quality\":\"720p\",\"title\":\"1280x720(MP4 720p HD)\",\"canList\":true},\n" +
            "{\"itag\":34,\"width\":640,\"height\":360,\"format\":\"FLV\",\"acodec\":\"AAC\",\"vcodec\":\"H.264\",\"quality\":\"360p\",\"title\":\"640x360(FLV 360p)\",\"canList\":true},\n" +
            "{\"itag\":35,\"width\":854,\"height\":480,\"format\":\"FLV\",\"acodec\":\"AAC\",\"vcodec\":\"H.264\",\"quality\":\"480p\",\"title\":\"854x480(FLV 480p)\",\"canList\":true},\n" +
            "{\"itag\":36,\"width\":320,\"height\":240,\"format\":\"3GP\",\"acodec\":\"AAC\",\"vcodec\":\"MPEG-4\",\"quality\":\"240p\",\"title\":\"320x240(3GP 240p)\",\"canList\":true},\n" +
            "{\"itag\":37,\"width\":1920,\"height\":1080,\"format\":\"MP4\",\"acodec\":\"AAC\",\"vcodec\":\"H.264\",\"quality\":\"1080p\",\"title\":\"1920x1080 (MP4 1080p HD)\",\"canList\":true},\n" +
            "{\"itag\":38,\"width\":2048,\"height\":1536,\"format\":\"MP4\",\"acodec\":\"AAC\",\"vcodec\":\"H.264\",\"quality\":\"4K\",\"title\":\"2048x1536(MP4 4K)\",\"canList\":true},\n" +
            "{\"itag\":43,\"width\":640,\"height\":360,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"360p\",\"title\":\"640x360(WEBM 360p)\",\"canList\":true},\n" +
            "{\"itag\":44,\"width\":854,\"height\":480,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"480p\",\"title\":\"854x480(WEBM 480p)\",\"canList\":true},\n" +
            "{\"itag\":45,\"width\":1280,\"height\":720,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"720p\",\"title\":\"1280x720(WEBM 720p HD)\",\"canList\":true},\n" +
            "{\"itag\":46,\"width\":1920,\"height\":1080,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"1080p\",\"title\":\"1920x1080(WEBM 1080p HD)\",\"canList\":true},\n" +
            "{\"itag\":82,\"width\":640,\"height\":360,\"format\":\"MP4\",\"acodec\":\"AAC\",\"vcodec\":\"H.264\",\"quality\":\"360p\",\"title\":\"640x360(MP4 360p)\",\"canList\":true},\n" +
            "{\"itag\":83,\"width\":854,\"height\":480,\"format\":\"MP4\",\"acodec\":\"AAC\",\"vcodec\":\"H.264\",\"quality\":\"480p\",\"title\":\"854x480(MP4 480p)\",\"canList\":true},\n" +
            "{\"itag\":84,\"width\":1280,\"height\":720,\"format\":\"MP4\",\"acodec\":\"AAC\",\"vcodec\":\"H.264\",\"quality\":\"720p\",\"title\":\"1280x720(MP4 720p HD)\",\"canList\":true},\n" +
            "{\"itag\":85,\"width\":1920,\"height\":1080,\"format\":\"MP4\",\"acodec\":\"AAC\",\"vcodec\":\"H.264\",\"quality\":\"1080p\",\"title\":\"1920x1080(MP4 1080p HD)\",\"canList\":true},\n" +
            "{\"itag\":100,\"width\":640,\"height\":360,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"360p\",\"title\":\"640x360(WEBM 360p)\",\"canList\":true},\n" +
            "{\"itag\":101,\"width\":854,\"height\":480,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"480p\",\"title\":\"854x480(WEBM 480p)\",\"canList\":true},\n" +
            "{\"itag\":102,\"width\":1280,\"height\":720,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"720p\",\"title\":\"1280x720(WEBM 720p HD)\",\"canList\":true},\n" +
            "{\"itag\":133,\"width\":320,\"height\":240,\"format\":\"MP4\",\"acodec\":\"\",\"vcodec\":\"H.264\",\"quality\":\"240p\",\"title\":\"320x240(MP4 240p)\",\"canList\":false},\n" +
            "{\"itag\":134,\"width\":640,\"height\":360,\"format\":\"MP4\",\"acodec\":\"\",\"vcodec\":\"H.264\",\"quality\":\"360p\",\"title\":\"640x360(MP4 360p)\",\"canList\":false},\n" +
            "{\"itag\":135,\"width\":854,\"height\":480,\"format\":\"MP4\",\"acodec\":\"\",\"vcodec\":\"H.264\",\"quality\":\"480p\",\"title\":\"854x480(MP4 480p)\",\"canList\":false},\n" +
            "{\"itag\":136,\"width\":1280,\"height\":720,\"format\":\"MP4\",\"acodec\":\"\",\"vcodec\":\"H.264\",\"quality\":\"720p\",\"title\":\"1280x720(MP4 720p HD)\",\"canList\":false},\n" +
            "{\"itag\":137,\"width\":1920,\"height\":1020,\"format\":\"MP4\",\"acodec\":\"\",\"vcodec\":\"H.264\",\"quality\":\"1080p\",\"title\":\"1920x1080(MP4 1080p HD)\",\"canList\":true},\n" +
            "{\"itag\":138,\"width\":2048,\"height\":1536,\"format\":\"MP4\",\"acodec\":\"\",\"vcodec\":\"H.264\",\"quality\":\"4K\",\"title\":\"2048x1536(MP4 4K)\",\"canList\":false},\n" +
            "{\"itag\":139,\"width\":0,\"height\":0,\"format\":\"M4A\",\"acodec\":\"AAC\",\"vcodec\":\"\",\"quality\":\"48kbps\",\"title\":\"Music - M4A\",\"canList\":true},\n" +
            "{\"itag\":140,\"width\":0,\"height\":0,\"format\":\"M4A\",\"acodec\":\"AAC\",\"vcodec\":\"\",\"quality\":\"128kbps\",\"title\":\"Music - M4A\",\"canList\":true},\n" +
            "{\"itag\":141,\"width\":0,\"height\":0,\"format\":\"M4A\",\"acodec\":\"AAC\",\"vcodec\":\"\",\"quality\":\"256kbps\",\"title\":\"Music - M4A\",\"canList\":true},\n" +
            "{\"itag\":160,\"width\":176,\"height\":144,\"format\":\"MP4\",\"acodec\":\"\",\"vcodec\":\"H.264\",\"quality\":\"144p\",\"title\":\"176x144(MP4 144p)\",\"canList\":false},\n" +
            "{\"itag\":171,\"width\":0,\"height\":0,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"\",\"quality\":\"128kbps\",\"title\":\"WEBM(Vorbis 128k)\",\"canList\":false},\n" +
            "{\"itag\":172,\"width\":0,\"height\":0,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"\",\"quality\":\"256kbps\",\"title\":\"WEBM(Vorbis 256k)\",\"canList\":false},\n" +
            "{\"itag\":242,\"width\":320,\"height\":240,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"240p\",\"title\":\"320x240(WEBM 240p)\",\"canList\":false},\n" +
            "{\"itag\":243,\"width\":640,\"height\":360,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"360p\",\"title\":\"640x360(WEBM 360p)\",\"canList\":false},\n" +
            "{\"itag\":244,\"width\":854,\"height\":480,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"480p\",\"title\":\"854x480(FLV 480p)\",\"canList\":false},\n" +
            "{\"itag\":245,\"width\":854,\"height\":480,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"480p\",\"title\":\"854x480(FLV 480p)\",\"canList\":false},\n" +
            "{\"itag\":246,\"width\":854,\"height\":480,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"480p\",\"title\":\"854x480(FLV 480p)\",\"canList\":false},\n" +
            "{\"itag\":247,\"width\":1280,\"height\":720,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"720p\",\"title\":\"1280x720(WEBM 720p HD)\",\"canList\":false},\n" +
            "{\"itag\":248,\"width\":1920,\"height\":1080,\"format\":\"WebM\",\"acodec\":\"Vorbis\",\"vcodec\":\"VP8\",\"quality\":\"1080p\",\"title\":\"1920x1080(WEBM 1080p HD)\",\"canList\":false},\n" +
            "{\"itag\":264,\"width\":1920,\"height\":1080,\"format\":\"MP4\",\"acodec\":\"AAC\",\"vcodec\":\"H.264\",\"quality\":\"1080p\",\"title\":\"1920x1080(MP4 1080p HD)\",\"canList\":false}\n" +
            "]}";

    public class MateFormat {
        public Integer itag;
        public int width;
        public int height;
        public String format;
        public String acodec;
        public String vcodec;
        public String quality;
        public String title;
        public boolean canList;
    }

    private static YouTubeAnalytics INSTANCE;

    private YouTubeAnalytics() {
        initMateFormatMap();
    }

    public static synchronized YouTubeAnalytics getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new YouTubeAnalytics();
        }
        return INSTANCE;
    }

    public static ExecutorService singleThread = Executors.newSingleThreadExecutor();

    private HashMap<Integer, MateFormat> mateFormatMap = new HashMap<Integer, MateFormat>();

    private void initMateFormatMap() {
        try {
            JSONObject obj = new JSONObject(VID_MATE_YTB_FORMAT);
            JSONArray jsonArray = obj.optJSONArray("formats");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject formatObj = jsonArray.optJSONObject(i);
                MateFormat mateFormat = new MateFormat();
                Integer itag = formatObj.optInt("itag");
                mateFormat.itag = itag;
                mateFormat.width = formatObj.optInt("width");
                mateFormat.height = formatObj.optInt("height");
                mateFormat.format = formatObj.optString("format");
                mateFormat.acodec = formatObj.optString("acodec");
                mateFormat.vcodec = formatObj.optString("vcodec");
                mateFormat.quality = formatObj.optString("quality");
                mateFormat.title = formatObj.optString("title");
                mateFormat.canList = formatObj.optBoolean("canList");
                mateFormatMap.put(itag, mateFormat);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }  catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public MateFormat getFormat(int itag) {
        return mateFormatMap.get(itag);
    }
}
