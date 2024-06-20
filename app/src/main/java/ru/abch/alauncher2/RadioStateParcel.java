package ru.abch.alauncher2;

import android.os.Parcel;
import android.os.Parcelable;

public class RadioStateParcel implements Parcelable {
    public int freq, volume, rssi;
    public boolean mute;
    public String name, info;
    public float voltage;
    protected RadioStateParcel(Parcel in) {
        freq = in.readInt();
        volume = in.readInt();
        rssi = in.readInt();
        mute = in.readByte() != 0;
        name = in.readString();
        info = in.readString();
        voltage = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(freq);
        dest.writeInt(volume);
        dest.writeInt(rssi);
        dest.writeByte((byte) (mute ? 1 : 0));
        dest.writeString(name);
        dest.writeString(info);
        dest.writeFloat(voltage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RadioStateParcel> CREATOR = new Creator<RadioStateParcel>() {
        @Override
        public RadioStateParcel createFromParcel(Parcel in) {
            return new RadioStateParcel(in);
        }

        @Override
        public RadioStateParcel[] newArray(int size) {
            return new RadioStateParcel[size];
        }
    };
}
