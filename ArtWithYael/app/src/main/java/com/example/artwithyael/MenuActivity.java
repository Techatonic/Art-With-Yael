package com.example.artwithyael;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MenuActivity extends AppCompatActivity {

    MaterialButton viewArtButton;
    MaterialButton viewProfileButton;
    MaterialButton uploadNewArtworkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        viewArtButton = findViewById(R.id.view_art_button);
        viewProfileButton = findViewById(R.id.view_profile_button);
        uploadNewArtworkButton = findViewById(R.id.upload_new_artwork_button);

        viewArtButton.setOnClickListener(v -> onButtonClick(0));
        viewProfileButton.setOnClickListener(v -> onButtonClick(1));
        uploadNewArtworkButton.setOnClickListener(v -> onButtonClick(2));
    }

    private void onButtonClick(int buttonClicked){
        Intent intent = null;
        switch (buttonClicked){
            case 0:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(this, ViewProfileActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(this, UploadNewArtworkActivity.class);
                startActivity(intent);
            default:
                break;
        }
    }

}
