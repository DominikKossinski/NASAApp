package com.example.nasa_app.asynctasks

import android.os.AsyncTask
import android.util.Log
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.User
import com.example.nasa_app.activities.CreateAccountActivity
import com.example.nasa_app.activities.LauncherActivity
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class CreateAccountAsyncTask(private val user: User, val activity: CreateAccountActivity) :
    AsyncTask<String, String, JsonObject?>() {

    override fun doInBackground(vararg params: String?): JsonObject? {
        val url = URL("http://${LauncherActivity.SERVER_IP}/api/createUser")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        connection.setRequestProperty("Accept", "*/*")
        connection.doInput = true
        connection.doOutput = true
        connection.connect()
        val outputStream: OutputStream = connection.outputStream
        // Create a writer container to pass the output over the stream
        val outputWriter = OutputStreamWriter(outputStream)
        // Add the string to the writer container
        outputWriter.write(Gson().toJson(user, User::class.java))
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
            return Gson().fromJson(respString, JsonElement::class.java).asJsonObject

        }
        return null
    }

    override fun onPostExecute(result: JsonObject?) {
        if (result != null) {
            if (BuildConfig.DEBUG) {
                Log.d("CreateAccountAsyncTask", "Response: $result")
            }
            if (result.get("status").asString!!.contentEquals("ok")) {
                val newUser = Gson().fromJson(result.getAsJsonObject("user"), User::class.java)
                activity.showSuccessAlert(newUser)
            } else {
                if (result.get("description").asString!!.contentEquals("user name exists")) {
                    activity.showUserExistsError()
                }
            }
        }
        super.onPostExecute(result)
    }

}