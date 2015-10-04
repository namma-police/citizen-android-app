package nammapolice.ak.com.theateam.nammapolice;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMyLocationChangeListener {

    private GoogleMap map;
    MapView mapView;
    static String lati="0.0";
    static String lang="0.0";
    public  static   String latlangjsonString="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapView = (MapView) findViewById(R.id.MapView);
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);


    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMyLocationChange(Location lastKnownLocation) {

        lati=Double.toString(lastKnownLocation.getLatitude());
        lang=Double.toString(lastKnownLocation.getLongitude());
        double lt=lastKnownLocation.getLatitude();
        double ln=lastKnownLocation.getLongitude();
        LatLng coordinates= new LatLng(lt,ln);
        Marker me = map.addMarker(new MarkerOptions().position(coordinates).title("You are here!!").icon(BitmapDescriptorFactory.fromResource(R.drawable.citizenmarker)));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 8.0f));
        map.setOnMyLocationChangeListener(null);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);

        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(this);
       // Location myLocation= map.getMyLocation();
        final JSONArray latlangArray = new JSONArray();

        try {

            latlangArray.put(0, lati);
            latlangArray.put(1, lang);
            latlangjsonString=latlangArray.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        new AsyncTask<String, Void, String>() {

            int responseCode;

            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(NammaPolice.SERVER_URL + "/location/police");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    Uri.Builder _data = new Uri.Builder()
                            .appendQueryParameter("coordinates", String.valueOf(latlangArray));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                    writer.write(_data.build().getEncodedQuery());
                    writer.flush();
                    writer.close();

                    responseCode = connection.getResponseCode();

                    StringBuilder result = new StringBuilder();
                    String line;
                    if (responseCode > 199 && responseCode < 300) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        reader.close();
                        return result.toString();
                    } else {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        reader.close();
                        return result.toString();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {

                if (responseCode == 200) {
                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        System.out.print(jsonObj.toString());
                        String results = jsonObj.getString("locationDetails");
                        System.out.println(results);
                        JSONArray locationDetails=jsonObj.getJSONArray("locationDetails");
                        if(locationDetails.length()>0) {
                            for (int i = 0; i < locationDetails.length(); i++) {
                            JSONObject copLocation=locationDetails.getJSONObject(i);
                                String address=copLocation.getString("address");
                                JSONArray latlangArray=copLocation.getJSONArray("coordinates");
                                double lat=Double.valueOf(latlangArray.get(0).toString());
                                double lng=Double.valueOf(latlangArray.get(1).toString());
                                LatLng coordinates= new LatLng(lat,lng);
                                Marker me = map.addMarker(new MarkerOptions().position(coordinates).title(address).icon(BitmapDescriptorFactory.fromResource(R.drawable.policemarker)));

                            }
                        }

                        }
                     catch (Exception ex) {
                    }
                } else {
                    Log.d("ERROR"," CONNECTION ERROR");
                }

            }
        }.execute();
    }
}

//        LatLng sydney = new LatLng(-34, 151);
//        Marker me = map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").icon(BitmapDescriptorFactory.fromResource(R.drawable.mapcopmarkerldpi)));




