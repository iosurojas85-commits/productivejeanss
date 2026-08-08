package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

class ProductiveJeansWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        if (action == ACTION_TOGGLE_TIMER || action == ACTION_REFRESH_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, ProductiveJeansWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            
            var isRunning = intent.getBooleanExtra(EXTRA_IS_RUNNING, false)
            var remainingSecs = intent.getIntExtra(EXTRA_REMAINING_SECS, 1500)
            val companionName = intent.getStringExtra(EXTRA_COMPANION) ?: "Hanni 🌸"

            if (action == ACTION_TOGGLE_TIMER) {
                // Broadcast to active ViewModel
                val broadcastIntent = Intent(ACTION_VIEWMODEL_TOGGLE_TIMER)
                context.sendBroadcast(broadcastIntent)

                // Toggle local state feedback
                isRunning = !isRunning
            }

            for (appWidgetId in allWidgetIds) {
                updateAppWidgetState(
                    context,
                    appWidgetManager,
                    appWidgetId,
                    isRunning,
                    remainingSecs,
                    companionName
                )
            }
        }
    }

    companion object {
        const val ACTION_TOGGLE_TIMER = "com.example.widget.ACTION_TOGGLE_TIMER"
        const val ACTION_REFRESH_WIDGET = "com.example.widget.ACTION_REFRESH_WIDGET"
        const val ACTION_VIEWMODEL_TOGGLE_TIMER = "com.example.widget.ACTION_VIEWMODEL_TOGGLE_TIMER"
        const val EXTRA_IS_RUNNING = "extra_is_running"
        const val EXTRA_REMAINING_SECS = "extra_remaining_secs"
        const val EXTRA_COMPANION = "extra_companion"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            updateAppWidgetState(context, appWidgetManager, appWidgetId, false, 1500, "Hanni 🌸")
        }

        private fun updateAppWidgetState(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            isRunning: Boolean,
            remainingSeconds: Int,
            companionName: String
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_pomodoro)

            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            val timeText = String.format("%02d:%02d", minutes, seconds)

            val bgColor = when {
                companionName.contains("Hanni", ignoreCase = true) -> android.graphics.Color.parseColor("#FFFFE0EF")
                companionName.contains("Danielle", ignoreCase = true) || companionName.contains("Dani", ignoreCase = true) -> android.graphics.Color.parseColor("#FFFFFDE0")
                companionName.contains("Minji", ignoreCase = true) -> android.graphics.Color.parseColor("#FFE1F5FE")
                companionName.contains("Haerin", ignoreCase = true) -> android.graphics.Color.parseColor("#FFF1F8E9")
                companionName.contains("Hyein", ignoreCase = true) -> android.graphics.Color.parseColor("#FFF3E5F5")
                else -> android.graphics.Color.parseColor("#FFFFFFFF")
            }
            views.setInt(R.id.widget_inner_bg, "setBackgroundColor", bgColor)

            views.setTextViewText(R.id.widget_timer_text, timeText)
            views.setTextViewText(R.id.widget_subtitle, " • $companionName")
            views.setTextViewText(
                R.id.widget_status_pill,
                if (isRunning) "● FOCUSING" else "● READY"
            )
            views.setTextViewText(
                R.id.widget_btn_play,
                if (isRunning) "⏸ PAUSE" else "▶ START"
            )

            // Intent to open MainActivity on widget tap
            val mainIntent = Intent(context, MainActivity::class.java)
            val mainPendingIntent = PendingIntent.getActivity(
                context,
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, mainPendingIntent)

            // Intent to toggle timer state from widget
            val toggleIntent = Intent(context, ProductiveJeansWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE_TIMER
            }
            val togglePendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_play, togglePendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun sendWidgetUpdate(
            context: Context,
            isRunning: Boolean,
            remainingSeconds: Int,
            companionName: String
        ) {
            val intent = Intent(context, ProductiveJeansWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_WIDGET
                putExtra(EXTRA_IS_RUNNING, isRunning)
                putExtra(EXTRA_REMAINING_SECS, remainingSeconds)
                putExtra(EXTRA_COMPANION, companionName)
            }
            context.sendBroadcast(intent)
        }
    }
}
