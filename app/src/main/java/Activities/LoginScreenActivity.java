package Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tall_tale.R;

import Database.TallTaleDatabase;
import Database.User;
import Database.UserDao;

public class LoginScreenActivity extends AppCompatActivity {

    //containers for login/create account views
    CardView login;
    CardView create;
    //get work to main thread
    Handler mainHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        //initialize form view values
        login = findViewById(R.id.login_form);
        create = findViewById(R.id.create_account);

        //if user does not exist, open login form
        doesUserExist();
    }

    //check for existing user
    public void doesUserExist() {
        UserDao userDao = TallTaleDatabase.getInstance(getApplicationContext()).userDao();

        new Thread(new Runnable() {

            @Override
            public void run() {
                //get user from database
                User potentialUser = userDao.getUser();

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //if user does not exist
                        if(potentialUser == null) {
                            //reveal login form
                            revealCreate();
                        }

                    }
                });
            }
        }).start();

    }

    //activate login sequence
    public void onClickLogin(View v) {
        //get user username/password input
        //get EditText view references
        EditText editUsername = findViewById(R.id.edit_user_name);
        EditText editPassword = findViewById(R.id.edit_password);

        //check that the edit texts are not empty
        if(isEmpty(editUsername) || isEmpty(editPassword)) {
            Toast.makeText(getApplicationContext(),    "Sneaky, but that won't work on us.\n" +
                                                            "You have to enter SOMETHING", Toast.LENGTH_LONG).show();
            return;
        }

        //catch values
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();

        //validate credentials
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserDao userDao = TallTaleDatabase.getInstance(getApplicationContext()).userDao();
                User attemptedUser = userDao.validateLogin(username, password);

                //if the search yields no results
                if (attemptedUser == null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "That isn't quite right.\n" +
                                    "Have we met, my guy?", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //if credentials are correct
                else {
                    //load user info and launch home activity
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            User.userId = attemptedUser.getId();
                            User.userGoal = attemptedUser.getGoal();
                            User.loggedInUser = attemptedUser.getUsername();

                            launchHome();
                        }
                    });
                }
            }
        }).start();
    }

    //create user account
    public void onClickCreate(View v) {
        //get EditText view references
        EditText editUsername = findViewById(R.id.edit_user_name2);
        EditText editPassword = findViewById(R.id.edit_password2);

        //check that the edit texts are not empty
        if(isEmpty(editUsername) || isEmpty(editPassword)) {
            Toast.makeText(getApplicationContext(), "Go on, pick something.", Toast.LENGTH_SHORT).show();
            return;
        }
        //catch values
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();


        //insert user
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserDao userDao = TallTaleDatabase.getInstance(getApplicationContext()).userDao();
                //note - the 0 is a placeholder value for user's height/height goal
                userDao.insertUser(new User(username, password, 0));

                //launch set goal activity
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        launchSetGoal();
                    }
                });
            }
        }).start();

    }


    //check if edit text is empty
    public boolean isEmpty(EditText editText){
        return editText.getText().toString().trim().length() == 0;
    }

    //launch home screen
    public void launchHome() {
        Intent i = new Intent(this, MainActivity.class);

        startActivity(i);
    }

    //launch set goal activity
    public void launchSetGoal() {
        Intent i = new Intent(this, SetGoalActivity.class);

        startActivity(i);
    }

    //reveal create account form
    public void revealCreate() {
        //hide login form
        login.setVisibility(View.GONE);
        //reveal create account form
        create.setVisibility(View.VISIBLE);
    }

    //introduce user to the application 
    public void tutorialPopUp(View v) {
        //create window
        LayoutInflater inflater = (LayoutInflater) getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_tutorial, null);
        int dimension = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow completionWindow = new PopupWindow(view, dimension, dimension);
        completionWindow.setFocusable(true);
        //get anchor view
        TextView anchor = findViewById(R.id.background_text);
        //show view
        completionWindow.showAtLocation(anchor, Gravity.TOP, 0, 0);

        //set listener for window
        ImageButton confirm = view.findViewById(R.id.bt_confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "You took notes, right?", Toast.LENGTH_SHORT).show();
                //dismiss popup
                completionWindow.dismiss();
            }
        });

    }
}