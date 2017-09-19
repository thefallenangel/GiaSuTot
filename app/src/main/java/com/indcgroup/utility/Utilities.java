package com.indcgroup.utility;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.indcgroup.dialog.SuperDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;


public class Utilities {

    public String base64Encode(String plainText) {
        try {
            byte[] data = plainText.getBytes("utf-8");
            String result = Base64.encodeToString(data, Base64.DEFAULT);
            return result.replace("\n", "").replace("\r", "").replace("+", "%2B").replace("/", "%2F");
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public String base64Encode2(byte[] arr) {
        try {
            String result = Base64.encodeToString(arr, Base64.DEFAULT);
            return result.replace("\n", "").replace("\r", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public String base64Decode(String base64EncodedData) {
        try {
            byte[] data = Base64.decode(base64EncodedData, Base64.DEFAULT);
            String result = new String(data, "utf-8");
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public byte[] base64Decode2(String text) {
        return Base64.decode(text, Base64.DEFAULT);
    }

    public void showSuperDialog(SuperDialog dialog, FragmentManager fm, boolean redirect, int type, String message) {
        Bundle b = new Bundle();
        b.putBoolean("Redirect", redirect);
        b.putInt("Type", type);
        b.putString("Message", message);

        dialog.setArguments(b);
        dialog.show(fm, "");
    }

    public boolean checkInternetConnection(ConnectivityManager conmng) {
        if (conmng.getActiveNetworkInfo() == null || !conmng.getActiveNetworkInfo().isConnected())
            return false;
        else
            return true;
    }


    public boolean validationEditText(EditText... list) {
        for (EditText item : list) {
            if (item.getText().length() == 0)
                return false;
        }
        return true;
    }

    public boolean validationMultiAutoCompleteTextView(MultiAutoCompleteTextView... list) {
        for (MultiAutoCompleteTextView item : list) {
            if (item.getText().length() == 0)
                return false;
        }
        return true;
    }

    public String convertBreakline(String source, int flag) {
        if (flag == 1) {
            source = source.replace("\n", "-br-");
            return source;
        } else {
            source = source.replace("-br-", "\n");
            return source;
        }
    }

    public boolean checkImageSize(Uri path, Context context) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), path);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();

            //Lower than 4 MB is acceptable
            return imageInByte.length <= 4200000;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private Bitmap rotateImage(Bitmap bm, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap result = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        bm.recycle();
        return result;
    }

    public Bitmap handleImageCaptured(int orientation, Context context, Uri selectedImage) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream stream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(stream, null, options);
        stream.close();

        //Calculate sample size
        int sampleSize = 1;
        int width = options.outWidth;
        int height = options.outHeight;
        if (width > 1024 || height > 1024) {
            int widthRatio = Math.round((float) width / (float) 1024);
            int heightRatio = Math.round((float) height / (float) 1024);
            sampleSize = width < heightRatio ? widthRatio : heightRatio;

            float totalPixels = width * height;
            while (totalPixels / (sampleSize * sampleSize) > 2097152) {
                sampleSize++;
            }
        }


        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        stream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap result = BitmapFactory.decodeStream(stream, null, options);
        if (orientation != 0)
            result = rotateImage(result, orientation);

        return result;
    }

    public Intent createRateUsIntent(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, context.getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    public boolean checkPermission(Context context, String permission) {
        if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    public boolean isLocationEnable(Context context) {
        int locationMode;
        String locationProvider;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProvider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProvider);
        }
    }

    public AdRequest createAdRequest() {
        //Delete test device when release
        return new AdRequest.Builder()
                //.addTestDevice("5DAE324D3D29A5FFB428BCEA6EA22D60")
                //.addTestDevice("D088A3B8D50FDD9B62EDCDE126160F29")
                .build();
    }

    public InterstitialAd createAdPage(Context context, String adUnitId) {
        InterstitialAd adPage = new InterstitialAd(context);
        adPage.setAdUnitId(adUnitId);
        adPage.loadAd(createAdRequest());
        return adPage;
    }

    public String createCurrentDateTime() {
        String result = "";
        Time now = new Time();
        now.setToNow();

        String day = String.valueOf(now.monthDay);
        String month = String.valueOf(now.month + 1);
        month = month.length() < 2 ? "0" + month : month;
        String year = String.valueOf(now.year);
        String hour = String.valueOf(now.hour);
        String minute = String.valueOf(now.minute);

        result += day + "-" + month + "-" + year + " " + hour + ":" + minute;

        return result;
    }
}
