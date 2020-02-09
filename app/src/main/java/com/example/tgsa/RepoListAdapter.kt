package com.example.tgsa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.repo_list_item.view.*

class RepoListAdapter(private val repos: ArrayList<RepoData>, private val clickListener: (RepoData) -> Unit) :
    RecyclerView.Adapter<RepoListAdapter.RepoListViewHolder>() {

    class RepoListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(repo: RepoData, clickListener: (RepoData) -> Unit) {
            itemView.textView_repo_list_item.text = repo.fullName
            itemView.setOnClickListener { clickListener(repo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :
            RepoListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.repo_list_item, parent, false)
        return RepoListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepoListViewHolder, position: Int) {
        holder.bind(repos[position], clickListener)
    }

    override fun getItemCount() = repos.size
}
