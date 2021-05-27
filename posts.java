package com.example.ourapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.Locale;
import java.util.function.LongToIntFunction;

public class posts extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView tvUsername;
    ListView listView1;
    Button marketBtn, usersBtn;
    FloatingActionButton createPost;
    EditText etSearch;
    customadapter adptr;
    String likedPost;
    boolean placeLike = false;
    private static String name[];
    private static String desig[];
    private static String img[];
    private static String lon[];
    private static String lat[];
    String username, longitude, latitude;
    String[] likedPosts; //to get
    String[]likedPostsNum, post; // to get posts id and number of likes on each
    String[] PostId; // to fill with postID when getting posts array

    String respLikes;
    String respNumLikes;
    String insertLikeResponse;



    private String MY_URL_STRING = "http://192.168.0.17/gettingdata/PostsData.php";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        listView1=findViewById(R.id.listView2);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView=findViewById(R.id.nav_view);
        marketBtn=findViewById(R.id.btn_market);
        usersBtn=findViewById(R.id.users_btn);
        createPost=findViewById(R.id.floatingActionButton2);
        toolbar=findViewById(R.id.toolbar);
        etSearch = findViewById(R.id.tv_search);
        tvUsername=findViewById(R.id.textViewUsername);

        username =((PassDataBtwnActvs) this.getApplication()).getUsername();
        longitude =((PassDataBtwnActvs) this.getApplication()).getLongitude();
        latitude= ((PassDataBtwnActvs) this.getApplication()).getLatitude();


        Log.i("That's my longitude", String.valueOf(Double.parseDouble(longitude)));
        Log.i("That's my latitude", String.valueOf(Double.parseDouble(latitude)));

        tvUsername.setText(username);
        placeLike=false;

        //Add listener for drawer menu
        ActionBarDrawerToggle toggle  = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.nav_drawer_open,R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //set listener on the navigation view for when pressing on item in it like "logout"
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        sendPostRequest(null);


        //Sets on item click listener for when pressed on like textView in listview

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView < ? > parent, View view, int position, long id) {
                long viewId = view.getId();

                if (viewId == R.id.textTextView2) {
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
            Intent intent0= new Intent(posts.this, MarketPosts.class);  ///the transition between this activity and the next
            startActivity(intent0);  // Start the new activity
            finish();

        });

        createPost.setOnClickListener(v -> {

            Intent intent0= new Intent(posts.this, CreatePost.class);  ///the transition between this activity and the next
            startActivity(intent0);  // Start the new activity
            finish();
        });

        //onTouch listener for the search icon on the search editText
        etSearch.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //final int DRAWABLE_LEFT = 0;
                //final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                //final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        goToSearchActivity();


                        return true;
                    }
                }
                return false;
            }
        });

    }

    ///This boolean to see if you pressed on something inside the drawer menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.itemLogout: {
                Intent intent0 = new Intent(posts.this, MainActivity.class);  ///the transition between this activity and the next
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


    //this function is called twice, on the creation and whenever user reacts to a post

    public void sendPostRequest (View v){
        new posts.PostClass(this).execute();
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
                //if the function was called when user presses like
                if(placeLike && likedPost !=null){
                    insertLikeResponse=insertLike();
                }
                //if the function was called not when user presses like
                if(!placeLike) {
                    String data  = URLEncoder.encode("longitude", "UTF-8") + "=" +
                            URLEncoder.encode(longitude, "UTF-8");
                    data += "&" + URLEncoder.encode("latitude", "UTF-8") + "=" +
                            URLEncoder.encode(latitude, "UTF-8");
                    URL url = new URL(MY_URL_STRING);   /// url :  "http://192.168.0.17/gettingdata/PostsData.php"
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
                    respLikes = getUserLikes(); //get likes the user performed before
                    respNumLikes = getLikesNumber();// get the number of likes on each post


                }

                //display the result in the main activity
                posts.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //if function was called not when user presses like
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
                                JSONArray jArrayLikesNum = jsonLikesNum.getJSONArray("likesNum"); //get the array of number of likes on each post
                                getLikesNumArray(jArrayLikesNum);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            String response_str = Postsresponse.toString(); //get the String from stringbuilder
                            JSONObject jsonPosts = null;
                            try {
                                jsonPosts = new JSONObject(response_str);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONArray jArray = jsonPosts.getJSONArray("posts");//get the array of posts data (image url, name, description, id, long, lat)
                                getmainArray(jArray);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        //if function was called when user likes a post
                        if (placeLike){
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

    //When user presses back button on his phone. If navigation menu drawer is open it gets closed. If not, application is closed.
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();

    }

    //gets the data out of the jason array that has which posts the user have liked before
public void getLikesArray(JSONArray arr) throws JSONException {
   likedPosts = new String[arr.length()];
    JSONObject ja = null;
    for (int i = 0; i < arr.length(); i++) {
        ja = arr.getJSONObject(i);
        likedPosts[i]=ja.getString("post_id");
        Log.i("LIKES", likedPosts[i] );
    }


}
    //gets the data out of the jason array that has num of likes on each post
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

    //gets the data out of the json array that has the posts (image url, name, description, longitude, latitude, id)
        // (a) checks on each post if the user has liked it. by using the data gotten from getLikesArray
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
                img[i] ="http://192.168.0.17/gettingdata/PostsImages/UserPosts/" + ja.getString("image");
                lon[i]=ja.getString("longitude");
                lat[i]=ja.getString("latitude");

                Log.i("longitude lat", String.valueOf(Double.parseDouble(lon[i])));
                Log.i("longi latitude", String.valueOf(Double.parseDouble(lat[i])));

                //Display the distance of posts that u got. The first should be the closest. We are using euclidean method
                Double distance= Math.sqrt(Math.pow(Double.parseDouble(longitude) - Double.parseDouble(lon[i]), 2) +
                        Math.pow(Double.parseDouble(latitude) - Double.parseDouble(lat[i]), 2));
                Log.i("Distance",  String.valueOf(distance));

                //(a)\\
                PostId[i]=ja.getString("id");
                if(Arrays.asList(likedPosts).contains(PostId[i])){  //checks if the list of posts the user likes (likedPosts) contains this postID
                    liked[i]= "1";    //sets the data as binary in an array that will be passed to the customadapter class
                }else liked[i]= "0";

                //(b)\\
                if(Arrays.asList(post).contains(PostId[i])){ //checks if posts are liked before. if not then we put NumOfLikes[i] = "0"
                    NumOfLikes[i]= likedPostsNum[Arrays.asList(post).indexOf(PostId[i])]; //gets the num of likes associated with the post id
                }else  NumOfLikes[i]= "0";
            }

            //create a custom adapter and give it the data to be filled in listview. Then set the adapter for the listview
             adptr = new customadapter(getApplicationContext(), name, desig, img, liked, NumOfLikes);
             listView1.setAdapter(adptr);

        } catch (Exception ex) {
            Log.i("exception getMAinArray", ex.getMessage());
        }
    }

    // This function is called inside doInBackground in the Async task and it gets whether the user reacted to any post before
    public String getUserLikes() throws IOException {
        StringBuilder resp;
        URL url= new URL("http://192.168.0.17/gettingdata/getLikes.php");
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
    //This function is called inside doInBackground in the Async task and it inserts or removes the like in the database
    public String insertLike() throws IOException {
        StringBuilder resp;
        URL url= new URL("http://192.168.0.17/gettingdata/insertLike.php");
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

    //this function gets the number of likes on each post
    public String getLikesNumber() throws IOException {
        StringBuilder resp;
        String response;
        URL url= new URL("http://192.168.0.17/gettingdata/getLikesNumber.php");
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

    //saves the searched text to PassDataBtwnActvs and opens SearchResultActivity
    public void goToSearchActivity(){
        ((PassDataBtwnActvs) this.getApplication()).setSearch(etSearch.getText().toString());
        Log.i("SearchText", etSearch.getText().toString());

        Intent searchIntent= new Intent(posts.this, SearchResultActivity.class);  ///the transition between this activity and the next
        startActivity(searchIntent);  // Start the new activity
        finish();

    }



}