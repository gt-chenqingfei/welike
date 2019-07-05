package com.redefine.welike.business.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FeedTextItemViewUI().setContentView(this)
//        setContentView(R.layout.text_feed_item)
//
//        val view = findViewById<ConstraintLayout>(R.id.common_feed_share_tab)
//
//        view.setOnClickListener {
//
//            Log.e("honlin", "1")
//
//        }

    }

}


//class NewPlayer(val textureView: VideoTextureView) : IPlayerControl {
//
//
//    val mMediaPlayer: MediaPlayer by lazy { MediaPlayer(textureView.context) }
//
//    var playerListener: IPlayerListener? = null
//
//    private var isPrepared = false
//    private var mSeekWhenPrepared = 0
//    //    private var isPausedInPreparing: Boolean = false
//    private var mUrl: String? = null
//
//    fun init() {
//        val mediaListener = object : MediaPlayer.OnBufferingUpdateListener
//                , MediaPlayer.OnCachedPositionsListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener
//                , MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener
//                , MediaPlayer.OnTimedTextListener, MediaPlayer.OnVideoSizeChangedListener {
//            override fun onBufferingUpdate(p0: MediaPlayer?, p1: Int) {
//
//            }
//
//            override fun onCachedPositions(p0: MediaPlayer?, p1: MutableMap<Any?, Any?>?) {
//            }
//
//            override fun onCompletion(p0: MediaPlayer?) {
//            }
//
//            override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
//                return false
//            }
//
//            override fun onInfo(p0: MediaPlayer?, i: Int, p2: Int): Boolean {
//                playerListener?.onInfo(i)
//                when (i) {
//                    android.media.MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
//                        playerListener?.onBufferingStart()
//                    }
//                    android.media.MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
//                        playerListener?.onBufferingEnd()
//                    }
//                    609 -> {
//                        playerListener?.onCompletion()
//                    }
//                    android.media.MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
//                        playerListener?.onFirstFrame()
//                    }
//                }
//                return false
//            }
//
//            override fun onPrepared(p0: MediaPlayer?) {
//                isPrepared = true
//                if (mSeekWhenPrepared != 0) {
//                    seekTo(mSeekWhenPrepared)
//                    mSeekWhenPrepared = 0
//                }
//                playerListener?.onPrepared()
//            }
//
//            override fun onSeekComplete(p0: MediaPlayer?) {
//            }
//
//            override fun onTimedText(p0: MediaPlayer?, p1: TimedText?) {
//            }
//
//            override fun onVideoSizeChanged(p0: MediaPlayer?, width: Int, height: Int) {
//                textureView.setVideoSize(width, height)
//            }
//
//        }
//        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
//            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {}
//
//            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}
//
//            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
//                pause()
//                mMediaPlayer.setSurface(null)
//                return true
//            }
//
//            override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
//                mMediaPlayer.setSurface(Surface(surface))
//            }
//
//        }
//
//        mMediaPlayer.setOnBufferingUpdateListener(mediaListener)
//        mMediaPlayer.setOnCachedPositionsListener(mediaListener)
//        mMediaPlayer.setOnCompletionListener(mediaListener)
//        mMediaPlayer.setOnErrorListener(mediaListener)
//        mMediaPlayer.setOnInfoListener(mediaListener)
//        mMediaPlayer.setOnPreparedListener(mediaListener)
//        mMediaPlayer.setOnSeekCompleteListener(mediaListener)
//        mMediaPlayer.setOnTimedTextListener(mediaListener)
//        mMediaPlayer.setOnVideoSizeChangedListener(mediaListener)
//        mMediaPlayer.setOption("rw.instance.set_looping", "1")
//        mMediaPlayer.setScreenOnWhilePlaying(true)
//    }
//
//    override fun setDataSource(url: String) {
//        try {
//            mUrl = url
//            mMediaPlayer.setDataSource(url)
//            mMediaPlayer.prepareAsync()
//        } catch (e: Throwable) {
//            e.printStackTrace()
//        }
//
//    }
//
//    override fun pause(): Boolean {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun start(): Boolean {
//        try {
//            if (isPrepared) {
//                mMediaPlayer.start()
//                return true
//            }
//        } catch (e: Exception) {
//            // do nothing
//        }
//        return false
//    }
//
//    override fun destroy() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun onPause() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun onResume() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun seekTo(pos: Int) {
//
//    }
//
//}
//
//interface IPlayerControl {
//
//    fun setDataSource(url: String)
//
//    fun pause(): Boolean
//
//    fun start(): Boolean
//
//    fun destroy()
//
//    fun onPause()
//
//    fun onResume()
//
//    fun seekTo(pos: Int)
//}
//
//interface IPlayerListener {
//
//    fun onInfo(i: Int)
//
//    fun onBufferingStart()
//
//    fun onBufferingEnd()
//
//    fun onCompletion()
//
//    fun onFirstFrame()
//
//    fun onPrepared()
//}