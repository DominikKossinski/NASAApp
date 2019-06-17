package com.example.nasa_app.asynctasks

import android.os.AsyncTask
import android.util.Log
import com.example.nasa_app.AppService
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.User
import com.example.nasa_app.activities.LauncherActivity
import com.example.nasa_app.activities.MainActivity
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Semaphore

class LogOutAsyncTask(
    val jsessionid: String,
    private val mainActivity: MainActivity?,
    private val semaphore: Semaphore
) : AsyncTask<String, String, JsonObject?>() {
    override fun doInBackground(vararg params: String?): JsonObject? {
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:LogOutAT", "doInBackGround()")
        }
        val url = URL("http://${LauncherActivity.SERVER_IP}/api/logout")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("charset", "utf-8")
        connection.addRequestProperty("Cookie", "JSESSIONID=$jsessionid;")
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        connection.setRequestProperty("Accept", "*/*")
        connection.doInput = true
        connection.doOutput = true
        connection.connect()
        val outputStream: OutputStream = connection.outputStream
        // Create a writer container to pass the output over the stream
        val outputWriter = OutputStreamWriter(outputStream)
        // Add the string to the writer container
        outputWriter.write("")
        // Send the data
        outputWriter.flush()
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val response = StringBuffer()
            BufferedReader(InputStreamReader(connection.inputStream)).use {
                // Container for input stream data
                var inputLine = it.readLine()
                // Add each line to the response container
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()

            }
            val respString = response.toString()
            connection.disconnect()
            val responseObject = Gson().fromJson(respString, JsonElement::class.java).asJsonObject
            if (BuildConfig.DEBUG) {
                Log.d("MyLog:LogoutAsyncTask", "Response: $responseObject")
            }
            return responseObject
        }
        return JsonObject()
    }

    override fun onPostExecute(result: JsonObject?) {
        if (result!!.has("status")) {
            if (result.get("status").asString!!.contentEquals("ok")) {
                if (BuildConfig.DEBUG) {
                    Log.d("MyLog:LogOutAsyncTask", "User Logged Out")
                }
                if (mainActivity != null) {
                    mainActivity.user = User(
                        mainActivity.user!!.id, mainActivity.user!!.name, mainActivity.user!!.password,
                        null, mainActivity.user!!.email, ""
                    )
                }
                AppService.jsessionid = ""
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.d("MyLog:LogOutAsyncTask", "Response: $result")
            }
        }
        semaphore.release()
        super.onPostExecute(result)
    }
}