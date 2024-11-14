package com.example.dl_sdk_sample_app

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.Fragment
import com.example.dl_sdk_sample_app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayAppInfo()
    }

    private fun displayAppInfo() {
        val appName = getString(R.string.app_name)
        val versionName = BuildConfig.VERSION_NAME
        val libraries = listOf(
            "Datalogic SDK v1.39",
            "AndroidX Libraries",
            "Kotlin Standard Library v1.8.10",
            "Material Components for Android"
        )

        // Detailed descriptions of each function without Markdown asterisks
        val functionDescriptions = listOf(
            "Bluetooth Silent Pairing: Enables the device to pair with Bluetooth peripherals without user intervention, enhancing user experience in environments where manual pairing is cumbersome.",
            "Change Device Name: Allows users to customize the device's Bluetooth name, making it easily identifiable among multiple devices.",
            "Configuration Change Notifications: Listens for configuration changes and notifies users through system notifications, ensuring that users are always aware of the device's current settings.",
            "USB Whitelisting: Provides security by allowing only approved USB devices to connect, preventing unauthorized access and ensuring data integrity."
        )

        // Build a styled, formatted text for the app information
        val appInfoText = buildSpannedString {
            bold { append("App Name: ") }
            append("$appName\n")

            bold { append("Version: ") }
            append("$versionName\n\n")

            bold { append("About This Demo App:\n") }
            append("This Datalogic SDK Sample App showcases various functionalities provided by the Datalogic SDK. It's designed to help developers understand how to integrate and utilize the SDK's features effectively.\n\n")

            bold { append("How to Use:\n") }
            append("Navigate through the app using the top navigation drawer. Each menu item corresponds to a specific feature of the SDK:\n\n")
            functionDescriptions.forEach {
                append("• ")
                bold { append("${it.substringBefore(":")}: ") }
                append("${it.substringAfter(": ")}\n")
            }

            append("\nFor more detailed instructions, refer to the documentation or contact support.")
        }

        // Display the formatted text in the textViewAppInfo
        binding.textViewAppInfo.text = appInfoText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
