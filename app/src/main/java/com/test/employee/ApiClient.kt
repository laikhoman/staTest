package com.test.employee

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.test.employee.RestAPI.GET_REQ_METHOD
import com.test.employee.RestAPI.POST_REQ_METHOD
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

interface OnTaskCompleted {
    fun onTaskCompleted(command: String, response: String)
}

object RestAPI {
    @JvmStatic val GET_REQ_METHOD = "GET"
    @JvmStatic val POST_REQ_METHOD = "POST"
}

class APIClient(
    context: Context
) : AsyncTask<String?, Int?, String>() {

    private val TAG = this::class.java.simpleName

    @SuppressLint("StaticFieldLeak") private val CONTEXT = context

    private val TIMEOUT = 20000

    private var listener: OnTaskCompleted? = null
    private var baseUrl = ""
    private var command = ""

    fun setListener(listener: OnTaskCompleted?): APIClient {
        this.listener = listener
        return this
    }

    fun setBaseUrl(baseUrl: String): APIClient {
        this.baseUrl = baseUrl
        return this
    }

    override fun doInBackground(vararg params: String?): String {
        if (params.isEmpty()) return ""
        command = "$baseUrl${params[0].toString()}"
        var param = ""
        return try {
            if (params.size == 1) {
                sendPost(command)
            } else {
                param = params[1].toString()
                sendPost(command, param)
            }
        } catch (e: Exception) {
            Log.d(TAG, "command: $command")
            Log.d(TAG, "param: $param")
            Log.e(TAG, "err: $e")
            val jsonResponse = JSONObject()
            jsonResponse.put("msg", e.toString())
            jsonResponse.put("message", e.toString())
            return jsonResponse.toString()
        }
    }

    override fun onPostExecute(result: String) {
        listener?.let { it.onTaskCompleted(command, result) }
    }

    @Throws(Exception::class)
    private fun sendPost(command: String, param: String): String {
        val uri = URL(command)
        val connection: HttpURLConnection = getHttpsURLConnection(CONTEXT, uri)
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Accept", "application/json")
        // connection.setRequestProperty("Authorization", "Bearer $token")

        // Send post request
        connection.requestMethod = POST_REQ_METHOD
        connection.readTimeout = TIMEOUT
        connection.connectTimeout = TIMEOUT
        connection.doOutput = true

        val wr = DataOutputStream(connection.outputStream)
        wr.writeBytes(param)
        wr.flush()
        wr.close()

        val responseCode = connection.responseCode
        Log.d(TAG, "Sending '$POST_REQ_METHOD' request to URL : $command")
        Log.d(TAG, "$POST_REQ_METHOD parameters : $param")
        Log.d(TAG, "Response Code : $responseCode")
        val `in` = BufferedReader(
            InputStreamReader(connection.inputStream)
        )
        var inputLine: String?
        val response = StringBuilder()
        while (`in`.readLine().also { inputLine = it } != null) {
            response.append(inputLine)
        }
        `in`.close()
        return response.toString()
    }

    @Throws(Exception::class)
    private fun sendPost(command: String): String {
        val uri = URL(command)
        val connection: HttpURLConnection = getHttpsURLConnection(CONTEXT, uri)
        connection.readTimeout = TIMEOUT
        connection.connectTimeout = TIMEOUT

        Log.d(TAG, "Sending '$GET_REQ_METHOD' request to URL : $command")
        val `in` = BufferedReader(
            InputStreamReader(connection.inputStream)
        )
        var inputLine: String?
        val response = StringBuilder()
        while (`in`.readLine().also { inputLine = it } != null) {
            response.append(inputLine)
        }
        `in`.close()
        return response.toString()
    }

    private fun getHttpsURLConnection(context: Context, url: URL): HttpsURLConnection {
        val connection: HttpsURLConnection = url.openConnection() as HttpsURLConnection
        try {
            // connection.sslSocketFactory = SSLConfig.getSSLConfig(context).socketFactory
        } catch (ex: AssertionError) {
            Log.e(TAG, "Exception: $ex")
        }
        return connection
    }
}
