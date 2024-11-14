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
import com.example.dl_sdk_sample_app.databinding.FragmentUsbWhitelistingBinding

class UsbWhitelistingFragment : Fragment() {

    private var _binding: FragmentUsbWhitelistingBinding? = null
    private val binding get() = _binding!!

    private lateinit var configurationManager: ConfigurationManager

    // Define Property IDs (ensure these match the SDK's definitions)
    private val USB_HOST_WHITELISTING = PropertyID.USB_HOST_WHITELISTING
    private val USB_PREDEFINED_HOST_WHITELISTING = PropertyID.USB_PREDEFINED_HOST_WHITELISTING

    // Use a nullable Property with String type
    private var usbWhitelistProperty: Property<String>? = null

    // Mutable list to hold whitelist entries
    private val whitelistEntries = mutableListOf<String>()

    // Adapter for ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUsbWhitelistingBinding.inflate(inflater, container, false)
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

        // Initialize ListView and Adapter
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, whitelistEntries)
        binding.listViewWhitelist.adapter = adapter
        binding.listViewWhitelist.choiceMode = ListView.CHOICE_MODE_SINGLE

        // Fetch USB_WHITELISTING properties safely
        fetchUsbWhitelistingProperties()

        // Set listener for Add button
        binding.buttonAddWhitelist.setOnClickListener {
            val newDevice = binding.editTextWhitelist.text.toString().trim()
            if (newDevice.isNotEmpty() && !whitelistEntries.contains(newDevice)) {
                whitelistEntries.add(newDevice)
                adapter.notifyDataSetChanged()
                binding.editTextWhitelist.text.clear()
            } else {
                Toast.makeText(context, "Invalid or duplicate USB Device ID", Toast.LENGTH_SHORT).show()
            }
        }

        // Set listener for Remove button
        binding.buttonRemoveWhitelist.setOnClickListener {
            val selectedPosition = binding.listViewWhitelist.checkedItemPosition
            if (selectedPosition != ListView.INVALID_POSITION) {
                val removedItem = whitelistEntries.removeAt(selectedPosition)
                adapter.notifyDataSetChanged()
                binding.listViewWhitelist.clearChoices()
                Toast.makeText(context, "Removed: $removedItem", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No item selected to remove", Toast.LENGTH_SHORT).show()
            }
        }

        // Set listener for Apply button
        binding.buttonApplyWhitelist.setOnClickListener {
            applyUsbWhitelist()
        }
    }

    /**
     * Fetches USB_WHITELISTING properties and sets up the whitelist list.
     */
    private fun fetchUsbWhitelistingProperties() {
        try {
            // Attempt to retrieve USB_HOST_WHITELISTING property
            usbWhitelistProperty = configurationManager.getPropertyById(USB_HOST_WHITELISTING) as Property<String>?
            Log.d("UsbWhitelistingFragment", "Retrieved USB_HOST_WHITELISTING: $usbWhitelistProperty")

            // If not found, attempt to retrieve USB_PREDEFINED_HOST_WHITELISTING
            if (usbWhitelistProperty == null) {
                usbWhitelistProperty = configurationManager.getPropertyById(USB_PREDEFINED_HOST_WHITELISTING) as Property<String>?
                Log.d("UsbWhitelistingFragment", "Retrieved USB_PREDEFINED_HOST_WHITELISTING: $usbWhitelistProperty")
            }

            // If still null, throw exception
            if (usbWhitelistProperty == null) {
                throw NullPointerException("USB_WHITELISTING property not found or unsupported.")
            }

            // Check if the property is supported and not read-only
            if (usbWhitelistProperty!!.isSupported && !usbWhitelistProperty!!.isReadOnly) {
                val whitelistString = usbWhitelistProperty!!.get()
                whitelistEntries.addAll(whitelistString.split(",").map { it.trim() }.filter { it.isNotEmpty() })
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "USB Whitelisting not supported or Read-Only", Toast.LENGTH_SHORT).show()
                binding.editTextWhitelist.isEnabled = false
                binding.buttonAddWhitelist.isEnabled = false
                binding.buttonRemoveWhitelist.isEnabled = false
                binding.buttonApplyWhitelist.isEnabled = false
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to retrieve USB Whitelisting property: ${e.message}", Toast.LENGTH_SHORT).show()
            // Disable input fields since the property isn't available
            binding.editTextWhitelist.isEnabled = false
            binding.buttonAddWhitelist.isEnabled = false
            binding.buttonRemoveWhitelist.isEnabled = false
            binding.buttonApplyWhitelist.isEnabled = false
        }
    }

    /**
     * Applies the USB whitelist based on user input.
     */
    private fun applyUsbWhitelist() {
        if (usbWhitelistProperty == null) {
            Toast.makeText(context, "USB Whitelisting property is not available.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Convert the whitelist entries to a comma-separated string
            val updatedWhitelist = whitelistEntries.joinToString(",")
            usbWhitelistProperty!!.set(updatedWhitelist)

            // Commit the changes
            configurationManager.commit()
            Toast.makeText(context, "USB Whitelist updated successfully", Toast.LENGTH_SHORT).show()

        } catch (e: ConfigException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to update USB Whitelist: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "An unexpected error occurred while updating USB Whitelist.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
