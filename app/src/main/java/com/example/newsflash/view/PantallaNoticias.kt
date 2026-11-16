package com.utez.newsflash.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.utez.newsflash.viewmodel.NoticiasViewModel

/** Pantalla principal: solo muestra el estado del ViewModel y avisa de los botones. */
@Composable
fun PantallaNoticias(viewModel: NoticiasViewModel) {
    val estado = viewModel.estado

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("NewsFlash", style = MaterialTheme.typography.headlineMedium)
        Text("Alertas de noticias de última hora")
        Spacer(Modifier.height(12.dp))

        Text(
            if (estado.revisionActiva) "Revisión automática: activa (cada 15 minutos)"
            else "Revisión automática: detenida"
        )
        Spacer(Modifier.height(8.dp))

        Row {
            Button(onClick = { viewModel.consultarAhora() }) {
                Text("Consultar ahora")
            }
            Spacer(Modifier.width(12.dp))
            if (estado.revisionActiva) {
                Button(onClick = { viewModel.detenerRevision() }) { Text("Detener") }
            } else {
                Button(onClick = { viewModel.activarRevision() }) { Text("Activar") }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(estado.mensaje)
        Spacer(Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Última noticia notificada", style = MaterialTheme.typography.titleMedium)
                val noticia = estado.ultimaNoticia
                if (noticia == null) {
                    Text("Todavía no hay noticias.")
                } else {
                    Text(noticia.titulo, style = MaterialTheme.typography.titleSmall)
                    Text("Fuente: ${noticia.fuente}")
                    Spacer(Modifier.height(4.dp))
                    Text(noticia.resumen)
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Última revisión", style = MaterialTheme.typography.titleMedium)
                Text(estado.ultimaRevision)
                Text(estado.resultadoRevision)
            }
        }
    }
}
