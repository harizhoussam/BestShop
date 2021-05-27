package com.example.ourapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.util.Base64;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class CreatePost extends AppCompatActivity {

    ImageView addImage;
    Button postBtn, currentLocationBtn;
    EditText write_post;
    TextView txt_v, txtVLocation;
    private static final int ResultLoadImage = 100;
    public Bitmap photo;
    String ConvertImage;
    String text, username;
    Double longitude, latitude;
    final static int RequestLocationCode = 99;
    private final String MY_URL_STRING = "http://192.168.0.17/gettingdata/creatingPost.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        addImage = findViewById(R.id.add_image);
        write_post = findViewById(R.id.createText);
        txt_v = findViewById(R.id.textView2);
        txtVLocation= findViewById(R.id.txtViewLocation);
        postBtn = findViewById(R.id.postButton);
        currentLocationBtn=findViewById(R.id.currentLocBtn);



        currentLocationBtn.setOnClickListener(v -> {
            getLocation();
        });

        postBtn.setOnClickListener(v -> {
            if (photo != null) {
                text = write_post.getText().toString();
                if(text.length() > 20){
                    if(longitude != null && latitude != null){
                    username = ((PassDataBtwnActvs) this.getApplication()).getUsername();
                sendPostRequest(null);
                    } else Toast.makeText(CreatePost.this, "Couldn't get location", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(CreatePost.this, "Write proper description", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(CreatePost.this, "Add photo", Toast.LENGTH_SHORT).show();

        });

        addImage.setOnClickListener(v -> {
            // Intent galleryintent =new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // startActivityForResult(galleryintent, ResultLoadImage);

            try {
                Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (imageIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(imageIntent, 1);

                } else
                    Toast.makeText(CreatePost.this, "Camera isn't working", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.i("myTag", ex.getMessage());
            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ResultLoadImage && resultCode == RESULT_OK) {
            try {
                Uri selectedImage = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                addImage.setImageBitmap(BitmapFactory.decodeStream(imageStream));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }


        if (requestCode == 1 && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            photo = Bitmap.createScaledBitmap(photo, 200, 200, false);
            addImage.setImageBitmap(photo);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] byteArrayVar = bytes.toByteArray();
            ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        } else Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_LONG).show();
        //super.onActivityResult(requestCode, resultCode, data);
    }



        public void onBackPressed () {
            Intent intent0 = new Intent(CreatePost.this, posts.class);  ///the transition between this activity and the next
            startActivity(intent0);  // Start the new activity
            finish();

        }

        public void sendPostRequest (View v){
            new CreatePost.PostClass(this).execute();
        }

        class PostClass extends AsyncTask<String, Void, Void> {
            Context ctxt;
            StringBuilder response;


            public PostClass(Context ctxt) {
                this.ctxt = ctxt;
            }

            @Override
            protected Void doInBackground(String... arg0) {
                Log.i("myTag", "This is my message");
                try {
                    String data = URLEncoder.encode("ConvertImage", "UTF-8") + "=" +
                            URLEncoder.encode(ConvertImage, "UTF-8");
                    data += "&" + URLEncoder.encode("text", "UTF-8") + "=" +
                            URLEncoder.encode(text, "UTF-8");
                    data += "&" + URLEncoder.encode("username", "UTF-8") + "=" +
                            URLEncoder.encode(username, "UTF-8");
                    data += "&" + URLEncoder.encode("longitude", "UTF-8") + "=" +
                            URLEncoder.encode(String.valueOf(longitude), "UTF-8");
                    data += "&" + URLEncoder.encode("latitude", "UTF-8") + "=" +
                            URLEncoder.encode(String.valueOf(latitude), "UTF-8");
                    //specify the url to send the request to
                    URL url = new URL(MY_URL_STRING);
                    //open an http connection to that url
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    response = new StringBuilder();
                    String line = "";
                    while ((line = br.readLine()) != null)
                        response.append(line);
                    br.close();
                    //display the result in the main activity
                    CreatePost.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                            Intent intent0 = new Intent(CreatePost.this, posts.class);  ///the transition between this activity and the next
                            startActivity(intent0);  // Start the new activity
                            finish();

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("myTag", "2: SOMETHING WRONG.......");
                }

                return null;

            }
        }
    public void getLocation(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreatePost.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, RequestLocationCode);
        }else
        {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.getFusedLocationProviderClient(CreatePost.this).requestLocationUpdates
                    (locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(CreatePost.this).removeLocationUpdates(this);
                            if(locationResult != null &&  locationResult.getLocations().size() > 0){
                                int latestLocationIndex = locationResult.getLocations().size() -1 ;
                                latitude =
                                        locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                longitude =
                                        locationResult.getLocations().get(latestLocationIndex).getLongitude();
                                if(longitude!= null && latitude != null){
                                    currentLocationBtn.setBackgroundColor(Color.GREEN);


                                }
                                String lat= String.valueOf(latitude);
                                String lon = String.valueOf(longitude);
                                String specificloc = ("geo:" + lat + "," + lon + "?q="+lat+ "," +lon);
                                txtVLocation.setText(String.format("lat: %s \n long:%s",latitude,longitude));

                                /* Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(specificloc));
                                Intent chooser = Intent.createChooser(intent, "Launch map");
                                startActivity(chooser);*/


                            }
                        }
                    }, Looper.getMainLooper());
        }


    }
    }
