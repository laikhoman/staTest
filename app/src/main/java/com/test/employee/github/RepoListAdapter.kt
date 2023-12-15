package com.test.employee.github

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.employee.R
import java.util.Date

class RepoListAdapter(
    context: Context
) : RecyclerView.Adapter<RepoListAdapter.ViewHolder>() {

    private val TAG = this::class.java.simpleName

    private var items = mutableListOf<RepoListModel>()
    private var filteredItems = mutableListOf<RepoListModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<RepoListModel>): RepoListAdapter {
        this.items.clear()
        this.items.addAll(items)
        this.filteredItems.clear()
        this.filteredItems.addAll(items)
        this.notifyDataSetChanged()
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.repo_list_item, parent, false)
        )
    }

    override fun getItemCount() = filteredItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(filteredItems[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var tvID: TextView
        private lateinit var tvName: TextView
        private lateinit var tvHtmlUrl: TextView

        @SuppressLint("SetTextI18n") fun bindView(item: RepoListModel) {
            tvID = itemView.findViewById(R.id.id_tv)
            tvName = itemView.findViewById(R.id.name_tv)
            tvHtmlUrl = itemView.findViewById(R.id.html_url_tv)

            tvID.text = item.id
            tvName.text = item.name
            tvHtmlUrl.text = item.html_url
        }
    }
}