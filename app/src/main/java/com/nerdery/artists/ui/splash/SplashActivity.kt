package com.nerdery.artists.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nerdery.artists.R
import com.nerdery.artists.ui.main.MainActivity
import kotlinx.android.synthetic.main.splash_activity.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext


class SplashActivity : AppCompatActivity(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

    }

    override fun onStart() {
        super.onStart()
        launch(Dispatchers.IO) {
            delay(TimeUnit.SECONDS.toMillis(6))
            withContext(Dispatchers.Main) {
                waves_view.endWaves {
                    val loginIntent = Intent(this@SplashActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(loginIntent)
                    overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out)
                }
            }
        }
    }
}