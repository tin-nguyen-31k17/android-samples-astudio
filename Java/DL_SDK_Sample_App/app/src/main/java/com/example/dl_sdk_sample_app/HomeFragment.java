package com.example.dl_sdk_sample_app;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.dl_sdk_sample_app.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        displayAppInfo();
    }

    private void displayAppInfo(){
        String appName = getString(R.string.app_name);
        String versionName = BuildConfig.VERSION_NAME;
        String[] libraries = {
                "Datalogic SDK v1.39",
                "AndroidX Libraries",
                "Kotlin Standard Library v1.8.10",
                "Material Components for Android"
        };

        String[] functionDescriptions = {
                "Bluetooth Silent Pairing: Enables the device to pair with Bluetooth peripherals without user intervention, enhancing user experience in environments where manual pairing is cumbersome.",
                "Change Device Name: Allows users to customize the device's Bluetooth name, making it easily identifiable among multiple devices.",
                "Configuration Change Notifications: Listens for configuration changes and notifies users through system notifications, ensuring that users are always aware of the device's current settings.",
                "USB Whitelisting: Provides security by allowing only approved USB devices to connect, preventing unauthorized access and ensuring data integrity."
        };

        SpannableStringBuilder appInfo = new SpannableStringBuilder();
        appendBold(appInfo, "App Name: ");
        appInfo.append(appName).append("\n");

        appendBold(appInfo, "Version: ");
        appInfo.append(versionName).append("\n\n");

        appendBold(appInfo, "About This Demo App:\n");
        appInfo.append("This Datalogic SDK Sample App showcases various functionalities provided by the Datalogic SDK. It's designed to help developers understand how to integrate and utilize the SDK's features effectively.\n\n");

        appendBold(appInfo, "How to Use:\n");
        appInfo.append("Navigate through the app using the top navigation drawer. Each menu item corresponds to a specific feature of the SDK:\n\n");
        for (String desc : functionDescriptions){
            appInfo.append("• ");
            int colonIndex = desc.indexOf(":");
            if(colonIndex != -1){
                appendBold(appInfo, desc.substring(0, colonIndex +1) + " ");
                appInfo.append(desc.substring(colonIndex +2)).append("\n");
            } else {
                appInfo.append(desc).append("\n");
            }
        }

        appInfo.append("\nFor more detailed instructions, refer to the documentation or contact support.");
        binding.textViewAppInfo.setText(appInfo);
    }

    private void appendBold(SpannableStringBuilder ssb, String text){
        int start = ssb.length();
        ssb.append(text);
        int end = ssb.length();
        ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, 0);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}
