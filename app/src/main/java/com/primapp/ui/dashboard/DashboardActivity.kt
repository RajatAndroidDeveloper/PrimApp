package com.primapp.ui.dashboard

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.primapp.BuildConfig
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.chat.ConnectionManager
import com.primapp.extensions.loadCircularImage
import com.primapp.fcm.MyFirebaseMessagingService
import com.primapp.retrofit.ApiConstant
import com.primapp.ui.MainActivity
import com.primapp.ui.base.BaseActivity
import com.primapp.ui.post.UpdatesFragment
import com.primapp.ui.profile.UserPostsFragment
import com.primapp.utils.AnalyticsManager
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.CommunitiesViewModel
import com.sendbird.android.SendbirdChat
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.ConnectHandler
import com.sendbird.android.handler.DisconnectHandler
import com.sendbird.android.handler.PushRequestCompleteHandler
import com.sendbird.android.params.UserUpdateParams
import com.sendbird.android.push.SendbirdPushHelper
import kotlinx.android.synthetic.main.activity_dashboard.bottomNavigationView
import kotlinx.android.synthetic.main.activity_dashboard.drawerLayout
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.ivClose
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.ivProfileImage
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llAboutUs
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llAnnouncements
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llBookmarks
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llDashboard
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llDeleteAccount
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llHiddenPosts
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llLogout
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llMessanger
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llPortFolio
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llProfile
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llProjectContracts
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llRewards
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llSecurity
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llSupport
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.llTerms
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.tvAppVersion
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.tvUserId
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.tvUserName
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.lang.Exception
import javax.inject.Inject


class DashboardActivity : BaseActivity() {

    val userData by lazy { UserCache.getUser(this) }

    override fun showTitleBar(): Boolean = false

