package com.example.tgsa

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.StringReader

class GithubReposFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var client: OkHttpClient
    private lateinit var klaxon: Klaxon

    private var page = 1
    private var perPage = 10

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

        client = OkHttpClient()
        klaxon = Klaxon()

        val editTextSearch = view.findViewById<EditText>(R.id.edittext_search)
        editTextSearch.setOnKeyListener(View.OnKeyListener { v, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN) {
                Utils.hideKeyboard(this.context, v)

                val hugo = SearchGithubReposTask(this)
                hugo.execute(editTextSearch.text.toString(), page.toString(), perPage.toString())
                return@OnKeyListener true
            }
            false
        })
    }

    private fun repoListItemClicked(repoData: RepoData) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(repoData.htmlUrl)
        startActivity(openURL)
    }

    companion object {
        class SearchGithubReposTask(private val githubReposFragment: GithubReposFragment) : AsyncTask<String, Void, JsonObject>() {
    
            override fun doInBackground(vararg p0: String?): JsonObject {
                val httpUrlBuilder = "https://api.github.com/search/repositories".toHttpUrlOrNull()
                    ?.newBuilder()?.addQueryParameter("q", p0[0])?.addQueryParameter("page", p0[1])?.addQueryParameter("per_page", p0[2])
                val request = Request.Builder().url(httpUrlBuilder!!.build()).build()
                val response = githubReposFragment.client.newCall(request).execute()
                return githubReposFragment.klaxon.parseJsonObject(StringReader(response.body?.string().toString()))
            }
    
            override fun onPostExecute(result: JsonObject?) {
                super.onPostExecute(result)
                val itemsArray = result?.array<Any>("items")
                val items = itemsArray?.let { githubReposFragment.klaxon.parseFromJsonArray<RepoData>(it) }
                githubReposFragment.recyclerView.adapter =  RepoListAdapter(items as ArrayList<RepoData>) { partItem: RepoData -> githubReposFragment.repoListItemClicked(partItem) }
                githubReposFragment.page++
            }
        }
    }
}
