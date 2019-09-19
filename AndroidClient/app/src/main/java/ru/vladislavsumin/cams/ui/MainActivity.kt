package ru.vladislavsumin.cams.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import ru.vladislavsumin.cams.R
import ru.vladislavsumin.cams.ui.cams.CamsActivity
import ru.vladislavsumin.cams.ui.login.LoginActivity
import ru.vladislavsumin.cams.ui.video.VideoActivity
import ru.vladislavsumin.core.mvp.BaseActivity

class MainActivity : BaseActivity() {
    companion object {
        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_videos.setOnClickListener {
            startActivity(VideoActivity.getLaunchIntent(applicationContext))
        }

        btn_cams.setOnClickListener {
            startActivity(CamsActivity.getLaunchIntent(applicationContext))
        }

        btn_logout.setOnClickListener {
            credentialStorage.serverAddress = null
            startActivity(LoginActivity.getLaunchIntent(this))
            finish()
        }
    }
}
