package com.example.tgsa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs

class RepoDetail : Fragment() {
    private val args: RepoDetailArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repo_detail, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val repoData = args.repoData

        view.findViewById<TextView>(R.id.textview_detail_full_name).text = repoData.fullName
        view.findViewById<TextView>(R.id.textview_detail_url).text = repoData.htmlUrl
        val textViewUrl = view.findViewById<TextView>(R.id.textview_detail_url)
        textViewUrl.text = repoData.htmlUrl
        textViewUrl.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(repoData.htmlUrl)
            startActivity(openURL)
        }
        super.onViewCreated(view, savedInstanceState)
    }
}
