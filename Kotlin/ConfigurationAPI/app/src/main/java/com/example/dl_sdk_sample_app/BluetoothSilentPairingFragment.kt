package com.example.dl_sdk_sample_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.datalogic.device.ErrorManager
import com.datalogic.device.configuration.*
import com.example.dl_sdk_sample_app.databinding.FragmentBluetoothSilentPairingBinding

class BluetoothSilentPairingFragment : Fragment() {

    private var _binding: FragmentBluetoothSilentPairingBinding? = null
    private val binding get() = _binding!!

    private lateinit var configurationManager: ConfigurationManager

    // List of Bluetooth-related property names
    private val btPropertyNames = listOf(
        "BT_DISCOVERABILITY",
        "BT_PAIRING_POLICY",
        "BT_SILENT_PAIRING_TRUSTED_ENABLE",
        "BT_SILENT_PAIRING_WHITELISTING_ENABLE",
        "BT_SILENT_PAIRING_WHITELISTING"
    )

    // Map to store Property objects and their corresponding Views
    private val propertyViews = mutableMapOf<Property<*>, View>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBluetoothSilentPairingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Enable exception handling for Datalogic SDK
        ErrorManager.enableExceptions(true)

        // Initialize ConfigurationManager with Context
        try {
            configurationManager = ConfigurationManager(requireContext())
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to initialize ConfigurationManager", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch and setup property inputs
        setupPropertyInputs()

        // Set listener for Apply button
        binding.buttonApplyBtSettings.setOnClickListener {
            applyBluetoothSettings()
        }
    }

    /**
     * Fetches Bluetooth-related properties and sets up input views.
     */
    private fun setupPropertyInputs() {
        val btProperties = btPropertyNames.mapNotNull { propertyName ->
            try {
                configurationManager.getPropertyByName(propertyName)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        for (property in btProperties) {
            if (property.isSupported && !property.isReadOnly) {
                val inputView = createInputForProperty(property)
                if (inputView != null) {
                    binding.linearLayoutBtProperties.addView(inputView)
                    propertyViews[property] = inputView
                }
            }
        }
    }

    /**
     * Creates an appropriate input view based on the property's type.
     */
    private fun createInputForProperty(property: Property<*>): View? {
        val context = requireContext()
        val propertyName = property.name

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 8, 0, 8)
        }

        val label = TextView(context).apply {
            text = propertyName
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        when (property) {
            is BooleanProperty -> {
                val switchView = Switch(context).apply {
                    isChecked = property.get()
                }
                container.addView(label)
                container.addView(switchView)
                return container
            }
            is EnumProperty<*> -> {
                val spinner = Spinner(context)
                val enumValues = property.enumConstants

                if (enumValues != null) {
                    val enumNames = enumValues.map { it.toString() }
                    val adapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_item,
                        enumNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter

                    // Set current selection
                    val currentValue = property.get()
                    val index = enumValues.indexOf(currentValue)
                    if (index >= 0) {
                        spinner.setSelection(index)
                    }
                }

                container.addView(label)
                container.addView(spinner)
                return container
            }
            is TextProperty -> {
                val editText = EditText(context).apply {
                    inputType = android.text.InputType.TYPE_CLASS_TEXT
                    setText(property.get())
                }
                container.addView(label)
                container.addView(editText)
                return container
            }
            else -> {
                Toast.makeText(context, "Unsupported property type: ${property.type}", Toast.LENGTH_SHORT).show()
                return null
            }
        }
    }

    /**
     * Applies the Bluetooth settings based on user input.
     */
    private fun applyBluetoothSettings() {
        try {
            for ((property, view) in propertyViews) {
                when (property) {
                    is BooleanProperty -> {
                        val switchView = (view as LinearLayout).getChildAt(1) as Switch
                        property.set(switchView.isChecked)
                    }
                    is EnumProperty<*> -> {
                        val spinner = (view as LinearLayout).getChildAt(1) as Spinner
                        val selectedPosition = spinner.selectedItemPosition
                        val enumValues = property.enumConstants

                        if (enumValues != null && selectedPosition in enumValues.indices) {
                            val enumValue = enumValues[selectedPosition]
                            // Use reflection to invoke the set method
                            try {
                                val setMethod = property.javaClass.getMethod("set", enumValue.javaClass)
                                setMethod.invoke(property, enumValue)
                            } catch (e: NoSuchMethodException) {
                                e.printStackTrace()
                                Toast.makeText(context, "Set method not found for property: ${property.name}", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Failed to set property: ${property.name}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    is TextProperty -> {
                        val editText = (view as LinearLayout).getChildAt(1) as EditText
                        val input = editText.text.toString()
                        property.set(input)
                    }
                }
            }

            // Commit the changes
            configurationManager.commit()
            Toast.makeText(context, "Bluetooth settings applied successfully", Toast.LENGTH_SHORT).show()
        } catch (e: ConfigException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to apply Bluetooth settings: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "An unexpected error occurred while applying settings.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
