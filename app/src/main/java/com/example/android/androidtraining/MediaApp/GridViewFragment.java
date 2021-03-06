package com.example.android.androidtraining.MediaApp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.androidtraining.R;

public class GridViewFragment extends Fragment implements AdapterView.OnItemClickListener {

    // TODO: Load images via AsyncTask for fluid performance
    // http://developer.android.com/training/displaying-bitmaps/display-bitmap.html

    private ImageAdapter mAdapter;
    /*
    * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
    * performs a swipe-to-refresh gesture.
    */
    private SwipeRefreshLayout refreshLayout;

    // A static dataset to back the ViewPager adapter
    public final static Integer[] imageResIds = new Integer[] {
            R.drawable.gtav, R.drawable.rafa,R.drawable.android_hd, R.drawable.androidparty,
            R.drawable.android_widget_preview, R.drawable.steve_jobs_close_up,
            R.drawable.prairiedogs};
    /*public final static Integer[] imageResIds = new Integer[] {
            R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4, R.drawable.img_5,
            R.drawable.img_6, R.drawable.img_7, R.drawable.img_8, R.drawable.img_9, R.drawable.img_10,
            R.drawable.img_11, R.drawable.img_12, R.drawable.img_13, R.drawable.img_14, R.drawable.img_15,
            R.drawable.img_16, R.drawable.img_16, R.drawable.img_17, R.drawable.img_18, R.drawable.img_19,
            R.drawable.img_20, R.drawable.img_21, R.drawable.img_22, R.drawable.img_23, R.drawable.img_24
    };*/

    // Empty constructor as per Fragment docs
    public GridViewFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ImageAdapter(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.image_grid_fragment, container, false);
        final GridView mGridView = (GridView) v.findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);

        final SwipeRefreshLayout refreshLayout =
                (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity().getApplicationContext(), "Refreshed !",
                        Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });


        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Intent intent = new Intent(getActivity(), ViewPagerActivity.class);
        intent.putExtra(ViewPagerActivity.EXTRA_IMAGE, position);
        startActivity(intent);
    }

    private class ImageAdapter extends BaseAdapter {
        private final Context mContext;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
        }

        @Override
        public int getCount() {
            return imageResIds.length;
        }

        @Override
        public Object getItem(int position) {
            return imageResIds[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new GridView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(imageResIds[position]); // load image into imageview
            return imageView;
        }
    }




}
