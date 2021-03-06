package com.example.artwithyael.showimages;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {

    private String imagePath;
    private String title;
    private String description;
    private String date;

    public Image(String imagePath, String title, String description, String date){
        this.imagePath = imagePath;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public String getImagePath(){
        return imagePath;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public String getDate(){
        return date;
    }


    public Image(Parcel in){
        String[] data = new String[4];

        in.readStringArray(data);
        this.imagePath = data[0];
        this.title = data[1];
        this.description = data[2];
        this.date = data[3];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeStringArray(new String[]{this.imagePath,this.title,this.description,this.date});
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>(){
        @Override
        public Image createFromParcel(Parcel source){
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size){
            return new Image[size];
        }
    };

}
