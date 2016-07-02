package com.nicolascarrasco.www.imagemirror.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.nicolascarrasco.www.imagemirror.R;
import com.nicolascarrasco.www.imagemirror.services.Constants.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class GetImageIntentService extends IntentService {

    private static final String TAG = GetImageIntentService.class.getSimpleName();

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GET_IMAGE = "com.nicolascarrasco.www.imagemirror.action.GET_IMAGE";

    private static final String EXTRA_URL = "com.nicolascarrasco.www.imagemirror.extra.URL";

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
        URL url;
        HttpURLConnection connection = null;
        InputStream input = null;

        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            input = connection.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
        }

        // Pretty much everything here is a clone from:
        // https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);

        if(options.outMimeType == null || !options.outMimeType.startsWith("image/")){
            // Not an image
            sendLocalBroadcast(Constants.FAILURE, getString(R.string.status_message_no_image));
            return;
        }

        if (connection != null){
            connection.disconnect();
        }
    }

    private void sendLocalBroadcast(String status, String message){
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                .putExtra(Constants.EXTENDED_DATA_STATUS, status)
                .putExtra(Constants.EXTENDED_DATA_MESSAGE, message);

        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
