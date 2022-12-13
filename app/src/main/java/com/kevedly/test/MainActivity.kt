package com.kevedly.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kevedly.test.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            finish()
            startActivity(Intent(this, Game3Activity::class.java))
        }

        binding.rulesButton.setOnClickListener {
            finish()
            startActivity(Intent(this, RulesActivity::class.java))
        }

        binding.button5.setOnClickListener {
            finish()
        }

    }

}