package com.example.nasa_app.asynctasks

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.R
import com.example.nasa_app.User
import com.example.nasa_app.activities.LauncherActivity
import com.example.nasa_app.activities.LoginActivity
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

class LoginAsyncTask(
    private val activity: Activity, var user: String, private val nameTextInputLayout: TextInputLayout? = null,
    private val passwordTextInputLayout: TextInputLayout? = null,
    private val nameTextInputEditText: TextInputEditText? = null,
    private val passwordTextInputEditText: TextInputEditText? = null,
    private val progressBar: ProgressBar? = null
) :
    AsyncTask<String, String, JsonObject>() {
    override fun doInBackground(vararg params: String?): JsonObject {
        val url = URL("http://${LauncherActivity.SERVER_IP}/api/login")
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
        outputWriter.write(user)
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
            val setCookie = connection.getHeaderField("Set-Cookie")
            var jsessionid = ""
            if (setCookie != null) {
                val regex = "JSESSIONID=.*?;".toRegex()
                val cookie = regex.find(setCookie)
                if (cookie != null) {
                    jsessionid = cookie.value.replace("JSESSIONID=", "").replace(";", "")
                }
            }
            connection.disconnect()
            val responseObject = Gson().fromJson(respString, JsonElement::class.java).asJsonObject
            responseObject.addProperty("JSESSIONID", jsessionid)
            if (BuildConfig.DEBUG) {
                Log.d("MyLog:LoginAsyncTask", "JSESSIONID: $jsessionid")
                Log.d("MyLog:LoginAsyncTask", "Response: $responseObject")
            }
            return responseObject
        }
        return JsonObject()
    }

    override fun onPostExecute(response: JsonObject?) {
        if (response!!.has("logged")) {
            if (activity is LauncherActivity) {
                val launcherActivity = activity
                if (response.get("logged").asBoolean) {
                    val userData = Gson().fromJson(response.getAsJsonObject("user"), User::class.java)
                    launcherActivity.openMainActivity(userData, response.get("JSESSIONID").asString)
                    if (BuildConfig.DEBUG) {
                        Log.d("LoginAsyncTask", "JSESSIONID = ${response.get("JSESSIONID").asString}")
                    }
                } else {
                    launcherActivity.openLoginActivity()
                }
            } else if (activity is LoginActivity) {
                val loginActivity = activity
                if (response.get("logged").asBoolean) {
                    if (BuildConfig.DEBUG) {
                        Log.d("LoginAsyncTask", "JSESSIONID = ${response.get("JSESSIONID").asString}")
                    }
                    val userData = Gson().fromJson(response.getAsJsonObject("user"), User::class.java)
                    loginActivity.openMainActivity(userData, response.get("JSESSIONID").asString)

                } else {
                    if (response.get("error").asString!!.contentEquals("no user")) {
                        nameTextInputEditText!!.setText("")
                        passwordTextInputEditText!!.setText("")
                        nameTextInputLayout!!.error = activity.getString(R.string.no_user)
                    } else if (response.get("error").asString!!.contentEquals("wrong password")) {
                        passwordTextInputLayout!!.error = activity.getString(R.string.wrong_password)
                        passwordTextInputEditText!!.setText("")
                    }
                    progressBar!!.visibility = View.GONE
                }

            } else if (activity is MainActivity) {
                if (BuildConfig.DEBUG) {
                    Log.d("MyLog:LoginAsyncTask", "Login from MainActivity")
                }
                if (response.get("logged").asBoolean) {
                    val bundle = Bundle()
                    val jsessionid = response.get("JSESSIONID").asString!!
                    val userData = Gson().fromJson(response.getAsJsonObject("user"), User::class.java)
                    bundle.putString("JSESSIONID", jsessionid)
                    bundle.putLong("userId", userData.id)
                    bundle.putString("name", userData.name)
                    bundle.putString("password", userData.password)
                    bundle.putString("role", userData.role)
                    bundle.putString("email", userData.email)
                    bundle.putString("apiKey", userData.apiKey)
                    activity.getDataFromBundle(bundle, true)
                } else {
                    activity.logOut()
                }
            }
            //TODO
//            else if (activity is ArticleActivity) {
//                if (BuildConfig.DEBUG) {
//                    Log.d("MyLog:LoginAsyncTask", "Login from ArticleActivity")
//                }
//                if (response.get("logged").asBoolean) {
//                    val bundle = Bundle()
//                    val jsessionid = response.get("JSESSIONID").asString!!
//                    val userData = Gson().fromJson(response.getAsJsonObject("user"), User::class.java)
//                    bundle.putString("JSESSIONID", jsessionid)
//                    bundle.putLong("userId", userData.id)
//                    bundle.putString("name", userData.name)
//                    bundle.putString("password", userData.password)
//                    bundle.putString("role", userData.role)
//                    bundle.putString("email", userData.email)
//                    bundle.putString("apiKey", userData.apiKey)
//                    activity.setUpUserData(bundle, true)
//                } else {
//                    activity.logOut()
//                }
//            }
        }
        super.onPostExecute(response)
    }
}