package com.example.android.androidtraining.MediaApp;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class OpenGLActivity extends AppCompatActivity {

    private GLSurfaceView mGlSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGlSurfaceView = new MyGlSurfaceView(this);
        setContentView(mGlSurfaceView);
    }

    class MyGlSurfaceView extends GLSurfaceView {

        private final MyGlRenderer mRenderer;

        public MyGlSurfaceView(Context context) {
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            mRenderer = new MyGlRenderer();

            // Set the Renderer for drawing on the GlSurfaceView
            setRenderer(mRenderer);

            // Render the view only when there is a change in the drawing data
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

        private final float TOUCH_SCALE_FACTOR = 180.0f /320;
        private float mPreviousX;
        private float mPreviousY;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // MotionEvent reports input details from the touch screen
            // and other input controls. In this case, you are only
            // interested in events where the touch position changed.

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;

                    // reverse direction of rotation above the mid-line
                    if (y > getHeight() / 2) {
                        dx = dx * -1;
                    }

                    // reverse direction of rotation to left of the mid-line
                    if (x < getWidth() / 2) {
                        dy = dy * -1;
                    }

                    mRenderer.setAngle(mRenderer.getAngle() + ((dx + dy) * TOUCH_SCALE_FACTOR));
                    requestRender();
            }

            mPreviousX = x;
            mPreviousY = y;
            return true;
        }
    }

}
