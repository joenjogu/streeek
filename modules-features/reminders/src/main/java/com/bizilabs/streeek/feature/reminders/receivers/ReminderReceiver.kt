package com.bizilabs.streeek.feature.reminders.receivers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.icu.util.Calendar
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bizilabs.streeek.feature.reminders.manager.ReminderManager
import com.bizilabs.streeek.lib.domain.helpers.asJson
import com.bizilabs.streeek.lib.domain.helpers.buildUri
import com.bizilabs.streeek.lib.domain.managers.NotificationChannel
import com.bizilabs.streeek.lib.domain.managers.NotificationManager
import com.bizilabs.streeek.lib.domain.managers.Notifications
import com.bizilabs.streeek.lib.domain.models.notifications.NotificationResult
import com.bizilabs.streeek.lib.domain.repositories.WorkersRepository
import com.bizilabs.streeek.lib.resources.images.SafiDrawables
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

class ReminderReceiver : BroadcastReceiver() {
    private val workersRepository: WorkersRepository by inject(WorkersRepository::class.java)
    private val reminderManager: ReminderManager by inject(ReminderManager::class.java)
    private val notificationManager: NotificationManager by inject(NotificationManager::class.java)
    private val quirkyNotifications =
        listOf(
            "Keep the Streak Alive!" to "GitHub called. It misses you! Commit now! 💻",
            "Don't Break the Chain!" to "Your streak is crying in the corner. Save it with a commit! 😢",
            "Code. Commit. Conquer." to "Be a hero. Commit today and keep the streak alive! 🦸‍♂️",
            "Oops! Almost Forgot?" to "Your GitHub streak is on thin ice. Time to commit! ❄️",
            "Git it Done!" to "One commit today keeps the streak alive forever. Maybe. 😉",
            "Tick-Tock!" to "The clock is ticking, and so is your streak's patience! 🕒",
            "The Streak is Strong With You!" to "Don't let the dark side break your streak. 🛡️",
            "One More Commit!" to "It's just one commit. What's stopping you? 😏",
            "Save the Streak!" to "Your streak deserves better. Show it some love! ❤️",
            "Streaks are Sacred!" to "A commit a day keeps the streak alive. Don’t ghost it! 👻",
        )

    lateinit var label: String
    lateinit var day: Number
    lateinit var code: Number

