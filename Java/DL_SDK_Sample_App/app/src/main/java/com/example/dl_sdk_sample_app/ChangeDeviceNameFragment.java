package com.example.dl_sdk_sample_app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.datalogic.device.ErrorManager;
import com.datalogic.device.configuration.*;
import com.example.dl_sdk_sample_app.databinding.FragmentChangeDeviceNameBinding;

public class ChangeDeviceNameFragment extends Fragment {

    private FragmentChangeDeviceNameBinding binding;
    private ConfigurationManager configurationManager;
    private final int DEVICE_NAME_SUFFIX = PropertyID.DEVICE_NAME_SUFFIX;
    private Property<String> deviceNameSuffixProperty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        binding = FragmentChangeDeviceNameBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ErrorManager.enableExceptions(true);
        try {
            configurationManager = new ConfigurationManager(requireContext());
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to initialize ConfigurationManager", Toast.LENGTH_SHORT).show();
            return;
        }
        fetchDeviceNameSuffixProperty();
        binding.buttonApplyDeviceName.setOnClickListener(v -> applyDeviceNameSuffix());
    }

    private void fetchDeviceNameSuffixProperty(){
        try {
            Property<?> property = configurationManager.getPropertyById(DEVICE_NAME_SUFFIX);
            Log.d("ChangeDeviceNameFragment", "Retrieved DEVICE_NAME_SUFFIX: " + property);
            if(property != null && property.isSupported() && !property.isReadOnly()){
                deviceNameSuffixProperty = (Property<String>) property;
                String currentSuffix = deviceNameSuffixProperty.get();
                binding.editTextDeviceNameSuffix.setText(currentSuffix);
            } else {
                Toast.makeText(getContext(), "Device Name Suffix not supported or read-only", Toast.LENGTH_SHORT).show();
                binding.editTextDeviceNameSuffix.setEnabled(false);
                binding.buttonApplyDeviceName.setEnabled(false);
            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to retrieve Device Name Suffix: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            binding.editTextDeviceNameSuffix.setEnabled(false);
            binding.buttonApplyDeviceName.setEnabled(false);
        }
    }

    private void applyDeviceNameSuffix(){
        if(deviceNameSuffixProperty == null){
            Toast.makeText(getContext(), "Device Name Suffix property is not available.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String newSuffix = binding.editTextDeviceNameSuffix.getText().toString().trim();
            if(newSuffix.isEmpty()){
                Toast.makeText(getContext(), "Device Name Suffix cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }
            deviceNameSuffixProperty.set(newSuffix);
            configurationManager.commit();
            Toast.makeText(getContext(), "Device Name Suffix updated successfully", Toast.LENGTH_SHORT).show();
        } catch (ConfigException e){
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to update Device Name Suffix: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "An unexpected error occurred while updating Device Name Suffix.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}
