package com.example.dl_sdk_sample_app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.datalogic.device.ErrorManager;
import com.datalogic.device.configuration.*;
import com.example.dl_sdk_sample_app.databinding.FragmentConfigurePropertiesBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigurePropertiesFragment extends Fragment {

    private FragmentConfigurePropertiesBinding binding;
    private ConfigurationManager configurationManager;
    private final java.util.Map<String, Property<?>> propertiesMap = new java.util.HashMap<>();
    private Property<?> selectedProperty;
    private View currentInputView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        binding = FragmentConfigurePropertiesBinding.inflate(inflater, container, false);
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

        binding.buttonFetchProperties.setOnClickListener(v -> fetchProperties());

        binding.spinnerProperties.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String propertyName = (String) parent.getItemAtPosition(position);
                selectedProperty = propertiesMap.get(propertyName);
                displayInputForProperty(selectedProperty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
                selectedProperty = null;
                binding.linearLayoutInput.removeAllViews();
            }
        });

        binding.buttonApplyChanges.setOnClickListener(v -> applyConfigurationChanges());
    }

    private void fetchProperties(){
        try {
            PropertyGroup rootGroup = configurationManager.getTreeRoot();
            List<Property<?>> allProperties = getAllProperties(rootGroup);
            List<String> propertyNames = new ArrayList<>();
            propertiesMap.clear();

            for(Property<?> prop : allProperties){
                if(prop.isSupported() && !prop.isReadOnly()){
                    String propertyName = prop.getName();
                    propertyNames.add(propertyName);
                    propertiesMap.put(propertyName, prop);
                }
            }

            propertyNames.sort(String::compareTo);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    propertyNames
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerProperties.setAdapter(adapter);

            Toast.makeText(getContext(), "Properties fetched successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to fetch properties", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Property<?>> getAllProperties(PropertyGroup group){
        List<Property<?>> properties = new ArrayList<Property<?>>();
        for(Property prop : group.getProperties()){
            properties.add(prop);
        }
        for(PropertyGroup childGroup : group.getGroups()){
            properties.addAll(getAllProperties(childGroup));
        }
        return properties;
    }

    private void displayInputForProperty(Property<?> property){
        binding.linearLayoutInput.removeAllViews();
        currentInputView = null;

        if(property == null){
            return;
        }

        Context context = requireContext();
        if(property instanceof BooleanProperty){
            Switch switchView = new Switch(context);
            switchView.setChecked(((BooleanProperty) property).get());
            binding.linearLayoutInput.addView(switchView);
            currentInputView = switchView;
        } else if(property instanceof EnumProperty<?>){
            Spinner spinner = new Spinner(context);
            Enum<?>[] enums = ((EnumProperty<?>) property).getEnumConstants();
            if(enums != null){
                String[] enumNames = new String[enums.length];
                for(int i=0;i<enums.length;i++) enumNames[i] = enums[i].toString();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, enumNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                Object currentValue = ((EnumProperty<?>) property).get();
                int index = java.util.Arrays.asList(enums).indexOf(currentValue);
                if(index >=0 ) spinner.setSelection(index);
            }
            binding.linearLayoutInput.addView(spinner);
            currentInputView = spinner;
        } else if(property instanceof NumericProperty){
            EditText editText = new EditText(context);
            editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
            editText.setText(String.valueOf(((NumericProperty) property).get()));
            binding.linearLayoutInput.addView(editText);
            currentInputView = editText;
        } else if(property instanceof TextProperty){
            EditText editText = new EditText(context);
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
            editText.setText(((TextProperty) property).get());
            binding.linearLayoutInput.addView(editText);
            currentInputView = editText;
        } else if(property instanceof BlobProperty){
            Button button = new Button(context);
            button.setText("Manage " + property.getName());
            button.setOnClickListener(v -> manageBlobProperty((BlobProperty) property));
            binding.linearLayoutInput.addView(button);
            currentInputView = button;
        } else {
            Toast.makeText(getContext(), "Unsupported property type: " + property.getType(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("unchecked")
    private void applyConfigurationChanges(){
        if(selectedProperty == null || currentInputView == null){
            Toast.makeText(getContext(), "No property selected or input view is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if(selectedProperty instanceof BooleanProperty){
                boolean value = ((Switch)currentInputView).isChecked();
                ((BooleanProperty) selectedProperty).set(value);
            } else if(selectedProperty instanceof EnumProperty<?>){
                Spinner spinner = (Spinner) currentInputView;
                int selectedPosition = spinner.getSelectedItemPosition();
                Enum<?>[] enums = ((EnumProperty<?>) selectedProperty).getEnumConstants();
                if(enums != null && selectedPosition < enums.length){
                    Enum<?> selectedEnum = enums[selectedPosition];
                    // Use raw type to avoid generic type issues
                    ((EnumProperty) selectedProperty).set(selectedEnum);
                }
            } else if(selectedProperty instanceof NumericProperty){
                EditText editText = (EditText) currentInputView;
                String input = editText.getText().toString();
                try {
                    int value = Integer.parseInt(input);
                    ((NumericProperty) selectedProperty).set(value);
                } catch (NumberFormatException e){
                    Toast.makeText(getContext(), "Invalid input for " + selectedProperty.getName(), Toast.LENGTH_SHORT).show();
                    return;
                }
            } else if(selectedProperty instanceof TextProperty){
                EditText editText = (EditText) currentInputView;
                String input = editText.getText().toString();
                ((TextProperty) selectedProperty).set(input);
            } else {
                Toast.makeText(getContext(), "Unsupported property type", Toast.LENGTH_SHORT).show();
                return;
            }
            configurationManager.commit();
            Toast.makeText(getContext(), "Configuration updated successfully", Toast.LENGTH_SHORT).show();
        } catch (ConfigException e){
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to apply configuration: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "An unexpected error occurred.", Toast.LENGTH_LONG).show();
        }
    }

    private void manageBlobProperty(BlobProperty blobProperty){
        if(blobProperty == null || !blobProperty.isSupported()){
            Toast.makeText(getContext(), "Blob Property not supported", Toast.LENGTH_SHORT).show();
            return;
        }
        // Implement blob management as needed
        Toast.makeText(getContext(), "Managing Blob Property: " + blobProperty.getName() + " (Not Implemented)", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}
