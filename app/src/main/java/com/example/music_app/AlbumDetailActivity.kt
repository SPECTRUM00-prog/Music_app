package com.example.music_app

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AlbumDetailActivity : AppCompatActivity() {

    private lateinit var tvDetalleAlbum : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_album_detail)

        tvDetalleAlbum = findViewById(R.id.tvDetalleAlbum)

        mosntrarInformacionAlbum()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //Metodo para obtener toda l ainformacion
    private fun mosntrarInformacionAlbum(){

        val idAlbum = intent.getLongExtra("ID_ALBUM",1)
        val nombreAlbum = intent.getStringExtra("NOMBRE_ALBUM")
        val artista = intent.getStringExtra("ARTISTA_ALBUM")
        val genero = intent.getStringExtra("GENERO_ALBUM")
        val numeroCanciones = intent.getIntExtra("CANCIONES_ALBUM",0)
        val fecha = intent.getStringExtra("FECHA_ALBYM")
        val imagen = intent.getStringExtra("IMAGEN_ALBUM")

        tvDetalleAlbum.text = """
            "Album: $nombreAlbum"
            "ID: $idAlbum"
            "Artista: $artista"
            """

    }

}