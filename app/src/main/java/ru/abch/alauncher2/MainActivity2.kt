package ru.abch.alauncher2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class MainActivity2 : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
    lateinit var  viewPager :ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
//        supportFragmentManager.beginTransaction().add(R.id.fl_main2, AppsFragment.newInstance(), "Apps").commit()
        viewPager = findViewById(R.id.main_pager)
        viewPager.adapter = FragmentsAdapter(this)
    }
}