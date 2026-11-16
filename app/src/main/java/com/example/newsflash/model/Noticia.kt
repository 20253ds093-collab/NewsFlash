package com.utez.newsflash.model

/** Una noticia que regresa la API. */
data class Noticia(
    val id: Int,
    val titulo: String,
    val resumen: String,
    val fuente: String,
    val url: String
)
