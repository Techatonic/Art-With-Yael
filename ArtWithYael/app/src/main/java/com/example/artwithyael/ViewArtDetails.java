package com.example.artwithyael;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.artwithyael.showimages.Image;

public class ViewArtDetails extends AppCompatActivity {

    Image data;
    String URLTemplate = "http://artwithyael.000webhostapp.com/Images/";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_art_details_layout);
        data = getIntent().getParcelableExtra("image");

        ImageView imageView = findViewById(R.id.image);
        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        TextView dateView = findViewById(R.id.date);

        //imageView.getLayoutParams().width = (int) (0.8 * Resources.getSystem().getDisplayMetrics().widthPixels);

        Glide.with(getApplicationContext())
                .load(URLTemplate+data.getImagePath())
                //.override(imageView.getWidth(),500)
                .apply(new RequestOptions().override((int) Resources.getSystem().getDisplayMetrics().widthPixels, (int) (0.6 * Resources.getSystem().getDisplayMetrics().heightPixels)))
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        title.setText(data.getTitle());
        description.setText("");
        //description.setText(data.getDescription());

        String date = data.getDate();
        String[] splittedDate = date.split("-");
        String dateToShow = splittedDate[2]+"-"+splittedDate[1]+"-"+splittedDate[0];
        dateView.setText(dateToShow);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
