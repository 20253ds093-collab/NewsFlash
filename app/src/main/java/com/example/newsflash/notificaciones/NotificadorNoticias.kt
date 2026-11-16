package com.utez.newsflash.notificaciones

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.newsflash.MainActivity
import com.utez.newsflash.model.Noticia

/** Crea el canal de notificaciones y muestra las alertas de última hora. */
object NotificadorNoticias {

    private const val ID_CANAL = "noticias_ultima_hora"
    private const val ID_NOTIFICACION = 1


    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                ID_CANAL,
                "Noticias de última hora",
                NotificationManager.IMPORTANCE_HIGH
            )
            canal.description = "Avisos de nuevas noticias"
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    /** Muestra la notificación de una noticia. */
    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, noticia: Noticia) {
        crearCanal(context)

        // En Android 13 o superior se necesita el permiso del usuario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Al tocar la notificación se abre la app
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, ID_CANAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Última hora: ${noticia.fuente}")
            .setContentText(noticia.titulo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(noticia.titulo + "\n" + noticia.resumen))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Siempre el mismo ID: una noticia nueva reemplaza a la anterior y no se duplican
        NotificationManagerCompat.from(context).notify(ID_NOTIFICACION, notificacion)
    }
}
