package com.example.getuserlocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    private val locationService: LocationService = LocationService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvLocation = findViewById<TextView>(R.id.tvLocation)
        val btnLocation = findViewById<Button>(R.id.btnLocation)

        btnLocation.setOnClickListener {
            lifecycleScope.launch {
                val result = locationService.getUserLocation(this@MainActivity)
                if (result != null) {
                    val latitude = result.latitude
                    val longitude = result.longitude
                    tvLocation.text = "Latitud ${result.latitude} y longitud ${result.longitude}"

                    // Obtener el ID del usuario ingresado por el usuario
                    val etCedula = findViewById<EditText>(R.id.etCedula)
                    val usuarioIdStr = etCedula.text.toString()
                    // Obtener el nombre y apellido ingresados por el usuario
                    val etNombre = findViewById<EditText>(R.id.etNombre)
                    val etApellido = findViewById<EditText>(R.id.etApellido)
                    val nombre = etNombre.text.toString()
                    val apellido = etApellido.text.toString()

                    // Intenta convertir el ID del usuario a un entero
                    try {
                        val usuarioId = usuarioIdStr.toInt()

                        // Luego, envía las coordenadas y el ID del usuario al servidor Flask
                        sendUserDataToServer(usuarioId, nombre, apellido)
                        sendCoordinatesToServer(latitude, longitude, usuarioId)
                    } catch (e: NumberFormatException) {
                        // Maneja la excepción si el texto no es un número válido
                        // Puedes mostrar un mensaje de error al usuario aquí
                        println("Error: el ID del usuario no es un número válido")
                    }
                }
            }
        }
        val btnLimpiarData = findViewById<Button>(R.id.btnLimpiarData)

        btnLimpiarData.setOnClickListener {
            val etCedula = findViewById<EditText>(R.id.etCedula)
            val etNombre = findViewById<EditText>(R.id.etNombre)
            val etApellido = findViewById<EditText>(R.id.etApellido)
            val tvLocation = findViewById<TextView>(R.id.tvLocation)

            // Borra el texto en los campos de entrada de texto
            etCedula.text.clear()
            etNombre.text.clear()
            etApellido.text.clear()
            tvLocation.text = "Vacio" // Restablece el texto de ubicación
            // También puedes restablecer las variables de latitud y longitud aquí si es necesario
        }
    }

    private fun sendCoordinatesToServer(latitude: Double, longitude: Double, userId: Int) {
        val client = OkHttpClient()
        val url = "http://192.168.0.36:5000/agregar_punto"  // Reemplaza con la URL de tu API

        val json = """
        {
            "latitud": "$latitude",
            "longitud": "$longitude",
            "usuario_id": $userId
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
                        println("Error al enviar coordenadas al servidor")
                    } else {
                        // Procesar una respuesta exitosa si es necesario
                        println("Coordenadas enviadas correctamente al servidor")
                    }
                }
            }
        })
    }
    private fun sendUserDataToServer(usuarioId: Int, nombre: String, apellido: String) {
        val client = OkHttpClient()
        val url = "http://192.168.0.36:5000/agregar_usuario"  // Reemplaza con la URL de tu API

        val json = """
        {
            "id": $usuarioId,
            "nombre": "$nombre",
            "apellido": "$apellido"
            
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
                        println("Error al enviar coordenadas al servidor")
                    } else {
                        // Procesar una respuesta exitosa si es necesario
                        println("Coordenadas enviadas correctamente al servidor")
                    }
                }
            }
        })
    }

}
