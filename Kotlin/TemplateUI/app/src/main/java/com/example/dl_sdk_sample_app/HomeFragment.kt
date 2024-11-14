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
            "Kotlin Standard Library v1.8.10"
        )

        // Build a styled, formatted text for the app information
        val appInfoText = buildSpannedString {
            bold { append("App Name: ") }
            append("$appName\n")

            bold { append("Version: ") }
            append("$versionName\n")

            bold { append("Libraries in Use:\n") }
            libraries.forEach {
                append("- $it\n")
            }

            append("\n")

            bold { append("Function Descriptions:\n") }
            append("• Func0: Basic scanning functionality with Datalogic SDK.\n")
            append("• Func1: Placeholder for additional SDK functionality.\n")
            append("• Func2: Placeholder for additional SDK functionality.\n")
        }

        // Display the formatted text in the textViewAppInfo
        binding.textViewAppInfo.text = appInfoText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
