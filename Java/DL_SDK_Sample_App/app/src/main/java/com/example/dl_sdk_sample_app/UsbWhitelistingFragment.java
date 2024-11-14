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
import com.example.dl_sdk_sample_app.databinding.FragmentUsbWhitelistingBinding;
import java.util.Map;

public class UsbWhitelistingFragment extends Fragment {

    private FragmentUsbWhitelistingBinding binding;
    private ConfigurationManager configurationManager;
    private final int USB_HOST_WHITELISTING = PropertyID.USB_HOST_WHITELISTING;
    private final int USB_PREDEFINED_HOST_WHITELISTING = PropertyID.USB_PREDEFINED_HOST_WHITELISTING;
    private Property<String> usbWhitelistProperty;
    private final java.util.List<String> whitelistEntries = new java.util.ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        binding = FragmentUsbWhitelistingBinding.inflate(inflater, container, false);
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

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, whitelistEntries);
        binding.listViewWhitelist.setAdapter(adapter);
        binding.listViewWhitelist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        fetchUsbWhitelistingProperties();

        binding.buttonAddWhitelist.setOnClickListener(v -> {
            String newDevice = binding.editTextWhitelist.getText().toString().trim();
            if(!newDevice.isEmpty() && !whitelistEntries.contains(newDevice)){
                whitelistEntries.add(newDevice);
                adapter.notifyDataSetChanged();
                binding.editTextWhitelist.setText("");
            } else {
                Toast.makeText(getContext(), "Invalid or duplicate USB Device ID", Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonRemoveWhitelist.setOnClickListener(v -> {
            int selectedPosition = binding.listViewWhitelist.getCheckedItemPosition();
            if(selectedPosition != ListView.INVALID_POSITION){
                String removedItem = whitelistEntries.remove(selectedPosition);
                adapter.notifyDataSetChanged();
                binding.listViewWhitelist.clearChoices();
                Toast.makeText(getContext(), "Removed: " + removedItem, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "No item selected to remove", Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonApplyWhitelist.setOnClickListener(v -> applyUsbWhitelist());
    }

    private void fetchUsbWhitelistingProperties(){
        try {
            usbWhitelistProperty = (Property<String>) configurationManager.getPropertyById(USB_HOST_WHITELISTING);
            Log.d("UsbWhitelistingFragment", "Retrieved USB_HOST_WHITELISTING: " + usbWhitelistProperty);
            if(usbWhitelistProperty == null){
                usbWhitelistProperty = (Property<String>) configurationManager.getPropertyById(USB_PREDEFINED_HOST_WHITELISTING);
                Log.d("UsbWhitelistingFragment", "Retrieved USB_PREDEFINED_HOST_WHITELISTING: " + usbWhitelistProperty);
            }

            if(usbWhitelistProperty != null && usbWhitelistProperty.isSupported() && !usbWhitelistProperty.isReadOnly()){
                String whitelistString = usbWhitelistProperty.get();
                for(String entry : whitelistString.split(",")){
                    String trimmed = entry.trim();
                    if(!trimmed.isEmpty()) whitelistEntries.add(trimmed);
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "USB Whitelisting not supported or Read-Only", Toast.LENGTH_SHORT).show();
                binding.editTextWhitelist.setEnabled(false);
                binding.buttonAddWhitelist.setEnabled(false);
                binding.buttonRemoveWhitelist.setEnabled(false);
                binding.buttonApplyWhitelist.setEnabled(false);
            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to retrieve USB Whitelisting property: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            binding.editTextWhitelist.setEnabled(false);
            binding.buttonAddWhitelist.setEnabled(false);
            binding.buttonRemoveWhitelist.setEnabled(false);
            binding.buttonApplyWhitelist.setEnabled(false);
        }
    }

    private void applyUsbWhitelist(){
        if(usbWhitelistProperty == null){
            Toast.makeText(getContext(), "USB Whitelisting property is not available.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String updatedWhitelist = String.join(",", whitelistEntries);
            usbWhitelistProperty.set(updatedWhitelist);
            configurationManager.commit();
            Toast.makeText(getContext(), "USB Whitelist updated successfully", Toast.LENGTH_SHORT).show();
        } catch (ConfigException e){
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to update USB Whitelist: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "An unexpected error occurred while updating USB Whitelist.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}
