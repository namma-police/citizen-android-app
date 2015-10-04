package nammapolice.ak.com.theateam.nammapolice;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class IssueActivity extends AppCompatActivity {
        Button resolve_Button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        resolve_Button=(Button) findViewById(R.id.closeIssue_issue_button);
        Intent intent = getIntent();
        String citizenName=intent.getStringExtra("citizenName");
        final String issueId=intent.getStringExtra("issueId");
        resolve_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<String, Void, String>() {

                    int responseCode;

                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            URL url = new URL(NammaPolice.SERVER_URL + "/help/acknowledge/");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.setDoOutput(true);

                            Uri.Builder _data = new Uri.Builder()
                                    .appendQueryParameter("issueId", params[0]);
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
                    finish();
                        System.exit(0);

                    }
                }.execute(issueId);
            }
        });

    }

}
