package com.example.android.androidtraining;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BitmapActivity extends AppCompatActivity {
    private long startTime;
    private long endTime;

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
}
