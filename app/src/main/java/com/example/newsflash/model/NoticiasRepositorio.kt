package com.utez.newsflash.model

import android.content.Context
import android.content.SharedPreferences
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Se encarga de los datos: consulta la API en la nube y guarda
 * en SharedPreferences lo último que se notificó.
 */
class NoticiasRepositorio(context: Context) {

    private val preferencias =
        context.getSharedPreferences("newsflash", Context.MODE_PRIVATE)

    /** Consulta la API y regresa la noticia más reciente. */
    suspend fun obtenerUltimaNoticia(): Noticia = withContext(Dispatchers.IO) {
        val conexion = URL(URL_API).openConnection() as HttpURLConnection
        try {
            conexion.connectTimeout = 10000
            conexion.readTimeout = 10000
            if (conexion.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("La API respondió con el código ${conexion.responseCode}")
            }
            val texto = conexion.inputStream.bufferedReader().use { it.readText() }
            // La API regresa una lista llamada "results"; la primera es la más nueva
            val articulo = JSONObject(texto).getJSONArray("results").getJSONObject(0)
            Noticia(
                id = articulo.getInt("id"),
                titulo = articulo.getString("title"),
                resumen = articulo.getString("summary"),
                fuente = articulo.getString("news_site"),
                url = articulo.getString("url")
            )
        } finally {
            conexion.disconnect()
        }
    }

    fun guardarUltimaNoticia(noticia: Noticia) {
        preferencias.edit()
            .putInt("id", noticia.id)
            .putString("titulo", noticia.titulo)
            .putString("resumen", noticia.resumen)
            .putString("fuente", noticia.fuente)
            .putString("url", noticia.url)
            .apply()
    }

    fun leerUltimaNoticia(): Noticia? {
        val id = preferencias.getInt("id", -1)
        if (id == -1) return null
        return Noticia(
            id = id,
            titulo = preferencias.getString("titulo", "") ?: "",
            resumen = preferencias.getString("resumen", "") ?: "",
            fuente = preferencias.getString("fuente", "") ?: "",
            url = preferencias.getString("url", "") ?: ""
        )
    }

    fun leerUltimoId(): Int = preferencias.getInt("id", -1)

    fun guardarRevision(fechaHora: String, resultado: String) {
        preferencias.edit()
            .putString("ultimaRevision", fechaHora)
            .putString("resultado", resultado)
            .apply()
    }

    fun leerUltimaRevision(): String =
        preferencias.getString("ultimaRevision", "Aún no se ha revisado") ?: ""

    fun leerResultado(): String =
        preferencias.getString("resultado", "Sin resultados") ?: ""

    /** Permite avisar al ViewModel cuando el Worker guarda datos nuevos. */
    fun registrarEscucha(escucha: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferencias.registerOnSharedPreferenceChangeListener(escucha)
    }

    fun quitarEscucha(escucha: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferencias.unregisterOnSharedPreferenceChangeListener(escucha)
    }

    companion object {
        // API pública de noticias, no necesita clave
        const val URL_API = "https://api.spaceflightnewsapi.net/v4/articles/?limit=1"
    }
}
