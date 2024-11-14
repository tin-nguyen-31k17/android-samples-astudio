package com.example.dl_sdk_sample_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.dl_sdk_sample_app.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        displayAppInfo();
        return binding.getRoot();
    }

    private void displayAppInfo() {
        String appName = getString(R.string.app_name);
        String versionName = BuildConfig.VERSION_NAME;

        String libraries = "Datalogic SDK v1.39\nAndroidX Libraries\nKotlin Standard Library v1.8.10";

        StringBuilder appInfoText = new StringBuilder();
        appInfoText.append("App Name: ").append(appName).append("\n")
                .append("Version: ").append(versionName).append("\n")
                .append("Libraries in Use:\n").append(libraries).append("\n\n")
                .append("Function Descriptions:\n• Func0: Basic scanning functionality with Datalogic SDK.\n")
                .append("• Func1: Placeholder for additional SDK functionality.\n")
                .append("• Func2: Placeholder for additional SDK functionality.\n");

        binding.textViewAppInfo.setText(appInfoText.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
