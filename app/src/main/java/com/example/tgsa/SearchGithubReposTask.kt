package com.example.tgsa

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.StringReader

class SearchGithubReposTask(private val githubReposFragment: GithubReposFragment) : AsyncTask<String, Void, JsonObject>() {

    companion object {
        private const val QUERY_PARAM = "q"
        private const val PAGE_PARAM = "page"
        private const val PER_PAGE_PARAM = "per_page"
    }

    private val client = OkHttpClient()
    private val klaxon = Klaxon()

    override fun doInBackground(vararg p0: String?): JsonObject {
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
            return
        }
        val items = itemsArray?.let { klaxon.parseFromJsonArray<RepoData>(it) }
        githubReposFragment.recyclerView.adapter =
            RepoListAdapter(items as ArrayList<RepoData>) {
                    repoData: RepoData -> repoRecyclerViewItemClicked(repoData)
            }
    }

    private fun repoRecyclerViewItemClicked(repoData: RepoData) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(repoData.htmlUrl)
        githubReposFragment.startActivity(openURL)
    }
}