package com.example.madventuretv.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.madventuretv.R

/**
 * Загрузочный экран. Через 3 секунды открывает экран входа
 * (как в методичке: CountDownTimer + переход на следующую Activity).
 */
class SplashActivity : AppCompatActivity() {

    private var opened = false
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.logo)
        logo.alpha = 0f
        logo.scaleX = 0.82f
        logo.scaleY = 0.82f
        logo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(900L)
            .start()

        timer = object : CountDownTimer(3000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) = Unit

            override fun onFinish() = openNext()
        }
        timer.start()

        // Нажатие OK на пульте пропускает заставку.
        findViewById<android.view.View>(R.id.root).setOnClickListener { openNext() }
    }

    private fun openNext() {
        if (opened) return
        opened = true
        timer.cancel()
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        if (this::timer.isInitialized) timer.cancel()
        super.onDestroy()
    }
}
