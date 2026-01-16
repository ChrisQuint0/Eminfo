package com.eminfo.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.eminfo.app.R
import com.eminfo.app.data.local.database.EmergencyInfoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmergencyWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        updateWidget(context)
    }

    companion object {
        fun updateWidget(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, EmergencyWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = EmergencyInfoDatabase.getDatabase(context)
                    val profile = database.emergencyProfileDao().getProfileOnce()
                    val primaryContact = database.emergencyContactDao().getPrimaryContact()

                    withContext(Dispatchers.Main) {
                        val views = RemoteViews(context.packageName, R.layout.emergency_widget)

                        // Set profile data
                        views.setTextViewText(
                            R.id.widget_name,
                            profile?.fullName?.takeIf { it.isNotBlank() } ?: "Name Not Set"
                        )

                        // Combine conditions and allergies
                        val conditionsAndAllergies = buildString {
                            val conditions = profile?.medicalConditions?.takeIf { it.isNotBlank() }
                            val allergies = profile?.allergies?.takeIf { it.isNotBlank() }

                            if (conditions != null && allergies != null) {
                                append(conditions)
                                append(" • ")
                                append(allergies)
                            } else if (conditions != null) {
                                append(conditions)
                            } else if (allergies != null) {
                                append(allergies)
                            } else {
                                append("None listed")
                            }
                        }

                        views.setTextViewText(
                            R.id.widget_conditions_allergies,
                            conditionsAndAllergies
                        )

                        // Set medications
                        views.setTextViewText(
                            R.id.widget_medications,
                            profile?.currentMedications?.takeIf { it.isNotBlank() } ?: "None listed"
                        )

                        // Set emergency contact in format: EMERGENCY CONTACT: 0912345678 (MOTHER)
                        if (primaryContact != null) {
                            val contactText = "EMERGENCY CONTACT: ${primaryContact.phoneNumber} (${primaryContact.relationship.uppercase()})"
                            views.setTextViewText(
                                R.id.widget_emergency_contact,
                                contactText
                            )

                            // Update button text with contact name
                            views.setTextViewText(
                                R.id.widget_call_button,
                                "Call ${primaryContact.name}"
                            )

                            // Set up call button
                            val callIntent = Intent(Intent.ACTION_CALL).apply {
                                data = Uri.parse("tel:${primaryContact.phoneNumber}")
                            }
                            val callPendingIntent = PendingIntent.getActivity(
                                context,
                                0,
                                callIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                            views.setOnClickPendingIntent(R.id.widget_call_button, callPendingIntent)
                        } else {
                            views.setTextViewText(
                                R.id.widget_emergency_contact,
                                "EMERGENCY CONTACT: Not Set"
                            )
                            views.setTextViewText(
                                R.id.widget_call_button,
                                "Call Emergency Contact"
                            )
                        }

                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}