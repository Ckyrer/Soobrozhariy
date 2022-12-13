package com.kevedly.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kevedly.test.databinding.ActivityGameOverBinding

class GameOverActivity : AppCompatActivity() {
    lateinit var binding: ActivityGameOverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val name = intent.getStringExtra("player")

        binding.textView2.text = "Победил игрок "+name.toString()

        binding.button3.setOnClickListener {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}