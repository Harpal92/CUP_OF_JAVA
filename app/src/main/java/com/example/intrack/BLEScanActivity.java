package com.example.intrack;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class BLEScanActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSIONS = 2;
    private static final double RSSI_AT_ONE_METER = -59; // Typical value
    private static final double PATH_LOSS_EXPONENT = 2.0;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private final ArrayList<Device> deviceList = new ArrayList<>();

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String name = result.getDevice().getName();
            String address = result.getDevice().getAddress();
            int rssi = result.getRssi();

            // Manually measured reference RSSI at 1 meter
            int rssiAtOneMeter = -60;

// Manually calculated path loss exponent (environment factor)
            double pathLossExponent = 2.5;

// Use the updated method
            double distance = DistanceCalculator.calculateDistance(rssi, rssiAtOneMeter, pathLossExponent);


            // Show in toast temporarily
            String info = "Device: " + name + "\nRSSI: " + rssi + " dBm\nDistance: " + String.format("%.2f", distance) + " m";
            Toast.makeText(BLEScanActivity.this, info, Toast.LENGTH_SHORT).show();

            // Create device object
            distance = DistanceCalculator.calculateDistance(rssi, -60, 2.5);

            Device newDevice = new Device(name, address, rssi, distance);



            // Add if not already added
            boolean alreadyAdded = false;
            for (Device d : deviceList) {
                if (d.getAddress().equals(address)) {
                    alreadyAdded = true;
                    break;
                }
            }

            if (!alreadyAdded) {
                deviceList.add(newDevice);
            }

            // Launch map activity after 4 beacons
            if (deviceList.size() >= 3) {  // Use top 3 devices
                bluetoothLeScanner.stopScan(scanCallback);

                ArrayList<Device> topDevices = new ArrayList<>(deviceList.subList(0, 3));

                Intent intent = new Intent(BLEScanActivity.this, MapActivity.class);
                intent.putExtra("deviceList", topDevices);

                startActivity(intent);
                finish();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan);

        // Init Bluetooth
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Permission check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS);
        } else {
            startScan();
        }
    }

    private void startScan() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(scanCallback);

            // Auto-stop after 20 sec (safety)
            new Handler().postDelayed(() -> {
                if (bluetoothLeScanner != null) {
                    bluetoothLeScanner.stopScan(scanCallback);
                }
            }, 20000);
        } else {
            Toast.makeText(this, "Enable Bluetooth to scan", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }
}
