package com.test.employee.github

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.io.Serializable

data class RepoListModel(

    @SerializedName("id") var id: String?,
    @SerializedName("name") var name: String,
    @SerializedName("html_url") var html_url: String?

) : Serializable {
    constructor() : this("", "", "")

    @SuppressLint("Range") constructor(jsonObject: JSONObject?) : this(
        id = jsonObject?.optString("id", "") ?: "",
        name = jsonObject?.optString("name", "") ?: "",
        html_url = jsonObject?.optString("html_url", "") ?: "",
    )
}