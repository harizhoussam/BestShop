package com.example.ourapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    private EditText usernameField,passwordField;
    String usernameGlobal;
    TextView  t_signup, txtV;
    Double longitude, latitude;
    private String MY_URL_STRING = "http://192.168.0.17/gettingdata/LoginData.php";
    private static final int RequestLocationCode = 99 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonLogin = findViewById(R.id.btnLogin);
        usernameField = (EditText)findViewById(R.id.txtName);
        passwordField = (EditText)findViewById(R.id.txtPass);
        t_signup=findViewById(R.id.txt_signup);
        txtV=findViewById(R.id.txtV);
        TextView marketOwner = findViewById(R.id.txt_MarketOwner);

        marketOwner.setOnClickListener(v -> {
            Intent intent1= new Intent(MainActivity.this, MarketLogin.class);  ///the transition between this activity and the next
            startActivity(intent1);  // Start the new activity
            finish();
        });

        getLocation();
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameField.getText().length() > 0 && passwordField.getText().length() > 0 )
                    if(longitude != null || latitude != null)
                sendPostRequest(null);
                    else Toast.makeText(MainActivity.this, "Try again in a second, getting location", Toast.LENGTH_LONG).show();
                else Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            }
        });

        t_signup.setOnClickListener(v -> {
            Intent intent1= new Intent(MainActivity.this, SignUpActivity.class);  ///the transition between this activity and the next
            startActivity(intent1);  // Start the new activity
            finish();
        });

    }
    public void getLocation(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, RequestLocationCode);
        }else
        {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates
                    (locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
                    if(locationResult != null &&  locationResult.getLocations().size() > 0){
                        int latestLocationIndex = locationResult.getLocations().size() -1 ;
                         latitude =
                                locationResult.getLocations().get(latestLocationIndex).getLatitude();
                         longitude =
                                locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        txtV.setText(String.format("lat: %s \n long:%s",latitude,longitude));


                    }
                }
            }, Looper.getMainLooper());
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RequestLocationCode && grantResults.length >0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }

        }
    }

    public void sendPostRequest (View v){
        new PostClass(this).execute();
    }

    class PostClass extends AsyncTask <String, Void, Void>
    {
        Context ctxt;
        StringBuilder response;


        public PostClass(Context ctxt){
            this.ctxt=ctxt;
        }

        @Override
        protected Void doInBackground(String... arg0) {
            Log.i("myTag", "This is my message");
            try{
                String urlParameters="tf_un";
                String username = usernameField.getText().toString();
                String password =passwordField.getText().toString();
                String data  = URLEncoder.encode("username", "UTF-8") + "=" +
                        URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +
                        URLEncoder.encode(password, "UTF-8");
                //specify the url to send the request to
                URL url= new URL(MY_URL_STRING);
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
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        usernameGlobal = username;
                         CheckCredentials(response);

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

    public void CheckCredentials(StringBuilder response){
        Log.i("myTag", "ENTERED CHECK CREDENTIALS FUNCTION");

        if (response.toString().contains("True")){
            Log.i("myTag", "Welcome");
            ((PassDataBtwnActvs) this.getApplication()).setUsername(usernameGlobal);
            ((PassDataBtwnActvs) this.getApplication()).setLongitude(String.valueOf(longitude));
            ((PassDataBtwnActvs) this.getApplication()).setLatitude(String.valueOf(latitude));


            Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
            Intent intent0= new Intent(MainActivity.this, posts.class);  ///the transition between this activity and the next
            startActivity(intent0);  // Start the new activity
            finish();
        }
        else Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
    }

}

