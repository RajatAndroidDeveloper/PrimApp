package com.primapp.ui.dashboard

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
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
import com.primapp.ui.profile.UserPostsFragment
import com.primapp.utils.AnalyticsManager
import com.primapp.utils.DialogUtils
import com.primapp.utils.NetworkConnectionHelper
import com.sendbird.android.SendBird
import com.sendbird.android.SendBirdException
import com.sendbird.android.SendBirdPushHelper
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.custom_navigation_drawer_layout.*
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
        "CommunitiesFragment",
        "TodoListFragment",
        "ProfileFragment"
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
    }

    private fun clearAllNotifications() {
        UserCache.clearNotificationCache(this)
        val notifManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
            SendBird.ConnectHandler { user, sendBirdException ->
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
                updateCurrentUserInfo("${userData?.firstName} ${userData?.lastName}", userData?.userImage)
            })
    }

    private fun registerSendbirdForPushNotification() {
        Log.d(ConnectionManager.TAG, "Registering device token to Sendbird")
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(MyFirebaseMessagingService.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new Instance ID token
            task.result?.let {
                SendBird.registerPushTokenForCurrentUser(
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
        val profileUrl = if (SendBird.getCurrentUser() != null) SendBird.getCurrentUser().profileUrl else ""
        SendBird.updateCurrentUserInfo(name, profileUrl, SendBird.UserInfoUpdateHandler {
            if (it != null) {
                Log.d(ConnectionManager.TAG, "Failed to update name to sendbird")
                return@UserInfoUpdateHandler
            }
            Log.d(ConnectionManager.TAG, "Updated the Nickname")
        })
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
                badge.badgeTextColor = ContextCompat.getColor(this,R.color.white)
                badge.verticalOffset = 6
                badge.number = it.notificationsCount

            } else {
                navBar.removeBadge(R.id.notificationsFragment)
            }

            if (it.todoNotificationsCount > 0) {
                val badge: BadgeDrawable = navBar.getOrCreateBadge(R.id.todoListFragment)
                badge.backgroundColor = ContextCompat.getColor(this, R.color.red)
                badge.badgeTextColor = ContextCompat.getColor(this,R.color.white)
                badge.verticalOffset = 6
                badge.number = it.todoNotificationsCount
            } else {
                navBar.removeBadge(R.id.todoListFragment)
            }
        }
    }

    private fun setupNavigation() {
        bottomNavigationView.setupWithNavController(navController)
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
        } else {
            bottomNavigationView.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(navListener)
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(navListener)
        refreshNotificationBadge()
        clearAllNotifications()
    }

    private fun setNavigationHeaderData(name:String, userImage: String?){
        ivProfileImage.loadCircularImage(this,userImage)
        tvUserName.text = name
        tvUserId.text = "@${userData?.userName}"
    }
    private fun setUpUserDataForSideMenu(){
        tvAppVersion.text = "v${BuildConfig.VERSION_NAME}"

        ivClose.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        llAboutUs.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            openAboutUs()
        }

        llRewards.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            openRewards()
        }

        llProjectContracts.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            var bundle = Bundle()
            bundle.putString("from","dashboard")
            bundle.putString("contractType","")
            navController.navigate(R.id.allProjectsFragment, bundle)
        }

        llLogout.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            logout()
        }

        llDashboard.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            var bundle = Bundle()
            bundle.putString("from","dashboard_activity")
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
        val bundle = Bundle()
        bundle.putString("title", getString(R.string.about_us))
        bundle.putString("url", ApiConstant.ABOUT_US)
        navController.navigate(R.id.commonWebView, bundle)
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
        val bundle = Bundle()
        bundle.putString("title", "Prim Rewards")
        bundle.putString("url", ApiConstant.PRIM_REWARDS)
        navController.navigate(R.id.commonWebView, bundle)
    }

    fun logout() {
        DialogUtils.showYesNoDialog(this, R.string.logout_message, {
            showLoading()
            SendBirdPushHelper.unregisterPushHandler(object : SendBirdPushHelper.OnPushRequestCompleteListener {
                override fun onComplete(p0: Boolean, p1: String?) {
                    hideLoading()
                    logoutFromSendBird()
                }

                override fun onError(p0: SendBirdException?) {
                    hideLoading()
                    Log.d(ConnectionManager.TAG, "Failed to unregister push helper ${p0?.code}")
                    logoutFromSendBird()
                }
            })


        })
    }

    fun logoutFromSendBird() {
        showLoading()
        ConnectionManager.logout(SendBird.DisconnectHandler {
            hideLoading()
            val analyticsData = Bundle()
            analyticsData.putInt("user_id", UserCache.getUserId(this))
            analyticsManager.logEvent(AnalyticsManager.EVENT_LOGOUT, analyticsData)
            UserCache.clearAll(this)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        })
    }
}