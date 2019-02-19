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
import com.varvoux.aurelie.moodtracker.model.Constants;
import com.varvoux.aurelie.moodtracker.model.MoodHistory;
import com.varvoux.aurelie.moodtracker.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private ArrayList<MoodHistory> moodList = new ArrayList<>();
    long now = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        LinearLayout activityHistory = findViewById(R.id.activity_history_layout);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();

        /*Récupération history*/
        String moodListJson = pref.getString(Constants.MOOD_LIST_KEY, "");
        if (moodListJson != null && !moodListJson.equals("")) {
            Type listType = new TypeToken<ArrayList<MoodHistory>>() {
            }.getType();
            moodList = gson.fromJson(moodListJson, listType);
        }
// faire méthode boucle for
        for (int i = 0; i < 7; i++) {

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View moodBarLayout = inflater.inflate(R.layout.mood_bar, null, false);

            LinearLayout moodLayout = moodBarLayout.findViewById(R.id.moodBar_layout);
            LinearLayout contentView = moodBarLayout.findViewById(R.id.content_view);
            View invisibleView = moodBarLayout.findViewById(R.id.invisible_view);
            ImageButton commentBtn = moodBarLayout.findViewById(R.id.comment_img);

            //LinearLayout.LayoutParams parentParams = (LinearLayout.LayoutParams) moodLayout.getLayoutParams();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
            LinearLayout.LayoutParams paramsInvisibleView = (LinearLayout.LayoutParams) invisibleView.getLayoutParams();

            moodLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));

            if (moodList.size() > i) {
                final MoodHistory moodHistory = moodList.get(i);
                int moodHistoryPosition = moodHistory.getPosition();

                contentView.setBackgroundColor(getResources().getColor(Utils.moodColors[moodHistoryPosition]));// barre mood - color qu'on récupère de la classe Utils + cibler la méthode du tableau & moodlistposition en paramètres

                params.weight = 1;
                paramsInvisibleView.weight = Utils.invisibleViewWeight[moodHistoryPosition];
                contentView.setLayoutParams(params);
                invisibleView.setLayoutParams(paramsInvisibleView);

                TextView diffDaysTxt = moodBarLayout.findViewById(R.id.diffDays_txt);
                long moodDate = moodList.get(i).getDate();
                int diffDaysResult = Utils.diffDays(moodDate, now);
                String diffStr = diffDaysString(diffDaysResult);
                diffDaysTxt.setText(diffStr);


                if (moodHistory.getUserComment() == null) {
                    commentBtn.setVisibility(View.INVISIBLE);
                } else {
                    commentBtn.setOnClickListener(new View.OnClickListener() { //on touch listener fait mais non ?
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

    private String diffDaysString(int daysBetween) {
        switch (daysBetween) {
            case 1:
                return getString(R.string.diff_days_yesterday);
            case 2:
                return getString(R.string.diff_days_case2);
            case 3:
                return getString(R.string.diff_days_case3);
            case 4:
                return getString(R.string.diff_days_case4);
            case 5:
                return getString(R.string.diff_days_case5);
            case 6:
                return getString(R.string.diff_days_case6);
            case 7:
                return getString(R.string.diff_days_week);
            default:
                return getString(R.string.diff_days_default);
        }
    }
}

