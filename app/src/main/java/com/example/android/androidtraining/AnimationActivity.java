package com.example.android.androidtraining;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.PathMotion;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AnimationActivity extends AppCompatActivity {

    private Scene mAscene;
    private Scene mAnotherScene;
    private ViewGroup mSceneRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
    }

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

        TransitionManager.go(mAnotherScene, transitionSet);

    }

    private TextView mLabelText;
    private Fade mFade;
    private ViewGroup mRootView;
    private int textViewCount = 1;

    public void addTextView(View view) {
        // Create a new TextView and set some View properties
        mLabelText = new TextView(this);
        mLabelText.setText("TextView" + textViewCount++);

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

    // Remove me animation
    ChangeBounds changeBounds;
    public void removeMe(View view) {
        mRootView = (ViewGroup) findViewById(R.id.root_layout);

        changeBounds = new ChangeBounds();

        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mRootView.removeView(findViewById(R.id.button_remove_me));

    }

}
