package com.primapp.ui.base

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import com.primapp.R
import com.primapp.databinding.LayoutVideoViewBinding
import com.primapp.extensions.showError


class VideoViewDialog : BaseDialogFragment<LayoutVideoViewBinding>() {

    private var playerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun getLayoutRes(): Int = R.layout.layout_video_view

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        playerView = binding.fullScreenVideo

        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        //hideSystemUi()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        val url = ImageViewDialogArgs.fromBundle(requireArguments()).url
        url?.let {
            player = SimpleExoPlayer.Builder(requireContext()).build()
            playerView!!.player = player
            val mediaItem: MediaItem = MediaItem.fromUri(it)
            player!!.setMediaItem(mediaItem)
            player!!.playWhenReady = playWhenReady
            playerView!!.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            player!!.seekTo(currentWindow, playbackPosition)
            player!!.prepare()
            player!!.addListener(listners)
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            player!!.release()
            player = null
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }


    val listners = object : Player.EventListener {
        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            showError(requireContext(), "Failed to play media")
        }
    }

}