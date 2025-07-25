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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class BLEScanActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 1;
    private static final long NO_NEW_DEVICE_TIMEOUT = 5000; // 5 seconds

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private final ArrayList<Device> deviceList = new ArrayList<>();
    private final ArrayList<String> deviceDisplayList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private ListView listView;
    private Button openMapBtn;

    private Handler handler = new Handler();
    private long lastDeviceFoundTime = 0;

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String name = result.getDevice().getName();
            String address = result.getDevice().getAddress();
            int rssi = result.getRssi();

            if (name == null) name = "Unknown";

            // Avoid duplicates
            boolean exists = false;
            for (Device d : deviceList) {
                if (d.getAddress().equals(address)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                double distance = DistanceCalculator.calculateDistance(rssi, -60, 2.5);
                Device device = new Device(name, address, rssi, distance);
                deviceList.add(device);
                deviceDisplayList.add(name + "\nRSSI: " + rssi + " dBm\nDistance: " + String.format("%.2f", distance) + " m");
                adapter.notifyDataSetChanged();
                lastDeviceFoundTime = System.currentTimeMillis();
            }
        }
    };

    private final Runnable checkNoNewDevicesRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastDeviceFoundTime > NO_NEW_DEVICE_TIMEOUT && deviceList.size() > 0) {
                openMapBtn.setVisibility(View.VISIBLE);
            }
            handler.postDelayed(this, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan);

        listView = findViewById(R.id.deviceListView);
        openMapBtn = findViewById(R.id.openMapButton);
        openMapBtn.setVisibility(View.GONE);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceDisplayList);
        listView.setAdapter(adapter);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, REQUEST_PERMISSIONS);
        } else {
            startScan();
        }

        openMapBtn.setOnClickListener(v -> {
            if (bluetoothLeScanner != null) {
                bluetoothLeScanner.stopScan(scanCallback);
            }
            Intent intent = new Intent(BLEScanActivity.this, MapActivity.class);
            intent.putExtra("deviceList", deviceList);
            startActivity(intent);
        });
    }

    private void startScan() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(scanCallback);
            lastDeviceFoundTime = System.currentTimeMillis();
            handler.postDelayed(checkNoNewDevicesRunnable, 2000);
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
        handler.removeCallbacks(checkNoNewDevicesRunnable);
    }
}
