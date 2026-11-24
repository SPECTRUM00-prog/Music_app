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
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.music_app.model.Album
import com.example.music_app.model.ITunesResponse
import com.example.music_app.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    //Hacer referencia a las vistas de la pantalla
    private lateinit var tvEstado: TextView
    private lateinit var contenedorPop: LinearLayout
    private lateinit var contenedorRock: LinearLayout
    private lateinit var contenedorIndie: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        //Hacer referencia a los componentes de la vista
        tvEstado = findViewById(R.id.tvEstado)
        contenedorPop = findViewById(R.id.contenedorPop)
        contenedorRock = findViewById(R.id.contenedorRock)
        contenedorIndie = findViewById(R.id.contenedorIndie)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)
        //Llamar a los metodos para cargar cada categoria
        obtenerAlbum("indie", contenedorIndie)
        obtenerAlbum("pop", contenedorPop)
        obtenerAlbum("rock", contenedorRock)

        menuNavegacion()

    }

    //Metodo para poder cargar los albumes segun el genero y contenedor
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
                    Toast.makeText(this@HomeActivity, "No hay resultados", Toast.LENGTH_SHORT)
                        .show()
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
                val idAlbum = album.collectionId
                if (idAlbum != null) {
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

    //Metodo para el funcionamiento del menu
    private fun menuNavegacion() {
        //Mostrar el elemento seleccionado
        bottomNavigation.selectedItemId = R.id.nav_inicio

        //Funcion de las opciones
        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.nav_inicio -> {
                    //True para indicar que el boton esta activo
                    true
                }

                R.id.nav_favoritos -> {
                    val intent = Intent(this, FavoritesActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_salir -> {
                    cerrarSesionGoogle()
                    true
                }

                else -> false
            }
        }

    }

    //Metodo para cerrar sesion
    private fun cerrarSesionGoogle() {
        auth.signOut()

        lifecycleScope.launch {

            try {

                //Limpiar las credenciales del usuario
                credentialManager.clearCredentialState(
                    androidx.credentials.ClearCredentialStateRequest()
                )

            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Error: ${e.toString()}", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this@HomeActivity, MainActivity::class.java)

            //Limpiar toda la información e iniciar la pantalla totalmente desde cero
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

}