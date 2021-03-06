package com.example.artwithyael;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;

import com.example.artwithyael.showimages.Image;
import com.example.artwithyael.showimages.RecyclerViewAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {


    public RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;

    String url = "http://artwithyael.000webhostapp.com/retrieve_images.php";

    Image[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            getJSON(url);
        } catch (Exception e) {
            Log.d("ERROR", "ERROR CAUGHT IN MAIN ACTIVITY");
            e.printStackTrace();
            return;
        }


        // Set up recycler view
        recyclerView = findViewById(R.id.recyclerView);
        int numberOfColumns = 2;

        //gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(numberOfColumns, 1));

        DividerItemDecoration dividerHorizontal = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        DividerItemDecoration dividerVertical = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);

        dividerHorizontal.setDrawable(getApplicationContext().getResources().getDrawable(R.drawable.border));
        dividerVertical.setDrawable(getApplicationContext().getResources().getDrawable(R.drawable.border));

        recyclerView.addItemDecoration(dividerHorizontal);
        recyclerView.addItemDecoration(dividerVertical);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void setUpAdapter(Image[] data){
        adapter = new RecyclerViewAdapter(this, data);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }



    public void onItemClick(View view, int position){
        Log.i("TAG", "You clicked number "+adapter.getItem(position)+", which is at cell position "+position);

        Intent intent = new Intent(this, ViewArtDetails.class);
        intent.putExtra("image", data[position]);
        startActivity(intent);
    }



    private void getJSON(final String urlLink){
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                try{
                    loadIntoListView(s);
                    setUpAdapter(data);
                } catch (JSONException e){
                    Log.d("ERROR", "ERROR");
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids){
                try{
                    URL url = new URL(urlLink);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    StringBuilder stringBuilder = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine()) != null){
                        stringBuilder.append(json+"\n");
                    }
                    return stringBuilder.toString().trim();
                } catch (IOException e){
                    Log.d("ERROR", "ERROR");
                    throw new RuntimeException(e);
                } catch (Exception e){
                    Log.d("ERROR", "ERROR");
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
        System.out.println(data);
    }

    private void loadIntoListView(String json) throws JSONException{
        JSONArray jsonArray = new JSONArray(json);
        data = new Image[jsonArray.length()];
        for(int i=0; i<jsonArray.length(); i++){
            JSONObject object = jsonArray.getJSONObject(i);

            String imagePath = object.getString("ImagePath");
            String title = object.getString("Title");
            String description = object.getString("Description");
            String date = object.getString("DateCreated");

            Image newImage = new Image(imagePath, title, description, date);
            data[i] = newImage;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }




}
