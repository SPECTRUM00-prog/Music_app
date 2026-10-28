package com.example.music_app

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.music_app.model.ITunesResponse
import com.example.music_app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvResultado: TextView
    private lateinit var imgAlbum: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        tvResultado = findViewById(R.id.tvResultado)
        imgAlbum = findViewById(R.id.imgAlbum)

    }

    //Metodo para cargar los albumes
    private fun obtenerAlbum() {

        RetrofitClient.api.obtenerAlbumGenero(
            genero = "Alternative",
            pais = "MX",
            media = "music",
            entidad = "album",
            limite = 10,
        ).enqueue(object : Callback<ITunesResponse?> {

            //Metodos obligatorios para el funcionamiento
            //Metodo para verificar que la respuesta sea correcta
            override fun onResponse(
                call: Call<ITunesResponse?>,
                response: Response<ITunesResponse?>
            ) {
                TODO("Not yet implemented")
            }

            //Metodo para verificar si no hay respuesta
            override fun onFailure(call: Call<ITunesResponse?>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error en el API", Toast.LENGTH_SHORT).show()
                TODO("Not yet implemented")
            }
        })

    }

}