package com.primapp.ui.post

import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.CommunityStatusTypes
import com.primapp.constants.PostFileType
import com.primapp.databinding.FragmentUpdatesBinding
import com.primapp.extensions.showError
import com.primapp.model.BookmarkPost
import com.primapp.model.CommentPost
import com.primapp.model.DeletePost
import com.primapp.model.DownloadFile
import com.primapp.model.EditPost
import com.primapp.model.HidePost
import com.primapp.model.LikePost
import com.primapp.model.LikePostMembers
import com.primapp.model.LoadWebUrl
import com.primapp.model.MuteVideo
import com.primapp.model.ReportPost
import com.primapp.model.SharePost
import com.primapp.model.ShowImage
import com.primapp.model.ShowUserProfile
import com.primapp.model.ShowVideo
import com.primapp.model.mentormentee.ResultsItem
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.communities.members.CommunityMembersFragment
import com.primapp.ui.dashboard.DashboardActivity
import com.primapp.ui.dashboard.WebSocketListener
import com.primapp.ui.post.adapter.PostListPagedAdapter
import com.primapp.ui.post.create.CreatePostFragment
import com.primapp.utils.AnalyticsManager
import com.primapp.utils.DialogUtils
import com.primapp.utils.DownloadUtils
import com.primapp.utils.checkIsNetworkConnected
import com.primapp.utils.getPrettyNumber
import com.primapp.utils.visible
import com.primapp.viewmodels.CommunitiesViewModel
import com.primapp.viewmodels.PostsViewModel
import com.sendbird.android.SendbirdChat
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.UserEventHandler
import com.sendbird.android.user.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_dashboard.drawerLayout
import kotlinx.android.synthetic.main.fragment_updates.rvCommunityPosts
import kotlinx.android.synthetic.main.item_list_post.ivComment
import kotlinx.android.synthetic.main.item_list_post.ivPostPreview
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.ivEndIcon
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.ivMenu
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.tvCount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import javax.inject.Inject


class UpdatesFragment : BaseFragment<FragmentUpdatesBinding>() {

    val UNIQUE_HANDLER_ID = "UserHandler_1"

    val userData by lazy { UserCache.getUser(requireContext()) }

    val adapter by lazy { PostListPagedAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<PostsViewModel> { viewModelFactory }

    val mViewModel by viewModels<CommunitiesViewModel> { viewModelFactory }
    private lateinit var webSocketListener: WebSocketListener
    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    @Inject
    lateinit var downloadManager: DownloadManager
    val activityScope = CoroutineScope(Dispatchers.Main)
    val communityViewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_updates

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setData()
        setAdapter()
        webSocketListener = WebSocketListener(mViewModel)

        if (checkIsNetworkConnected(requireContext())) {
            setObserver()
        } else {
            findNavController().navigate(R.id.networkErrorFragment)
        }
        activityScope.launch {
            delay(1500)
            findOnlineOfflineStatus()
        }
    }

    private fun findOnlineOfflineStatus() {
        webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)

