package com.varvoux.aurelie.moodtracker.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.varvoux.aurelie.moodtracker.R;
import com.varvoux.aurelie.moodtracker.utils.Constants;
import com.varvoux.aurelie.moodtracker.model.MoodHistory;
import com.varvoux.aurelie.moodtracker.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private ArrayList<MoodHistory> moodList = new ArrayList<>(); //ArrayList contains all the moods
    // saved by user
    private long now = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();

        //We get the history by Shared Preferences
        String moodListJson = pref.getString(Constants.MOOD_LIST_KEY, "");
        if (moodListJson != null && !moodListJson.equals("")) {
            Type listType = new TypeToken<ArrayList<MoodHistory>>() {
            }.getType();
            moodList = gson.fromJson(moodListJson, listType);
        }
        displayHistory();
    }

    /**
     * This method displays history in HistoryActivity
     * It will create bars dynamically in terms of the size of the ArrayList MoodHistory,
     * will make a Toast message if a comment is saved in a mood
     */
    public void displayHistory() {
        LinearLayout activityHistory = findViewById(R.id.activity_history_layout);

        for (int i = 0; i < Constants.MAX_MOODS; i++) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View moodBarLayout = inflater.inflate(R.layout.mood_bar, null, false);

            LinearLayout moodLayout = moodBarLayout.findViewById(R.id.moodBar_layout); // Container of all mood's bars
            LinearLayout contentView = moodBarLayout.findViewById(R.id.content_view); // View of each mood's bar
            View invisibleView = moodBarLayout.findViewById(R.id.invisible_view); // This simple view will padding dynamically
            // all bars in terms of mood saved                                    // Sadder is the mood, more this view will
            //take up space
            ImageButton commentBtn = moodBarLayout.findViewById(R.id.comment_img); //This button appears if a comment is saved
            //by the user with his mood

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
            LinearLayout.LayoutParams paramsInvisibleView = (LinearLayout.LayoutParams) invisibleView.getLayoutParams();
            moodLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));

            if (moodList.size() > i) {
                final MoodHistory moodHistory = moodList.get(i); // We get the moods in each cycle of "i"
                int moodHistoryPosition = moodHistory.getPosition(); // We get each mood's position to display bars in terms
                // of this position

                //We display the correct color and size of each bars in terms of mood saved
                contentView.setBackgroundColor(getResources().getColor(Constants.moodColors[moodHistoryPosition]));

                params.weight = 1;
                paramsInvisibleView.weight = Constants.invisibleViewWeight[moodHistoryPosition];
                contentView.setLayoutParams(params);
                invisibleView.setLayoutParams(paramsInvisibleView);

                TextView diffDaysTxt = moodBarLayout.findViewById(R.id.diffDays_txt);
                long moodDate = moodList.get(i).getDate();
                int diffDaysResult = Utils.diffDays(moodDate, now);
                String diffStr = diffDaysString(diffDaysResult);
                diffDaysTxt.setText(diffStr);

                //We display or not the comment button according to the presence of a comment
                if (moodHistory.getUserComment() == null) {
                    commentBtn.setVisibility(View.INVISIBLE);
                } else {
                    commentBtn.setOnClickListener(new View.OnClickListener() {
                        //If a comment is present, a Toast message will remind it to the user
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(HistoryActivity.this, moodHistory.getUserComment(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else moodBarLayout.setVisibility(View.INVISIBLE);

            activityHistory.addView(moodBarLayout);
        }
    }

    /**
     * We update the TextView with the difference of days between mood's date and the current date
     */
    private String diffDaysString(int daysBetween) {
        String[] diffDaysStrings = getResources().getStringArray(R.array.diff_days_array);
        if (daysBetween <= 0 || daysBetween > Constants.MAX_MOODS)
            return getString(R.string.diff_days_default);
        else
            return diffDaysStrings[daysBetween - 1];
    }
}

