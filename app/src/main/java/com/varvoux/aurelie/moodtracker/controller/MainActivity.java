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
import com.varvoux.aurelie.moodtracker.model.Constants;
import com.varvoux.aurelie.moodtracker.utils.Utils;
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
    private Gson gson = new Gson();
    private ArrayList<MoodHistory> moodList = new ArrayList<>();
    int currentSmileyPosition = Constants.DEFAULT_MOOD;


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
        ImageButton shareIcon = findViewById(R.id.share_icon);
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

        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMood();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        String json = mPreferences.getString(Constants.CURRENT_MOOD_KEY, "");
        if (json != null && !json.equals("")) {
            Type type = new TypeToken<MoodHistory>() {
            }.getType();
            mCurrentMood = gson.fromJson(json, type);
            currentSmileyPosition = mCurrentMood.getPosition();
            displayMood();
        }
        String moodListJson = mPreferences.getString(Constants.MOOD_LIST_KEY, "");
        if (moodListJson != null && !moodListJson.equals("")) {
            Type listType = new TypeToken<ArrayList<MoodHistory>>() {
            }.getType();
            moodList = gson.fromJson(moodListJson, listType);
        }
        long now = System.currentTimeMillis();

        if (!isSameDay(mCurrentMood.getDate(), now)) {
            moodList.add(mCurrentMood);

            while (moodList.size() > 7) {
                moodList.remove(0);
            }

            String listJson = gson.toJson(moodList);
            mPreferences.edit().putString(Constants.MOOD_LIST_KEY, listJson).apply();
            mCurrentMood = new MoodHistory();

            currentSmileyPosition = mCurrentMood.getPosition();
            displayMood();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        long now = System.currentTimeMillis();
        mCurrentMood.setDate(now);

        String json = gson.toJson(mCurrentMood);
        mPreferences.edit().putString(Constants.CURRENT_MOOD_KEY, json).apply();
    }

    private void displayMood() {
        smileyImg.setImageResource(Utils.moodsUITab[currentSmileyPosition].getSmileyResources());
        mainLayout.setBackgroundColor(getResources().getColor(Utils.moodsUITab[currentSmileyPosition].getColorResources()));
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

    public void onSwipeTop() { //When the user swipe top, the mood is more sad/sadder
        if (--currentSmileyPosition < 0) currentSmileyPosition = Utils.moodsUITab.length - 1;
        displayMood();
        mCurrentMood.setPosition(currentSmileyPosition);
    }

    public void onSwipeBottom() { // Moods are happier when the user swipe down
        if (++currentSmileyPosition > Utils.moodsUITab.length - 1) currentSmileyPosition = 0;
        displayMood();
        mCurrentMood.setPosition(currentSmileyPosition);

    }

    private void showNoteDialog() { // When we click on the comment icon, the user can write a comment

        View parentView = getLayoutInflater().inflate(R.layout.note_comment_layout, null);
        final EditText editText = parentView.findViewById(R.id.user_comment_input);
        String lastComment = mPreferences.getString(Constants.USER_ENTRY_KEY, "");
        editText.setText(lastComment);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(parentView);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.note_dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, editText.getText(), Toast.LENGTH_LONG).show();
                mPreferences.edit().putString(Constants.USER_ENTRY_KEY, editText.getText().toString()).apply();
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

    private boolean isSameDay(long date1, long date2) { // This method calculates if two dates in millisec are the same or not
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        calendar1.setTimeInMillis(date1);
        calendar2.setTimeInMillis(date2);

        if (calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR) || calendar1.get(Calendar.DAY_OF_YEAR) != calendar2.get(Calendar.DAY_OF_YEAR))
            return false;
        return true;
    }

    private void shareMood() { // The user can share his day's mood by SMS, Email, Whatsapp, etc.
        String[] shareMoodTab = getResources().getStringArray(R.array.mood_str);
        String shareBody = R.string.share_mood_str + " " + shareMoodTab[mCurrentMood.getPosition()];
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.share_subject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_alert_dialog)));
    }
}

