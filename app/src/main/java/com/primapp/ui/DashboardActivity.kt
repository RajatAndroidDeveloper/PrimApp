package com.primapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : BaseActivity() {

    override fun showTitleBar(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        getData()
    }

    private fun getData() {
        val user = UserCache.getUser(this)
        user?.apply {
            tvJunk.text =
                "Name : ${firstName} ${lastName} \nEmail : ${email} \nUsername : ${userName}"
        }

        btnLogout.setOnClickListener {
            UserCache.clearAll(this)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}