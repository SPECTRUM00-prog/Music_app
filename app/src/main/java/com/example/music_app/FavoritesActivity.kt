package com.example.music_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.music_app.model.AlbumFavorito
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    private lateinit var gridFavoritos: GridLayout

    private lateinit var tvEstado: TextView

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)

        bottomNavigation = findViewById(R.id.bottomNavigation)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)
        gridFavoritos = findViewById(R.id.GridFavoritos)
        tvEstado = findViewById(R.id.tvEstado)
        dbHelper = DatabaseHelper(this)

        // Llamar a la función del menú
        menuNavegacion()
        consultarFavoritos()

    }

    //Metodo para mostrar los cambios en la BD en tiempo real
    override fun onResume() {
        super.onResume()
    }

    // Metodo para el funcionamiento del menu
    private fun menuNavegacion() {
        // Indicar que el elemento seleccionado es Favoritos
        bottomNavigation.selectedItemId = R.id.nav_favoritos

        // Funcion de las opciones
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    // Regresar a la pantalla principal
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.nav_favoritos -> {
                    // Ya nos encontramos en Favoritos, solo mantenemos activo el botón
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

    // Metodo para cerrar sesion
    private fun cerrarSesionGoogle() {
        auth.signOut()

        lifecycleScope.launch {
            try {
                // Limpiar las credenciales del usuario
                credentialManager.clearCredentialState(
                    ClearCredentialStateRequest()
                )
            } catch (e: Exception) {
                Toast.makeText(this@FavoritesActivity, "Error: ${e.toString()}", Toast.LENGTH_SHORT)
                    .show()
            }

            val intent = Intent(this@FavoritesActivity, MainActivity::class.java)

            // Limpiar toda la información e iniciar la pantalla totalmente desde cero
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    //Metodo para consultar la informacion de la base de datos
    private fun mostrarAlbumFavoritos(favoritos: List<AlbumFavorito>) {
        //Ciclo para recorrer la lista
        for (album in favoritos) {

            val vistaAlbum = layoutInflater.inflate(
                R.layout.item_album_favorito,
                gridFavoritos,
                false
            )

            //Obtener la imagen del cuadrado creado antes
            val imgAlbum = vistaAlbum.findViewById<ImageView>(R.id.imgFavoritoAlbum)

            //Mostrar Imagen
            Glide.with(this)
                .load(album.imagen)
                .centerCrop()
                .into(imgAlbum)

            vistaAlbum.setOnClickListener {

                val intent = Intent(this, AlbumDetailActivity::class.java)

                intent.putExtra("ID_ALBUM", album.albumId)
                intent.putExtra("NOMBRE_ALBUM", album.nombreAlbum)
                intent.putExtra("ARTISTA_ALBUM", album.artista)
                intent.putExtra("GENERO_ALBUM", album.genero)
                intent.putExtra("CANCIONES_ALBUM", album.canciones ?: 0)
                intent.putExtra("FECHA_ALBUM", album.fecha)
                intent.putExtra("IMAGEN_ALBUM", album.imagen)

                startActivity(intent)
            }

            //Agregar las cartas
            gridFavoritos.addView(vistaAlbum)

        }
    }

    //Metodo para consultar en la BD
    private fun consultarFavoritos() {

        val favoritos = dbHelper.obtenerAlbumesFavoritos()

        //Eliminar vistas duplicadas en caso de exister
        gridFavoritos.removeAllViews()

        //Validar si la consulta devuelve algo
        if (favoritos.isEmpty()) {
            tvEstado.text = "No tiene ningun album como favorito :("
            tvEstado.visibility = View.VISIBLE
        } else {
            tvEstado.visibility = View.GONE
            mostrarAlbumFavoritos(favoritos)
        }

    }

}