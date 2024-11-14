package com.example.dl_sdk_sample_app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.datalogic.device.ErrorManager;
import com.datalogic.device.configuration.*;
import com.example.dl_sdk_sample_app.databinding.FragmentBluetoothSilentPairingBinding;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BluetoothSilentPairingFragment extends Fragment {

    private FragmentBluetoothSilentPairingBinding binding;
    private ConfigurationManager configurationManager;
    private final String[] btPropertyNames = {
            "BT_DISCOVERABILITY",
            "BT_PAIRING_POLICY",
            "BT_SILENT_PAIRING_TRUSTED_ENABLE",
            "BT_SILENT_PAIRING_WHITELISTING_ENABLE",
            "BT_SILENT_PAIRING_WHITELISTING"
    };
    private final Map<Property<?>, View> propertyViews = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        binding = FragmentBluetoothSilentPairingBinding.inflate(inflater, container, false);
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
        setupPropertyInputs();
        binding.buttonApplyBtSettings.setOnClickListener(v -> applyBluetoothSettings());
    }

    private void setupPropertyInputs(){
        List<Property<?>> btProperties = new ArrayList<>();
        for(String name : btPropertyNames){
            try {
                Property<?> prop = configurationManager.getPropertyByName(name);
                if(prop == null){
                    Log.e("BluetoothSilentPairingFragment", "Property '" + name + "' is null. Check if the property name is correct.");
                    Toast.makeText(getContext(), "Property '" + name + "' not found.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("BluetoothSilentPairingFragment", "Property '" + name + "' retrieved successfully.");
                    btProperties.add(prop);
                }
            } catch (Exception e){
                Log.e("BluetoothSilentPairingFragment", "Error retrieving property '" + name + "': " + e.getMessage());
            }
        }
        for(Property<?> property : btProperties){
            if(property.isSupported() && !property.isReadOnly()){
                View inputView = createInputForProperty(property);
                if(inputView != null){
                    binding.linearLayoutBtProperties.addView(inputView);
                    propertyViews.put(property, inputView);
                }
            }
        }
    }

    private View createInputForProperty(Property<?> property){
        Context context = requireContext();
        String propertyName = property.getName();
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        container.setPadding(0, 8, 0, 8);

        TextView label = new TextView(context);
        label.setText(propertyName);
        label.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        if(property instanceof BooleanProperty){
            Switch switchView = new Switch(context);
            switchView.setChecked(((BooleanProperty) property).get());
            container.addView(label);
            container.addView(switchView);
            return container;
        } else if(property instanceof EnumProperty<?>){
            Spinner spinner = new Spinner(context);
            Enum<?>[] enumConstants = ((EnumProperty<?>) property).getEnumConstants();
            if(enumConstants != null){
                String[] enumNames = new String[enumConstants.length];
                for(int i=0;i<enumConstants.length;i++) enumNames[i] = enumConstants[i].toString();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, enumNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                Object currentValue = ((EnumProperty<?>) property).get();
                int index = java.util.Arrays.asList(enumConstants).indexOf(currentValue);
                if(index >=0 ) spinner.setSelection(index);
            }
            container.addView(label);
            container.addView(spinner);
            return container;
        } else if(property instanceof TextProperty){
            EditText editText = new EditText(context);
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
            editText.setText(((TextProperty) property).get());
            container.addView(label);
            container.addView(editText);
            return container;
        } else {
            Toast.makeText(getContext(), "Unsupported property type: " + property.getType(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void applyBluetoothSettings(){
        try {
            for(Map.Entry<Property<?>, View> entry : propertyViews.entrySet()){
                Property<?> property = entry.getKey();
                View view = entry.getValue();
                if(property instanceof BooleanProperty){
                    boolean value = ((Switch)((LinearLayout)view).getChildAt(1)).isChecked();
                    ((BooleanProperty) property).set(value);
                } else if(property instanceof EnumProperty){
                    Spinner spinner = (Spinner)((LinearLayout)view).getChildAt(1);
                    int selectedPosition = spinner.getSelectedItemPosition();
                    Enum<?>[] enums = ((EnumProperty<?>) property).getEnumConstants();
                    if(enums != null && selectedPosition < enums.length){
                        Enum<?> enumValue = enums[selectedPosition];
                        try {
                            Method setMethod = property.getClass().getMethod("set", enumValue.getClass());
                            setMethod.invoke(property, enumValue);
                        } catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Set method not found for property: " + property.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if(property instanceof TextProperty){
                    String input = ((EditText)((LinearLayout)view).getChildAt(1)).getText().toString();
                    ((TextProperty) property).set(input);
                }
            }
            configurationManager.commit();
            Toast.makeText(getContext(), "Bluetooth settings applied successfully", Toast.LENGTH_SHORT).show();
        } catch (ConfigException e){
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to apply Bluetooth settings: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "An unexpected error occurred while applying settings.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}
