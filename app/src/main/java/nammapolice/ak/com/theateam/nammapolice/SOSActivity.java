package nammapolice.ak.com.theateam.nammapolice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SOSActivity extends AppCompatActivity {

    private boolean bound = false;

    SosService sosService;

    ServiceConnection locationConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SosService.SoSServiceBinder binder = (SosService.SoSServiceBinder) service;
            sosService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra("RESPONSE");

            try {
                JSONObject jsonResult=new JSONObject(response);

            } catch (JSONException e) {
                e.printStackTrace();
            }
//            if(bound){
//                sosService.setSendLocation(false);
//            }
        }
    };

    ImageButton callHelp_Button;
    Button mapNavigator_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        callHelp_Button = (ImageButton) findViewById(R.id.callHelp_sos_Button);
        mapNavigator_Button = (Button) findViewById(R.id.mapNavigator_sos_button);
        Intent intent = new Intent(SOSActivity.this, SosService.class);
        HashMap<String, String> current = NammaPolice.getUser(getApplicationContext());
        intent.putExtra("USER_ID", current.get("USER_ID"));
        intent.putExtra("USER_NAME", current.get("USER_NAME"));
        startService(intent);
        bindService(intent, locationConnection, BIND_AUTO_CREATE);
        registerReceiver(receiver, new IntentFilter(SocketService.BROADCAST_ACTION));
       mapNavigator_Button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(SOSActivity.this, MapsActivity.class);
               startActivity(intent);
           }
       });


        callHelp_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bound) {

                    sosService.setSendLocation(true);


                }
            }
        });




    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        unbindService(locationConnection);
        super.onDestroy();

    }
}