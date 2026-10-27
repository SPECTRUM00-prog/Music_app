package com.example.music_app.model

//Archivo para verificar si obtenemos una respuesta de una API

data class ITunesResponse(
    val resultCount: Int,
    val results: List<Album>
)
