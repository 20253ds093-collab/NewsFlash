package com.utez.newsflash.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

/** Programa y cancela las tareas en segundo plano con WorkManager. */
object ProgramadorTareas {

    const val CLAVE_FORZAR = "forzar"
    private const val NOMBRE_TAREA = "revision_noticias"

    // Restricciones para ahorrar recursos: solo con internet y batería no baja
    private val restricciones = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()

    /** Revisión automática cada 15 minutos (el mínimo que permite WorkManager). */
    fun iniciarRevisionPeriodica(context: Context) {
        val peticion = PeriodicWorkRequestBuilder<NoticiasWorker>(15, TimeUnit.MINUTES)
            .setConstraints(restricciones)
            .build()
        // KEEP: si ya existe la tarea no se crea otra, así no se duplica
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NOMBRE_TAREA, ExistingPeriodicWorkPolicy.KEEP, peticion
        )
    }

    fun detenerRevisionPeriodica(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(NOMBRE_TAREA)
    }

    /** Ejecuta una revisión inmediata (para hacer pruebas sin esperar 15 minutos). */
    fun consultarAhora(context: Context) {
        val peticion = OneTimeWorkRequestBuilder<NoticiasWorker>()
            .setConstraints(restricciones)
            .setInputData(workDataOf(CLAVE_FORZAR to true))
            .build()
        WorkManager.getInstance(context).enqueue(peticion)
    }
}
