package com.example.android.androidtraining.GettingStarted;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.TextView;

import com.example.android.androidtraining.R;

public class GesturesActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private static final String TAG ="Gestures";
    private GestureDetectorCompat mDetector;
    private TextView textView_speed_x;
    private TextView textView_speed_y;
    private TextView textView_numberOfTouches;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestures);
        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this, this);
        mDetector.setOnDoubleTapListener(this);
        textView_speed_x = (TextView) findViewById(R.id.textView_gesture_speed_x);
        textView_speed_y = (TextView) findViewById(R.id.textView_gesture_speed_y);
        textView_numberOfTouches = (TextView) findViewById(R.id.textView_numberOfTouches);
    }

    // This example shows an Activity, but you would use the same approach if
    // you were subclassing a View.

    private VelocityTracker mVelocityTracker = null;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        int action = MotionEventCompat.getActionMasked(event);
        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);

        textView_numberOfTouches.setText("Number of touches: " + event.getPointerCount());

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Log.i("Gesture test", "Action was DOWN");
                if (mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);
                return true;
            case (MotionEvent.ACTION_MOVE):
                mVelocityTracker.addMovement(event);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mVelocityTracker.computeCurrentVelocity(1000);
                // Log velocity of pixels per second
                // Best practice to use VelocityTrackerCompat where possible.
                textView_speed_x.setText("Speed (X): " + String.format("%6.4f",
                        VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId)));
                textView_speed_y.setText("Speed (Y): " + String.format("%6.4f",
                        VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId)));
                Log.i("Gesture test", "Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP):
                Log.i("Gesture test", "Action was UP");
                textView_numberOfTouches.setText("Number of touches: 0");
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Log.i("Gesture test", "Action was CANCEL");
                // Return a VelocityTracker object back to be re-used by others.
                mVelocityTracker.recycle();
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.i("Gesture test", "Action was OUTSIDE");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(TAG, "onDown: " + e.toString());

        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "onFling: " + e1.toString() + ", " + e2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "onLongPress: " + e.toString());
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(TAG, "onScroll: " + e1.toString() + ", " + e2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(TAG, "onShowPress: " + e.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp: " + e.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "onDoubleTap: " + e.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d(TAG, "onDoubleTapEvent: " + e.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d(TAG, "onSingleTapConfirmed: " + e.toString());
        return true;
    }
}
