package com.example.intrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<Device> deviceList;

    public DeviceAdapter(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = deviceList.get(position);
        holder.nameTextView.setText(device.getName());
        holder.addressTextView.setText(device.getAddress());
        holder.rssiTextView.setText("RSSI: " + device.getRssi());
        holder.distanceTextView.setText(String.format("Distance: %.2f m", device.getDistance()));
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, rssiTextView, distanceTextView;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvName);
            addressTextView = itemView.findViewById(R.id.tvAddress);
            rssiTextView = itemView.findViewById(R.id.tvRssi);
            distanceTextView = itemView.findViewById(R.id.tvDistance);
        }
    }
}
