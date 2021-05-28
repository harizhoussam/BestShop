package com.example.ourapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

public class MarketSearchResult extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView tvUsername;
    ListView listView1;
    Button marketBtn, usersBtn;
    FloatingActionButton createPost;
    EditText tvSearch;
    customadapter adptr;

    String respLikes; // response from getLikes
    String respNumLikes; // response of
    String insertLikeResponse;

    String likedPost; //to save in the postID whenever a post is liked
    boolean placeLike = false;
    private static String name[];
    private static String desig[];
    private static String img[];
    private static String lon[];
    private static String lat[];
    String username, longitude, latitude, searchText;  //the data passed between activities
    String[] likedPosts; //to get
    String[]likedPostsNum, post; // to get posts id and number of likes on each
    String[] PostId; // to fill with postID when getting posts array

    String MY_URL_STRING= "http://192.168.0.17/gettingdata/marketSearchResult.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_search_result);

        listView1=findViewById(R.id.listView2);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView=findViewById(R.id.nav_view);
        marketBtn=findViewById(R.id.btn_market);
        usersBtn=findViewById(R.id.users_btn);
        createPost=findViewById(R.id.floatingActionButton2);
        toolbar=findViewById(R.id.toolbar);
        tvSearch = findViewById(R.id.tv_search);
        tvUsername=findViewById(R.id.textViewUsername);

        username =((PassDataBtwnActvs) this.getApplication()).getUsername();
        longitude =((PassDataBtwnActvs) this.getApplication()).getLongitude();
        latitude= ((PassDataBtwnActvs) this.getApplication()).getLatitude();
        searchText= ((PassDataBtwnActvs) this.getApplication()).getSearch();

        Log.i("That's my longitude", String.valueOf(Double.parseDouble(longitude)));
        Log.i("That's my latitude", String.valueOf(Double.parseDouble(latitude)));// to show how the nearest posts show first


        tvUsername.setText(username);
        placeLike=false;

        ActionBarDrawerToggle toggle  = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.nav_drawer_open,R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setNavigationViewListener();
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        sendPostRequest(null);

        marketBtn.setTextColor(Color.parseColor("#A5D1F4"));
        usersBtn.setTextColor(Color.parseColor("#000000"));

        usersBtn.setOnClickListener(v -> {
            Intent intent0= new Intent(MarketSearchResult.this, posts.class);  ///the transition between this activity and the next
            startActivity(intent0);  // Start the new activity
            finish();
        });

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long viewId = view.getId();

                if (viewId == R.id.textTextView2) {
                    Log.i("Zaza", String.valueOf(PostId[position]));
                    likedPost = String.valueOf(PostId[position]);
                    placeLike = true;
                    sendPostRequest(null);

                } else {

                    //Toast.makeText(getApplicationContext(), lon[position], Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String specificloc = ("geo:" + lat[position] + "," + lon[position] + "?q=" + lat[position] + "," + lon[position]);
                    intent.setData(Uri.parse(specificloc));
                    Intent chooser = Intent.createChooser(intent, "Launch map");
                    startActivity(chooser);
                }
            }
        });


        marketBtn.setOnClickListener(v -> {
            Intent intent0= new Intent(MarketSearchResult.this, MarketPosts.class);  ///the transition between this activity and the next
            startActivity(intent0);  // Start the new activity
            finish();
        });

        createPost.setOnClickListener(v -> {

            Intent intent0= new Intent(MarketSearchResult.this, CreateMarketPost.class);  ///the transition between this activity and the next
            startActivity(intent0);  // Start the new activity
            finish();
        });

        tvSearch.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //final int DRAWABLE_LEFT = 0;
                //final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                //final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (tvSearch.getRight() - tvSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // action here
                        searchText=tvSearch.getText().toString();
                        sendPostRequest(null);

                        return true;
                    }
                }
                return false;
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.itemLogout: {
                Intent intent0= new Intent(MarketSearchResult.this, MainActivity.class);  ///the transition between this activity and the next
                startActivity(intent0);  // Start the new activity
                finish();
                break;
            }
            case R.id.item1: {
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.item2: {
                Toast.makeText(getApplicationContext(), "Account", Toast.LENGTH_LONG).show();
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    public void sendPostRequest (View v){
        new MarketSearchResult.PostClass(this).execute();
    }

    class PostClass extends AsyncTask<String, Void, Void>
    {
        Context ctxt;
        StringBuilder Postsresponse;


        public PostClass(Context ctxt){
            this.ctxt=ctxt;
        }

        @Override
        protected Void doInBackground(String... arg0) {
            Log.i("myTag", "This is my   FIRST   message");
            try{
                if(placeLike && likedPost !=null){
                    insertLikeResponse=insertLike();
                }
                if(!placeLike) {
                    String data  = URLEncoder.encode("longitude", "UTF-8") + "=" +
                            URLEncoder.encode(longitude, "UTF-8");
                    data += "&" + URLEncoder.encode("latitude", "UTF-8") + "=" +
                            URLEncoder.encode(latitude, "UTF-8");
                    data += "&" + URLEncoder.encode("searchText", "UTF-8") + "=" +
                            URLEncoder.encode(searchText, "UTF-8");
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
                    Postsresponse = new StringBuilder();
                    String line = "";
                    while ((line = br.readLine()) != null)
                        Postsresponse.append(line);
                    br.close();
                    respLikes = getLikes(); //get likes the user performed before
                    respNumLikes = getLikesNumber();


                }

                //display the result in the main activity
               MarketSearchResult.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(!placeLike) {
                            JSONObject jsonLikes = null;
                            try {
                                jsonLikes = new JSONObject(respLikes);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONArray jArray1 = jsonLikes.getJSONArray("likes"); //get the array of likes user performed from likes table
                                getLikesArray(jArray1);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JSONObject jsonLikesNum = null;
                            try {
                                jsonLikesNum = new JSONObject(respNumLikes);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONArray jArrayLikesNum = jsonLikesNum.getJSONArray("likesNum"); //get the array of likes user performed from likes table
                                getLikesNumArray(jArrayLikesNum);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            String response_str = Postsresponse.toString(); //get the String from
                            if(response_str.equals("No Result")) {
                                Toast.makeText(getApplicationContext(), "No Result", Toast.LENGTH_LONG).show();
                            }
                            else {
                                JSONObject jsonPosts = null;
                                try {
                                    jsonPosts = new JSONObject(response_str);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    JSONArray jArray = jsonPosts.getJSONArray("posts");
                                    getmainArray(jArray);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                        if (placeLike){
                            Log.i("TTTT", insertLikeResponse);
                            placeLike=false;
                        }
                    }
                });

            }
            catch (IOException e) {
                e.printStackTrace();
                Log.i("myTag", "2: SOMETHING WRONG.......");
            }

            return null;

        }
    }

    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            Intent intent0 = new Intent(MarketSearchResult.this, MarketPosts.class);
            startActivity(intent0);  // Start the new activity
            finish();
        }

    }

    public void getLikesArray(JSONArray arr) throws JSONException {
        likedPosts = new String[arr.length()];
        JSONObject ja = null;
        for (int i = 0; i < arr.length(); i++) {
            ja = arr.getJSONObject(i);
            likedPosts[i]=ja.getString("post_id");
            Log.i("LIKES", likedPosts[i] );
        }


    }
    public void getLikesNumArray(JSONArray arr) throws JSONException {
        likedPostsNum = new String[arr.length()];
        post = new String[arr.length()];
        JSONObject ja = null;
        for (int i = 0; i < arr.length(); i++) {
            ja = arr.getJSONObject(i);
            post[i] = ja.getString("post_id");
            likedPostsNum[i]= ja.getString("count");
            Log.i("LIKESNUM", likedPostsNum[i]);
        }
    }
    public void getmainArray(JSONArray arr){
        try {
            JSONObject ja = null;
            name = new String[arr.length()];
            desig = new String[arr.length()];
            img = new String[arr.length()];
            lon = new String[arr.length()];
            lat = new String[arr.length()];
            PostId = new String[arr.length()];
            String[] liked = new String[arr.length()];
            String[] NumOfLikes = new String[arr.length()];


            for (int i = 0; i < arr.length(); i++) {
                ja = arr.getJSONObject(i);
                name[i] = ja.getString("username");
                desig[i] = ja.getString("text");
                img[i] ="http://192.168.0.17/gettingdata/PostsImages/MarketPosts/" + ja.getString("image");
                lon[i]=ja.getString("longitude");
                lat[i]=ja.getString("latitude");
                //Log.i("longitude lat", String.valueOf(Double.parseDouble(lon[i])+Double.parseDouble(lat[i])));
                Log.i("longitude lat", String.valueOf(Double.parseDouble(lon[i])));
                Log.i("longi latitude", String.valueOf(Double.parseDouble(lat[i])));
                //Display the distance of posts that u got. The first should be the closest
                Double distance= Math.sqrt( Math.pow(Double.parseDouble(longitude) - Double.parseDouble(lon[i]), 2) +
                        Math.pow(Double.parseDouble(latitude) - Double.parseDouble(lat[i]), 2));
                Log.i("Distance",  String.valueOf(distance));

                PostId[i]=ja.getString("id");
                if(Arrays.asList(likedPosts).contains(PostId[i])){ //see if the list of posts the user likes contains this postID
                    liked[i]= "1";
                }else liked[i]= "0";

                if(Arrays.asList(post).contains(PostId[i])){ //see if the list of posts the user likes contains this postID
                    NumOfLikes[i]= likedPostsNum[Arrays.asList(post).indexOf(PostId[i])];
                }else  NumOfLikes[i]= "0";
                Log.i("LIKED?", liked[i]);
            }

            adptr = new customadapter(getApplicationContext(), name, desig, img, liked, NumOfLikes);
            listView1.setAdapter(adptr);
            listView1.setTextFilterEnabled(true);

        } catch (Exception ex) {
            //Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.i("exception getMAinArray", ex.getMessage());
        }
    }

    // this method gets the likes that the user have inserted before to any post
    public String getLikes() throws IOException {
        StringBuilder resp;
        URL url= new URL("http://192.168.0.17/gettingdata/getMarketLikes.php");
        HttpURLConnection connection =(HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        String data  = URLEncoder.encode("username", "UTF-8") + "=" +
                URLEncoder.encode(username, "UTF-8");

        connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
        connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
        connection.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(data);
        wr.flush();
        Log.i("myTag", "THIS IS 2ND MESSAGE.....");
        BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream()));
        resp= new StringBuilder();
        String line="";
        while ((line=br.readLine())!=null)
            resp.append(line);
        br.close();
        String resp_str = resp.toString();
        return resp_str;


    }

    public String insertLike() throws IOException {
        StringBuilder resp;
        URL url= new URL("http://192.168.0.17/gettingdata/insertMarketLike.php");
        HttpURLConnection connection =(HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        String data  = URLEncoder.encode("username", "UTF-8") + "=" +
                URLEncoder.encode(username, "UTF-8");
        data += "&" + URLEncoder.encode("likedPost", "UTF-8") + "=" +
                URLEncoder.encode(likedPost, "UTF-8");
        connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
        connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
        connection.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(data);
        wr.flush();
        Log.i("myTag", "PLACINGLIKE.....");
        BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream()));
        resp= new StringBuilder();
        String line="";
        while ((line=br.readLine())!=null)
            resp.append(line);
        br.close();
        insertLikeResponse = resp.toString();
        return insertLikeResponse;



    }

    public String getLikesNumber() throws IOException {
        StringBuilder resp;
        String response;
        URL url= new URL("http://192.168.0.17/gettingdata/getMarketLikesNumber.php");
        HttpURLConnection connection =(HttpURLConnection)url.openConnection();
        connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
        connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
        connection.setDoOutput(true);
        Log.i("myTag", "getting num of likes");
        BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream()));
        resp= new StringBuilder();
        String line="";
        while ((line=br.readLine())!=null)
            resp.append(line);
        br.close();
        response = resp.toString();
        return response;



    }


}