    enum class ReminderActions(
        val action: String,
        val label: String,
    ) {
        SNOOZE("streeek.action.reminder.snooze", "snooze"),
        CANCEL("streeek.action.reminder.cancel", "cancel"),
        DELETE("streeek.action.reminder.delete", "delete"),
    }

    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        Timber.d("Reminder Receiver got their attention")
        if (intent == null) return
        if (context == null) return
        val isReminder = intent.getStringExtra("streeek.receiver.type").equals("reminder")
        Timber.d("Is a reminder type -> $isReminder")
        if (isReminder.not()) return
        label = intent.getStringExtra("reminder.label") ?: ""
        day = intent.getIntExtra("reminder.day", -1)
        code = intent.getIntExtra("reminder.code", -1)
        val type = intent.getStringExtra("streeek.reminder.type")
        Timber.d(
            "Reminder Values -> ${
                buildString {
                    append("\n")
                    append("label = $label")
                    append("\n")
                    append("day = $day")
                    append("\n")
                    append("code = $code")
                    append("\n")
                    append("type = $type")
                }
            }",
        )
        with(context) {
            workersRepository.runReminder(isStarting = false)
            when (type) {
                "action" -> handleAction(intent = intent)
                "ring" -> handleRing()
                else -> {}
            }
        }
    }

    private fun Context.handleRing() {
        notify()
        workersRepository.runReminder(isStarting = true)
    }

    private fun Context.handleAction(intent: Intent) {
        val action = intent.action ?: return
        Timber.d("Actioning on the stuff -> $action")
        cancelNotifications()
        stopPlayingAllReminders()
        when (action) {
            ReminderActions.SNOOZE.action -> snoozeReminder()
            else -> cancelReminder()
        }
    }

    private fun Context.cancelNotifications() {
        Timber.d("Cancelling all notifications")
        NotificationManagerCompat.from(this).cancelAll()
    }

    private fun Context.stopPlayingAllReminders() {
        val player =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                MediaPlayer(this)
            } else {
                MediaPlayer()
            }
        player.stop()
        player.release()
    }

    private fun Context.snoozeReminder() {
        Timber.d("Snoozing Reminder")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendar.timeInMillis + (1 * 60 * 1000)
        reminderManager.createAlarm(
            label = label,
            day = day.toInt(),
            millis = calendar.timeInMillis,
        )
    }

    private fun Context.cancelReminder() {
        Timber.d("Cancelling Reminder")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendar.timeInMillis + (7 * 24 * 60 * 60 * 1000)
        reminderManager.createAlarm(
            label = label,
            day = day.toInt(),
            millis = calendar.timeInMillis,
        )
    }

    private fun Context.notify() {
        val actions = getNotificationActions()
        val clickIntent = getClickIntent()
        val swipeIntent = getSwipeIntent()
        val (title, body) = quirkyNotifications.random()
        notify(
            title = title,
            body = body,
            channel = Notifications.Channels.reminders,
            actions = actions,
            clickIntent = clickIntent,
            swipeIntent = swipeIntent,
        )
    }

    private fun Context.getNotificationActions(): List<NotificationCompat.Action> {
        val actions =
            ReminderActions.entries.filter { it != ReminderActions.DELETE }.map { value ->
                val intent =
                    Intent(this, ReminderReceiver::class.java).apply {
                        putExtra("streeek.receiver.type", "reminder")
                        putExtra("streeek.reminder.type", "action")
                        putExtra("reminder.label", label)
                        putExtra("reminder.code", code)
                        putExtra("reminder.day", day)
                        action = value.action
                    }
                val pendingIntent: PendingIntent =
                    PendingIntent.getBroadcast(
                        this,
                        code.toInt(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    )
                NotificationCompat.Action.Builder(
                    SafiDrawables.IconNotification,
                    value.label,
                    pendingIntent,
                ).build()
            }
        return actions
    }

    private fun Context.getClickIntent(): PendingIntent? {
        val intent =
            Intent().apply {
                setClassName(
                    this@getClickIntent,
                    "com.bizilabs.streeek.lib.presentation.MainActivity",
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(
                    "notification_result",
                    NotificationResult(
                        type = "reminder",
                        uri =
                            buildUri(
                                "action" to "navigate",
                                "destination" to "REMINDERS",
                                "label" to label,
                                "day" to day,
                                "code" to code,
                            ),
                    ).asJson(),
                )
            }
        val contentIntent =
            PendingIntent.getActivity(
                this,
                code.toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        return contentIntent
    }

    private fun Context.getSwipeIntent(): PendingIntent? {
        val intent =
            Intent(this, ReminderReceiver::class.java).apply {
                putExtra("streeek.receiver.type", "reminder")
                putExtra("streeek.reminder.type", "action")
                putExtra("reminder.label", label)
                putExtra("reminder.code", code)
                putExtra("reminder.day", day)
                action = ReminderActions.DELETE.action
            }
        return PendingIntent.getBroadcast(
            this,
            code.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}

fun Context.notify(
    title: String,
    body: String,
    channel: NotificationChannel,
    imageUrl: Uri? = null,
) {
    createNotificationAndSend(
        title = title,
        body = body,
        channel = channel,
        imageUrl = imageUrl,
    )
}

fun Context.notify(
    title: String,
    body: String,
    channel: NotificationChannel,
    imageUrl: Uri? = null,
    clickIntent: PendingIntent? = null,
    swipeIntent: PendingIntent? = null,
    actions: List<NotificationCompat.Action> = emptyList(),
) {
    createNotificationAndSend(
        title = title,
        body = body,
        channel = channel,
        imageUrl = imageUrl,
        actions = actions,
        clickIntent = clickIntent,
        swipeIntent = swipeIntent,
    )
}

@SuppressLint("MissingPermission")
private fun Context.createNotificationAndSend(
    title: String,
    body: String,
    channel: NotificationChannel,
    imageUrl: Uri? = null,
    clickIntent: PendingIntent? = null,
    swipeIntent: PendingIntent? = null,
    actions: List<NotificationCompat.Action> = emptyList(),
) {
    val notification =
        createNotification(
            clickIntent = clickIntent,
            imageUrl = imageUrl,
            channel = channel,
            title = title,
            body = body,
            actions = actions,
            swipeIntent = swipeIntent,
        )

    val id = System.currentTimeMillis().toInt()
    val notificationManager =
        this.getSystemService(android.app.NotificationManager::class.java) as android.app.NotificationManager

    notificationManager.notify(id, notification)
}

fun Context.createNotification(
    title: String,
    body: String,
    clickIntent: PendingIntent?,
    swipeIntent: PendingIntent?,
    imageUrl: Uri?,
    channel: NotificationChannel,
    actions: List<NotificationCompat.Action>,
): Notification {
    val intent =
        Intent().apply {
            setClassName(
                this@createNotification,
                "com.bizilabs.streeek.lib.presentation.MainActivity",
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

    val contentIntent =
        clickIntent ?: PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

    val bitmap =
        try {
            imageUrl?.let {
                val source = ImageDecoder.createSource(contentResolver, imageUrl)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: RuntimeException) {
            Timber.e(e)
            null
        }

    val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    val notification =
        NotificationCompat.Builder(this, channel.id)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(SafiDrawables.IconNotification)
            .setAutoCancel(true)
            .setGroup(channel.group.id)
            .setContentIntent(contentIntent)
            .setDeleteIntent(swipeIntent)
            .setSound(sound)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(clickIntent, true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .apply {
                actions.forEach { addAction(it) }
                if (bitmap != null) {
                    setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                } else {
                    setStyle(NotificationCompat.BigTextStyle().bigText(body))
                }
            }
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    return notification
}
