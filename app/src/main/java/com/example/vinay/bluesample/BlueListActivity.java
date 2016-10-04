package com.example.vinay.bluesample;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.SystemClock;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BlueListActivity extends AppCompatActivity {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    ListView paired,available;
    ToggleButton bluetooth;
    TextView visibilty,paireddev,availdev;
    BluetoothAdapter adapter;
    BroadcastReceiver find;
    ProgressBar prog;
    Button listen,con;
    BluetoothDevice serververdev;
    int flaglist,conlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_list);
        listen=(Button)findViewById(R.id.button);

        visibilty=(TextView)findViewById(R.id.visibility);
        paireddev=(TextView) findViewById(R.id.textView2);
        availdev= (TextView) findViewById(R.id.textView3);
        paired= (ListView) findViewById(R.id.paireddiv);
        available= (ListView) findViewById(R.id.availdev);
        bluetooth = (ToggleButton) findViewById(R.id.toggleButton2);
        prog = (ProgressBar) findViewById(R.id.progress);

        adapter=BluetoothAdapter.getDefaultAdapter();
        if(adapter==null){
            Toast.makeText(BlueListActivity.this, "Blutooth not supported by your device", Toast.LENGTH_SHORT).show();
        }
        if(adapter.isEnabled()){
            bluetooth.toggle();
            if(flaglist==0){
            visibilty.setVisibility(View.VISIBLE);
            Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i,0);}

        }
        bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(flaglist==0){
                    visibilty.setVisibility(View.VISIBLE);
                    Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(i,0);}
                    //  adapter.enable();
                }
                else{
                    adapter.disable();
                    paired.setVisibility(View.INVISIBLE);
                    paireddev.setVisibility(View.INVISIBLE);
                    availdev.setVisibility(View.INVISIBLE);
                    available.setVisibility(View.INVISIBLE);
                    visibilty.setVisibility(View.INVISIBLE);
                }
            }
        });

        visibilty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

                startActivityForResult(i,1);
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flaglist=1;
                listen.setEnabled(false);
                available.setVisibility(View.INVISIBLE);
                availdev.setVisibility(View.INVISIBLE);
                paired.setVisibility(View.INVISIBLE);
                paireddev.setVisibility(View.INVISIBLE);
                prog.setVisibility(View.INVISIBLE);
               // con.setEnabled(false);
              AcceptThread ob=new AcceptThread(adapter,MY_UUID);
               //ServerConnectThread ob=new ServerConnectThread();
              ob.start();
                //  ob.acceptConnect(adapter,MY_UUID);

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
                                            ActivityCompat.requestPermissions(BlueListActivity.this,
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

            ArrayList<String> list = new ArrayList<>();
            final ArrayList<BluetoothDevice> avail = new ArrayList<>();
            Set<BluetoothDevice> s = adapter.getBondedDevices();
            final ArrayList<Details> availname=new ArrayList<>();

            for (BluetoothDevice bd : s) {

                list.add(bd.getName());
            }
            ArrayAdapter ad = new ArrayAdapter(BlueListActivity.this, android.R.layout.simple_dropdown_item_1line, list);
            paired.setAdapter(ad);
            paired.setVisibility(View.VISIBLE);
            paireddev.setVisibility(View.VISIBLE);

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
                            availname.add(new Details(dev.getName(),dev.getAddress(),rssi));
                        avail.add(dev);

                    }
                    if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        prog.setVisibility(View.INVISIBLE);
                        // ArrayAdapter ad = new ArrayAdapter(MainActivity.this, android.R.layout.simple_dropdown_item_1line, avail);
                        // available.setAdapter(ad);
                    }
                    if(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                        Toast.makeText(BlueListActivity.this, "Entered", Toast.LENGTH_SHORT).show();
                        String state = intent.getStringExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE);
                        BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (state.equals(String.valueOf(BluetoothAdapter.STATE_CONNECTED))) {
                            //BluetoothDevice d=(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                            Toast.makeText(BlueListActivity.this, "connected to " + d.getName(), Toast.LENGTH_SHORT).show();
                        }

                    }
                    if(avail.size()>0){
                       // ArrayAdapter ad = new ArrayAdapter(BlueListActivity.this, android.R.layout.simple_dropdown_item_1line, availname);
                        CustomAdapter ad=new CustomAdapter(BlueListActivity.this,availname);
                        available.setAdapter(ad);
                    }
                }
            };

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
           // filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            registerReceiver(find, filter);
            adapter.startDiscovery();


            availdev.setVisibility(View.VISIBLE);
            available.setVisibility(View.VISIBLE);


            available.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Toast.makeText(BlueListActivity.this,"Pairing with "+avail.get(position).getName(),Toast.LENGTH_LONG).show();
                    serververdev=avail.get(position);
                    Connectionclient ob=new Connectionclient(adapter,serververdev,MY_UUID);
                    ob.start();
                }
            });
        }

        if(requestCode==1){
            visibilty.setText("Your device is now visible for 2:00 minutes");
            new CountDownTimer(120000,1000){

                @Override
                public void onTick(long millisUntilFinished) {
                    visibilty.setText("Your device is now visible for "+(millisUntilFinished/1000)+" seconds");
                }

                @Override
                public void onFinish() {
                    visibilty.setText("Tap to make your device visible to all other devices");
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

