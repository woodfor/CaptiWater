
package com.example.firebasetest1.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class SelectedHouse implements Parcelable {

    public int hid;
    public String name;
    public int bday;
    public String cpl;
    public int nop;
    public int uid;

    public SelectedHouse(int hid, String name, int bday, String cpl, int nop, int uid){
        this.name = name;
        this.bday = bday;
        this.cpl = cpl;
        this.nop = nop;
        this.uid = uid;
    }
    public SelectedHouse(){

    }
    public SelectedHouse(Parcel in){
        this.hid = in.readInt();
        this.name = in.readString();
        this.bday = in.readInt();
        this.cpl = in.readString();
        this.nop = in.readInt();
        this.uid = in.readInt();
    }
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeInt(nop);
        out.writeInt(uid);
        out.writeInt(bday);
        out.writeInt(uid);
        out.writeString(cpl);
    }
    public static final Creator<SelectedHouse> CREATOR = new Creator<SelectedHouse>() {
        @Override
        public SelectedHouse createFromParcel(Parcel in) {
            return new SelectedHouse(in);
        }
        @Override
        public SelectedHouse[] newArray(int size) {
            return new SelectedHouse[size];
        }
    };
    public String getCpl() {
        return cpl;
    }

    public int getNop() {
        return nop;
    }

    public int getUid() {
        return uid;
    }

    public int getBday() {
        return bday;
    }

    public int getId() {
        return hid;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
