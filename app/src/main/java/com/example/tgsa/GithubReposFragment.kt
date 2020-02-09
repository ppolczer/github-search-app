package com.example.tgsa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GithubReposFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_github_repos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Inflate the layout for this fragment
        viewManager = LinearLayoutManager(this.context)
        val repos = ArrayList<RepoData>()
        repos.add(RepoData("hugo", "gohugoio/hugo", "https://github.com/gohugoio/hugo"))
        repos.add(RepoData("hugo", "JakeWharton/hugo", "https://github.com/JakeWharton/hugo"))
        repos.add(RepoData("hugo-academic", "gcushen/hugo-academic", "https://github.com/gcushen/hugo-academic"))

        viewAdapter = RepoListAdapter(repos, { partItem: RepoData -> partItemClicked(partItem) })

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_repos).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun partItemClicked(partItem: RepoData) {
        Toast.makeText(this.context, "Clicked on ${partItem.fullName}", Toast.LENGTH_SHORT).show()
    }
}
