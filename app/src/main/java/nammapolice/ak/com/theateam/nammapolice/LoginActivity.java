package nammapolice.ak.com.theateam.nammapolice;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.RequestQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;


public class LoginActivity extends Activity {
    Button login_Button,register_Button,support_Button;
    EditText phoneNumber_EditText,password_EditText;
    CheckBox rememberMe_CheckBox;
    final String url = "some/url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneNumber_EditText=(EditText) findViewById(R.id.phNo_login_editText);
        password_EditText=(EditText) findViewById(R.id.pwd_login_editText);
        rememberMe_CheckBox=(CheckBox) findViewById(R.id.remember_login_checkBox);
        login_Button=(Button) findViewById(R.id.login_login_button);
        register_Button=(Button) findViewById(R.id.rgstr_login_button);
        support_Button=(Button) findViewById(R.id.support_login_button);

        login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            AuthAsync authAsync=new AuthAsync();
                authAsync.execute();
            }
        });
        register_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_so, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private class AuthAsync extends AsyncTask<Void, Void, Integer> {
        String phno,pwd;

        protected void onPreExecute() {
        phno=phoneNumber_EditText.getText().toString();
            pwd=password_EditText.getText().toString();
        }
        //http://namitbehl.net/hackathon/fetch_data.php?update_rider=true&riderID=8002144009&riderSource=ndkjfhnskjfdnsjkddnksJ&riderDestination=nnsfkjdnskjndskjandksjd1s53dsaa1
        protected Integer doInBackground(Void... params) {
            try {
                URL url = new URL("http://192.168.1.3:8000/citizen/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                Uri.Builder _data = new Uri.Builder().appendQueryParameter("phone",phno).appendQueryParameter("password", pwd);
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
                    Intent intent=new Intent(LoginActivity.this,SOSActivity.class);
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















//class AuthAsyncs extends AsyncTask<Void, Void, Integer>
//{
//    protected void onPreExecute (){
//        Log.d("PreExceute", "On pre Exceute......");
//    }
//
//    protected Integer doInBackground(Void...arg0) {
//        Log.d("DoINBackGround","On doInBackground...");
//        HttpResponse response;
//        JSONObject json = new JSONObject();
//        HttpClient client = new DefaultHttpClient();
//        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
//        try {
//            HttpPost post = new HttpPost("http://192.168.1.3:8000/citizen/login");
//            json.put("phone", "8553659345");
//            json.put("password", "12345");
//            StringEntity se = new StringEntity( json.toString());
//            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//            post.setEntity(se);
//            response = client.execute(post);
//
//                    /*Checking response */
//            if(response!=null){
//                InputStream in = response.getEntity().getContent(); //Get the data in the entity
//            }
//
//        } catch(Exception e) {
//            e.printStackTrace();
//            Log.d("response ", "Cannot Estabilish Connection");
//        }
//
//
//        return 0;
//    }
//
//
//
//
//
//
//    protected void onPostExecute(String result) {
//
//    }
//}
