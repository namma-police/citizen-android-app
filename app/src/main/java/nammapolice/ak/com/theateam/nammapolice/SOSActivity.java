package nammapolice.ak.com.theateam.nammapolice;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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

public class SOSActivity extends Activity {

    ImageButton callHelp_Button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        callHelp_Button=(ImageButton) findViewById(R.id.callHelp_sos_Button);
        callHelp_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private class AuthAsync extends AsyncTask<Void, Void, Integer> {
        String phno,pwd;
        double longitude,latitude;
        protected void onPreExecute() {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        //http://namitbehl.net/hackathon/fetch_data.php?update_rider=true&riderID=8002144009&riderSource=ndkjfhnskjfdnsjkddnksJ&riderDestination=nnsfkjdnskjndskjandksjd1s53dsaa1
        protected Integer doInBackground(Void... params) {
            try {
                URL url = new URL("http://192.168.1.3:8000/citizen/location/update");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                String[] latlng={Double.toString(latitude),Double.toString(longitude)};
                ArrayList<String> ltlng=new ArrayList<String>();
                ltlng.add(0,Double.toString(latitude));
                ltlng.add(1,Double.toString(longitude));

                Uri.Builder _data = new Uri.Builder().appendQueryParameter("coordinates", ltlng.toString());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(_data.build().getEncodedQuery());
                writer.flush();
                writer.close();
                String line;
                String res = "";
                String result = null;
                InputStreamReader in = new InputStreamReader(connection.getInputStream());


                StringBuilder jsonResults1 = new StringBuilder();
                ArrayList<String> resultList = null;
// Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults1.append(buff, 0, read);
                }
                connection.disconnect();
                JSONObject jsonObj = new JSONObject(jsonResults1.toString());
                System.out.print(jsonObj.toString());
                String results = jsonObj.getString("status");
                Log.d("results", results);
                if(results.matches("loggedIn"))


                {
                    Intent intent=new Intent(SOSActivity.this,MapsActivity.class);
                    startActivity(intent);
                }
//                Intent intent=new Intent(LoginActivity.this,SOSActivity.class);
//                startActivity(intent);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 1;
        }
        protected void onPostExecute(Integer result) {
        }
    }








}
