package com.indcgroup.loadimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.indcgroup.utility.Constants;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by thefa on 28/08/2017.
 */

public class ImageCommunication extends AsyncTask<String, Void, Bitmap> {

    ImageView imageView;
    ImageButton imageButton;

    public ImageCommunication(ImageView img) {
        this.imageView = img;
    }

    public ImageCommunication(ImageButton img) {
        this.imageButton = img;
    }

    Bitmap getImageScale(byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        ByteArrayInputStream stream = new ByteArrayInputStream(data);

        //Get size of original bitmap
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        int width = options.outWidth;
        int height = options.outHeight;
        int multiple = (width * height) / 500000 <= 1 ? 1 : (width * height) / 500000;

        //Create scale bitmap
        options.inJustDecodeBounds = false;
        options.inSampleSize = multiple;
        stream = new ByteArrayInputStream(data);
        return BitmapFactory.decodeStream(stream, null, options);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
        if (imageButton != null) {
            imageButton.setImageBitmap(bitmap);
        }
    }

    @Override
    protected Bitmap doInBackground(String... param) {
        Bitmap bitmap = null;
        //Log.e(Constants.TAG, param[0]);

        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(param[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Connection", "close");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = urlConnection.getInputStream();
            IOUtils.copy(inputStream, outputStream);
            byte[] bytes = outputStream.toByteArray();

            bitmap = getImageScale(bytes);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(Constants.TAG, ex.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return bitmap;

    }
}
