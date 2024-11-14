package com.example.dl_sdk_sample_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.datalogic.device.ErrorManager
import com.datalogic.device.configuration.*
import com.example.dl_sdk_sample_app.databinding.FragmentConfigurePropertiesBinding

class ConfigurePropertiesFragment : Fragment() {

    private var _binding: FragmentConfigurePropertiesBinding? = null
    private val binding get() = _binding!!

    private lateinit var configurationManager: ConfigurationManager

    // Map to store property names to Property objects
    private val propertiesMap: MutableMap<String, Property<*>> = mutableMapOf()

    // Currently selected property
    private var selectedProperty: Property<*>? = null

    // Current input view
    private var currentInputView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfigurePropertiesBinding.inflate(inflater, container, false)
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

        // Set listeners
        binding.buttonFetchProperties.setOnClickListener {
            fetchProperties()
        }

        binding.spinnerProperties.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val propertyName = parent.getItemAtPosition(position) as String
                selectedProperty = propertiesMap[propertyName]
                displayInputForProperty(selectedProperty)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedProperty = null
                binding.linearLayoutInput.removeAllViews()
            }
        }

        binding.buttonApplyChanges.setOnClickListener {
            applyConfigurationChanges()
        }
    }

    /**
     * Fetches all supported and writable properties and populates the spinner.
     */
    private fun fetchProperties() {
        try {
            val rootGroup = configurationManager.treeRoot
            val allProperties = getAllProperties(rootGroup)

            val propertyNames = mutableListOf<String>()
            propertiesMap.clear()

            for (property in allProperties) {
                val writable = !property.isReadOnly

                if (property.isSupported && writable) {
                    val propertyName = property.name
                    propertyNames.add(propertyName)
                    propertiesMap[propertyName] = property
                }
            }

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                propertyNames.sorted()
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerProperties.adapter = adapter

            Toast.makeText(context, "Properties fetched successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to fetch properties", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Recursively fetches all properties from the property tree.
     */
    private fun getAllProperties(group: PropertyGroup): List<Property<*>> {
        val properties = mutableListOf<Property<*>>()
        val childGroups = group.groups
        val childProperties = group.properties

        // Add properties in this group
        properties.addAll(childProperties)

        // Recursively add properties from child groups
        for (childGroup in childGroups) {
            properties.addAll(getAllProperties(childGroup))
        }

        return properties
    }

    /**
     * Displays the appropriate input field based on the property type.
     */
    private fun displayInputForProperty(property: Property<*>?) {
        binding.linearLayoutInput.removeAllViews()
        currentInputView = null

        if (property == null) {
            return
        }

        when (property) {
            is BooleanProperty -> {
                val switchView = Switch(requireContext()).apply {
                    isChecked = property.get()
                }
                binding.linearLayoutInput.addView(switchView)
                currentInputView = switchView
            }
            is EnumProperty<*> -> {
                val spinner = Spinner(requireContext())
                val enumValues = property.enumConstants

                if (enumValues != null) {
                    val enumNames = enumValues.map { it.toString() }
                    val adapter = ArrayAdapter(
                        requireContext(),
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

                binding.linearLayoutInput.addView(spinner)
                currentInputView = spinner
            }
            is NumericProperty -> {
                val editText = EditText(requireContext()).apply {
                    inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
                    setText(property.get().toString())
                }
                binding.linearLayoutInput.addView(editText)
                currentInputView = editText
            }
            is TextProperty -> {
                val editText = EditText(requireContext()).apply {
                    inputType = android.text.InputType.TYPE_CLASS_TEXT
                    setText(property.get())
                }
                binding.linearLayoutInput.addView(editText)
                currentInputView = editText
            }
            is BlobProperty -> {
                // Handling BlobProperty can be complex; provide a placeholder or implement as needed
                val buttonManageBlob = Button(requireContext()).apply {
                    text = "Manage ${property.name}"
                    setOnClickListener {
                        manageBlobProperty(property)
                    }
                }
                binding.linearLayoutInput.addView(buttonManageBlob)
                currentInputView = buttonManageBlob
            }
            else -> {
                Toast.makeText(context, "Unsupported property type: ${property.type}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Applies the configuration changes based on user input.
     */
    private fun applyConfigurationChanges() {
        val property = selectedProperty
        val view = currentInputView

        if (property == null || view == null) {
            Toast.makeText(context, "No property selected or input view is missing", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            when (property) {
                is BooleanProperty -> {
                    val switchView = view as Switch
                    property.set(switchView.isChecked)
                }
                is EnumProperty<*> -> {
                    val spinner = view as Spinner
                    val selectedPosition = spinner.selectedItemPosition
                    val enumValues = property.enumConstants

                    if (enumValues != null && selectedPosition in enumValues.indices) {
                        val enumValue = enumValues[selectedPosition]
                        property.set(enumValue as Nothing?)
                    }
                }
                is NumericProperty -> {
                    val editText = view as EditText
                    val input = editText.text.toString().toIntOrNull()
                    if (input != null) {
                        property.set(input)
                    } else {
                        Toast.makeText(context, "Invalid input for ${property.name}", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                is TextProperty -> {
                    val editText = view as EditText
                    val input = editText.text.toString()
                    property.set(input)
                }
                is BlobProperty -> {
                    // Handle BlobProperty accordingly
                    Toast.makeText(context, "Blob properties cannot be set directly here", Toast.LENGTH_SHORT).show()
                    return
                }
                else -> {
                    Toast.makeText(context, "Unsupported property type", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            // Commit the changes
            configurationManager.commit()
            Toast.makeText(context, "Configuration updated successfully", Toast.LENGTH_SHORT).show()
        } catch (e: ConfigException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to apply configuration: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "An unexpected error occurred.", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Handles the management of BlobProperty.
     */
    private fun manageBlobProperty(blobProperty: BlobProperty?) {
        if (blobProperty == null || !blobProperty.isSupported) {
            Toast.makeText(context, "Blob Property not supported", Toast.LENGTH_SHORT).show()
            return
        }

        // Implement the logic to manage BlobProperty based on your application's requirements
        // For example, open a dialog to edit Blob data
        Toast.makeText(context, "Managing Blob Property: ${blobProperty.name} (Not Implemented)", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
