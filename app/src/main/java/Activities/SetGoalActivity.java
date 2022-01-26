package Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.tall_tale.R;

import Database.TallTaleDatabase;
import Database.User;
import Database.UserDao;

public class SetGoalActivity extends AppCompatActivity {

    //get work to main thread
    Handler mainHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        //set min/max values for seekbars
        SeekBar feetBar = findViewById(R.id.seek_bar_feet);
        SeekBar inchesBar = findViewById(R.id.seek_bar_inches);
        feetBar.setMin(1);
        feetBar.setMax(12);
        inchesBar.setMin(0);
        inchesBar.setMax(11);


        //set listeners to notify user of seek bar value
        feetBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int selectedValue;
            String displayText;
            TextView feetSize = findViewById(R.id.feet_size);
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectedValue = feetBar.getProgress();
                displayText = selectedValue + "'";
                feetSize.setText(displayText);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        inchesBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int selectedValue;
            String displayText;
            TextView inchesSize = findViewById(R.id.inches_size);
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectedValue = inchesBar.getProgress();
                displayText = selectedValue + "\"";
                inchesSize.setText(displayText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //confirm height selection and progress user to next activity (home page)
    public void confirmSelection(View v) {
        UserDao userDao = TallTaleDatabase.getInstance(getApplicationContext()).userDao();
        //get progress bar references
        SeekBar feetBar = findViewById(R.id.seek_bar_feet);
        SeekBar inchesBar = findViewById(R.id.seek_bar_inches);

        int feet = feetBar.getProgress();
        int inches = inchesBar.getProgress();
        int goalHeight = (feet * 12) + inches;

        //insert height
        new Thread(new Runnable() {
            @Override
            public void run() {
                userDao.updateGoal(goalHeight);
                //pass information of logged in user to user
                User newUser = userDao.getUser();
                User.userId = newUser.getId();
                User.userGoal = newUser.getGoal();
                User.loggedInUser = newUser.getUsername();

                //set work to main thread
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        launchHome();
                    }
                });

            }
        }).start();


    }

    //launch Home screen
    public void launchHome() {
        Intent i = new Intent(this, MainActivity.class);

        startActivity(i);
    }



}


















