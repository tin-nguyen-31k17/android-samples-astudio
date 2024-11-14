package com.example.dl_sdk_sample_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.dl_sdk_sample_app.databinding.FragmentFunc0Binding;
import com.datalogic.decode.BarcodeManager;
import com.datalogic.decode.ReadListener;

public class Func0Fragment extends Fragment {

    private FragmentFunc0Binding binding;
    private BarcodeManager barcodeManager;
    private ReadListener readListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFunc0Binding.inflate(inflater, container, false);

        binding.scanButton.setOnClickListener(v -> initScanner());

        return binding.getRoot();
    }

    private void initScanner() {
        barcodeManager = new BarcodeManager();
        readListener = decodeResult -> getActivity().runOnUiThread(() -> {
            binding.resultTextView.setText(decodeResult.getText());
            Toast.makeText(getContext(), "Barcode Scanned: " + decodeResult.getText(), Toast.LENGTH_SHORT).show();
        });

        barcodeManager.addReadListener(readListener);
        Toast.makeText(getContext(), "Scanning Started...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (barcodeManager != null && readListener != null) {
            barcodeManager.removeReadListener(readListener);
        }
        binding = null;
    }
}
