package com.adhamnour.thedarkblurtooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "MainActivity";

    private BluetoothAdapter mBTA;

    private LinearLayout MainLayout;
    private TextView NotAvailableText;

    private Button BT_State_Button,BT_Scan_Button;

    private View.OnClickListener TurnOnBluetooth,TurnOffBluetooth,DoNothing,StartScaning,StopScaning;

    private RecyclerView PairedDevicesRecyclerView,FoundDevicesRecyclerView;
    private ArrayList<BluetoothDevice> FoundDevicesArrayList;
    private RecyclerView.Adapter FoundDevicesAdapter;

    private final BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR)){
                        case BluetoothAdapter.STATE_OFF:
                            BT_State_Button.setText("Turn On Bluetooth");
                            BT_State_Button.setEnabled(true);
                            BT_State_Button.setOnClickListener(TurnOnBluetooth);
                            MainLayout.removeView(PairedDevicesRecyclerView);
                            MainLayout.removeView(BT_Scan_Button);
                            if(MainLayout.indexOfChild(FoundDevicesRecyclerView)!=-1)
                                MainLayout.removeView(FoundDevicesRecyclerView);
                            FoundDevicesArrayList =null;
                            FoundDevicesRecyclerView = null;
                            FoundDevicesAdapter =null;
                            break;
                        case BluetoothAdapter.STATE_ON:
                            BT_State_Button.setText("Turn Off Bluetooth");
                            BT_State_Button.setEnabled(true);
                            BT_State_Button.setOnClickListener(TurnOffBluetooth);
                            PairedDevicesRecyclerView.setHasFixedSize(true);
                            BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(getApplicationContext(),mBTA.getBondedDevices());
                            PairedDevicesRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
                            PairedDevicesRecyclerView.setAdapter(adapter);
                            MainLayout.addView(PairedDevicesRecyclerView,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            MainLayout.addView(BT_Scan_Button);
                            BT_Scan_Button.setOnClickListener(StartScaning);
                            BT_Scan_Button.setText("Start Scaning");
                            if(mBTA.isDiscovering())
                                mBTA.cancelDiscovery();
                            FoundDevicesRecyclerView = new RecyclerView(getApplicationContext());
                            FoundDevicesArrayList = new ArrayList<>();
                            FoundDevicesAdapter= new BluetoothDeviceAdapter(getApplicationContext(),FoundDevicesArrayList);
                            FoundDevicesRecyclerView.setAdapter(FoundDevicesAdapter);
                            FoundDevicesRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            BT_State_Button.setText("The Bluetooth is Turnning off");
                            BT_State_Button.setEnabled(false);
                            BT_State_Button.setOnClickListener(DoNothing);
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            BT_State_Button.setText("The Bluetooth is Turnning on");
                            BT_State_Button.setEnabled(false);
                            BT_State_Button.setOnClickListener(DoNothing);
                            break;

                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    FoundDevicesArrayList.add(device);
                    FoundDevicesAdapter.notifyDataSetChanged();
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    BluetoothDevice mDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (mDevice.getBondState()){
                        case BluetoothDevice.BOND_BONDED:
                            Log.e(TAG,"BOND_BONDED");
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            Log.e(TAG,"BOND_BONDING");
                            break;
                        case BluetoothDevice.BOND_NONE:
                            Log.e(TAG,"BOND_BONDING");
                            break;
                    }
                    break;

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TurnOnBluetooth = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
            }
        };

        TurnOffBluetooth = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBTA.disable();
            }
        };

        DoNothing = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        StartScaning = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"Start Discovery");
                if(MainLayout.indexOfChild(FoundDevicesRecyclerView)== -1)
                    MainLayout.addView(FoundDevicesRecyclerView);
                else {
                    FoundDevicesArrayList.clear();
                    FoundDevicesAdapter.notifyDataSetChanged();
                    if (mBTA.isDiscovering())
                        mBTA.cancelDiscovery();
                }
                mBTA.startDiscovery();


            }
        };
        StopScaning = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"stop Discovery");
                mBTA.cancelDiscovery();
            }
        };

        MainLayout = findViewById(R.id.main_layout);
        PairedDevicesRecyclerView = new RecyclerView(getApplicationContext());

        NotAvailableText = new TextView(getApplicationContext());
        NotAvailableText.setText("NotAvailable");

        BT_State_Button=new Button(getApplicationContext());

        mBTA = BluetoothAdapter.getDefaultAdapter();

        if (mBTA == null){
            MainLayout.addView(NotAvailableText);
        }
        else if (mBTA.isEnabled()){
            BT_State_Button.setText("Turn Off Bluetooth");
            MainLayout.addView(BT_State_Button);
            BT_State_Button.setOnClickListener(TurnOffBluetooth);
        }
        else if(!mBTA.isEnabled()){
            BT_State_Button.setText("Turn On Bluetooth");
            MainLayout.addView(BT_State_Button);
            BT_State_Button.setOnClickListener(TurnOnBluetooth);
        }


        IntentFilter filter= new IntentFilter();
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReciever,filter);

        BT_Scan_Button = new Button(getApplicationContext());



    }

}
