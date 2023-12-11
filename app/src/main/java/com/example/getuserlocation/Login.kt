package com.example.getuserlocation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        //VARIABLES DEL LOGIN
        val username = findViewById<TextView>(R.id.etUser)
        val user = username.text.toString()
        val pass = findViewById<TextView>(R.id.etPassword)
        val password = pass.text.toString()
        //BOTON DE REGISTRAR
        val btnRegister = findViewById<Button>(R.id.btnResgisteraccount)
        btnRegister.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)}
        //BOTON INICIAR SESION
        val btnIniciar = findViewById<Button>(R.id.btnResgister)
        btnIniciar.setOnClickListener {
            val username = findViewById<TextView>(R.id.etUser)
            val user = username.text.toString()
            //intent.putExtra("id_user", user)
            val pass = findViewById<TextView>(R.id.etPassword)
            val password = pass.text.toString()
            createlogin(user,password)
        }
        }


    private fun createlogin( user: String,password: String) {
        val client = OkHttpClient()
        val url = "http://172.23.216.39:8080/login"  // Reemplaza con la URL de tu API

        val json = """
        {
            "username": "$user",
            "password": "$password"
            
        }
    """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        // Manejar un error si la respuesta no es exitosa
                        showToast("Credenciales Incorrectas")
                    } else {
                        // Procesar una respuesta exitosa si es necesario
                        val responseData = response.body?.string()
                        val gson = Gson()
                        val responseJson = gson.fromJson(responseData, Map::class.java) as Map<String, Any>

                        val roles = responseJson["roles"] as List<String>
                        val accessToken = responseJson["access_token"] as String

                        // Obtener el primer (y único) rol de la lista (si existe)
                        val rol = roles.firstOrNull()

                        // Puedes usar 'rol' y 'accessToken' como desees
                        println("Rol: $rol")
                        println("Access Token: $accessToken")

                        val intent = when (rol) {
                            "admin" -> Intent(this@Login, db_query::class.java)
                            else -> Intent(this@Login, MainActivity::class.java)
                        }
                        intent.putExtra("TOKEN_EXTRA", accessToken)
                        intent.putExtra("USERNAME_EXTRA", user)
                        startActivity(intent)
                        println("Sí pasó esta prueba, se creó el token")
                    }
                }
            }
        })
    }
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@Login, message, Toast.LENGTH_SHORT).show()
        }
    }
    }

