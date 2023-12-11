package com.example.getuserlocation

import android.content.Intent
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
import android.widget.Toast
import kotlinx.coroutines.delay


class MainActivity : AppCompatActivity() {
    private val locationService: LocationService = LocationService()
    private var autoFetchActive = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvLocation = findViewById<TextView>(R.id.tvLocation)
        val btnLocation = findViewById<Button>(R.id.btnLocation)
        val btnStop = findViewById<Button>(R.id.btnStop)


        btnLocation.setOnClickListener {
            lifecycleScope.launch {
                val result = locationService.getUserLocation(this@MainActivity)
                //Toast.makeText(this@MainActivity, "Obtención automática Iniciada", Toast.LENGTH_SHORT).show()
                if (result != null) {
                    val usuarioId = intent.getStringExtra("USERNAME_EXTRA")
                    val userId = usuarioId.toString()
                    val latitude = result.latitude
                    val longitude = result.longitude
                    tvLocation.text = "Latitud ${result.latitude} y longitud ${result.longitude}"
                    val token = intent.getStringExtra("TOKEN_EXTRA")
                    val strtoken =token.toString()
                    sendCoordinatesToServer(latitude, longitude, userId,strtoken)
                    btnLocation.isEnabled = false
                    autoFetchActive = true
                    println(strtoken)
                    println(userId)
                }
            }
        }

        val btnSalir=findViewById<Button>(R.id.btnSalir)
        btnSalir.setOnClickListener {
            // Crear un Intent para la nueva actividad
            val intent = Intent(this, Login::class.java)

            startActivity(intent)

            finish()
        }

        val btnLimpiarData = findViewById<Button>(R.id.btnLimpiarData)

        btnLimpiarData.setOnClickListener {
            val tvLocation = findViewById<TextView>(R.id.tvLocation)

            tvLocation.text = "Vacio" // Restablece el texto de ubicación
        }

        btnStop.setOnClickListener {
            autoFetchActive = false
            btnLocation.isEnabled = true
            Toast.makeText(this, "Obtención automática detenida", Toast.LENGTH_SHORT).show()
        }
        lifecycleScope.launch {
            while (true) {
                if (autoFetchActive) {
                    val result = locationService.getUserLocation(this@MainActivity)

                    if (result != null) {
                        val usuarioId = intent.getStringExtra("USERNAME_EXTRA")
                        val userId = usuarioId.toString()
                        val latitude = result.latitude
                        val longitude = result.longitude
                        tvLocation.text = "Latitud ${result.latitude} y longitud ${result.longitude}"
                        val token = intent.getStringExtra("TOKEN_EXTRA")
                        val strtoken =token.toString()
                        sendCoordinatesToServer(latitude, longitude, userId,strtoken)
                    }
                }

                // Espera un intervalo de tiempo (por ejemplo, 5 segundos) antes de la próxima obtención
                delay(1800000)
            }
        }
    }

    private fun sendCoordinatesToServer(latitude: Double, longitude: Double, userId: String, token: String) {
        println("Hola luego 30sg")
        val client = OkHttpClient()
        val url = "https://apirest-qywgms5y2q-ue.a.run.app/agregar_punto"  // Reemplaza con la URL de tu API
        //val url = "http://127.0.0.1:5000/agregar_punto"
        val json = """
        {
            "latitud": "$latitude",
            "longitud": "$longitude",
            "usuario_id": "$userId"
            
        }
    """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        // Manejar un error si la respuesta no es exitosa
                        println("Error al enviar PUNTOS al servidor")
                    } else {
                        // Procesar una respuesta exitosa si es necesario
                        println("PUNTOS enviados correctamente al servidor")
                    }
                }
            }
        })
        Toast.makeText(this@MainActivity, "Ubicacion actualizada", Toast.LENGTH_SHORT).show()

    }

    override fun onBackPressed() {
        finishAffinity()

    }
}