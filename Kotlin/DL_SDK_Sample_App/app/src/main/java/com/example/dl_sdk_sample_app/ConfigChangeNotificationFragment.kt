package com.example.dl_sdk_sample_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.datalogic.device.Intents
import com.example.dl_sdk_sample_app.databinding.FragmentConfigChangeNotificationBinding

class ConfigChangeNotificationFragment : Fragment() {

    private var _binding: FragmentConfigChangeNotificationBinding? = null
    private val binding get() = _binding!!

    private val CHANNEL_ID = "configuration_changes"

    private lateinit var notificationManager: NotificationManager
    private lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        registerConfigChangeReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(receiver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfigChangeNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Creates a notification channel for configuration changes.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Configuration Changes"
            val descriptionText = "Notifications for configuration changes"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Registers a BroadcastReceiver to listen for configuration changes.
     */
    private fun registerConfigChangeReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intents.ACTION_CONFIGURATION_CHANGED) {
                    // Retrieve extras safely
                    val changedProps = intent.getSerializableExtra(Intents.EXTRA_CONFIGURATION_CHANGED_MAP) as? HashMap<Int, String>
                    val time = intent.getLongExtra(Intents.EXTRA_CONFIGURATION_CHANGED_TIME, 0L)

                    // Prepare notification
                    val notificationIntent = Intent(context, HomeActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(
                        context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
                    )

                    val changedPropsText = changedProps?.entries?.joinToString(", ") { "${it.key}=${it.value}" } ?: "Unknown"

                    val notification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setContentTitle("Configuration Changed")
                        .setContentText("Properties changed: $changedPropsText")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()

                    notificationManager.notify(0, notification)
                }
            }
        }

        val filter = IntentFilter(Intents.ACTION_CONFIGURATION_CHANGED)
        // Removed addFlags as IntentFilter does not support it
        context?.registerReceiver(receiver, filter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
