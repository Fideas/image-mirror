package com.nicolascarrasco.www.imagemirror.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 * helper methods.
 */
public class GetImageIntentService extends IntentService {

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

    private void handleActionGetImage(String url) {
        // TODO: Handle action Get Image
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
