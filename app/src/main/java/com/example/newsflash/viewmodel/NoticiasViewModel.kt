package com.utez.newsflash.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.utez.newsflash.model.EstadoNoticias
import com.utez.newsflash.model.NoticiasRepositorio
import com.utez.newsflash.worker.ProgramadorTareas

/**
 * ViewModel: controla las tareas en segundo plano y le entrega a la pantalla
 * los datos que el Worker va guardando.
 */
class NoticiasViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = NoticiasRepositorio(app)

    // La pantalla observa este estado y se actualiza sola
    var estado by mutableStateOf(EstadoNoticias())
        private set

    // Se ejecuta cuando el Worker guarda datos nuevos: así la pantalla se entera del evento
    private val escuchaCambios = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        actualizarDatos()
    }

    init {
        repositorio.registrarEscucha(escuchaCambios)
        actualizarDatos()
        activarRevision()
    }

    fun activarRevision() {
        ProgramadorTareas.iniciarRevisionPeriodica(getApplication())
        estado = estado.copy(revisionActiva = true)
    }

    fun detenerRevision() {
        ProgramadorTareas.detenerRevisionPeriodica(getApplication())
        estado = estado.copy(revisionActiva = false)
    }

    fun consultarAhora() {
        ProgramadorTareas.consultarAhora(getApplication())
        estado = estado.copy(mensaje = "Consultando noticias...")
    }

    private fun actualizarDatos() {
        estado = estado.copy(
            ultimaNoticia = repositorio.leerUltimaNoticia(),
            ultimaRevision = repositorio.leerUltimaRevision(),
            resultadoRevision = repositorio.leerResultado(),
            mensaje = ""
        )
    }

    override fun onCleared() {
        repositorio.quitarEscucha(escuchaCambios)
    }
}
