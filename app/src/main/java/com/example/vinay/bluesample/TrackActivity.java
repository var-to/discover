package com.example.vinay.bluesample;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TrackActivity extends AppCompatActivity {
int initial;
    EditText ed;
    int thres;
    String add;
    String mydev;

    Button b1,b2;
    BluetoothAdapter adapter;
    int flag=0, initflag=1;
    BroadcastReceiver find;
    MediaPlayer media;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        ed=(EditText)findViewById(R.id.editText);
        b1=(Button)findViewById(R.id.button2);
        b2=(Button)findViewById(R.id.button3);
        Intent i=getIntent();
        //Toast.makeText(this, "Intent nhi mila", Toast.LENGTH_SHORT).show();

        initial=(i.getExtras().getInt("initial"));
        mydev=i.getExtras().getString("device");
        add=i.getExtras().getString("address");

        Toast.makeText(this, "Device: "+mydev+"\nAddress: "+add+"\nRssiinitial: "+initial, Toast.LENGTH_SHORT).show();
        adapter=BluetoothAdapter.getDefaultAdapter();




        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.isDiscovering())adapter.cancelDiscovery();
                ed.setEnabled(true);
                b1.setEnabled(true);
                initflag=1;
            }
        });}
    public void scan_dev(final View v) {
        String e=ed.getText().toString();
        if(initflag==1 && (e.equals("")))
            Toast.makeText(TrackActivity.this, "threshold daaldo bhyii", Toast.LENGTH_SHORT).show();

        else if(initflag==1 && !e.equals("") ) {

            // b1.setEnabled(false);

            thres=Integer.parseInt(ed.getText().toString());
            ed.setEnabled(false);
            initflag=0;
            scan_dev(v);
        }
        else{
            Toast.makeText(TrackActivity.this, "yippee", Toast.LENGTH_SHORT).show();
            if(find!=null)unregisterReceiver(find);

            find = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                        //dialog.dismiss();
                        BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if(device!=null){
                            if(device.getAddress().equals(add)){
                                int rssi =intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                               Toast.makeText(TrackActivity.this,rssi,Toast.LENGTH_SHORT).show();
                                flag=1;
                                Log.e("Here :" , "" + rssi);
                                if(Math.abs(rssi)>(Math.abs(initial)+thres)){
                                    Toast.makeText(context, rssi+" "+initial+" "+thres, Toast.LENGTH_SHORT).show();
                                    media = MediaPlayer.create(TrackActivity.this, R.raw.alert );
                                    media.start();
                                    adapter.cancelDiscovery();
                                }
                            }
                        }}

                    if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) && flag==0){
                        Toast.makeText(context, "Discovery khataam", Toast.LENGTH_SHORT).show();
                        media = MediaPlayer.create(TrackActivity.this, R.raw.alert );
                        media.start();
                    }
                    if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) && flag==1){
                       scan_dev(v);
                    }
                    if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                        Toast.makeText(context, "Discovery shuru", Toast.LENGTH_SHORT).show();
                        //  dialog.setMessage("Discovery started");
                        //dialog.show();
                    }

                }
            };
            IntentFilter filter = new IntentFilter();

            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            if (adapter != null) {
                if (adapter.isDiscovering()) {
                    adapter.cancelDiscovery();

                }
            }


            registerReceiver(find, filter);
            adapter.startDiscovery();
        }


    }







@Override
    protected void onDestroy() {
        unregisterReceiver(find);
        super.onDestroy();
    }


}
