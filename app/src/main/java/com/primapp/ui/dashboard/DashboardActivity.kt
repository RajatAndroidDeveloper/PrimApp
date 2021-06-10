package com.primapp.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.chat.ConnectionManager
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
        registerUserOnSendbird()
    }

    private fun registerUserOnSendbird() {
        Log.d(ConnectionManager.TAG,"Connecting user to Sendbird : ${userData?.id}")
        ConnectionManager.login(
            userData!!.id.toString(),
            SendBird.ConnectHandler { user, sendBirdException ->
                if (sendBirdException != null) {
                    Log.d(ConnectionManager.TAG,"Failed to connect to sendbird : ${sendBirdException.code} ${sendBirdException.cause}")
                    return@ConnectHandler
                }
                Log.d(ConnectionManager.TAG,"Connected to Sendbird")
                UserCache.saveSendBirdIsConnected(this, true)

                // Update the user's nickname
                updateCurrentUserInfo("${userData?.firstName} ${userData?.lastName}")
            })
    }

    private fun updateCurrentUserInfo(name: String) {
       SendBird.updateCurrentUserInfo(name,null, SendBird.UserInfoUpdateHandler {
           if(it != null){
               Log.d(ConnectionManager.TAG,"Failed to update name to sendbird")
           }
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
    }

}