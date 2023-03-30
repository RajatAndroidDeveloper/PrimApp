package com.primapp.model.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.primapp.PrimApp
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    app: Application
) : AndroidViewModel(app) {
    private val context by lazy { getApplication<PrimApp>().applicationContext }


}