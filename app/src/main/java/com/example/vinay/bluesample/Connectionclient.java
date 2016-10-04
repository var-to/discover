package com.example.vinay.bluesample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Vinay on 02-10-2016.
 */

public class Connectionclient extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

private  final  BluetoothAdapter bTAdapter;
    public Connectionclient(BluetoothAdapter bTAdapter,BluetoothDevice device, UUID myUUID) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.bTAdapter=bTAdapter;


        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(myUUID);
        } catch (IOException e){}
        catch (NullPointerException e){
            Log.d("COnnection","null"+e.toString());
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        bTAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
       // manageConnectedSocket(mmSocket);
        ConnectedThread ob=new ConnectedThread(mmSocket);
        ob.start();
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}

