package com.primapp.ui.player

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.primapp.R

class InstaLikePlayerView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? =  null,
    defStyleAttr: Int = 0
) : FrameLayout(
    context!!, attrs, defStyleAttr
) {
    companion object{
        var isMuted: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    }
    private var videoSurfaceView: View?
    private var player: SimpleExoPlayer? = null
    private var isTouching = false
    private var lastPos: Long? = 0
    private var videoUri: Uri? = null

    /**
     * Returns the player currently set on this view, or null if no player is set.
     */
    fun getPlayer(): SimpleExoPlayer? {
        return player
    }


    /**
     * Set the [Player] to use.
     *

     * To transition a [Player] from targeting one view to another, it's recommended to use
     * [.switchTargetView] rather than this method. If you do
     * wish to use this method directly, be sure to attach the player to the new view *before*
     * calling `setPlayer(null)` to detach it from the old one. This ordering is significantly
     * more efficient and may allow for more seamless transitions.
     *
     * @param player The [Player] to use, or `null` to detach the current player. Only
     * players which are accessed on the main thread are supported (`player.getApplicationLooper() == Looper.getMainLooper()`).
     */
    private fun setPlayer(player: SimpleExoPlayer?) {
        if (this.player === player) {
            return
        }
        val oldPlayer = this.player

        oldPlayer?.clearVideoSurfaceView(videoSurfaceView as SurfaceView?)
        this.player = player
        player?.setVideoSurfaceView(videoSurfaceView as SurfaceView?)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        // Work around https://github.com/google/SimpleExoPlayer/issues/3160.
        videoSurfaceView?.visibility = visibility
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (player != null && player!!.isPlayingAd) {
            return super.dispatchKeyEvent(event)
        }
//        val isDpadKey = isDpadKey(event.keyCode)
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (player == null) {
            false
        } else when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouching = true
                true
            }
            MotionEvent.ACTION_UP -> {
                if (isTouching) {
                    isTouching = false
                    performClick()
                    return true
                }
                false
            }
            else -> false
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return false
    }

    override fun onTrackballEvent(ev: MotionEvent): Boolean {
        return false
    }


//    @SuppressLint("InlinedApi")
//    private fun isDpadKey(keyCode: Int): Boolean {
//        return keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_UP_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_DOWN_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_CENTER
//    }

    init {
        if (isInEditMode) {
            videoSurfaceView = null

        } else {
            val playerLayoutId = R.layout.exo_simple_player_view
            LayoutInflater.from(context).inflate(playerLayoutId, this)
            descendantFocusability = FOCUS_AFTER_DESCENDANTS

            // Content frame.
            videoSurfaceView = findViewById(R.id.surface_view)
            initPlayer()
        }
    }

    private fun initPlayer() {
        reset()

        /*Setup player + Adding Cache Directory*/
        val player = SimpleExoPlayer.Builder(context).build()
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.volume = (1f).takeIf { isMuted.value==false }?:(0f)
        player.addListener(
            object : Player.EventListener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    print("playbackState== $playbackState")
                    if(playbackState == Player.STATE_READY){
                        alpha = 1f
                    }

                }
            }
        )
        setPlayer(player)
    }

    /**
     * This will resuse the player and will play new URI we have provided
     */
    fun startPlaying() {
        if(videoUri==null)return
        val mediaItem = MediaItem.fromUri(videoUri!!)
        player?.setMediaItem(mediaItem)
        player?.seekTo(lastPos!!)
        player?.prepare()
        player?.play()
    }

    /**
     * This will stop the player, but stopping the player shows blackscreen
     * so to cover that we set alpha to 0 of player
     * and lastFrame of player using imageView over player to make it look like paused player
     *
     * (If we will not stop the player, only pause , then it can cause memory issue due to overload of player
     * and paused player can not be payed with new URL, after stopping the player we can reuse that with new URL
     *
     */
    fun removePlayer() {
        getPlayer()?.playWhenReady = false
        lastPos = getPlayer()?.currentPosition
        reset()
        getPlayer()?.stop()

    }

    fun reset() {
        // This will prevent surface view to show black screen,
        // and we will make it visible when it will be loaded
        alpha = 0f
    }

    fun setVideoUri(uri: Uri?) {
        this.videoUri = uri
    }
}