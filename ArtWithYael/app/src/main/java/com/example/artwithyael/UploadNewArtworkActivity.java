package com.example.artwithyael;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class UploadNewArtworkActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    Toolbar toolbar;
    Button uploadButton;
    EditText artworkTitleField;
    Button selectDateButton;

    DatePickerDialog datePicker;
    Calendar calendar;
    Integer year,month,dayOfMonth;

    Button submitButton;
    ImageView uploadedImage;
    TextView dateText;

    // Entered Info
    Bitmap image = null;
    String title = null;
    String description = null;
    String date = null;

    boolean check = true;

    ProgressBar progressBar;

    boolean success = false;

    String uploadURL = "http://artwithyael.000webhostapp.com/upload_image.php";

    int GET_FROM_GALLERY = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_artwork_layout);

        toolbar = findViewById(R.id.toolbar);
        uploadButton = findViewById(R.id.uploadButton);
        artworkTitleField = findViewById(R.id.artworkTitleField);
        selectDateButton = findViewById(R.id.selectDateButton);
        submitButton = findViewById(R.id.submitButton);
        uploadedImage = findViewById(R.id.uploadedImage);
        dateText = findViewById(R.id.dateText);
        progressBar = findViewById(R.id.progressBar);

        calendar = Calendar.getInstance();

        // Handle back button stuff
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        uploadButton.setOnClickListener(v -> onUploadButtonClick());
        selectDateButton.setOnClickListener(v -> onSelectDateButtonClick());
        submitButton.setOnClickListener(v -> {
            try {
                onSubmitButtonClick();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void onSelectDateButtonClick() {
        uploadButton.setVisibility(View.INVISIBLE);
        artworkTitleField.setVisibility(View.INVISIBLE);
        selectDateButton.setVisibility(View.INVISIBLE);
        submitButton.setVisibility(View.INVISIBLE);
        uploadedImage.setVisibility(View.INVISIBLE);
        dateText.setVisibility(View.INVISIBLE);

        showCalendar();
    }
    private void showCalendar(){
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePicker = new DatePickerDialog(this, this, year, month, dayOfMonth);
        datePicker.show();
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        dateText.setText(dayOfMonth + " - "+(month+1)+" - "+year);
        date = year +"-"+ (month + 1) +"-"+ dayOfMonth;
        Log.d("Date", date);

        uploadButton.setVisibility(View.VISIBLE);
        artworkTitleField.setVisibility(View.VISIBLE);
        selectDateButton.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
        uploadedImage.setVisibility(View.VISIBLE);
        dateText.setVisibility(View.VISIBLE);
    }

    private void onUploadButtonClick() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // Detects request codes
        if(requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK){
            Uri selectedImage = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                uploadedImage.setImageBitmap(image);
            } catch (FileNotFoundException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
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

    private boolean validateInputs(){
        if(year==null || month==null || dayOfMonth == null){
            return false;
        }
        if(image == null){
            return false;
        }
        return true;
    }

    private void onSubmitButtonClick() throws IOException {
        Editable editable = artworkTitleField.getText();
        title = editable == null ? "" : editable.toString(); // Make title empty string if Edit Text is empty
        description = "This is a test description";

        if(title.replaceAll(" ", "") == ""){
            title = "";
        }


        if(validateInputs()){
            artworkTitleField.setEnabled(false);
            selectDateButton.setEnabled(false);
            uploadButton.setEnabled(false);
            submitButton.setEnabled(false);
            //uploadImage();
            ImageUploadToServerFunction();
        }
    }

    public void ImageUploadToServerFunction(){
        ByteArrayOutputStream byteArrayOutputStreamObject ;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStreamObject);
        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            private Context context;

            public AsyncTaskUploadClass(Context context){
                this.context = context;
            }

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);

                // Dismiss the progress dialog after done uploading.
                progressBar.setVisibility(View.GONE);

                // Printing uploading success message coming from server on android app.
                Log.d("Result", result);

                if(result.equals("Success")){
                    Toast.makeText(UploadNewArtworkActivity.this,result,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, MenuActivity.class);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                } else{
                    Toast.makeText(UploadNewArtworkActivity.this,"Error uploading artwork",Toast.LENGTH_LONG).show();
                    artworkTitleField.setEnabled(true);
                    selectDateButton.setEnabled(true);
                    uploadButton.setEnabled(true);
                    submitButton.setEnabled(true);
                }

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put("title", title);
                HashMapParams.put("image", ConvertImage);
                HashMapParams.put("date", date);
                HashMapParams.put("description", description);

                Log.d("Title", title);
                Log.d("Image", ConvertImage);
                Log.d("Description", description);
                Log.d("Date", date);

                String FinalData = imageProcessClass.ImageHttpRequest("http://artwithyael.000webhostapp.com/upload_image.php", HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass(this);
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);
                httpURLConnectionObject = (HttpURLConnection) url.openConnection();
                httpURLConnectionObject.setConnectTimeout(19000);
                httpURLConnectionObject.setReadTimeout(19000);
                httpURLConnectionObject.setRequestMethod("POST");
                httpURLConnectionObject.setDoInput(true);
                httpURLConnectionObject.setDoOutput(true);
                OutPutStream = httpURLConnectionObject.getOutputStream();
                bufferedWriterObject = new BufferedWriter(
                        new OutputStreamWriter(OutPutStream, "UTF-8"));
                bufferedWriterObject.write(bufferedWriterDataFN(PData));
                bufferedWriterObject.flush();
                bufferedWriterObject.close();
                OutPutStream.close();
                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReaderObject.readLine()) != null){
                        stringBuilder.append(RC2);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
            StringBuilder stringBuilderObject;
            stringBuilderObject = new StringBuilder();
            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilderObject.append("&");
                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilderObject.append("=");
                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }
            return stringBuilderObject.toString();
        }

    }
}