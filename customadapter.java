package com.example.ourapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class customadapter extends ArrayAdapter<String> implements Filterable {
    Context context;
    String[] ttl;
    String[] dsc;
    String[] rimg;
    String[] liked;
    String[] likesNum;


    customadapter(Context c, String[] ttl, String[] dsc, String[] rimg,  String[] liked, String[] likesNum)
    {
        super(c,R.layout.listview_row,R.id.nameTextView,ttl);
        context=c;
        this.ttl=ttl;
        this.dsc=dsc;
        this.rimg=rimg;
        this.liked=liked;
        this.likesNum=likesNum;
    }
    static class ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;
        ImageView img;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {

        ViewHolder holder;
        holder = new ViewHolder();

        //if(convertView == null) {}
            LayoutInflater vi =
                    (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.listview_row, null);


            // cache view fields into the holder
            holder.tv1 = (TextView) convertView.findViewById(R.id.nameTextView);
            holder.tv2 = (TextView) convertView.findViewById(R.id.textTextView);
            holder.tv3 = (TextView) convertView.findViewById(R.id.textTextView2);
            holder.img = (ImageView) convertView.findViewById(R.id.imageViewID);
            holder.tv4 = (TextView) convertView.findViewById(R.id.textViewLikes);

            String url = rimg[position];

            holder.tv1.setText(ttl[position]);
            if (dsc[position].length() < 60)
                holder.tv2.setText(dsc[position]);
            else holder.tv2.setText(dsc[position].substring(0, 57) + "...");

            if (liked[position] == "1") {
                holder.tv3.setText("Unlike");
                holder.tv3.setTextColor(Color.parseColor("#FF0000"));

            } else {
                holder.tv3.setText("Like");
                holder.tv3.setTextColor(Color.parseColor("#00008B"));
                holder.tv3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_like, 0);
            }

            if (likesNum[position].equals("1"))
                holder.tv4.setText(likesNum[position] + " like");
            else holder.tv4.setText(likesNum[position] + " likes");



        holder.tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
                if(holder.tv3.getText().toString().contains("Unlike")){
                    holder.tv3.setText("Like");
                    holder.tv3.setTextColor(Color.parseColor("#00008B"));
                    holder.tv3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_like, 0);
                    Integer numofLikes = Integer.parseInt(likesNum[position]) -1;
                    likesNum[position]=String.valueOf(numofLikes);
                    if(numofLikes != 1)
                    holder.tv4.setText(numofLikes.toString() + " likes");
                    else holder.tv4.setText(numofLikes.toString() + " like");
                }else
                {
                    holder.tv3.setText("Unlike");
                    holder.tv3.setTextColor(Color.parseColor("#FF0000"));
                    holder.tv3.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    //String likes = holder.tv4.getText().toString();
                    Integer numofLikes = Integer.parseInt(likesNum[position]) + 1;
                    likesNum[position]=String.valueOf(numofLikes);
                    if(numofLikes != 1)
                        holder.tv4.setText(numofLikes.toString() + " likes");
                    else holder.tv4.setText(numofLikes.toString() + " like");


                }
            }
        });




        class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
            private String url;
            private ImageView imageView;

            public ImageLoadTask(String url, ImageView imageView) {
                this.url = url;
                this.imageView = imageView;
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    URL connection = new URL(url);
                    InputStream input = connection.openStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    Bitmap resized = Bitmap.createScaledBitmap(myBitmap, 200, 200, true);
                    return resized;
                } catch (Exception e) {
                    Log.i("myTag", e.getMessage());
                }
                return null;
            }
            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                imageView.setImageBitmap(result);
            }
        }

        ImageLoadTask obj=new ImageLoadTask(url,holder.img);
        obj.execute();

        return convertView;
    }

}

