package com.example.dl_sdk_sample_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.datalogic.device.Intents
import com.example.dl_sdk_sample_app.databinding.FragmentConfigureIntentBinding

class ConfigureIntentFragment : Fragment() {

    private var _binding: FragmentConfigureIntentBinding? = null
    private val binding get() = _binding!!

    private val properties = listOf(
        "com.datalogic.device.configuration.CAMERA_FLASH",
        "com.datalogic.device.configuration.KEYBOARD_TYPE"
        // Add other property names you want to configure
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfigureIntentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set up Spinner with properties
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            properties
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProperties.adapter = adapter

        // Set listener for Send Intent button
        binding.buttonSendIntent.setOnClickListener {
            sendConfigurationIntent()
        }
    }

    /**
     * Sends a configuration intent to update a specific property.
     */
    private fun sendConfigurationIntent() {
        val propertyName = binding.spinnerProperties.selectedItem as String
        val propertyValue = binding.editTextValue.text.toString()

        if (propertyValue.isEmpty()) {
            Toast.makeText(context, "Please enter a value for the property.", Toast.LENGTH_SHORT).show()
            return
        }

        // Prepare the Map for EXTRA_CONFIGURATION_CHANGED_MAP
        val changedMap = HashMap<Int, String>()
        // Assuming you have a way to get PropertyID from propertyName
        // This is a placeholder. Replace with actual PropertyID mapping
        val propertyID = getPropertyID(propertyName)
        if (propertyID != null) {
            changedMap[propertyID] = propertyValue
        } else {
            Toast.makeText(context, "Invalid property selected.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intents.ACTION_CONFIGURATION_COMMIT).apply {
            putExtra(Intents.EXTRA_CONFIGURATION_CHANGED_MAP, changedMap as java.io.Serializable)
//            putExtra(Intents.EXTRA_SET_BY, "APP")
            // Add flags if necessary
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }

        context?.sendBroadcast(intent)
        Toast.makeText(context, "Configuration intent sent successfully.", Toast.LENGTH_SHORT).show()
    }

    /**
     * Placeholder function to map property names to PropertyIDs.
     * Replace this with actual implementation based on your SDK.
     */
    private fun getPropertyID(propertyName: String): Int? {
        // Example mapping. Replace with actual PropertyID values.
        return when (propertyName) {
            "com.datalogic.device.configuration.CAMERA_FLASH" -> 101
            "com.datalogic.device.configuration.KEYBOARD_TYPE" -> 102
            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
