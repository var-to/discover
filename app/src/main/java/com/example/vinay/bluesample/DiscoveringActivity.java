package com.example.vinay.bluesample;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class DiscoveringActivity extends AppCompatActivity {
Switch aSwitch;
    TextView vis,ptext;
    ListView list;
BluetoothAdapter adapter;
    ProgressBar prog;
    BroadcastReceiver find;
    final ArrayList<BluetoothDevice> avail = new ArrayList<>();

    final ArrayList<Details> availname=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovering);
        aSwitch=(Switch)findViewById(R.id.switch1);
        vis=(TextView)findViewById(R.id.textView4);
        ptext=(TextView)findViewById(R.id.textView5);
        list=(ListView)findViewById(R.id.list);
        prog=(ProgressBar)findViewById(R.id.progressBar2);
        adapter= BluetoothAdapter.getDefaultAdapter();
        if(adapter==null){
            Toast.makeText(DiscoveringActivity.this, "Blutooth not supported by your device", Toast.LENGTH_SHORT).show();
        }
        if(adapter.isEnabled()){
            aSwitch.toggle();

                vis.setVisibility(View.VISIBLE);
                Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i,0);

        }
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                        vis.setVisibility(View.VISIBLE);
                        Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(i,0);
                    //  adapter.enable();
                }
                else{
                    adapter.disable();
                    ptext.setVisibility(View.INVISIBLE);
                    list.setVisibility(View.INVISIBLE);

                }
            }
        });

        vis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

                startActivityForResult(i,1);
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String address=availname.get(position).s2;
                String name=availname.get(position).s1;
                int rssi=availname.get(position).value;
                Intent i=new Intent(DiscoveringActivity.this,TrackActivity.class);
                i.putExtra("address",address);
                i.putExtra("device",name);
                i.putExtra("initial",rssi);

                Toast.makeText(DiscoveringActivity.this, "Starting tracking for "+name+"\ninitial="+rssi, Toast.LENGTH_SHORT).show();
                startActivity(i);
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
                switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    case PackageManager.PERMISSION_DENIED:
                        ((TextView) new AlertDialog.Builder(this)
                                .setTitle("Runtime Permissions up ahead")
                                .setMessage(Html.fromHtml("<p>To find nearby bluetooth devices please click \"Allow\" on the runtime permissions popup.</p>" +
                                        "<p>For more info see <a href=\"http://developer.android.com/about/versions/marshmallow/android-6.0-changes.html#behavior-hardware-id\">here</a>.</p>"))
                                .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(DiscoveringActivity.this,
                                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                                    1);
                                        }
                                    }
                                })
                                .show()
                                .findViewById(android.R.id.message))
                                .setMovementMethod(LinkMovementMethod.getInstance());       // Make the link clickable. Needs to be called after show(), in order to generate hyperlinks
                        break;
                    case PackageManager.PERMISSION_GRANTED:
                        break;
                }
            }





            if(find!=null)unregisterReceiver(find);
            find = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String action = intent.getAction();

                    if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                        prog.setVisibility(View.VISIBLE);
                    }
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        int rssi=intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                        if(dev.getName()!=null)
                        {availname.add(new Details(dev.getName(),dev.getAddress(),rssi));
                        avail.add(dev);}

                    }
                    if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        prog.setVisibility(View.INVISIBLE);
                        // ArrayAdapter ad = new ArrayAdapter(MainActivity.this, android.R.layout.simple_dropdown_item_1line, avail);
                        // available.setAdapter(ad);
                    }

                    if(avail.size()>0){
                        // ArrayAdapter ad = new ArrayAdapter(BlueListActivity.this, android.R.layout.simple_dropdown_item_1line, availname);
                        CustomAdapter ad=new CustomAdapter(DiscoveringActivity.this,availname);
                        list.setAdapter(ad);
                    }
                }
            };

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
          //  filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            // filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
          //  filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            registerReceiver(find, filter);
            adapter.startDiscovery();


            ptext.setVisibility(View.VISIBLE);
            list.setVisibility(View.VISIBLE);


        }

        if(requestCode==1){
            vis.setText("Your device is now visible for 2:00 minutes");
            new CountDownTimer(120000,1000){

                @Override
                public void onTick(long millisUntilFinished) {
                    vis.setText("Your device is now visible for "+(millisUntilFinished/1000)+" seconds");
                }

                @Override
                public void onFinish() {
                    vis.setText("Tap to make your device visible to all other devices");
                }
            }.start();
        }
    }

    @Override
    protected void onDestroy() {
        adapter.cancelDiscovery();
        unregisterReceiver(find);
        super.onDestroy();
    }


}


