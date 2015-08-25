package com.example.android.androidtraining.MediaApp;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.androidtraining.R;

import java.lang.ref.WeakReference;

public class BitmapActivity extends AppCompatActivity {
    private long startTime;
    private long endTime;
    private int radioHeight = 100;
    private int radioWidth = 100;
    private Bitmap mPlaceHolderBitmap;
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final long DISK_CACHE_SIZE = 1024 * 1014 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);

        /** Initialize memory cache */
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache
        final int cacheSize = maxMemory / 8;

        /** Initialize disk cache */

        /** Retain fragment in order to save the reference to the cached files if device is being rotated  */
        RetainFragment retainFragment = RetainFragment.findOrCreateRetaingFragment(getFragmentManager());
        mMemoryCache = retainFragment.mRetainedCache;
        if (mMemoryCache == null) {
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return value.getByteCount() / 1024;
                }
            };
            retainFragment.mRetainedCache = mMemoryCache;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bitmap, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Bitmap getResizedBitmap(Bitmap bitmap, int newHeight, int newWidth) {
        // Get the Bitmap dimensions
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        // Create Matrix for the manipulation
        Matrix matrix = new Matrix();
        // Resize the bitmap
        matrix.postScale(newWidth, newHeight);
        // "Recreate" the new bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, newWidth, newHeight, matrix, false);
        return resizedBitmap;
    }

    public void loadRaw(View view) {
        startTime = System.currentTimeMillis();
        BitmapFactory.Options options = new BitmapFactory.Options();
        // Resize the image to the maximum allowed height and width (4096x4096)
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gtav);
        ImageView imageView = (ImageView) findViewById(R.id.imageView_loadBitmap);
        imageView.setImageBitmap(bitmap);
        endTime = System.currentTimeMillis();
        TextView textView = (TextView) findViewById(R.id.textView_bitmap_load_speed);
        textView.setText(endTime - startTime + " ms");
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Log.i("Bitmap Dimensions: ", "Height: " + bitmap.getHeight() + ", " + "Width: " + bitmap.getWidth());
    }

    // Read bitmap dimensions and type
    public BitmapFactory.Options getDimensions(Bitmap bitmap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.gtav, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;
        return options;
    }

    // Load scaled down version into memory
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of the image
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

    public static Bitmap decodeSampledBitmapFromResource(Resources resources, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    public void loadEfficiently(View view) {
        ImageView imageView = (ImageView) findViewById(R.id.imageView_loadBitmap);
        startTime = System.currentTimeMillis();
        imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.gtav, radioWidth, radioHeight));
        endTime = System.currentTimeMillis();
        TextView textView = (TextView) findViewById(R.id.textView_bitmap_load_speed);
        textView.setText(endTime - startTime + " ms");
    }

    // Change the resolution of the image
    public void onRadioButtonClicked(View view) {
        // Is the button now checked ?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_100_100:
                radioHeight = 100;
                radioWidth = 100;
                break;
            case R.id.radio_400_400:
                radioHeight = 400;
                radioWidth = 400;
                break;
            case R.id.radio_800_800:
                radioHeight = 800;
                radioWidth = 800;
                break;
            case R.id.radio_1600_1600:
                radioHeight = 1600;
                radioWidth = 1600;
                break;
        }

    }

    // Use an AsyncTask
    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewWeakReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            final Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), data, radioWidth, radioHeight);
            addBitmapToMemoryCache(String.valueOf(data), bitmap);
            return bitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewWeakReference != null && bitmap != null) {
                final ImageView imageView = imageViewWeakReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            } else if (imageViewWeakReference != null && bitmap != null) {
                final ImageView imageView = imageViewWeakReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public void loadAsynchronously(View view) {
        startTime = System.currentTimeMillis();
        BitmapWorkerTask task = new BitmapWorkerTask((ImageView) findViewById(R.id.imageView_loadBitmap));
        task.execute(R.drawable.gtav);
        endTime = System.currentTimeMillis();
        TextView textView = (TextView) findViewById(R.id.textView_bitmap_load_speed);
        textView.setText(endTime - startTime + " ms");
    }

    // TODO: Learn this thoroughly !
    // Concurrency
    // Create a dedicated Drawable subclass to store a reference back to the worker class
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskWeakReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskWeakReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskWeakReference.get();
        }
    }

    // Before executing the BitmapWorkerTask, you create an AsyncDrawable and bind it to the target ImageView
    public void loadConcurrency(View view) {
        startTime = System.currentTimeMillis();
        ImageView imageView = (ImageView) findViewById(R.id.imageView_loadBitmap);
        if (cancelPotentialWork(R.drawable.gtav, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(R.drawable.gtav);
        }
        endTime = System.currentTimeMillis();
        TextView textView = (TextView) findViewById(R.id.textView_bitmap_load_speed);
        textView.setText(endTime - startTime + " ms");
        Toast.makeText(BitmapActivity.this, "Learn this thoroughly !", Toast.LENGTH_SHORT).show();
    }

    // The cancelPotentialWork method checks if another running task is already associated with the ImageView
    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    // A helper method, getBitmapWorkerTask(), is used to retrieve the task associated with a particular ImageView
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    /**
     * Cache bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void cacheLoad(View view) {
        ImageView imageView = (ImageView) findViewById(R.id.imageView_loadBitmap);
        TextView textView = (TextView) findViewById(R.id.textView_bitmap_load_speed);

        startTime = System.currentTimeMillis();
        final String imageKey = String.valueOf(R.drawable.gtav);

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            Toast.makeText(BitmapActivity.this, "Loaded from cache", Toast.LENGTH_SHORT).show();
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.gtav);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(R.drawable.gtav);
            Toast.makeText(BitmapActivity.this, "Loaded from resource", Toast.LENGTH_SHORT).show();
        }
        endTime = System.currentTimeMillis();
        textView.setText(endTime - startTime + " ms");
    }

    // TODO: Complete from http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
    // Snapshot understanding is the issue at the moment
    // Disk Cache AsyncTask class and methods
    /*class InitDiskCacheTask extends AsyncTask<File, Void, Void> {

        @Override
        protected Void doInBackground(File... params) {

            try {
                synchronized (mDiskCacheLock) {
                    File cacheDir = params[0];
                    mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
                    mDiskCacheStarting = false; // Finished initialization
                    mDiskLruCache.notifyAll(); // Wake any waiting threads
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class BitmapWorkerTaskDisk extends AsyncTask<Integer, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Integer... params) {
            final String imageKey = String.valueOf(params[0]);

            // Check disk cache in background thread
            Bitmap bitmap =
        }
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            if (mDiskLruCache != null) {
                try {


                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }*/
}
