package com.versapp.confessions;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.versapp.Logger;
import com.versapp.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by william on 20/09/14.
 */
public class ConfessionsFragment extends Fragment {

    private ArrayList<Confession> confessions;
    private ListView confessionsListView;
    private ConfessionListArrayAdapter adapter;
    private ImageButton favoriteBtn;
    private ImageButton startMessageBtn;
    private ImageView composeConfessionBtn;
    private TextView removeConfessionLabel;

    private ConfessionTutorial tutorial;

    private int selectedConfessionPosition = -1;
    private int previousConfessionPosition = -1;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.fragment_confessions, container, false);

        confessions = new ArrayList<Confession>();

        confessionsListView = (ListView) convertView.findViewById(R.id.fragment_big_confessions_list_view);
        adapter = new ConfessionListArrayAdapter(getActivity(), confessions);
        confessionsListView.setAdapter(adapter);


        // Action btns
        favoriteBtn = (ImageButton) convertView.findViewById(R.id.big_confession_favorite_btn);
        startMessageBtn = (ImageButton) convertView.findViewById(R.id.big_confession_msg_btn);
        composeConfessionBtn = (ImageView) convertView.findViewById(R.id.big_confession_compose_new_confession_btn);
        removeConfessionLabel = (TextView) convertView.findViewById(R.id.big_confession_remove_confession_btn);

        // Due to differences among devices, we need to programatically adjust
        // size of a few elements.
        adjustLayoutElementsSize(convertView);

        // Tutorial
        if (!ConfessionTutorial.isCompleted(getActivity())) {
            tutorial = new ConfessionTutorial(getActivity(), convertView);
            tutorial.start();
        }

        confessionsListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == SCROLL_STATE_IDLE) {

                    // smoothScroll does not always work. This line ensures //
                    // confessions are aligned with the top of the screen
                    confessionsListView.setSelection(selectedConfessionPosition);

                    updateLayout();

                    if (tutorial != null) {
                        tutorial.complete();
                        tutorial = null;
                    }

                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub

            }

        });

        favoriteBtn.setOnTouchListener(new CustomTouchableElementListener(getActivity(), new Runnable() {

            @Override
            public void run() {

                if (!confessions.get(selectedConfessionPosition).isFavorited()) {
                    favoriteBtn.setImageResource(R.drawable.big_confession_heart_filled);
                } else {
                    favoriteBtn.setImageResource(R.drawable.big_confession_heart_outline);
                }

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        confessions.get(selectedConfessionPosition).toggleFavorite();
                        System.out.println(confessions.get(selectedConfessionPosition).isFavorited());
                        return null;
                    }
                }.execute();

                adapter.notifyDataSetChanged();

            }
        }));


        new AsyncTask<Void, Void, Confession[]>() {
            @Override
            protected Confession[] doInBackground(Void... params) {

                Log.d(Logger.CONFESSIONS_DEBUG, "About to get confessions");
                Confession[] confessions = ConfessionManager.getInstance().downloadConfessions(getActivity());
                Log.d(Logger.CONFESSIONS_DEBUG, "Size: " + confessions.length);

                return confessions;

            }

            @Override
            protected void onPostExecute(Confession[] result) {

                if (result.length > 0) {
                    confessions.addAll(Arrays.asList(result));
                    selectedConfessionPosition = 0;
                    adapter.notifyDataSetChanged();
                }

                super.onPostExecute(result);
            }
        }.execute();


        confessionsListView.setOnTouchListener(new ConfessionListOnTouchListener());

        return convertView;
    }


    private void updateLayout() {

        removeConfessionLabel.setVisibility(View.GONE); // Always invisible
        // unless specified down
        // below
        startMessageBtn.setImageResource(R.drawable.big_confession_start_conversation_btn);
        startMessageBtn.setBackgroundColor(getResources().getColor(R.color.confessionBlue));

        // Always update favorite and message icon
        if (confessions.get(selectedConfessionPosition).isMine()) {

            removeConfessionLabel.setVisibility(View.VISIBLE);
            startMessageBtn.setImageResource(R.color.transparent);

        } else if ( true ) { //isGlobal
            startMessageBtn.setImageResource(R.drawable.big_confession_global_icon);
        } else { // friend of friend

        }

        if (confessions.get(selectedConfessionPosition).isFavorited()) {
            favoriteBtn.setImageResource(R.drawable.big_confession_heart_filled);
        } else {
            favoriteBtn.setImageResource(R.drawable.big_confession_heart_outline);
        }

        // If next confession has image, start loading now.
        if (selectedConfessionPosition < confessions.size() - 2) {

            Confession nextConfession = confessions.get(selectedConfessionPosition + 2);

            if (!nextConfession.getImageUrl().startsWith("#")) {

               // imageCache.cacheImage(nextConfession.getImageUrl(), confessionsListView.getWidth(), confessionsListView.getWidth());

            }

        }

    }


    private void adjustLayoutElementsSize(View convertView) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();

        int width = 0;
        int height = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;

        } else {
            width = display.getWidth();
            height = display.getHeight();
        }

        RelativeLayout.LayoutParams paramsList = (RelativeLayout.LayoutParams) confessionsListView.getLayoutParams();
        paramsList.height = height - (80 + width / 2);
        confessionsListView.setLayoutParams(paramsList);

        LinearLayout dashboard = (LinearLayout) convertView.findViewById(R.id.big_confession_dashboard);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dashboard.getLayoutParams();
        params.width = width;
        params.height = width / 2;
        dashboard.setLayoutParams(params);

    }



    private class CustomTouchableElementListener implements View.OnTouchListener {

        private GestureDetector gestureDetector;

        public CustomTouchableElementListener(Context context, Runnable runnable) {
            gestureDetector = new GestureDetector(context, new CustomGestureDetector(runnable));
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            gestureDetector.onTouchEvent(event);

            return true;
        }

    }

    private class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private Runnable onClickRunnable;

        public CustomGestureDetector(Runnable runnable) {
            this.onClickRunnable = runnable;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // The fling must have a minimum length
            if (Math.abs((e2.getY() - e1.getY())) > 100) {

                if (velocityY < 400) {

                    if (hasNextConfession()) {
                        moveToNextConfession();
                    }

                }

                if (velocityY > -400) {

                    if (hasPreviousConfession()) {
                        moveToPreviousConfession();
                    }

                }

            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            onClickRunnable.run();
            return super.onSingleTapUp(e);
        }

    }

    private class ConfessionListOnTouchListener implements View.OnTouchListener {

        private float initialY;

        GestureDetector flingRecongnizer = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return true;
            };
        });

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                initialY = event.getY();

                return true;

            } else if (event.getAction() == MotionEvent.ACTION_UP) {

                if (initialY > event.getY()) {

                    if (hasNextConfession()) {
                        moveToNextConfession();
                    }

                } else if (initialY < event.getY()) {

                    if (hasPreviousConfession()) {
                        moveToPreviousConfession();
                    }

                } else {
                    // Did not move enough
                }

                return true;
            }

            // Check if it's fling.
            if (flingRecongnizer.onTouchEvent(event)) {

                if (initialY > event.getY()) {

                    if (hasNextConfession()) {
                        moveToNextConfession();
                    }

                } else if (initialY < event.getY()) {

                    if (hasPreviousConfession()) {
                        moveToPreviousConfession();
                    }

                } else {
                    // Did not move enough
                }

                return true;

            }

            return false;
        }
    }

    private boolean supportSmoothScrollToPositionFromTop() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB);
    }

    private void moveToPosition(int position) {
        if (supportSmoothScrollToPositionFromTop()) {
            confessionsListView.smoothScrollToPositionFromTop(selectedConfessionPosition, 0);
        } else {
            confessionsListView.setSelection(selectedConfessionPosition);
        }
    }

    private void moveToNextConfession() {

        previousConfessionPosition = selectedConfessionPosition;
        selectedConfessionPosition++;

        moveToPosition(selectedConfessionPosition);

    }

    private void moveToPreviousConfession() {
        previousConfessionPosition = selectedConfessionPosition;
        selectedConfessionPosition--;

        moveToPosition(selectedConfessionPosition);

    }

    private boolean hasNextConfession() {

        if (confessions.size() == 0) {
            return false;
        } else if (selectedConfessionPosition < confessions.size() - 1) {
            return true;
        }

        return false;

    }

    private boolean hasPreviousConfession() {

        if (confessions.size() == 0 || selectedConfessionPosition <= 0) {
            return false;
        }

        return true;

    }
}