        mViewModel.messages.observe(requireActivity()) {
            var mainMentorsMenteeList = ArrayList<ResultsItem>()
            mainMentorsMenteeList.clear()
            mainMentorsMenteeList = Gson().fromJson<ArrayList<ResultsItem>>(it.second, object :
                TypeToken<ArrayList<ResultsItem>>() {}.type)
            mergeListAndAdapterData(mainMentorsMenteeList)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun mergeListAndAdapterData(mainMentorsMenteeList: java.util.ArrayList<ResultsItem>) {
        var data = adapter.snapshot().items
        for (i in mainMentorsMenteeList.indices) {
            for (j in 0 until (data.size)) {
                if (mainMentorsMenteeList[i].id == data[j].user.id) {
                    data[j].user.userOnlineStatus = mainMentorsMenteeList[i].userOnlineStatus
                }
            }
        }
        lifecycleScope.launch {
            adapter.submitData(PagingData.from(data))
        }
    }

    private fun setData() {
        analyticsManager.trackScreenView(AnalyticsManager.SCREEN_UPDATES)
        binding.frag = this
        binding.userData = userData

        binding.groupNoPostView.isVisible = userData!!.joinedCommunityCount == 0
        binding.groupNoCommunityView.isVisible = userData!!.joinedCommunityCount > 0

        ivMenu.visible(true)
        //Toolbar
        checkUnreadMessages()
        ivEndIcon.visibility = View.GONE
        ivEndIcon.setOnClickListener {
            findNavController().navigate(R.id.channelListFragment)
        }
        ivMenu.setOnClickListener {
            (requireActivity() as DashboardActivity).drawerLayout.openDrawer(GravityCompat.START)
        }
    }


    private fun getPostData() {
        viewModel.getPostsList().observe(viewLifecycleOwner, Observer {
            it?.let {
                lifecycleScope.launch {
                    try {
                        adapter.submitData(it)
                        communityViewModel.getUserData(UserCache.getUserId(requireContext()))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun setObserver() {
        getPostData()

        viewModel.likePostLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.LOADING -> {
                        showLoading()
                    }

                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }

                    Status.SUCCESS -> {
                        it.data?.content?.let {
                            adapter.markPostAsLiked(it.postId)
                        }
                    }
                }
            }
        })

        viewModel.unlikePostLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.LOADING -> {
                        showLoading()
                    }

                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }

                    Status.SUCCESS -> {
                        it.data?.content?.let {
                            adapter.markPostAsDisliked(it.postId)
                        }
                    }
                }
            }
        })

        viewModel.deletePostLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.LOADING -> {
                        showLoading()
                    }

                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }

                    Status.SUCCESS -> {
                        it.data?.content?.let {
                            UserCache.decrementPostCount(requireContext())
                            adapter.removePost(it.postId)
                        }
                    }
                }
            }
        })

        viewModel.bookmarkPostLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.LOADING -> {
                        showLoading()
                    }

                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }

                    Status.SUCCESS -> {
                        it.data?.content?.let {
                            adapter.addPostToBookmark(it.postId)
                        }
                    }
                }
            }
        })

        viewModel.removeBookmarkLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.LOADING -> {
                        showLoading()
                    }

                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }

                    Status.SUCCESS -> {
                        it.data?.content?.let {
                            adapter.removePostAsBookmarked(it.postId)
                        }
                    }
                }
            }
        })

        viewModel.hidePostLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            it.getContentIfNotHandled()?.let {
                when (it.status) {
                    Status.LOADING -> {
                        showLoading()
                    }

                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }

                    Status.SUCCESS -> {
                        it.data?.content?.let {
                            adapter.removePost(it.postId)
                        }
                    }
                }
            }
        })

        communityViewModel.userLiveData.observe(viewLifecycleOwner, Observer {
            it.peekContent().let { it ->
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }

                    Status.LOADING -> {
                        //showLoading()
                    }

                    Status.SUCCESS -> {
                        //hideLoading()
                        it.data?.content?.let {
                            if (UserCache.getUserId(requireContext()) == it.id) {
                                //Update user data if viewing own profile
                                UserCache.saveUser(requireContext(), it)
                                (activity as? DashboardActivity)?.refreshNotificationBadge()
                                binding.userData = UserCache.getUser(requireContext())
                            }
                        }
                    }
                }
            }
        })
    }

    fun releaseExoPlayer(){
        activePlayer.release()
    }

    lateinit var activeImageView: ImageView
    lateinit var activePlayer: SimpleExoPlayer
    private fun setAdapter() {
        mLayoutManager = LinearLayoutManager(requireContext())
        binding.rvCommunityPosts.apply {
            layoutManager = mLayoutManager
        }

        binding.tvScrollUp.setOnClickListener {
            rvCommunityPosts.smoothScrollToPosition(0)
        }

        binding.rvCommunityPosts.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommunityPagedLoadStateAdapter { adapter.retry() },
            footer = CommunityPagedLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                binding.swipeRefresh.isRefreshing = false
                binding.pbPost.isVisible = false

                val error = when {
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error

                    else -> null
                }
                error?.let {
                    if (adapter.snapshot().isEmpty()) {
                        showError(requireContext(), it.error.localizedMessage)
                    }
                }

            } else {
                binding.swipeRefresh.isRefreshing = false
                if (!binding.swipeRefresh.isRefreshing && userData!!.joinedCommunityCount != 0 && adapter.itemCount < 1) {
                    binding.pbPost.isVisible = true
                }
            }
        }

        setUpRecyclerScrollView()
    }

    private fun setUpRecyclerScrollView() {
        binding.rvCommunityPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var activeAdapter = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy);

                // Get the index of the first Completely visible item
                var firstCompletelyVisibleItemPosition =
                    mLayoutManager.findFirstCompletelyVisibleItemPosition()
                Log.d(
                    "Update_fragment_video_play",
                    "onScrolled: firstCompletelyVisibleItemPosition : " + firstCompletelyVisibleItemPosition
                );

                // Even if we scroll by a few millimeters the video will start playing from the beginning
                // So we need to check if the new Active row layout position is equal to the current active row layout position

                if (activeAdapter != firstCompletelyVisibleItemPosition) {
                    try {
                        val video_url: String =
                            adapter.getPostUrl(firstCompletelyVisibleItemPosition)
                        val video_view =
                            mLayoutManager.findViewByPosition(firstCompletelyVisibleItemPosition)!!
                                .findViewById<View>(R.id.videoView) as PlayerView
                        val iv_cover =
                            mLayoutManager.findViewByPosition(firstCompletelyVisibleItemPosition)!!
                                .findViewById<View>(R.id.ivPostPreview) as ImageView
                        val ivMuted =
                            mLayoutManager.findViewByPosition(firstCompletelyVisibleItemPosition)!!
                                .findViewById<View>(R.id.ivMuteVideo) as ImageView

                        // Start playing the video in Active row layout
                        if (adapter.getPostType(firstCompletelyVisibleItemPosition) == PostFileType.VIDEO) {
                            video_view.visibility = View.VISIBLE
                            ivPostPreview.visibility = View.GONE
                            val player = SimpleExoPlayer.Builder(context!!).build()
                            video_view.player = player
                            val mediaItem: MediaItem = MediaItem.fromUri(video_url)
                            player.addMediaItem(mediaItem)
                            video_view.hideController()
                            player.volume = 0f
                            player.prepare()
                            player.playWhenReady = true
                            activePlayer = player
                            activeImageView = ivMuted
                            // Hide the thumbnail ImageView with a delay of 300 millisecond or else there will be black
                            // screen before a video plays
                            Handler().postDelayed({ iv_cover.visibility = View.INVISIBLE }, 300)
                        }

                        // assign this row layout position as active row Layout
                        activeAdapter = firstCompletelyVisibleItemPosition
                        Log.d(
                            "Update_fragment_video_play",
                            "onScrolled: activeAdapter : $activeAdapter"
                        )
                    } catch (e: java.lang.NullPointerException) {
                        // Sometimes you scroll so fast that the views are not attached so it gives a NullPointerException
                    } catch (e: ArrayIndexOutOfBoundsException) {
                    }

                    /* Get the Video Surface directly above the fully visible Row Layout so that you may stop the playback
                  when a new row Layout is fully visible
                  */
                    /* Get the Video Surface directly above the fully visible Row Layout so that you may stop the playback
                  when a new row Layout is fully visible
                  */if (firstCompletelyVisibleItemPosition >= 1) {
                        try {
                            val video_view_above =
                                mLayoutManager.findViewByPosition(firstCompletelyVisibleItemPosition - 1)!!
                                    .findViewById<View>(R.id.videoView) as PlayerView

                            var player = video_view_above.player
                            player!!.release()

                            val thumbnail_string: String =
                                adapter.getPostThumbnailUrl(firstCompletelyVisibleItemPosition - 1)
                            //  video_view_above.start(Uri.parse(thumbnail_string));
                            val iv_cover_above =
                                mLayoutManager.findViewByPosition(firstCompletelyVisibleItemPosition - 1)!!
                                    .findViewById<View>(R.id.ivPostPreview) as ImageView

                            // Show the cover Thumbnail ImageView
                            iv_cover_above.visibility = View.VISIBLE
                            Picasso.with(requireActivity())
                                .load(Uri.parse(thumbnail_string))
                                .into(iv_cover_above)

                            // video_view_above.setBackground(Uri.parse(thumbnail_string));
                        } catch (e: java.lang.NullPointerException) {
                        } catch (e: ArrayIndexOutOfBoundsException) {
                        }
                    }

                    /* Get the Video Surface directly Below the fully visible Row Layout so that you may stop the playback
                  when a new row Layout is fully visible
                  */
                    /* Get the Video Surface directly Below the fully visible Row Layout so that you may stop the playback
                  when a new row Layout is fully visible
                  */if (firstCompletelyVisibleItemPosition + 1 < adapter.snapshot().size) {
                        try {
                            val video_view_below =
                                mLayoutManager.findViewByPosition(firstCompletelyVisibleItemPosition + 1)!!
                                    .findViewById<View>(R.id.videoView) as PlayerView

                            var player = video_view_below.player
                            player!!.release()

                            val thumbnail_string: String =
                                adapter.getPostUrl(firstCompletelyVisibleItemPosition + 1)
                            //  video_view_below.start(Uri.parse(thumbnail_string));
                            val iv_cover_below =
                                mLayoutManager.findViewByPosition(firstCompletelyVisibleItemPosition + 1)!!
                                    .findViewById<View>(R.id.ivPostPreview) as ImageView

                            // Show the cover Thumbnail ImageView
                            iv_cover_below.visibility = View.VISIBLE
                            Picasso.with(requireActivity())
                                .load(Uri.parse(thumbnail_string))
                                .into(iv_cover_below)

                        } catch (e: java.lang.NullPointerException) {
                        } catch (e: ArrayIndexOutOfBoundsException) {
                        }
                    }

                }

                if (activeAdapter == 0 && adapter.getPostType(0) == PostFileType.VIDEO) {
                    try {
                        val video_url: String =
                            adapter.getPostUrl(0)
                        val video_view =
                            mLayoutManager.findViewByPosition(0)!!
                                .findViewById<View>(R.id.videoView) as PlayerView
                        val iv_cover =
                            mLayoutManager.findViewByPosition(0)!!
                                .findViewById<View>(R.id.ivPostPreview) as ImageView
                        val ivMuted =
                            mLayoutManager.findViewByPosition(0)!!
                                .findViewById<View>(R.id.ivMuteVideo) as ImageView

                        try {
                            video_view.visibility = View.VISIBLE
                            iv_cover.visibility = View.GONE
                            val player = SimpleExoPlayer.Builder(context!!).build()
                            video_view.player = player
                            val mediaItem: MediaItem = MediaItem.fromUri(video_url)
                            player.addMediaItem(mediaItem)
                            video_view.hideController()
                            player.prepare()
                            player.volume = 0f
                            player.playWhenReady = true
                            activePlayer = player
                            activeImageView = ivMuted
                            // Hide the thumbnail ImageView with a delay of 300 millisecond or else there will be black
                            // screen before a video plays
                            Handler().postDelayed({ iv_cover.visibility = View.INVISIBLE }, 300)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    } catch (e: java.lang.NullPointerException) {
                        // Sometimes you scroll so fast that the views are not attached so it gives a NullPointerException
                    } catch (e: ArrayIndexOutOfBoundsException) {
                    }
                }

            }
        })
    }

    fun refreshData() {
        if (checkIsNetworkConnected(requireContext())) {
            getPostData()
        } else
            findNavController().navigate(R.id.networkErrorFragment)
    }

    fun createPost() {
        val action = UpdatesFragmentDirections.actionUpdatesFragmentToCreatePostFragment(null)
        findNavController().navigate(action)
    }

    private fun onItemClick(item: Any?) {
        when (item) {
            is MuteVideo -> {
                try {
                    if (activePlayer.volume == 0f) {
                        activeImageView.setImageResource(R.drawable.ic_unmute_video)
                        activePlayer.volume = 1f
                    } else {
                        activeImageView.setImageResource(R.drawable.ic_mute_vide)
                        activePlayer.volume = 0f
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            is ShowImage -> {
                val bundle = Bundle()
                bundle.putString("url", item.url)
                findNavController().navigate(R.id.imageViewDialog, bundle)
            }

            is ShowVideo -> {
                val bundle = Bundle()
                bundle.putString("url", item.url)
                findNavController().navigate(R.id.videoViewDialog, bundle)
            }

            is DownloadFile -> {
                DownloadUtils.download(requireContext(), downloadManager, item.url)
            }

            is LikePost -> {
                if (item.postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.inactive_community_action_message,
                        R.drawable.question_mark
                    )
                    return
                }

                if (item.postData.isLike) {
                    viewModel.unlikePost(
                        item.postData.community.id,
                        userData!!.id,
                        item.postData.id
                    )
                } else {
                    viewModel.likePost(item.postData.community.id, userData!!.id, item.postData.id)
                }
            }

            is BookmarkPost -> {
                if (item.postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.inactive_community_action_message,
                        R.drawable.question_mark
                    )
                    return
                }

                if (item.postData.isBookmark) {
                    viewModel.removeBookmark(
                        item.postData.community.id,
                        userData!!.id,
                        item.postData.id
                    )
                } else {
                    viewModel.bookmarkPost(
                        item.postData.community.id,
                        userData!!.id,
                        item.postData.id
                    )
                }
            }

            is EditPost -> {
                val bundle = Bundle()
                bundle.putString("type", CreatePostFragment.UPDATE_POST)
                bundle.putSerializable("postData", item.postData)
                findNavController().navigate(R.id.createPostFragment, bundle)
            }

            is ReportPost -> {
                val bundle = Bundle()
                bundle.putSerializable("postData", item.postData)
                findNavController().navigate(R.id.popUpReportPost, bundle)
            }

            is HidePost -> {
                DialogUtils.showYesNoDialog(requireActivity(), R.string.hide_post_confirmation, {
                    viewModel.hidePost(item.postData.id)
                })
            }

            is DeletePost -> {
                if (item.postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.inactive_community_action_message,
                        R.drawable.question_mark
                    )
                    return
                }

                DialogUtils.showYesNoDialog(requireActivity(), R.string.delete_post_message, {
                    viewModel.deletePost(
                        item.postData.community.id,
                        userData!!.id,
                        item.postData.id
                    )
                })
            }

            is CommentPost -> {
                val bundle = Bundle()
                bundle.putSerializable("postData", item.postData)
                findNavController().navigate(R.id.postCommentFragment, bundle)
            }

            is LikePostMembers -> {
                val bundle = Bundle()
                bundle.putInt("communityId", item.postData.community.id)
                bundle.putInt("postId", item.postData.id)
                bundle.putString("type", CommunityMembersFragment.POST_LIKE_MEMBERS_LIST)
                findNavController().navigate(R.id.communityMembersFragment, bundle)
            }

            is ShowUserProfile -> {
                val bundle = Bundle()
                bundle.putInt("userId", item.userId)
                findNavController().navigate(R.id.otherUserProfileFragment, bundle)
            }

            is SharePost -> {
                sharePostAsImage(
                    item.view,
                    "${item.postData.user.firstName} ${item.postData.user.lastName}",
                    item.postData.community.communityName
                )
            }

            is LoadWebUrl -> {
                val bundle = Bundle()
                bundle.putString("title", getString(R.string.sensitive_content))
                bundle.putString("url", item.url)
                findNavController().navigate(R.id.commonWebView, bundle)
            }
        }
    }

    private fun checkUnreadMessages() {
        SendbirdChat.getTotalUnreadMessageCount { totalUnreadMessageCount: Int, e: SendbirdException? ->
            if (e != null) {
                return@getTotalUnreadMessageCount
            }
            updateUnreadMessageCount(totalUnreadMessageCount.toLong())
        }
    }

    override fun onResume() {
        super.onResume()
        SendbirdChat.addUserEventHandler(UNIQUE_HANDLER_ID, object : UserEventHandler() {
            override fun onFriendsDiscovered(users: List<User>) {}

            override fun onTotalUnreadMessageCountChanged(
                totalCount: Int,
                totalCountByCustomType: Map<String, Int>
            ) {
                updateUnreadMessageCount(totalCount.toLong())
                (requireActivity() as DashboardActivity).refreshUnreadMessages(totalCount.toLong())
            }
        })
    }

    fun updateUnreadMessageCount(totalUnreadMessageCount: Long) {
        (requireActivity() as DashboardActivity).refreshUnreadMessages(totalUnreadMessageCount)
        CoroutineScope(Dispatchers.Main).launch {
            Log.d("anshul", "textview is null : ${tvCount == null} conext : ${context == null}")
            tvCount?.text = getPrettyNumber(totalUnreadMessageCount)
            tvCount?.isVisible = totalUnreadMessageCount > 0
            tvCount?.visible(false)
        }
    }

    private lateinit var mLayoutManager: LinearLayoutManager
    override fun onPause() {
        super.onPause()
        SendbirdChat.removeUserEventHandler(UNIQUE_HANDLER_ID);

        // We need to pause any playback when the application is minimised
        try {
            val firstCompletelyVisibleItemPosition: Int =
                mLayoutManager.findFirstCompletelyVisibleItemPosition()
            val videoView = mLayoutManager.findViewByPosition(firstCompletelyVisibleItemPosition)
                ?.findViewById<PlayerView>(R.id.videoView)
            var player = videoView!!.player
            player!!.release()

            activePlayer.release()
        } catch (e: NullPointerException) {
            // Sometimes you scroll so fast that the views are not attached so it gives a NullPointerException
        } catch (e: ArrayIndexOutOfBoundsException) {
        }
    }

    override fun onStop() {
        super.onStop()

        try{
            activePlayer.release()
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
    }

    private fun createRequest(): Request {
        val websocketURL = "wss://api.prim-technology.com/ws/online-status/?token=${
            UserCache.getAccessToken(requireContext())
        }"

        return Request.Builder()
            .url(websocketURL)
            .build()
    }
}