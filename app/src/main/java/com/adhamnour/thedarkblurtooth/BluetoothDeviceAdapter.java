package com.adhamnour.thedarkblurtooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.BluetoothDeviceViewHolder> {
    private static final String TAG = "BluetoothDeviceAdapter";


    private ArrayList<BluetoothDevice> BluetoothDevicesArrayList ;
    private Context mContext;

    public BluetoothDeviceAdapter(Context context,ArrayList<BluetoothDevice> bluetoothDevicesArrayList) {
        BluetoothDevicesArrayList = bluetoothDevicesArrayList;
        this.mContext=context;
    }

    public BluetoothDeviceAdapter(Context context,Set<BluetoothDevice>bluetoothDevices) {
        BluetoothDevicesArrayList = new ArrayList<>();
        this.mContext=context;
        if (bluetoothDevices.size()>0)
            for (BluetoothDevice btd : bluetoothDevices) {
                BluetoothDevicesArrayList.add(btd);
            }
    }

    @NonNull
    @Override
    public BluetoothDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_device_item,parent,false);
        final BluetoothDeviceViewHolder bluetoothDeviceViewHolder = new BluetoothDeviceViewHolder(view);
        return bluetoothDeviceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final BluetoothDeviceViewHolder holder, final int position) {
        holder.address.setText(String.format("%s%s", holder.address.getText().toString(), BluetoothDevicesArrayList.get(position).getAddress()));
        holder.name.setText(new StringBuilder().append(holder.name.getText()).append(BluetoothDevicesArrayList.get(position).getName()).toString());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,BluetoothDevicesArrayList.get(position).getName(),Toast.LENGTH_SHORT).show();
                BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
                bta.cancelDiscovery();
                if(BluetoothDevicesArrayList.get(position).getBondState() == BluetoothDevice.BOND_NONE)
                    BluetoothDevicesArrayList.get(position).createBond();

                Intent intent = new Intent(mContext,MessegingActivity.class);
                intent.putExtra("Device Name",BluetoothDevicesArrayList.get(position).getName());
                intent.putExtra("Device Address",BluetoothDevicesArrayList.get(position).getAddress());
                mContext.startActivity(intent);

//                BluetoothConnectionServices mBluetoothConnection = new BluetoothConnectionServices(mContext);
//                mBluetoothConnection.startClient(BluetoothDevicesArrayList.get(position),UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));

            }
        });
    }

    @Override
    public int getItemCount() {
        return BluetoothDevicesArrayList.size();
    }

    public class BluetoothDeviceViewHolder extends RecyclerView.ViewHolder  {
        public CircleImageView circleImageView;
        public TextView name,address;
        public LinearLayout parent;
        public BluetoothDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.device_name);
            address = itemView.findViewById(R.id.device_address);
            parent=itemView.findViewById(R.id.parent_layout);
        }



    }

}
