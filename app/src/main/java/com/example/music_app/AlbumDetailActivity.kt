package com.example.music_app

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class AlbumDetailActivity : AppCompatActivity() {

    private lateinit var imgPortadaDetalle: ImageView
    private lateinit var tvNombreAlbumDetalle: TextView
    private lateinit var tvArtistaAlbum: TextView
    private lateinit var tvAlbumDetalle: TextView
    private lateinit var btnFavorito: ImageView

    private lateinit var tvDetalleAlbum: TextView

    private lateinit var dbHelper: DatabaseHelper

    private var albumFavorito = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_album_detail)

        dbHelper = DatabaseHelper(this)

        imgPortadaDetalle = findViewById(R.id.imgPortadaDetalle)
        tvNombreAlbumDetalle = findViewById(R.id.tvNombreAlbumDetalle)
        tvArtistaAlbum = findViewById(R.id.tvArtistaAlbumDetalle)
        tvAlbumDetalle = findViewById(R.id.tvAlbumDetalle)
        btnFavorito = findViewById(R.id.btnFavorito)

        mosntrarInformacionAlbum()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //Metodo para obtener toda la información
    private fun mosntrarInformacionAlbum() {

        val idAlbum = intent.getLongExtra("ID_ALBUM", -1L)
        val nombreAlbum = intent.getStringExtra("NOMBRE_ALBUM") ?: "Album Desconocido"
        val artista = intent.getStringExtra("ARTISTA_ALBUM") ?: "Artista Desconocido"
        val genero = intent.getStringExtra("GENERO_ALBUM") ?: "Genero Desconocido"
        val numeroCanciones = intent.getIntExtra("CANCIONES_ALBUM", 0)
        val fecha = intent.getStringExtra("FECHA_ALBUM") ?: "Sin Fecha"
        val imagen = intent.getStringExtra("IMAGEN_ALBUM") ?: " "

        tvNombreAlbumDetalle.text = nombreAlbum

        tvArtistaAlbum.text = artista

        tvAlbumDetalle.text = genero + " - " + "$numeroCanciones canciones"

        Glide.with(this)
            .load(imagen)
            .centerCrop()
            .into(imgPortadaDetalle)

        //Validacion para verificar si el album es favorito o no
        if(idAlbum != -1L){
            albumFavorito = dbHelper.existeAlbumFavorito(idAlbum)
        }

        btnFavorito.setOnClickListener {

            //albumFavorito = !albumFavorito

            if(albumFavorito){

                val eliminado = dbHelper.eliminarAlbumFavorito(idAlbum)

                if(eliminado){
                    albumFavorito = !albumFavorito
                    btnFavorito.setImageResource(R.drawable.no_favorito)
                    Toast.makeText(this, "Album $nombreAlbum eliminado de favoritos", Toast.LENGTH_SHORT).show()
                }


            } else {

                val guardado = dbHelper.guardarAlbumFavorito(albumId = idAlbum,
                    nombreAlbum = nombreAlbum,
                    artista = artista,
                    genero = genero,
                    canciones = numeroCanciones,
                    fecha = fecha,
                    imagen = imagen
                )
                //Validar que el registro sea exitoso
                if (guardado){
                    albumFavorito = !albumFavorito
                    Toast.makeText(this, "Album $nombreAlbum agregado a favoritos", Toast.LENGTH_SHORT).show()
                    btnFavorito.setImageResource(R.drawable.favorito)
                }

            }

        }

    }

}