package com.utez.newsflash.model

data class EstadoNoticias(
    val ultimaNoticia: Noticia? = null,
    val ultimaRevision: String = "Aún no se ha revisado",
    val resultadoRevision: String = "Sin resultados",
    val revisionActiva: Boolean = false,
    val mensaje: String = ""
)
