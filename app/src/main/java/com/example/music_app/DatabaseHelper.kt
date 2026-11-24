package com.example.music_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.music_app.model.AlbumFavorito

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION,

    ) {

    companion object {
        private const val DATABASE_NAME = "music_app.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"
        const val TABLE_FAVORITOS = "favoritos"
        const val COL_FAVORITO_ID = "id"
        const val COL_ALBUM_ID = "album_id"
        const val COL_NOMBRE_ALBUM = "nombre_album"
        const val COL_ARTISTA = "artista"
        const val COL_GENERO = "genero"
        const val COL_CANCIONES = "canciones"
        const val COL_FECHA = "fecha"
        const val COL_IMAGEN = "imagen"

        //Crear tabla de usuarios
        private const val CREATE_TABLE_USUARIOS = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL
            )
        """

        //Crear tabla de favoritos
        private const val CREATE_TABLE_FAVORITOS = """
            CREATE TABLE $TABLE_FAVORITOS (
            $COL_FAVORITO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_ALBUM_ID INTEGER UNIQUE NOT NULL,
            $COL_NOMBRE_ALBUM TEXT NOT NULL,
            $COL_ARTISTA TEXT,
            $COL_GENERO TEXT,
            $COL_CANCIONES INTEGER,
            $COL_FECHA TEXT,
            $COL_IMAGEN TEXT
            )
        """
    }

    //METODO PARA CREAR LA BASE DE DATOS
    override fun onCreate(db: SQLiteDatabase?) {
        //Crear tabla de SQL
        db?.execSQL(CREATE_TABLE_USUARIOS)
        db?.execSQL(CREATE_TABLE_FAVORITOS)
    }

    //METODO PARA ACTUALIZAR LA BASE DE DATOS
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //Verificar que la version sea menor a 2
        if (oldVersion < 2) {
            db?.execSQL(CREATE_TABLE_FAVORITOS)
        }
    }

    //METODO PARA INSERTAR REGISTRO
    fun registrarUsuario(nombre: String, correo: String, password: String): Boolean {

        //Crear variable para que la base de datos se pueda modificar
        val db = writableDatabase

        //Crear variable para recibir los datos que escribe el usuario
        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_PASSWORD, password)
        }

        //Insertar datos en la tabla
        val resultado = db.insert(TABLE_USUARIOS, null, datos)
        //1L = Insercion exitosa, o -1L = Insercion fallida, L es long
        //1L o -1L

        //Cerrar conexion con la BD
        db.close()

        //Comparar el valor del resultado de la insercion y regresarlo como boolean
        return resultado != -1L

    }

    //Metodo para consultar a un usuario
    fun iniciarSesion(correo: String, password: String): Boolean {
        //Crear variable para que la base de datos se pueda leer
        val db = readableDatabase

        //Crear variable para realizar la consulta
        val query_sql = """
            SELECT * FROM $TABLE_USUARIOS
            WHERE $COL_CORREO = ? AND $COL_PASSWORD = ?
        """.trimIndent()

        //Crear cursor para recorrer la informacion
        val cursor = db.rawQuery(
            query_sql, arrayOf(correo, password)
        )

        //Crear variable de tipo boolean que permita saber si el cursor encontro un registro
        val usuarioEncontrado = cursor.count > 0

        cursor.close()
        db.close()

        return usuarioEncontrado

    }

    //METODO PARA REGISTRAR INFORMACION PROVENIENTE DEL API
    fun guardarAlbumFavorito(
        albumId: Long,
        nombreAlbum: String,
        artista: String,
        genero: String,
        canciones: Int,
        fecha: String,
        imagen: String
    ): Boolean {

        //Declarar uso de la BD
        val db = writableDatabase

        val datos = ContentValues().apply {
            put(COL_ALBUM_ID, albumId)
            put(COL_NOMBRE_ALBUM, nombreAlbum)
            put(COL_ARTISTA, artista)
            put(COL_GENERO, genero)
            put(COL_CANCIONES, canciones)
            put(COL_FECHA, fecha)
            put(COL_IMAGEN, imagen)
        }

        //Insercion en la tabla
        val resultado = db.insertWithOnConflict(
            TABLE_FAVORITOS, null, datos, SQLiteDatabase.CONFLICT_IGNORE
        )

        return resultado != -1L
    }

    //METODO PARA ELIMINAR UN REGISTRO
    fun eliminarAlbumFavorito(albumId: Long): Boolean {
        val db = writableDatabase

        val filasAfectadas = db.delete(
            TABLE_FAVORITOS, "$COL_ALBUM_ID = ?", arrayOf(albumId.toString())
        )
        return filasAfectadas > 0
    }

    //Metodo para verificar que un registro existe
    fun existeAlbumFavorito(albumId: Long): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_FAVORITO_ID FROM $TABLE_FAVORITOS WHERE $COL_ALBUM_ID = ?",
            arrayOf(albumId.toString())
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    //Metodo para acceder a la tabla de favoritos
    fun obtenerAlbumesFavoritos(): List<AlbumFavorito> {
        val listaFavoritos = mutableListOf<AlbumFavorito>()
        val db = readableDatabase
        val query_sql = """
           SELECT 
            $COL_ALBUM_ID,
            $COL_NOMBRE_ALBUM,
            $COL_ARTISTA,
            $COL_GENERO,
            $COL_CANCIONES,
            $COL_FECHA,
            $COL_IMAGEN
            FROM $TABLE_FAVORITOS
            ORDER BY $COL_FAVORITO_ID DESC
        """.trimIndent()
        val cursor = db.rawQuery(query_sql, null)

        //Validar que existan registros en la tabla

        if(cursor.moveToFirst()){
            //Ciclo para guardar la lista que se encuentra en la base de datos
            do {
                //Variable que guarda informacion individual de los registros
                val album = AlbumFavorito(
                    albumId = cursor.getLong(0),
                    nombreAlbum = cursor.getString(1) ?: "Album Desconocido",
                    artista = cursor.getString(2) ?: "Artista Desconocido",
                    genero = cursor.getString(3) ?: "Genero Desconocido",
                    canciones = cursor.getInt(4),
                    fecha = cursor.getString(5) ?: "Sin Fecha",
                    imagen = cursor.getString(6) ?: " "

                )

                //Añadir elemento a la lista
                listaFavoritos.add(album)

            } while (cursor.moveToNext())
        }
        cursor.close()
        return listaFavoritos
    }
}
