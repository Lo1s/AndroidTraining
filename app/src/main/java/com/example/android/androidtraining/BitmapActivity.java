package com.example.android.androidtraining;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class BitmapActivity extends AppCompatActivity {
    private long startTime;
    private long endTime;
    private int radioHeight = 100;
    private int radioWeight = 100;
    private Bitmap mPlaceHolderBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
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
        imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.gtav, radioWeight, radioHeight));
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
                radioWeight = 100;
                break;
            case R.id.radio_400_400:
                radioHeight = 400;
                radioWeight = 400;
                break;
            case R.id.radio_800_800:
                radioHeight = 800;
                radioWeight = 800;
                break;
            case R.id.radio_1600_1600:
                radioHeight = 1600;
                radioWeight = 1600;
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
            return decodeSampledBitmapFromResource(getResources(), data, radioWeight, radioHeight);
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
            if (drawable instanceof  AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }
}
