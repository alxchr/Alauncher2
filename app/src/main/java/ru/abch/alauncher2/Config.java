package ru.abch.alauncher2;


import android.graphics.drawable.Drawable;
import android.util.Log;


/**
 * Created by alx on 23.08.17.
 */

public class Config {
    public static int TOUCH_SCROLL_SLOP = 5;
    String TAG = "Config";
    public final static int cellSize = 64;
    public static int iconSize = 160; //80 -normal 160 - Vim
    public static int radioPage = 0;
    public static int startPage = 1;
    public static int appsPage = 2;
    public static int totalPages = 3;
    private static int widgetAreaWidth = 0;
    private static int widgetAreaHeight = 0;
    public static int widgetAreaColumns = 0;
    public static int widgetAreaRows = 0;
    public static int REQUEST_PICK_APPWIDGET = 100;
    public static int REQUEST_CREATE_APPWIDGET = 101;
    public static int DB_VERSION = 1;
    public static double earthRadius = 6371210.0;
    private static boolean busycell[][]= new boolean[32][64];
    private static String mcuDevice = "";
    private static int mcuSpeed = 9600;
    public static String getMCUDevice(){
        return mcuDevice;
    }
    public static int getMCUSpeed(){
        return mcuSpeed;
    }
    public static void setMCUDevice(String dev){
        mcuDevice = dev;
    }
    public static void setMCUSpeed(int speed){
        mcuSpeed = speed;
    }

    public static void setConfig (int w, int h, int c, int r) {
        widgetAreaWidth = w;
        widgetAreaHeight = h;
        widgetAreaColumns = c;
        widgetAreaRows = r;
    }
    public static void setBusy(int i, int j) {
        busycell[i][j] = true;
    }
    public static void setBusy(int x, int y, int w, int h) {
        for (int j = y; j < y + h; j++) {
            for (int i = x; i < x + w; i++) {
                busycell[i][j] = true;
            }
        }
    }
    public static void setFree (int i, int j) {
        busycell[i][j] = false;
    }
    public static void setFree(int x, int y, int w, int h) {
        for (int j = y; j < y + h; j++) {
            for (int i = x; i < x + w; i++) {
                busycell[i][j] = false;
            }
        }
    }
    public static boolean isBusy(int i, int j){
        return busycell[i][j];
    }
    public static boolean isBusy(int x, int y, int w, int h) {
        boolean res = false;
        for (int j = y; j < y + h; j++) {
            for (int i = x; i < x + w; i++) {
                if (busycell[i][j]) res = true;
            }
        }
        return res;
    }
    public static int getCellRow (int cY) {
        int res;
        res = (cY * widgetAreaRows / widgetAreaHeight);
        if (res >= widgetAreaRows)
            res = widgetAreaRows -1;
        return res;
    }
    public static int getCellColumn (int cX) {
        int res;
        res = (cX * widgetAreaColumns / widgetAreaWidth );
        if (res >= widgetAreaColumns)
            res = widgetAreaColumns -1;
        return res;
    }
    public static int getNCells (int size) {
        int res;
        res = size / cellSize;
        if (size % cellSize > 0)  res++;
        return res;
    }
    public static final String wpFileName = "wallpaper";
    public static Drawable getWallpaper (){
        Drawable res, def;
        def = App.instance.getResources().getDrawable(R.drawable.noblesse);
        return def;
    }
    public static final String script = "/system/etc/alauncher.sh";
}
