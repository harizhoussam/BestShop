package com.example.ourapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SignUpActivity extends AppCompatActivity {

    private Button button1;
    private EditText usernameField, passwordField, fullnameField;
    private TextView t_already_have_an_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        button1 =findViewById(R.id.btn_createMarketAccnt);
        usernameField=findViewById(R.id.t_marketName);
        fullnameField=findViewById(R.id.t_firstname);
        passwordField=findViewById(R.id.t_marketPass);
        t_already_have_an_account=findViewById(R.id.txt_have_MarketAccount);

        t_already_have_an_account.setOnClickListener(v -> {
            Intent intent1= new Intent(SignUpActivity.this, MainActivity.class);  ///the transition between this activity and the next
            startActivity(intent1);  // Start the new activity
            finish();
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPostRequest(null);
            }
        });

    }
    public void sendPostRequest (View v){
        new SignUpActivity.PostClass(this).execute();
    }

    private class PostClass extends AsyncTask<String, Void, Void>
    {
        Context ctxt;
        TextView tv_response;
        StringBuilder response;


        public PostClass(Context ctxt){
            this.ctxt=ctxt;
            this.tv_response=tv_response;
        }

        @Override
        protected Void doInBackground(String... arg0) {
            Log.i("myTag", "This is my message");
            try{
                String urlParameters="tf_un";

                String username = usernameField.getText().toString();
                String fullname = fullnameField.getText().toString();
                String password =passwordField.getText().toString();
                String data  = URLEncoder.encode("username", "UTF-8") + "=" +
                        URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("fullname", "UTF-8") + "=" +
                        URLEncoder.encode(fullname, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +
                        URLEncoder.encode(password, "UTF-8");
                //specify the url to send the request to
                URL url= new URL("http://192.168.0.17/gettingdata/SignUpData.php");
                //open an http connection to that url
                HttpURLConnection connection =(HttpURLConnection)url.openConnection();
                //the the request method to POST or GET
                connection.setRequestMethod("POST");
                //set the parameters for the user agent
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
                
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(data);
                wr.flush();
                Log.i("myTag", "THIS IS 2ND MESSAGE.....");
                BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream()));
                response= new StringBuilder();
                String line="";
                while ((line=br.readLine())!=null)
                    response.append(line);
                br.close();
                //display the result in the main activity
                SignUpActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        Log.i("myTag", response.toString());
                        Toast.makeText(SignUpActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        if(response.toString().contains("SignUp Successful")){

                            Intent intent2= new Intent(SignUpActivity.this, MainActivity.class);  ///the transition between this activity and the next
                            startActivity(intent2);  // Start the new activity
                            finish();
                        }

                    }
                });

            }
            
            catch (IOException e)
            {
                e.printStackTrace();
                Log.i("myTag", "2: SOMETHING WRONG.......");
            }

            return null;

        }
    }
    public void onBackPressed () {
        Intent intent0 = new Intent(SignUpActivity.this, MainActivity.class);  ///the transition between this activity and the next
        startActivity(intent0);  // Start the new activity
        finish();

    }
}
