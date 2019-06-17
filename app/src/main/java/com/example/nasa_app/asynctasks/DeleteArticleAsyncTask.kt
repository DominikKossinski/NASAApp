package com.example.nasa_app.asynctasks

import android.os.AsyncTask
import com.example.nasa_app.Article
import com.example.nasa_app.User
import com.example.nasa_app.activities.ArticleActivity
import com.example.nasa_app.activities.LauncherActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class DeleteArticleAsyncTask(
    val user: User,
    val article: Article,
    private val jsessionid: String,
    val activity: ArticleActivity
) :
    AsyncTask<String, String, JsonObject?>() {
    override fun doInBackground(vararg params: String?): JsonObject? {
        val url = URL("http://${LauncherActivity.SERVER_IP}/api/${user.id}/deleteArticle")
        val connection = url.openConnection() as HttpURLConnection
        connection.addRequestProperty("Cookie", "JSESSIONID=$jsessionid;")
        connection.requestMethod = "DELETE"
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
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        val newArticle =
            Article(article.title, article.explanation, article.date, article.mediaType, article.url, saved = null)
        outputWriter.write(gson.toJson(newArticle, Article::class.java))
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
        activity.showDeletingEnd(result!!.get("status").asString!!.contentEquals("ok"))
        super.onPostExecute(result)
    }

}