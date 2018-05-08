package com.cabrera.parcial_v7;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Contacto implements Parcelable{
    private long id;
    private String name;
    private ArrayList<String> number;
    private String imageUri;
    private String email;
    private String type;
    private boolean fav;
    private boolean searched;
    private static int amount=0;

    public Contacto(long id,String name, ArrayList<String> number, String imageUri,String email,boolean fav, String type){
        this.id = id;
        this.name = name;
        this.number = number;
        this.imageUri = imageUri;
        this.email = email;
        this.fav=fav;
        searched=true;
        this.type=type;
        amount++;
    }
    public String getName() {
        return name;
    }

    public ArrayList<String> getNumber() {
        return number;
    }

    public String getImageUri() {
        return imageUri;
    }

    public boolean isFav() {
        return fav;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public boolean isSearched() {
        return searched;
    }

    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(ArrayList<String> number) {
        this.number = number;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }

    protected Contacto(Parcel in) {
        id = in.readLong();
        name = in.readString();
        number = in.readArrayList(null);
        imageUri = in.readString();
        email = in.readString();
        type = in.readString();
        fav = in.readByte() != 0;
        searched = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeList(number);
        dest.writeString(imageUri);
        dest.writeString(email);
        dest.writeString(type);
        dest.writeByte((byte) (fav ? 1 : 0));
        dest.writeByte((byte) (searched ? 1 : 0));

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Contacto createFromParcel(Parcel in) {
            return new Contacto(in);
        }

        public Contacto[] newArray(int size) {
            return new Contacto[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static int getAmount() {
        return amount;
    }
}
