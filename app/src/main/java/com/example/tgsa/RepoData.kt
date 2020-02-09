package com.example.tgsa

import com.beust.klaxon.Json

data class RepoData (val name: String, @Json(name = "full_name") val fullName: String, @Json(name = "html_url") val htmlUrl: String) {
}