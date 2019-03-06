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
import com.varvoux.aurelie.moodtracker.utils.Constants;
import com.varvoux.aurelie.moodtracker.utils.Utils;
import com.varvoux.aurelie.moodtracker.model.MoodHistory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;


/***
 * This is the main activity, that is first run and showing the different moods.
 * Moods can be changed by the user, he has to swipe up or down.
 * A comment can be made if the user click on the "comment button", located at the bottom left-hand
 * of the screen.
 */
public class MainActivity extends AppCompatActivity {

    private ImageView smileyImg; // This is a smiley, at the middle of the screen, which will change
    //according to moods
    private RelativeLayout mainLayout; // Dynamic background of main Activity, changing according to
    //moods
    private GestureDetector mGestureDetector; // GestureDetector will detect swipes
    private SharedPreferences mPreferences; // SharedPreferences to save and load history and moods
    private MoodHistory mCurrentMood; // Current mood displayed
    private Gson gson = new Gson(); // Gson library allow us to convert Java objects into JSON
    private ArrayList<MoodHistory> moodList = new ArrayList<>(); // This ArrayList stacks moods
    //saved by the user
    private int currentSmileyPosition = Constants.DEFAULT_MOOD; // This is the position of the smiley which
    // is shown first when the application is launched

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

    /*Lifecycle*/
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

        if (!Utils.isSameDay(mCurrentMood.getDate(), now)) { //If it's not the same day, mCurrentMood added to history
            //Prevents the user from saving a previous mood by changing the date in the phone
            if (moodList.size() > 0) {
                Date lastMoodDate = new Date(moodList.get(moodList.size() - 1).getDate()); //Date of the last mood saved
                Date currentMoodDate = new Date(mCurrentMood.getDate()); //Date of the current mood

                if (currentMoodDate.before(lastMoodDate)) { //If the currentMood is before the last mood saved,we don't save and reset everything
                    mCurrentMood = new MoodHistory();
                    currentSmileyPosition = mCurrentMood.getPosition();
                    displayMood();
                    return; //No need to continue, we quit
                }
            }
            moodList.add(mCurrentMood);

            while (moodList.size() > Constants.MAX_MOODS) {
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

        //Here, we convert mCurrentMood in Json & put it in the Shared Preferences
        String json = gson.toJson(mCurrentMood);
        mPreferences.edit().putString(Constants.CURRENT_MOOD_KEY, json).apply();
    }

    /**
     * This is a method to change the smiley and the background in terms of the current position
     */
    private void displayMood() {
        smileyImg.setImageResource(Constants.moodsUITab[currentSmileyPosition].getSmileyResources());
        mainLayout.setBackgroundColor(getResources().getColor(Constants.moodsUITab[currentSmileyPosition].getColorResources()));
    }

    /**
     * Gesture
     */
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

    /*
     * Gesture methods
     */

    /**
     * When the user swipes top, the mood will be sadder
     */
    public void onSwipeTop() {
        if (--currentSmileyPosition < 0) currentSmileyPosition = Constants.moodsUITab.length - 1;
        displayMood();
        mCurrentMood.setPosition(currentSmileyPosition);
    }

    /**
     * Moods will be happier when user swipes down
     */
    public void onSwipeBottom() {
        if (++currentSmileyPosition > Constants.moodsUITab.length - 1) currentSmileyPosition = 0;
        displayMood();
        mCurrentMood.setPosition(currentSmileyPosition);
    }

    /**
     * We create an AlertDialog to add a comment to mCurrentMood
     */
    private void showNoteDialog() { // When we click on the comment icon, the user can write a comment

        @SuppressLint("InflateParams") View parentView = getLayoutInflater().inflate(R.layout.note_comment_layout, null);
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


    /**
     * We can share mCurrentMood (mood and comment) with a Sharing Intent
     */
    private void shareMood() { // The user can share his day's mood by SMS, Email, WhatsApp, etc.
        String[] shareMoodTab = getResources().getStringArray(R.array.mood_str);
        String shareBody = getString(R.string.share_mood_string) + " " + shareMoodTab[mCurrentMood.getPosition()];
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_alert_dialog)));
    }
}
