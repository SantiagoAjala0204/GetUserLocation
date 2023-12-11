package com.example.getuserlocation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class db_query : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.queries_bd)

        val btnSalir = findViewById<Button>(R.id.btnSalir)
        btnSalir.setOnClickListener {
            // Crear un Intent para la nueva actividad
            val intent = Intent(this, Login::class.java)

            startActivity(intent)

            finish()
        }
    }
}
