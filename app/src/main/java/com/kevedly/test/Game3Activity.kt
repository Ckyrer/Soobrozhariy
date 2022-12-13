package com.kevedly.test

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.kevedly.test.databinding.ActivityGame3Binding
import org.json.JSONArray

class Game3Activity : AppCompatActivity() {

    lateinit var binding: ActivityGame3Binding
    var speaker: Boolean = false
    lateinit var votebuttons: Array<Button>

    fun message(msg: String) {
        val t = Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT)
        t.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGame3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        fun showVoteButtons(exc: Int) {
            for (i in (0 until exc) + (exc+1 .. 2)) {
                votebuttons[i].isVisible = true
            }
            for (i in (3 until exc+3) + (exc+4 .. 5)) {
                votebuttons[i].isVisible = true
            }
        }

        fun hideVoteButtons() {
            for (i in 0..5) {
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
            speaker = false
            hideVoteButtons()
        }

        val themes: MutableList<String> = readJson("themes.json")
        val letters: MutableList<String> = mutableListOf("а", "б", "в", "г", "д", "е", "ё", "ж", "з", "и", "й", "к", "л", "м", "н", "о", "п", "р", "с", "т", "у", "ф", "х", "ц", "ч", "ш", "щ", "э", "ю", "я")
        val playerscore: MutableList<Int> = mutableListOf(0, 0, 0)
        val playerbuttons: Array<Button> = arrayOf(binding.player1, binding.player2, binding.player3)
        val playerlabels: Array<TextView> = arrayOf(binding.player1Score, binding.player2Score, binding.player3Score)
        votebuttons = arrayOf(binding.p1Yes, binding.p2Yes, binding.p3Yes, binding.p1No, binding.p2No, binding.p3No)
        var votes: Int = 0
        var votesSum: Int = 0
        var markedPlayer: Int = 0

        letters.shuffle()
        themes.shuffle()

        // show theme
        binding.theme.setOnClickListener {
            if (themes.isNotEmpty()) {
                binding.theme.text = themes[0]
                themes.removeFirst()
                binding.theme.isEnabled = false
                binding.letter.isEnabled = true
            } else {
                println("Конец игры!")
            }
        }

        // show letter
        binding.letter.setOnClickListener {
            binding.letter.text = letters[0]
            letters.add(letters[0])
            letters.removeFirst()
            binding.letter.isEnabled = false
            binding.player1.isEnabled = true
            binding.player2.isEnabled = true
            binding.player3.isEnabled = true
        }

        // exit
        binding.exit.setOnClickListener {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }

        fun bindVoteButton(button: Button, agree: Boolean, number: Int) {
            button.setOnClickListener {
                it.isEnabled = false
                if (number < 3) {votebuttons[number+3].isEnabled = false}
                else {votebuttons[number-3].isEnabled = false}
                if (agree) { votesSum ++ } else { votesSum -- }
                if (++votes==2) {
                    hideVoteButtons()
                    for (i in 0..5) {votebuttons[i].isEnabled = true}
                    if (votesSum == 2) {
                        playerscore[markedPlayer] += 1
                        playerlabels[markedPlayer].text = playerscore[markedPlayer].toString()
                        speaker = false
                        binding.input.text.clear()
                        binding.input.isEnabled = false
                        message("Большинство проголосовало \"да\"")
                        clear()
                    } else {
                        for (i in 0..2) {
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
                    for (i in (0 until b) + (b+1 .. 2)) {
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
                    }
                }
            }
        }

        for (i in 0..2) {
            bindPlayerButton(i)
        }

        for (i in 0..2) {
            bindVoteButton(votebuttons[i], true, i)
        }

        for (i in 3..5) {
            bindVoteButton(votebuttons[i], false, i)
        }

    }

}
