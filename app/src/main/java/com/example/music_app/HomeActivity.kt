package com.example.music_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.music_app.model.Album
import com.example.music_app.model.ITunesResponse
import com.example.music_app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    //Hacer referencia a las vistas de la pantalla
    private lateinit var tvEstado: TextView
    private lateinit var contenedorPop: LinearLayout
    private lateinit var contenedorRock: LinearLayout
    private lateinit var contenedorIndie: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        //Hacer referencia a los componentes de la vista
        tvEstado = findViewById(R.id.tvEstado)
        contenedorPop = findViewById(R.id.contenedorPop)
        contenedorRock = findViewById(R.id.contenedorRock)
        contenedorIndie = findViewById(R.id.contenedorIndie)

        //Llamar a los metodos para cargar cada categoria
        obtenerAlbum("indie", contenedorIndie)
        obtenerAlbum("pop", contenedorPop)
        obtenerAlbum("rock", contenedorRock)
    }

    //Metodo para cargar los albumes segun el genero y contenedor
    private fun obtenerAlbum(genero: String, contenedor: LinearLayout) {
        RetrofitClient.api.obtenerAlbumGenero(
            genero = genero, // Corregido: toma el genero que le pasas
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
                //Variable que obtenga los resultados de la API
                val albumes = response.body()?.results ?: emptyList()

                if (albumes.isNotEmpty()) {
                    tvEstado.visibility = View.GONE
                    mostrarAlbumes(albumes, contenedor)
                    tvEstado.text = "Catálogo cargado con éxito"
                } else {
                    Toast.makeText(this@HomeActivity, "No hay resultados", Toast.LENGTH_SHORT).show()
                }
            }

            //Metodo para verificar si no hay respuesta
            override fun onFailure(call: Call<ITunesResponse?>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error en el API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //Metodo para mostrar un listado de albumes
    private fun mostrarAlbumes(albumes: List<Album>, contenedor: LinearLayout) {

        //Evitar resultados duplicados
        contenedor.removeAllViews()

        //Ciclo para recorrer la informacion de la respuesta del API
        for (album in albumes) {
            //Indicar la vista en la que se va a mostrar la informacion
            // (item_album)
            val vistaAlbum = layoutInflater.inflate(R.layout.item_album, contenedor, false)

            val imgPortada = vistaAlbum.findViewById<ImageView>(R.id.imgPortadaAlbum)
            val tvNombreAlbum = vistaAlbum.findViewById<TextView>(R.id.tvNombreAlbum)
            val tvNombreArtista = vistaAlbum.findViewById<TextView>(R.id.tvNombreArtista)

            tvNombreAlbum.text = album.collectionName ?: "Album sin nombre"
            tvNombreArtista.text = album.artistName ?: "Artista desconocido"

            Glide.with(this)
                .load(album.obtenerImagenGrande())
                .centerCrop()
                .into(imgPortada)

            //Generar evento para mostrar la info de un album
            vistaAlbum.setOnClickListener {

                //Variable para verificar si el album tiene ID
                val idAlbum = album.collectionName

                if (idAlbum != null){
                    val intent = Intent(this@HomeActivity, AlbumDetailActivity::class.java)

                    intent.putExtra("ID_ALBUM", idAlbum)
                    intent.putExtra("NOMBRE_ALBUM", album.collectionName)
                    intent.putExtra("ARTISTA_ALBUM", album.artistName)
                    intent.putExtra("GENERO_ALBUM", album.primaryGenreName)
                    intent.putExtra("CANCIONES_ALBUM", album.trackCount ?: 0)
                    intent.putExtra("FECHA_ALBUM", album.releaseDate)
                    intent.putExtra("IMAGEN_ALBUM", album.obtenerImagenGrande())

                    startActivity(intent)
                }

            }

            //Mostrar toda la informacion
            contenedor.addView(vistaAlbum)
        }
    }
}