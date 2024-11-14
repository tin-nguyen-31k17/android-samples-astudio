package com.example.dl_sdk_sample_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.datalogic.device.Intents;
import com.example.dl_sdk_sample_app.databinding.FragmentConfigureIntentBinding;
import java.util.HashMap;

public class ConfigureIntentFragment extends Fragment {

    private FragmentConfigureIntentBinding binding;
    private final String[] properties = {
            "com.datalogic.device.configuration.CAMERA_FLASH",
            "com.datalogic.device.configuration.KEYBOARD_TYPE"
            // Add other property names you want to configure
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        binding = FragmentConfigureIntentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                properties
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerProperties.setAdapter(adapter);

        binding.buttonSendIntent.setOnClickListener(v -> sendConfigurationIntent());
    }

    private void sendConfigurationIntent(){
        String propertyName = (String) binding.spinnerProperties.getSelectedItem();
        String propertyValue = binding.editTextValue.getText().toString();

        if(propertyValue.isEmpty()){
            Toast.makeText(getContext(), "Please enter a value for the property.", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<Integer, String> changedMap = new HashMap<>();
        Integer propertyID = getPropertyID(propertyName);
        if(propertyID != null){
            changedMap.put(propertyID, propertyValue);
        } else {
            Toast.makeText(getContext(), "Invalid property selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intents.ACTION_CONFIGURATION_COMMIT);
        intent.putExtra(Intents.EXTRA_CONFIGURATION_CHANGED_MAP, changedMap);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        requireContext().sendBroadcast(intent);
        Toast.makeText(getContext(), "Configuration intent sent successfully.", Toast.LENGTH_SHORT).show();
    }

    private Integer getPropertyID(String propertyName){
        switch(propertyName){
            case "com.datalogic.device.configuration.CAMERA_FLASH":
                return 101;
            case "com.datalogic.device.configuration.KEYBOARD_TYPE":
                return 102;
            // Add actual PropertyID mappings
            default:
                return null;
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}
