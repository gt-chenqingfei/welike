package com.redefine.welike.business.videoplayer.management

import android.content.Context
import android.media.AudioManager

/**
 * Created by nianguowang on 2019/2/20
 */
object WelikeAudioManager {

    fun requestAudioFocus(context: Context, afChangeListener: AudioManager.OnAudioFocusChangeListener) {
        context.getSystemService(Context.AUDIO_SERVICE)?.let {
            val audioManager = it as AudioManager
            audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        }

    }

    fun abandonAudioFocus(context: Context, afChangeListener: AudioManager.OnAudioFocusChangeListener) {
        context.getSystemService(Context.AUDIO_SERVICE)?.let {
            val audioManager = it as AudioManager
            audioManager.abandonAudioFocus(afChangeListener)
        }
    }
}