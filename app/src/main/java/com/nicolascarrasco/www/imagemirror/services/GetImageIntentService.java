package com.nicolascarrasco.www.imagemirror.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.nicolascarrasco.www.imagemirror.R;
import com.nicolascarrasco.www.imagemirror.constants.Constants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class GetImageIntentService extends IntentService {

    private static final String TAG = GetImageIntentService.class.getSimpleName();

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GET_IMAGE =
            "com.nicolascarrasco.www.imagemirror.action.GET_IMAGE";

    private static final String EXTRA_URL = "com.nicolascarrasco.www.imagemirror.extra.URL";

    private InputStream mInputStream;
    private HttpURLConnection mConnection;

    public GetImageIntentService() {
        super("GetImageIntentService");
    }

    public static void getImage(Context context, String url) {
        Intent intent = new Intent(context, GetImageIntentService.class);
        intent.setAction(ACTION_GET_IMAGE);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_IMAGE.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_URL);
                handleActionGetImage(url);
            }
        }
    }

    private void handleActionGetImage(String urlString) {

        startConnection(urlString);

        // Pretty much everything here is a clone from:
        // https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(mInputStream, null, options);

        if (options.outMimeType == null || !options.outMimeType.startsWith("image/")) {
            // Not an image
            sendLocalBroadcast(Constants.FAILURE, getString(R.string.status_message_no_image));
            return;
        }

        options.inSampleSize = calculateInSampleSize(options,
                getResources().getDimensionPixelSize(R.dimen.image_width),
                getResources().getDimensionPixelSize(R.dimen.image_height));

        closeConnection();

        startConnection(urlString);

        options.inJustDecodeBounds = false;
        Bitmap downloadedImage = BitmapFactory.decodeStream(mInputStream, null, options);

        saveToInternalStorage(downloadedImage);
        sendLocalBroadcast(Constants.SUCCESS, null);

        closeConnection();
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void sendLocalBroadcast(String status, String message) {
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                .putExtra(Constants.EXTENDED_DATA_STATUS, status)
                .putExtra(Constants.EXTENDED_DATA_MESSAGE, message);

        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void saveToInternalStorage(Bitmap bitmapImage) {
        FileOutputStream outputStream;
        try {
            outputStream = this.openFileOutput(Constants.STORED_IMAGE_FILENAME,
                    Context.MODE_PRIVATE);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void startConnection(String urlString) {
        try {
            URL url = new URL(urlString);
            mConnection = (HttpURLConnection) url.openConnection();
            mConnection.connect();

            mInputStream = mConnection.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void closeConnection() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
        }

        if (mConnection != null) {
            mConnection.disconnect();
        }
    }
}