    private lateinit var navController: NavController
    private lateinit var navBar: BottomNavigationView

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    var bottomNavLabels: ArrayList<String> = arrayListOf(
        "UpdatesFragment",
        "NotificationsFragment",
        "CreatePostFragment",
        "CommunitiesFragment",
        "ChannelListFragment"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initData()
        setupNavigation()
        setUpUserDataForSideMenu()
        setNavigationHeaderData("${userData?.firstName} ${userData?.lastName}", userData?.userImage)
        if (intent.hasExtra("channelUrl")) {
            Log.d("SendBird_Intent", "Normal Intent")
            openChatForChannel(intent.getStringExtra("channelUrl")!!)
        } else {
            //Do not connect to sendbird if opening from notification
            registerUserOnSendbird()
        }

        webSocketListener = WebSocketListener(viewModel)
        webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)
        viewModel.socketStatus.observe(this) {
            Log.e("Connected12", it.toString())
        }
    }

    private fun clearAllNotifications() {
        UserCache.clearNotificationCache(this)
        val notifManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifManager.cancelAll()
    }

    /* fun cancelNotification(ctx: Context, notifyId: Int) {
         val ns = Context.NOTIFICATION_SERVICE
         val nMgr = ctx.getSystemService(ns) as NotificationManager
         nMgr.cancel(notifyId)
     }*/

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null && intent.hasExtra("channelUrl")) {
            Log.d("SendBird_Intent", "New Intent")
            openChatForChannel(intent.getStringExtra("channelUrl")!!)
        }
    }

    fun openChatForChannel(channelUrl: String) {
        val bundle = Bundle()
        bundle.putString("channelUrl", channelUrl)
        navController.navigate(R.id.chatFragment, bundle)
    }

    private fun registerUserOnSendbird() {
        Log.d(ConnectionManager.TAG, "Connecting user to Sendbird : ${userData?.id}")
        ConnectionManager.login(
            userData!!.id.toString(),
            ConnectHandler { user, sendBirdException ->
                if (sendBirdException != null) {
                    Log.d(
                        ConnectionManager.TAG,
                        "Failed to connect to sendbird : ${sendBirdException.code} ${sendBirdException.cause}"
                    )
                    registerUserOnSendbird()
                    return@ConnectHandler
                }
                Log.d(ConnectionManager.TAG, "Connected to Sendbird")
                UserCache.saveSendBirdIsConnected(this, true)

                //Register for Push
                registerSendbirdForPushNotification()

                // Update the user's nickname
                updateCurrentUserInfo(
                    "${userData?.firstName} ${userData?.lastName}",
                    userData?.userImage
                )
            })
    }

    private fun registerSendbirdForPushNotification() {
        Log.d(ConnectionManager.TAG, "Registering device token to Sendbird")
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(
                    MyFirebaseMessagingService.TAG,
                    "Fetching FCM registration token failed",
                    task.exception
                )
                return@OnCompleteListener
            }

            // Get new Instance ID token
            task.result?.let {
                SendbirdChat.registerPushToken(
                    it
                ) { status, e ->
                    if (e != null) {
                        Log.e(
                            ConnectionManager.TAG,
                            "Failed to register token for push notification from activity"
                        )
                    } else {
                        Log.d(
                            ConnectionManager.TAG,
                            "Successfully registered token for sendbird"
                        )
                    }
                }
            }
        })
    }

    fun updateCurrentUserInfo(name: String, userImage: String?) {
        val profileUrl =
            if (SendbirdChat.currentUser != null) SendbirdChat.currentUser!!.profileUrl else ""

        val params = UserUpdateParams().apply {
            name
            profileUrl
        }

        SendbirdChat.updateCurrentUserInfo(params) {
            if (it != null) {
                Log.d(ConnectionManager.TAG, "Failed to update name to sendbird")
                return@updateCurrentUserInfo
            }
            Log.d(ConnectionManager.TAG, "Updated the Nickname")
        }
        setNavigationHeaderData(name, userImage)
    }

    private fun initData() {
        navController = findNavController(R.id.nav_host_fragment)
        navBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    }

    fun refreshNotificationBadge() {
        val userData = UserCache.getUser(this)
        userData?.let {
            if (it.notificationsCount > 0) {
                val badge: BadgeDrawable = navBar.getOrCreateBadge(R.id.notificationsFragment)
                badge.backgroundColor = ContextCompat.getColor(this, R.color.red)
                badge.badgeTextColor = ContextCompat.getColor(this, R.color.white)
                badge.verticalOffset = 6
                badge.number = it.notificationsCount

            } else {
                navBar.removeBadge(R.id.notificationsFragment)
            }

            if (it.todoNotificationsCount > 0) {
                val badge: BadgeDrawable = navBar.getOrCreateBadge(R.id.todoListFragment)
                badge.backgroundColor = ContextCompat.getColor(this, R.color.red)
                badge.badgeTextColor = ContextCompat.getColor(this, R.color.white)
                badge.verticalOffset = 6
                badge.number = it.todoNotificationsCount
            } else {
                navBar.removeBadge(R.id.todoListFragment)
            }
        }
    }

    fun refreshUnreadMessages(totalUnreadMessageCount: Long) {
        if (totalUnreadMessageCount > 0) {
            val badge: BadgeDrawable = navBar.getOrCreateBadge(R.id.channelListFragment)
            badge.backgroundColor = ContextCompat.getColor(this@DashboardActivity, R.color.red)
            badge.badgeTextColor = ContextCompat.getColor(this@DashboardActivity, R.color.white)
            badge.verticalOffset = 6
            badge.number = totalUnreadMessageCount.toInt()
        } else {
            navBar.removeBadge(R.id.channelListFragment)
        }
    }

    private fun setupNavigation() {
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.getOrCreateBadge(R.id.message)
        // To stop loading fragment again and again
        bottomNavigationView.setOnNavigationItemReselectedListener { }

        var actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()
    }

    override fun onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private val navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        //Hide Show the toolbar here
        if (bottomNavLabels.contains(destination.label)) {
            bottomNavigationView.visibility = View.VISIBLE
        }
        if(destination.label == "ChatFragment") {
            bottomNavigationView.visibility = View.GONE
        }
    }

    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null
    private lateinit var webSocketListener: WebSocketListener
    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(navListener)
        disconnectSocket()

        try{
            val navHostFragment: Fragment? =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            navHostFragment?.childFragmentManager?.fragments?.get(0)

           if(navHostFragment is UpdatesFragment) {
               navHostFragment.activePlayer.volume = 0f
               navHostFragment.releaseExoPlayer()
           }
        } catch (e: Exception) {
          e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()

        try{
            val navHostFragment: Fragment? =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            navHostFragment?.childFragmentManager?.fragments?.get(0)

            if(navHostFragment is UpdatesFragment) {
                navHostFragment.activePlayer.volume = 0f
                navHostFragment.releaseExoPlayer()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        try{
            val navHostFragment: Fragment? =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            navHostFragment?.childFragmentManager?.fragments?.get(0)

            if(navHostFragment is UpdatesFragment) {
                navHostFragment.activePlayer.volume = 0f
                navHostFragment.releaseExoPlayer()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        disconnectSocket()
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(navListener)
        refreshNotificationBadge()
        clearAllNotifications()
        webSocketListener = WebSocketListener(viewModel)
        webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)
    }

    private fun setNavigationHeaderData(name: String, userImage: String?) {
        ivProfileImage.loadCircularImage(this, userImage)
        tvUserName.text = name
        tvUserId.text = "@${userData?.userName}"
    }

    private fun setUpUserDataForSideMenu() {
        tvAppVersion.text = "v${BuildConfig.VERSION_NAME}"

        llPortFolio.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            var user = UserCache.getUser(this)
            val bundle = Bundle()
            bundle.putInt("userId", user?.id ?: 0)
            bundle.putString("title", "${user?.firstName ?: ""} ${user?.lastName ?: ""}")
            if (user?.isPortfolioAvailable != true) {
                DialogUtils.showYesNoDialog(this, R.string.create_mentoring_porfolio, {
                    navController.navigate(R.id.portfolioDashboardFragment, bundle)
                })
            } else {
                navController.navigate(R.id.portfolioDashboardFragment, bundle)
            }
        }

        llProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            navController?.navigate(R.id.profileFragment)
        }

        llMessanger.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            navController?.navigate(R.id.channelListFragment)
        }

        ivClose.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        llTerms.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            openTermsOfConditions()
        }

        llAboutUs.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            openAboutUs()
        }

        llAnnouncements.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            openAnnouncements()
        }

        llRewards.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            openRewards()
        }

        llProjectContracts.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            var bundle = Bundle()
            bundle.putString("from", "dashboard")
            bundle.putString("contractType", "")
            navController.navigate(R.id.allProjectsFragment, bundle)
        }

        llLogout.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            logout()
        }

        llDeleteAccount.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(R.id.accountSettingsFragment)
        }

        llDashboard.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            var bundle = Bundle()
            bundle.putString("from", "dashboard_activity")
            navController.navigate(R.id.dashboardFragment, bundle)
        }

        llSupport.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            openHelpSupport()
        }

        llSecurity.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            openSecurity()
        }

        llBookmarks.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            openBookmarks()
        }

        llHiddenPosts.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            openHiddenPosts()
        }
    }

    fun openAboutUs() {
//        val bundle = Bundle()
//        bundle.putString("title", getString(R.string.about_us))
//        bundle.putString("url", ApiConstant.ABOUT_US)
//        navController.navigate(R.id.commonWebView, bundle)
        openBrowser(ApiConstant.ABOUT_US)
    }

    fun openAnnouncements() {
//        val bundle = Bundle()
//        bundle.putString("title", getString(R.string.about_us))
//        bundle.putString("url", ApiConstant.ABOUT_US)
//        navController.navigate(R.id.commonWebView, bundle)
        openBrowser(ApiConstant.ANNOUNCMENTS)
    }

    fun openTermsOfConditions() {
//        val bundle = Bundle()
//        bundle.putString("title", getString(R.string.terms_of_services))
//        bundle.putString("url", ApiConstant.TERMS_OF_SERVICES)
//        navController.navigate(R.id.commonWebView, bundle)
        openBrowser(ApiConstant.TERMS_OF_SERVICES)
    }

    fun openHelpSupport() {
        navController.navigate(R.id.helpAndSupportFragment)
    }

    fun openSecurity() {
        navController.navigate(R.id.securityFragment)
    }

    fun openBookmarks() {
        val bundle = Bundle()
        bundle.putString("type", UserPostsFragment.BOOKMARK_POST)
        bundle.putInt("userId", UserCache.getUserId(this))
        navController.navigate(R.id.userPostsFragment, bundle)
    }

    fun openHiddenPosts() {
        val bundle = Bundle()
        bundle.putString("type", UserPostsFragment.HIDDEN_POST)
        bundle.putInt("userId", UserCache.getUserId(this))
        navController.navigate(R.id.userPostsFragment, bundle)
    }

    fun openRewards() {
//        val bundle = Bundle()
//        bundle.putString("title", "Prim Rewards")
//        bundle.putString("url", ApiConstant.PRIM_REWARDS)
//        navController.navigate(R.id.commonWebView, bundle)
        openBrowser(ApiConstant.PRIM_REWARDS)
    }

    fun openBrowser(url: String){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    fun logout() {
        DialogUtils.showYesNoDialog(this, R.string.logout_message, {
            showLoading()
            SendbirdPushHelper.unregisterPushHandler(object : PushRequestCompleteHandler {
                override fun onComplete(p0: Boolean, p1: String?) {
                    hideLoading()
                    logoutFromSendBird()
                }

                override fun onError(e: SendbirdException) {
                    hideLoading()
                    Log.d(ConnectionManager.TAG, "Failed to unregister push helper ${e?.code}")
                    logoutFromSendBird()
                }
            })
        })
    }

    fun logoutFromSendBird() {
        showLoading()
        ConnectionManager.logout(DisconnectHandler {
            hideLoading()
            val analyticsData = Bundle()
            analyticsData.putInt("user_id", UserCache.getUserId(this))
            analyticsManager.logEvent(AnalyticsManager.EVENT_LOGOUT, analyticsData)
            UserCache.clearAll(this)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        })
    }

    private fun createRequest(): Request {
        val websocketURL =
            "wss://api.prim-technology.com/ws/online-status/?token=${UserCache.getAccessToken(this)}"

        return Request.Builder()
            .url(websocketURL)
            .build()
    }

    private fun disconnectSocket() {
        viewModel.socketStatus.observe(this) {
            Log.e("Connected12", it.toString())
            if (it) okHttpClient.dispatcher.executorService.shutdown()
        }
    }
}