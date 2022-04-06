package com.rein.android.appfortests

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rein.android.appfortests.databinding.ActivityMainBinding
import com.rein.android.appfortests.fragments.StartFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }



        if (savedInstanceState == null) {
            val fragment = StartFragment.newInstance()

            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }
    }
}