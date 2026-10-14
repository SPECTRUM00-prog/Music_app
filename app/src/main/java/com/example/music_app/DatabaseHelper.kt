package com.example.music_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION,

    ) {

    companion object {
        private const val DATABASE_NAME = "music_app.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COLUMN_PASSWORD = "password"
    }

    //METODO PARA CREAR LA BASE DE DATOS

    override fun onCreate(db: SQLiteDatabase?) {
        val crearTablaUsuarios = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        //Crear tabla de SQL
        db?.execSQL(crearTablaUsuarios)
    }

    //METODO PARA ACTUALIZAR LA BASE DE DATOS
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    //METODO PARA INSERTAR REGISTRO
    fun registrarUsuario(nombre: String, correo: String, password: String): Boolean {

        //Crear variable para que la base de datos se pueda modificar
        val db = writableDatabase

        //Crear variable para recibir los datos que escribe el usuario
        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COLUMN_PASSWORD, password)
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
}
