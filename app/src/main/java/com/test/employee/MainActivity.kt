package com.test.employee

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.test.employee.databinding.ActivityMainBinding
import com.test.employee.github.RepoListActivity

class MainActivity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName

    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setSupportActionBar(_binding.toolbar)
        _binding.toolbar.title = "Halaman Utama"

        _binding.dataKaryawanBtn.setOnClickListener {
//            startActivity(Intent(this, DataKaryawanActivity::class.java))
        }
        _binding.dataDinamisBtn.setOnClickListener {
            startActivity(Intent(this, RepoListActivity::class.java))
        }
    }
}