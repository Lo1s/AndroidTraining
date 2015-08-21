package com.example.android.androidtraining;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AnimationActivity extends AppCompatActivity {

    private Scene mAscene;
    private Scene mAnotherScene;
    private ViewGroup mSceneRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        mContentView = findViewById(R.id.content);
        mLoadingView = findViewById(R.id.loading_content);

        // Initially hide the content view.
        mContentView.setVisibility(View.GONE);
    }

    boolean upDown = true;

    public void animateScene(View view) {
        // Create the scene root for the scenes in this app
        mSceneRoot = (ViewGroup) findViewById(R.id.scene_root);

        // Create the scenes
        mAscene = Scene.getSceneForLayout(mSceneRoot, R.layout.a_scene, this);
        mAnotherScene = Scene.getSceneForLayout(mSceneRoot, R.layout.another_scene, this);

        // Inflate the transition from the resource file
        //Transition mFadeTransition = TransitionInflater.from(this)
        //.inflateTransition(R.transition.fade_transition);

        // Create transition dynamically in the code
        //Transition mFadeTransition = new Fade();

        // Inflate transition set
        Transition transitionSet = TransitionInflater.from(this)
                .inflateTransition(R.transition.auto_transition);

        if (upDown) {
            TransitionManager.go(mAnotherScene, transitionSet);
            upDown = false;
        } else {
            TransitionManager.go(mAscene, transitionSet);
            upDown = true;
        }

    }

    private TextView mLabelText;
    private Fade mFade;
    private ViewGroup mRootView;

    public void addTextView(View view) {
        if (mLabelText == null) {
            // Create a new TextView and set some View properties
            mLabelText = new TextView(this);
            mLabelText.setText("TextView");

            // Get the root view and create a transition
            mRootView = (ViewGroup) findViewById(R.id.scene_root2);
            mFade = new Fade(Fade.IN);

            // Start recording changes to the view hierarchy
            TransitionManager.beginDelayedTransition(mRootView, mFade);

            // Add the new TextView to the view hierarchy
            mRootView.addView(mLabelText);

            // When the system redraws the screen to show this update,
            // the framework will animate the addition as a fade in
        }
    }

    // Move left-to-right/right-to-left animation
    ChangeBounds changeBounds;
    boolean moveRight = true;
    Scene leftMoveScene;
    Scene rightMoveScene;

    public void moveMe(View view) {
        mSceneRoot = (ViewGroup) findViewById(R.id.frameLayout_button_move);
        Transition transition = new ChangeBounds();

        if (moveRight) {
            rightMoveScene = Scene.getSceneForLayout(mSceneRoot, R.layout.button_move_right, this);
            TransitionManager.go(rightMoveScene, transition);
            moveRight = false;
        } else {
            leftMoveScene = Scene.getSceneForLayout(mSceneRoot, R.layout.button_move_left, this);
            TransitionManager.go(leftMoveScene, transition);
            moveRight = true;
        }

    }

    // Test Cross Fade animation
    private View mContentView;
    private View mLoadingView;
    private int mShortAnimationDuration;
    public void startCrossFade(View view) {

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        crossFade();
    }

    // Cross fade animation
    private void crossFade() {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mContentView.setAlpha(0f);
        mContentView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mContentView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mLoadingView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setVisibility(View.GONE);
                    }

                });
    }

    // ViewPager animation
    public void startViewPagerAnimation(View view) {
        startActivity(new Intent(this, ViewPagerAnimationActivity.class));
    }

    // Start Flip Card activity
    public void startFlipCardActivity(View view) {
        startActivity(new Intent(this, CardFlipActivity.class));
    }

    // Start the Zoom Animation Activity
    public void startZoomAnimationActivity(View view) {
        startActivity(new Intent(this, ZoomAnimationActivity.class));
    }

    // Start the Add Items Activity
    public void addItems(View view) {
        startActivity(new Intent(this, AddItemsActivity.class));
    }

}
