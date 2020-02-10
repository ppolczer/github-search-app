package com.example.tgsa

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GithubReposFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var textViewPages: TextView
    lateinit var buttonPrev: Button
    lateinit var buttonNext: Button

    private lateinit var editTextSearch: EditText
    private lateinit var searchTerm: String

    private var page = 1
    private var perPage = 10

    var maxPages = 1

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

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_repos).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
        }

        buttonPrev = view.findViewById(R.id.button_prev)
        buttonPrev.setOnClickListener {
            if (page > 1){
                page--
            }
            searchGithubRepos()
        }

        buttonNext = view.findViewById(R.id.button_next)
        buttonNext.setOnClickListener {
            if(page < maxPages){
                page++
            }
            searchGithubRepos()
        }

        editTextSearch = view.findViewById(R.id.edittext_search)
        editTextSearch.setOnKeyListener(View.OnKeyListener { v, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN) {
                Utils.hideKeyboard(this.context, v)
                page = 1
                searchTerm = editTextSearch.text.toString()
                if (searchTerm.isNotBlank()){
                    searchGithubRepos()
                    return@OnKeyListener true
                }
            }
            false
        })

        textViewPages = view.findViewById<TextView>(R.id.textview_pages)
    }

    private fun searchGithubRepos() {
        val hugo = SearchGithubReposTask(this)
        hugo.execute(searchTerm, page.toString(), perPage.toString())
    }
}
