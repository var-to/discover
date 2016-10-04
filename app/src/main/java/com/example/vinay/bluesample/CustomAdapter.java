package com.example.vinay.bluesample;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by deepika on 9/29/2016.
 */

public class CustomAdapter extends BaseAdapter{
    ArrayList<Details> ar;
    Context context;
    LayoutInflater lf;
    CustomAdapter(Context context, ArrayList<Details> a)
    {
        this.context=context;
        ar=a;
        lf=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return ar.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v= lf.inflate(R.layout.detalay,null);
        final TextView t=(TextView)v.findViewById(R.id.devicename);
        final TextView t2=(TextView)v.findViewById(R.id.mac);
        final TextView t3=(TextView)v.findViewById(R.id.rssi);
        ProgressBar iv=(ProgressBar)v.findViewById(R.id.progressBar);
        String a=ar.get(position).s1;
        String b=ar.get(position).s2;
        int k=ar.get(position).value;
        iv.setMax(100);

        t.setText(a);
        t2.setText(b);
        t3.setText(String.valueOf(k));
        iv.setProgress(100-Math.abs(k));
      /* v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color=context.getResources().getColor(android.R.color.darker_gray,null);
                v.setBackgroundColor(color);
                int rssi=Integer.parseInt(t3.getText().toString());
                String add=t2.getText().toString();
                String dev=t.getText().toString();
                Toast.makeText(context, "Initial= "+rssi+"\nStarting tracking for "+dev, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context,TrackActivity.class);
                intent.putExtra("initial",rssi);
                intent.putExtra("address",add);
                intent.putExtra("device",dev);
                context.startActivity(intent);

            }
        });*/


        return v;
    }
}
