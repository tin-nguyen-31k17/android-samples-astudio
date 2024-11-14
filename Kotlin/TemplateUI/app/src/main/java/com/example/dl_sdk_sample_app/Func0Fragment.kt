package com.example.dl_sdk_sample_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dl_sdk_sample_app.databinding.FragmentFunc0Binding
import com.datalogic.decode.BarcodeManager
import com.datalogic.decode.ReadListener

class Func0Fragment : Fragment() {

    private var _binding: FragmentFunc0Binding? = null
    private val binding get() = _binding!!

    private var barcodeManager: BarcodeManager? = null
    private var readListener: ReadListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFunc0Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the scan button click listener
        binding.scanButton.setOnClickListener {
            initScanner()
        }
    }

    private fun initScanner() {
        // Initialize BarcodeManager
        barcodeManager = BarcodeManager()

        // Define the ReadListener
        readListener = ReadListener { decodeResult ->
            activity?.runOnUiThread {
                binding.resultTextView.text = decodeResult.text
                Toast.makeText(requireContext(), "Barcode Scanned: ${decodeResult.text}", Toast.LENGTH_SHORT).show()
            }
        }

        // Add the ReadListener to BarcodeManager
        barcodeManager?.addReadListener(readListener)

        // Optionally, show a message indicating scanning has started
        Toast.makeText(requireContext(), "Scanning Started...", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up resources
        barcodeManager?.removeReadListener(readListener)
        barcodeManager = null
        readListener = null
        _binding = null
    }
}
