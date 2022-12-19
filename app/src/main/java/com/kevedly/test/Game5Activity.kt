package com.kevedly.test

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import com.kevedly.test.databinding.ActivityGame3Binding
import com.kevedly.test.databinding.ActivityGame5Binding
import org.json.JSONArray

class Game5Activity : AppCompatActivity() {
    lateinit var binding: ActivityGame5Binding
    var speaker: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGame5Binding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        val votebuttons = arrayOf(binding.p1Yes, binding.p2Yes, binding.p3Yes, binding.p4Yes, binding.p5Yes, binding.p1No, binding.p2No, binding.p3No, binding.p4No, binding.p5No)

        fun message(msg: String) {
            val t = Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT)
            t.show()
        }

        fun showVoteButtons(exc: Int) {
            for (i in (0 until exc) + (exc+1 .. 4)) {
                votebuttons[i].isVisible = true
            }
            for (i in (5 until exc+5) + (exc+6 .. 9)) {
                votebuttons[i].isVisible = true
            }
        }

        fun hideVoteButtons() {
            for (i in 0..9) {
                votebuttons[i].isVisible = false
            }
        }

        // read JSON
        fun readJson(file: String): MutableList<String> {
            val json = JSONArray(
                applicationContext.assets.open(file).bufferedReader().use {
                    it.readText()
                }
            )
            return MutableList<String>(json.length()){json.getString(it)}
        }

        fun clear() {
            binding.input.text.clear()
            binding.input.isEnabled = false
            binding.theme.text = "Открыть категорию"
            binding.letter.text = "Открыть букву"
            binding.theme.isEnabled = true
            binding.player1.isEnabled = false
            binding.player2.isEnabled = false
            binding.player3.isEnabled = false
            binding.player4.isEnabled = false
            binding.player5.isEnabled = false
            speaker = false
            hideVoteButtons()
        }

        val themes: MutableList<String> = readJson("themes.json")
        val letters: MutableList<String> = mutableListOf("а", "б", "в", "г", "д", "е", "ё", "ж", "з", "и", "й", "к", "л", "м", "н", "о", "п", "р", "с", "т", "у", "ф", "х", "ц", "ч", "ш", "щ", "э", "ю", "я")
        val playerscore: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0)
        val playerbuttons: Array<Button> = arrayOf(binding.player1, binding.player2, binding.player3, binding.player4, binding.player5)
        val playerlabels: Array<TextView> = arrayOf(binding.player1Score, binding.player2Score, binding.player3Score, binding.player4Score, binding.player5Score)
        var votes: Int = 0
        var votesSum: Int = 0
        var markedPlayer: Int = 0
        var repeat = false

        letters.shuffle()
        themes.shuffle()

        // show theme
        binding.theme.setOnClickListener {
            if (themes.isNotEmpty()) {
                binding.theme.text = ""
                ObjectAnimator.ofFloat(
                    binding.theme,
                    View.ROTATION_X,
                    0f, 360f
                ).apply {
                    duration = 700
                    LinearInterpolator()
                    addListener( doOnEnd {
                        if (!repeat) {
                            binding.theme.text = themes[0]
                            themes.removeFirst()
                            binding.theme.isEnabled = false
                            binding.letter.isEnabled = true
                            repeat = true
                        } else {
                            repeat = false
                        }
                    })
                    start()
                }
            } else {
                val intent = Intent(this, GameOverActivity::class.java)
                intent.putExtra("player", (playerscore.indexOf(playerscore.max())+1).toString())
                finish()
                startActivity(intent)
            }
        }

        // show letter
        binding.letter.setOnClickListener {
            binding.letter.text = ""
            ObjectAnimator.ofFloat(
                binding.letter,
                View.ROTATION_X,
                0f, 360f
            ).apply {
                duration = 700
                LinearInterpolator()
                addListener(doOnEnd {
                    binding.letter.text = letters[0]
                    letters.add(letters[0])
                    letters.removeFirst()
                    binding.letter.isEnabled = false
                    binding.player1.isEnabled = true
                    binding.player2.isEnabled = true
                    binding.player3.isEnabled = true
                    binding.player4.isEnabled = true
                    binding.player5.isEnabled = true
                })
                start()
            }
        }

        // exit
        binding.exit.setOnClickListener {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }

        fun bindVoteButton(button: Button, agree: Boolean, number: Int) {
            button.setOnClickListener {
                it.isEnabled = false
                if (number < 5) {votebuttons[number+5].isEnabled = false}
                else {votebuttons[number-5].isEnabled = false}
                if (agree) { votesSum ++ }
                if (++votes==4) {
                    hideVoteButtons()
                    for (i in 0..9) {votebuttons[i].isEnabled = true}
                    if (votesSum >= 3) {
                        playerscore[markedPlayer] += 1
                        playerlabels[markedPlayer].text = playerscore[markedPlayer].toString()
                        speaker = false
                        binding.input.text.clear()
                        binding.input.isEnabled = false
                        message("Большинство проголосовало \"да\"")
                        clear()
                    } else {
                        for (i in 0..4) {
                            playerbuttons[i].isEnabled = true
                        }
                        speaker = false
                        binding.input.text.clear()
                        binding.input.isEnabled = false
                        message("Большиенство проглосовало \"нет\"")
                    }
                }
            }
        }

        fun bindPlayerButton(b: Int) {
            playerbuttons[b].setOnClickListener {
                if (!speaker) {
                    for (i in (0 until b) + (b+1 .. 4)) {
                        playerbuttons[i].isEnabled = false
                    }
                    binding.input.isEnabled = true
                    speaker = true
                } else {
                    if (binding.input.text.toString().lowercase().startsWith(binding.letter.text)) {
                        markedPlayer = b
                        votes = 0
                        votesSum = 0
                        playerbuttons[b].isEnabled = false
                        binding.input.isEnabled = false
                        showVoteButtons(b)
                    } else {
                        message("Слово начинается не с той буквы!")
                        speaker = false
                        binding.input.text.clear()
                        binding.input.isEnabled = false
                        binding.player1.isEnabled = true
                        binding.player2.isEnabled = true
                        binding.player3.isEnabled = true
                        binding.player4.isEnabled = true
                        binding.player5.isEnabled = true
                    }
                }
            }
        }

        for (i in 0..4) {
            bindPlayerButton(i)
        }

        for (i in 0..4) {
            bindVoteButton(votebuttons[i], true, i)
        }

        for (i in 5..9) {
            bindVoteButton(votebuttons[i], false, i)
        }

    }

}