package com.varvoux.aurelie.moodtracker;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.lang.reflect.Type;


public class MainActivity extends AppCompatActivity {

    private ImageView smileyImg;
    private RelativeLayout mainLayout;
    private MoodUI[] mMoodUIS;
    private GestureDetector mGestureDetector;
    private SharedPreferences mPreferences;
    private MoodHistory mCurrentMood;
    private Gson mGson = new Gson();

    int currentSmileyPosition = 3;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = getPreferences(MODE_PRIVATE);

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

        mMoodUIS = new MoodUI[5];
        mMoodUIS[0] = new MoodUI(R.color.faded_red, R.drawable.smiley_sad, 0);
        mMoodUIS[1] = new MoodUI(R.color.warm_grey, R.drawable.smiley_disappointed, 1);
        mMoodUIS[2] = new MoodUI(R.color.cornflower_blue_65, R.drawable.smiley_normal, 2);
        mMoodUIS[3] = new MoodUI(R.color.light_sage, R.drawable.smiley_happy, 3);
        mMoodUIS[4] = new MoodUI(R.color.banana_yellow, R.drawable.smiley_super_happy, 4);

        noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoteDialog();
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HistoryActivity = new Intent(MainActivity.this, com.varvoux.aurelie.moodtracker.HistoryActivity.class);
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
        if (--currentSmileyPosition < 0) currentSmileyPosition = mMoodUIS.length - 1;
        displayMood();
    }

    public void onSwipeBottom() {
        if (++currentSmileyPosition > mMoodUIS.length - 1) currentSmileyPosition = 0;
        displayMood();

    }
    private void displayMood() {
        smileyImg.setImageResource(mMoodUIS[currentSmileyPosition].getSmileyResources());
        mainLayout.setBackgroundColor(getResources().getColor(mMoodUIS[currentSmileyPosition].getColorResources()));
    }

    private void showNoteDialog (){

        View parentView = getLayoutInflater().inflate(R.layout.note_comment_layout,null);
        final EditText editText = parentView.findViewById(R.id.user_comment_input);
        String lastComment = mPreferences.getString("userEntry","");
        editText.setText(lastComment);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(parentView);
        builder.setCancelable(false);
        builder.setPositiveButton (R.string.note_dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this,editText.getText(), Toast.LENGTH_LONG).show();
                mPreferences.edit().putString("userEntry",editText.getText().toString()).apply();
                mCurrentMood.setUserComment(editText.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.note_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this,R.string.toast_cancel,Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    //isSameDay


    @Override
    protected void onPause() {
        super.onPause();

        long now = System.currentTimeMillis();
        mCurrentMood.setDate(now);

        String json = mGson.toJson(mCurrentMood);
        mPreferences.edit().putString("currentMood",json).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String json = mPreferences.getString("currentMood","");
        if (json != null && !json.equals("")){
            Type type = new TypeToken<MoodHistory>(){}.getType();
            mCurrentMood = mGson.fromJson(json,type);
            //Toast.makeText(MainActivity.this,"Nombre de mood sauvegardé " + moodList.size(),Toast.LENGTH_SHORT).show();
        }


    }
}
