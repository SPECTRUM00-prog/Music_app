package com.example.music_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnInicio: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val tvCrearCuenta = findViewById<TextView>(R.id.tvCrearCuenta)

        etCorreo = findViewById(R.id.etCorreo)

        etPassword = findViewById(R.id.etPassword)

        btnInicio = findViewById(R.id.btnIngresar)

        dbHelper = DatabaseHelper(this)

        //Evento para iniciar sesion
        btnInicio.setOnClickListener {
            //TODO: Agregar metodo de incio de sesion
            iniciarSesion()

        }

        tvCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    //Metodo para iniciar sesion
    private fun iniciarSesion() {
        //Acceder a la informacion que escribe el usuario
        val correo = etCorreo.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        //Llamar al metodo de inicio de sesion en la BD
        val resultado = dbHelper.iniciarSesion(correo, password)

        if (resultado) {
            //Si la sesion es correcta
            Toast.makeText(this, "Bienvenido :D", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            //Cerrar la pantalla actual
            finish()
        } else {
            //Si la sesion es incorrecta
            Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_SHORT).show()
        }

    }
}
