package com.varvoux.aurelie.moodtracker.controller;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.varvoux.aurelie.moodtracker.R;
import com.varvoux.aurelie.moodtracker.Utils.Utils;
import com.varvoux.aurelie.moodtracker.model.MoodHistory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private ImageView smileyImg;
    private RelativeLayout mainLayout;
    private GestureDetector mGestureDetector;
    private SharedPreferences mPreferences;
    private MoodHistory mCurrentMood;
    private Gson mGson = new Gson();
    private ArrayList<MoodHistory> moodList = new ArrayList<>();


    int currentSmileyPosition = 3;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentMood = new MoodHistory();

        final ImageButton noteBtn = findViewById(R.id.note_img);
        smileyImg = findViewById(R.id.smiley_img);
        ImageButton historyBtn = findViewById(R.id.history_img);
        mainLayout = findViewById(R.id.main_layout);

        mGestureDetector = new GestureDetector(this, new GestureListener());
        mainLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });

        noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoteDialog();
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HistoryActivity = new Intent(MainActivity.this, com.varvoux.aurelie.moodtracker.controller.HistoryActivity.class);
                startActivity(HistoryActivity);
            }
        });

    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 20;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffY) > SWIPE_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeTop() {
        if (--currentSmileyPosition < 0) currentSmileyPosition = Utils.getMoodsUI().length - 1;
        displayMood();
        mCurrentMood.setPosition(currentSmileyPosition);
    }

    public void onSwipeBottom() {
        if (++currentSmileyPosition > Utils.getMoodsUI().length - 1) currentSmileyPosition = 0;
        displayMood();
        mCurrentMood.setPosition(currentSmileyPosition);

    }

    private void displayMood() {
        smileyImg.setImageResource(Utils.getMoodsUI()[currentSmileyPosition].getSmileyResources());
        mainLayout.setBackgroundColor(getResources().getColor(Utils.getMoodsUI()[currentSmileyPosition].getColorResources()));
    }

    private void showNoteDialog() {

        View parentView = getLayoutInflater().inflate(R.layout.note_comment_layout, null);
        final EditText editText = parentView.findViewById(R.id.user_comment_input);
        String lastComment = mPreferences.getString("userEntry", "");
        editText.setText(lastComment);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(parentView);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.note_dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, editText.getText(), Toast.LENGTH_LONG).show();
                mPreferences.edit().putString("userEntry", editText.getText().toString()).apply();
                mCurrentMood.setUserComment(editText.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.note_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, R.string.toast_cancel, Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }
    private boolean isSameDay(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        calendar1.setTimeInMillis(date1);
        calendar2.setTimeInMillis(date2);

        if (calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR) || calendar1.get(Calendar.DAY_OF_YEAR) != calendar2.get(Calendar.DAY_OF_YEAR))
            return false;
        return true;
    }


    @Override
    protected void onPause() {
        super.onPause();

        long now = System.currentTimeMillis();
        mCurrentMood.setDate(now);
        System.out.println("On Pause : " + mCurrentMood.getUserComment());

        String json = mGson.toJson(mCurrentMood);
        mPreferences.edit().putString("currentMood", json).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String json = mPreferences.getString("currentMood", "");
        if (json != null && !json.equals("")) {
            Type type = new TypeToken<MoodHistory>() {
            }.getType();
            mCurrentMood = mGson.fromJson(json, type);
        }
        String moodListJson = mPreferences.getString("moodList", "");
        if (moodListJson != null && !moodListJson.equals("")) {
            Type listType = new TypeToken<ArrayList<MoodHistory>>() {
            }.getType();
            moodList = mGson.fromJson(moodListJson, listType);
        }
        long now = System.currentTimeMillis();
        System.out.println("Now = " + now);
        System.out.println("Mood date = " + mCurrentMood.getDate());
        if (!isSameDay(mCurrentMood.getDate(),now)){
            moodList.add(mCurrentMood);
            System.out.println("voici le commentaire écrit " + mCurrentMood.getUserComment());
            String listJson = mGson.toJson(moodList);
            mPreferences.edit().putString("moodList",listJson).apply();
            mCurrentMood = new MoodHistory();

            while (moodList.size() > 7){
                moodList.remove(0);
            }
            Toast.makeText(MainActivity.this,"Nombre de mood sauvegardé " + moodList.size(),Toast.LENGTH_SHORT).show();
        }
    }
}

