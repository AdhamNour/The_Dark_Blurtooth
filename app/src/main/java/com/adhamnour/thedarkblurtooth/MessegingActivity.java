package com.adhamnour.thedarkblurtooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MessegingActivity extends AppCompatActivity {
    private static final String TAG = "MessegingActivity";

    private BluetoothDevice Connected =null;
    private BluetoothConnectionServices mBluetoothConnection;
    private LinearLayout MainLayout ;
    private GridLayout Keypad;
    private ArrayList<Button> KeypadButtons;

    private StringBuilder ConnectedStringBuilder;

    private TextView Status ;

    private BroadcastReceiver mReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Log.d(TAG, "onReceive: ACTION_ACL_CONNECTED");
                    Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                    Keypad = new GridLayout(getApplicationContext());
                    Keypad.setColumnCount(4);
                    MainLayout.addView(Keypad);
                    int counter = 1;
                    for (int i =1 ; i<=16;i++){
                        final Button button = new Button(getApplicationContext());
                        if(i%4==0)
                            switch (i/4){
                                case 1:
                                    button.setText("Start");
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            byte[] bytes = "t".getBytes(Charset.defaultCharset());
                                            mBluetoothConnection.write(bytes);
                                        }
                                    });
                                    break;
                                case 2:
                                    button.setText("Stop");
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            byte[] bytes = "p".getBytes(Charset.defaultCharset());
                                            mBluetoothConnection.write(bytes);
                                        }
                                    });
                                    break;
                                case 3:
                                    button.setText("Shift");
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            byte[] bytes = "h".getBytes(Charset.defaultCharset());
                                            mBluetoothConnection.write(bytes);
                                        }
                                    });
                                    break;
                                case 4:
                                    button.setText("Enter");
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            byte[] bytes = "e".getBytes(Charset.defaultCharset());
                                            mBluetoothConnection.write(bytes);
                                        }
                                    });
                                    break;
                            }
                        else {
                            switch (counter){
                                case 10:
                                    button.setText("mode");
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            byte[] bytes = "m".getBytes(Charset.defaultCharset());
                                            mBluetoothConnection.write(bytes);
                                        }
                                    });
                                    break;
                                case 11:
                                    button.setText(String.format("%d", 0));
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            byte[] bytes = "0".getBytes(Charset.defaultCharset());
                                            mBluetoothConnection.write(bytes);
                                        }
                                    });
                                    break;
                                case 12:
                                    button.setText("cancel");
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            byte[] bytes = "c".getBytes(Charset.defaultCharset());
                                            mBluetoothConnection.write(bytes);
                                        }
                                    });
                                    break;
                                default:
                                    button.setText(String.format("%d", counter));
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            byte[] bytes = button.getText().toString().getBytes(Charset.defaultCharset());
                                            mBluetoothConnection.write(bytes);
                                        }
                                    });
                                    break;
                            }
                            counter++;
                        }

                        KeypadButtons.add(button);
                        Keypad.addView(button);
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Keypad.removeAllViews();
                    MainLayout.removeView(Keypad);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messeging);
        Log.d(TAG, "onCreate: The Messeging Activity");

        KeypadButtons = new ArrayList<>();

        if (getIntent().hasExtra("Device Name") && getIntent().hasExtra("Device Address")){
            BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
            String adddress = getIntent().getStringExtra("Device Address");
            String name = getIntent().getStringExtra("Device Name");
            Set<BluetoothDevice> bluetoothDevices = ba.getBondedDevices();
            for (BluetoothDevice bd : bluetoothDevices)
                if(bd.getName().equals(name) && bd.getAddress().equals(adddress)){
                    Connected = bd;
                    break;
                }
        }
        if(Connected != null){
             mBluetoothConnection = new BluetoothConnectionServices(getApplicationContext());
             mBluetoothConnection.startClient(Connected, UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            registerReceiver(mReciver,filter);
        }

        MainLayout = findViewById(R.id.MessegingMainLayout);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(TheIncomingReceiver,new IntentFilter("TheIncomingMessege"));

//        Status = new TextView(getApplicationContext());
//        Status.setText("This the Status of the Application");
//        MainLayout.addView(Status);

        ConnectedStringBuilder = new StringBuilder();
    }

    private BroadcastReceiver TheIncomingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(getApplicationContext(),intent.getStringExtra("The Message"),Toast.LENGTH_SHORT).show();
            ConnectedStringBuilder.append(intent.getStringExtra("The Message"));
            Log.d(TAG, "onReceive: "+ConnectedStringBuilder.toString());
        }
    };

}
