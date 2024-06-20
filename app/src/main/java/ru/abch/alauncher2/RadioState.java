package ru.abch.alauncher2;

public class RadioState {
    public int freq, volume, rssi;
    public boolean mute;
    public String name, info;
    public float voltage;
    RadioState(int f, int v) {
        this.freq = f;
        this.volume = v;
    }
}
