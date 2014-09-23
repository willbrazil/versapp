package com.versapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.versapp.confessions.ConfessionImageCache;

/**
 * Created by william on 22/09/14.
 */
public class DownloadImageAT extends AsyncTask<Void, Void, Bitmap> {

    String key;
    ImageView imageView;
    Context context;
    ConfessionImageCache cache;
    ProgressBar progressBar;

    public DownloadImageAT(Context context, String key, ImageView imageView, ConfessionImageCache cache, ProgressBar progressBar) {
        this.context = context;
        this.key = key;
        this.imageView = imageView;
        this.cache = cache;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        Bitmap image = GCSManager.getInstance(context).downloadImage(key, imageView.getWidth(), imageView.getHeight());

        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (!isCancelled()) {

            if (imageView != null) {

                progressBar.setVisibility(View.GONE);

                if (bitmap != null) {

                    ConfessionImageCache.setBitmapOnView(context, imageView, bitmap);
                    cache.addToCache(key, bitmap);
                } else {
                    imageView.setImageBitmap(null);
                    // set normal image.
                }
            }

        }

    }




}
