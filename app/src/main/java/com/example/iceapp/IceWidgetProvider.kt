package com.example.iceapp

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class IceWidgetProvider : AppWidgetProvider() {

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

        // This listens for the custom action we attach to the button
        if (intent.action == "ACTION_TRIGGER_SOS") {
            // Keep the background process alive while GPS fetches
            val pendingResult = goAsync()

            EmergencyAction.triggerSOS(context) {
                // Tell Android we are done so it can clean up
                pendingResult.finish()
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_ice)

        // Create the custom intent
        val intent = Intent(context, IceWidgetProvider::class.java).apply {
            action = "ACTION_TRIGGER_SOS"
        }

        // Use PendingIntent.getBroadcast for Stealth Mode
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Attach it to the button
        views.setOnClickPendingIntent(R.id.btnWidgetSOS, pendingIntent)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}