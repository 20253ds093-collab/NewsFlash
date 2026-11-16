package com.utez.newsflash.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.utez.newsflash.model.NoticiasRepositorio
import com.utez.newsflash.notificaciones.NotificadorNoticias
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tarea en segundo plano: consulta la API y, si hay una noticia nueva,
 * lanza la notificación. WorkManager la ejecuta aunque la app esté cerrada.
 */
class NoticiasWorker(context: Context, parametros: WorkerParameters) :
    CoroutineWorker(context, parametros) {

    override suspend fun doWork(): Result {
        val repositorio = NoticiasRepositorio(applicationContext)
        val forzar = inputData.getBoolean(ProgramadorTareas.CLAVE_FORZAR, false)
        val ahora = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        return try {
            val noticia = repositorio.obtenerUltimaNoticia()

            // Solo se notifica si la noticia es nueva (o si se pidió forzar la prueba)
            if (forzar || noticia.id != repositorio.leerUltimoId()) {
                NotificadorNoticias.mostrar(applicationContext, noticia)
                repositorio.guardarUltimaNoticia(noticia)
                repositorio.guardarRevision(ahora, "Noticia nueva: se envió la notificación")
            } else {
                repositorio.guardarRevision(ahora, "Sin noticias nuevas")
            }
            Result.success()
        } catch (e: Exception) {
            // Sin internet o error de la API: WorkManager lo intenta de nuevo más tarde
            repositorio.guardarRevision(ahora, "Error al consultar la API: se reintentará")
            Result.retry()
        }
    }
}
