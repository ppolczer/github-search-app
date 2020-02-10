package com.example.tgsa

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.StringReader

class SearchGithubReposTask(private val githubReposFragment: GithubReposFragment) :
    AsyncTask<String, Void, JsonObject>() {

    companion object {
        private const val QUERY_PARAM = "q"
        private const val PAGE_PARAM = "page"
        private const val PER_PAGE_PARAM = "per_page"
    }

    private val client = OkHttpClient()
    private val klaxon = Klaxon()

    private var currentPage = 0
    private var perPage = 0

    override fun doInBackground(vararg p0: String?): JsonObject {
        currentPage = p0[1]!!.toInt()
        perPage = p0[2]!!.toInt()
        val httpUrlBuilder = "https://api.github.com/search/repositories".toHttpUrlOrNull()
            ?.newBuilder()
            ?.addQueryParameter(QUERY_PARAM, p0[0])
            ?.addQueryParameter(PAGE_PARAM, p0[1])
            ?.addQueryParameter(PER_PAGE_PARAM, p0[2])
        val request = Request.Builder().url(httpUrlBuilder!!.build()).build()
        val response = client.newCall(request).execute()
        if (response.header("Status") == "403 Forbidden") {
            Log.d("MY_TAG", "Call returned error")
        }
        return klaxon.parseJsonObject(StringReader(response.body?.string().toString()))
    }

    override fun onPostExecute(result: JsonObject?) {
        super.onPostExecute(result)
        val itemsArray = result?.array<Any>("items")
        // TODO: Handle errors (e.g. api limit reached)
        if (itemsArray == null) {
            Toast.makeText(githubReposFragment.context, "An error occurred!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        var totalCount = result["total_count"] as Int

        if (totalCount > 0) {
            val items = itemsArray.let { klaxon.parseFromJsonArray<RepoData>(it) }
            githubReposFragment.recyclerView.visibility = View.VISIBLE
            githubReposFragment.recyclerView.adapter =
                RepoListAdapter(items as ArrayList<RepoData>) { repoData: RepoData ->
                    repoRecyclerViewItemClicked(repoData)
                }
        } else {
            githubReposFragment.recyclerView.visibility = View.INVISIBLE
        }

        // Github provides only the first 1000 results
        if (totalCount > 1000) {
            totalCount = 1000
        }
        val maxPages = (totalCount + perPage - 1) / perPage
        if (maxPages == 0) {
            currentPage = 0
        }
        // TODO: Not sure if setting maxPages, buttons, ... like this is a good architectural choice ...
        githubReposFragment.maxPages = maxPages
        githubReposFragment.textViewPages.visibility = View.VISIBLE
        githubReposFragment.textViewPages.text = String.format(
            githubReposFragment.getString(R.string.textview_pages_text),
            currentPage,
            maxPages
        )

        if (currentPage == 1) {
            if (maxPages == 1) {
                githubReposFragment.buttonPrev.isEnabled = false
                githubReposFragment.buttonNext.isEnabled = false
            } else {
                githubReposFragment.buttonPrev.isEnabled = false
                githubReposFragment.buttonNext.isEnabled = true
            }
        } else {
            if (currentPage == maxPages) {
                githubReposFragment.buttonNext.isEnabled = false
            } else {
                githubReposFragment.buttonPrev.isEnabled = true
            }
        }
    }

    private fun repoRecyclerViewItemClicked(repoData: RepoData) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(repoData.htmlUrl)
        githubReposFragment.startActivity(openURL)
        // Uncomment below code (and comment code above) to open a detail view when clicking on a repo name
        //val action = GithubReposFragmentDirections.actionGithubReposFragmentToRepoDetail(repoData)
        //githubReposFragment.view!!.findNavController().navigate(action)
    }
}