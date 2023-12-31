package ru.abch.alauncher2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by dag on 23/11/14.
 */
public class Utilities {

    private static String TAG = "Utilities";
    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    public static Matrix getResizedMatrix(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return matrix;
    }

    public static int dpToPx(int dp) {
        /// Converts 14 dip into its equivalent px
        Resources r = Objects.requireNonNull(App.get()).getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }

    public static int pxToDp(int px) {
        int px_per_dp = dpToPx(1);
        if (px_per_dp == 0) {
            return 0;
        }
        return px / px_per_dp;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static void showToast(Context cxt, String text) {
        try {
            if (toast != null) {
                toast.cancel();
                toast = null;
            }
            toast = Toast.makeText(cxt, text, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    static Toast toast;
    public static void showToast(Context cxt, int resource) {
        showToast(cxt, cxt.getResources().getString(resource));
    }

    /**
     * Utility method to determine whether the given point, in local coordinates,
     * is inside the view, where the area of the view is expanded by the slop factor.
     * This method is called while processing touch-move events to determine if the event
     * is still within the view.
     */
    public static boolean pointInView(View v, float localX, float localY, float slop) {
        return localX >= -slop && localY >= -slop && localX < (v.getWidth() + slop) &&
                localY < (v.getHeight() + slop);
    }

    public static Drawable viewToDrawable(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return new BitmapDrawable(view.getDrawingCache());
    }

    static public String getprop(String key){
        try { Class c = Class.forName("android.os.SystemProperties");
            try { Method method = c.getDeclaredMethod("get", String.class);
                try {
                    String ret = (String) method.invoke(null, key);
                    if (ret == null) ret = "";
                    return ret;
                }  catch (IllegalAccessException e) {e.printStackTrace();}
                catch (InvocationTargetException e) {e.printStackTrace();}
            } catch (NoSuchMethodException e) {e.printStackTrace();}
        } catch (ClassNotFoundException e) {e.printStackTrace();}
        return "";
    }
}
