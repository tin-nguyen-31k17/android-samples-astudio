package com.example.dl_sdk_sample_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import com.datalogic.device.Intents;
import com.example.dl_sdk_sample_app.databinding.FragmentConfigChangeNotificationBinding;
import java.util.HashMap;
import java.util.Map;

public class ConfigChangeNotificationFragment extends Fragment {

    private FragmentConfigChangeNotificationBinding binding;
    private final String CHANNEL_ID = "configuration_changes";
    private NotificationManager notificationManager;
    private BroadcastReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        registerConfigChangeReceiver();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(receiver != null){
            getContext().unregisterReceiver(receiver);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        binding = FragmentConfigChangeNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Configuration Changes";
            String description = "Notifications for configuration changes";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void registerConfigChangeReceiver(){
        receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                if(Intents.ACTION_CONFIGURATION_CHANGED.equals(intent.getAction())){
                    HashMap<Integer, String> changedProps = (HashMap<Integer, String>) intent.getSerializableExtra(Intents.EXTRA_CONFIGURATION_CHANGED_MAP);
                    long time = intent.getLongExtra(Intents.EXTRA_CONFIGURATION_CHANGED_TIME, 0L);

                    Intent notificationIntent = new Intent(context, HomeActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
                    );

                    String changedPropsText = "Unknown";
                    if(changedProps != null){
                        StringBuilder sb = new StringBuilder();
                        for(Map.Entry<Integer, String> entry : changedProps.entrySet()){
                            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
                        }
                        if(sb.length() > 2){
                            sb.setLength(sb.length()-2); // Remove last comma and space
                            changedPropsText = sb.toString();
                        }
                    }

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notifications)
                            .setContentTitle("Configuration Changed")
                            .setContentText("Properties changed: " + changedPropsText)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

                    notificationManager.notify(0, builder.build());
                }
            }
        };

        IntentFilter filter = new IntentFilter(Intents.ACTION_CONFIGURATION_CHANGED);
        getContext().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}
