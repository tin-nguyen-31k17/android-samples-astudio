package com.example.dl_sdk_sample_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.datalogic.device.ErrorManager
import com.datalogic.device.configuration.ConfigurationManager
import com.datalogic.device.configuration.ConfigException
import com.datalogic.device.configuration.Property
import com.datalogic.device.configuration.PropertyID
import com.example.dl_sdk_sample_app.databinding.FragmentChangeDeviceNameBinding

class ChangeDeviceNameFragment : Fragment() {

    private var _binding: FragmentChangeDeviceNameBinding? = null
    private val binding get() = _binding!!

    private lateinit var configurationManager: ConfigurationManager

    // Define Property ID for DEVICE_NAME_SUFFIX
    private val DEVICE_NAME_SUFFIX = PropertyID.DEVICE_NAME_SUFFIX

    // Use a nullable Property with String type
    private var deviceNameSuffixProperty: Property<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeDeviceNameBinding.inflate(inflater, container, false)
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

        // Fetch DEVICE_NAME_SUFFIX property safely
        fetchDeviceNameSuffixProperty()

        // Set listener for Apply button
        binding.buttonApplyDeviceName.setOnClickListener {
            applyDeviceNameSuffix()
        }
    }

    /**
     * Fetches the DEVICE_NAME_SUFFIX property and initializes the UI.
     */
    private fun fetchDeviceNameSuffixProperty() {
        try {
            // Retrieve the property using getPropertyByID
            val property = configurationManager.getPropertyById(DEVICE_NAME_SUFFIX)
            Log.d("ChangeDeviceNameFragment", "Retrieved DEVICE_NAME_SUFFIX: $property")

            // Check if the property is supported and not read-only
            if (property is Property<*> && property.isSupported && !property.isReadOnly) {
                // Safely cast to Property<String>
                deviceNameSuffixProperty = property as? Property<String>

                if (deviceNameSuffixProperty != null) {
                    // Get the current suffix value
                    val currentSuffix = deviceNameSuffixProperty!!.get()
                    binding.editTextDeviceNameSuffix.setText(currentSuffix)
                } else {
                    throw ClassCastException("DEVICE_NAME_SUFFIX property is not of type Property<String>")
                }
            } else {
                Toast.makeText(context, "Device Name Suffix not supported or read-only", Toast.LENGTH_SHORT).show()
                // Disable input fields since the property isn't available
                binding.editTextDeviceNameSuffix.isEnabled = false
                binding.buttonApplyDeviceName.isEnabled = false
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to retrieve Device Name Suffix: ${e.message}", Toast.LENGTH_SHORT).show()
            // Disable input fields since the property isn't available
            binding.editTextDeviceNameSuffix.isEnabled = false
            binding.buttonApplyDeviceName.isEnabled = false
        }
    }

    /**
     * Applies the updated device name suffix based on user input.
     */
    private fun applyDeviceNameSuffix() {
        if (deviceNameSuffixProperty == null) {
            Toast.makeText(context, "Device Name Suffix property is not available.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Get the new suffix from EditText
            val newSuffix = binding.editTextDeviceNameSuffix.text.toString().trim()

            // Validate input (optional)
            if (newSuffix.isEmpty()) {
                Toast.makeText(context, "Device Name Suffix cannot be empty.", Toast.LENGTH_SHORT).show()
                return
            }

            // Set the new value to the property
            deviceNameSuffixProperty!!.set(newSuffix)

            // Commit the changes
            configurationManager.commit()
            Toast.makeText(context, "Device Name Suffix updated successfully", Toast.LENGTH_SHORT).show()

        } catch (e: ConfigException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to update Device Name Suffix: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "An unexpected error occurred while updating Device Name Suffix.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
