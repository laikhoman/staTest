package com.test.employee.github

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.employee.APIClient
import com.test.employee.OnTaskCompleted
import com.test.employee.databinding.RepoListActivityBinding
import org.json.JSONArray

class RepoListActivity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName

    private lateinit var _binding: RepoListActivityBinding

    private var dataDinamisAdapter: RepoListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = RepoListActivityBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setSupportActionBar(_binding.toolbar)
        _binding.toolbar.title = "Github List"
        _binding.toolbar.subtitle = "https://api.github.com//users/google/repos"

        initRecyclerView(this)
        getData(this)
    }

    private fun initRecyclerView(context: Context){
        dataDinamisAdapter = RepoListAdapter(context)
        _binding.recyclerview.layoutManager = LinearLayoutManager(context)
        _binding.recyclerview.adapter = dataDinamisAdapter
    }

    private fun getData(context: Context){
        lifecycleScope.launchWhenCreated {
            APIClient(context)
                .setBaseUrl("https://api.github.com")
                .setListener(object : OnTaskCompleted {
                    override fun onTaskCompleted(command: String, response: String) {
                        Log.d(TAG, "response: $response")
                        try {
                            val jsonArray = JSONArray(response)
                            val items = getRepoListItems(jsonArray)
                            dataDinamisAdapter?.setItems(items)
                        } catch (e: Exception) {
                            Log.e(TAG, "Exception: ${e.message.toString()}")
                        }
                    }
                }).execute("/users/google/repos")
        }
    }

    private fun getRepoListItems(jsonArray: JSONArray): List<RepoListModel> {
        val items = mutableListOf<RepoListModel>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            items.add(RepoListModel(jsonObject))
        }
        return items
    }
}