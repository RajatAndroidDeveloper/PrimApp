package com.primapp.ui.dashboard

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.chat.ConnectionManager
import com.primapp.fcm.MyFirebaseMessagingService
import com.primapp.ui.base.BaseActivity
import com.sendbird.android.SendBird
import kotlinx.android.synthetic.main.activity_dashboard.*


class DashboardActivity : BaseActivity() {

    val userData by lazy { UserCache.getUser(this) }

    override fun showTitleBar(): Boolean = false

    private lateinit var navController: NavController
    private lateinit var navBar: BottomNavigationView

    var bottomNavLabels: ArrayList<String> = arrayListOf(
        "UpdatesFragment",
        "NotificationsFragment",
        "CommunitiesFragment",
        "ProfileFragment"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initData()
        setupNavigation()
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
                badge.verticalOffset = 6
                badge.number = it.notificationsCount

            } else {
                navBar.removeBadge(R.id.notificationsFragment)
            }
        }
    }

    private fun setupNavigation() {
        bottomNavigationView.setupWithNavController(navController)
        // To stop loading fragment again and again
        bottomNavigationView.setOnNavigationItemReselectedListener { }
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
}