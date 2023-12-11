package com.example.getuserlocation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val btnRegister = findViewById<Button>(R.id.btnResgister)
        btnRegister.setOnClickListener {
            // Obtener el ID del usuario ingresado por el usuario
            val etCedula = findViewById<EditText>(R.id.etCedula)
            val etPassword = findViewById<EditText>(R.id.etPassword)
            val etRole = findViewById<EditText>(R.id.etRole)
            val usuarioIdStr = etCedula.text.toString()
            val usuarioId = usuarioIdStr.toString()

            // Obtener el nombre y apellido ingresados por el usuario
            val etNombre = findViewById<EditText>(R.id.etNombre)
            val etApellido = findViewById<EditText>(R.id.etApellido)
            val nombre = etNombre.text.toString()
            val apellido = etApellido.text.toString()
            val password = etPassword.text.toString()
            val role = etRole.text.toString()
            sendUserDataToServer(usuarioId, nombre, apellido, password, role)
        }
    }

    private fun sendUserDataToServer(usuarioId: String, nombre: String, apellido: String, password: String, role: String) {
        val client = OkHttpClient()
        val url = "https://apirest-qywgms5y2q-ue.a.run.app/agregar_usuario"
        //val url = "http://172.23.216.39:5000/agregar_usuario"

        val json = """
            {
                "id": "$usuarioId",
                "nombre": "$nombre",
                "apellido": "$apellido",
                "password_hash": "$password",
                "role": "$role"
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
                showToast("Error al enviar Datos al servidor")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        // Manejar un error si la respuesta no es exitosa
                        showToast("Error al enviar Datos al servidor 2222")
                        println(usuarioId)
                        println(nombre)
                        println(apellido)
                        println(password)
                        println(role)
                    } else {
                        // Procesar una respuesta exitosa si es necesario
                        showToast("Su cuenta ha sido creada correctamente")
                        val intent = Intent(this@MainActivity2, Login::class.java)
                        startActivity(intent)
                    }
                }
            }
        })
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@MainActivity2, message, Toast.LENGTH_SHORT).show()
        }
    }
}
