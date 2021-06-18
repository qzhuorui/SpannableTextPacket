package com.choryan.spannabletextpacket.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.choryan.spannabletextpacket.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initClick()
    }

    private fun initClick() {
        btn_add_effect.setOnClickListener {

        }
    }
}