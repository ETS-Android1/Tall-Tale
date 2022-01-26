package Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tall_tale.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

import Database.Book;
import Database.BookAdapter;
import Database.BookDao;
import Database.TallTaleDatabase;
import Database.User;
import Database.UserDao;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior bottomSheetBehavior;
    private TextView mTextViewState;
    static String userHeight;
    //list of user selected book entries
    public static ArrayList<Integer> selected = new ArrayList<>();
    //list of book ids
    List<Integer> id = new LinkedList<>();
    //list of book titles
    List<String> title = new LinkedList<>();
    //list of book authors
    List<String> author = new LinkedList<>();
    //list of book startDateTimes
    List<String> startDateTime = new LinkedList<>();
    //list of book endDateTimes
    List<String> endDateTime = new LinkedList<>();
    //get work to main thread
    Handler mainHandler = new Handler();
    //list of all books in database
    List<Book> selectQueryResults = new LinkedList<>();
    //Book adapter
    BookAdapter adapter;
    //date/time format
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    //user search input value
    String search;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //handle control panel visibility
        CardView controlPanel = findViewById(R.id.control_panel);
        View bottomSheet = findViewById(R.id.bottom_sheet);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    //hide control panel
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        controlPanel.setVisibility(View.GONE);
                        break;
                    //reveal control panel
                    case BottomSheetBehavior.STATE_EXPANDED:
                        controlPanel.setVisibility(View.VISIBLE);
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });


        //connect book cards to adapter
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookAdapter(id, title, author, startDateTime, endDateTime);
        recyclerView.setAdapter(adapter);

        //populate book list
        SelectRunnable selectRunnable = new SelectRunnable();
        new Thread(selectRunnable).start();

        //set progress variables
        CurrentStackHeightRunnable currentStackHeightRunnable = new CurrentStackHeightRunnable();
        new Thread(currentStackHeightRunnable).start();

        //set listener for search action
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            //execute search
            @Override
            public void afterTextChanged(Editable editable) {

                //if the bar is empty, reset book adapter
                if(isEmpty(searchBar)) {
                    emptyAdapter();
                    SelectRunnable repopulate = new SelectRunnable();
                    new Thread(repopulate).start();

                }
                //empty book adapter
                emptyAdapter();

                //get search value
                search = searchBar.getText().toString();
                //format search for wildcard operation
                search = "%" + search + "%";

                //search for items and populate adapter
                SearchRunnable searchRunnable = new SearchRunnable();
                new Thread(searchRunnable).start();
            }
        });




        //clear all user selections
        selected.clear();
    }

    //empty out adapter
    public void emptyAdapter() {
        //empty adapter
        id.clear();
        title.clear();
        author.clear();
        startDateTime.clear();
        endDateTime.clear();
        //notify adapter
        adapter.notifyDataSetChanged();
        //clear selected items
        selected.clear();
    }

    //check if edit text is empty
    public boolean isEmpty(EditText editText){
        return editText.getText().toString().trim().length() == 0;
    }


    //convert inches to user format of "feet'inches"
    public String formatInchValue(int inches) {
        //total feet derived from inches
        int totalFeet;
        //leftover inches after feet are counted
        int remainingInches;
        //return value
        String returnValue;

        //check if inches reach one foot.
        if(inches < 12) {
            //if they do not, format and return
            returnValue = "0'" + inches;
            Log.d("Result", returnValue);
            return returnValue;
        }

        //convert inches to feet
        totalFeet = inches / 12;
        remainingInches = inches - (totalFeet * 12);

        //format
        returnValue = totalFeet + "'" + remainingInches;
        Log.d("Result", returnValue);

        return returnValue;
    }

    //complete all selected books
    public void completeSelectedBooks() {
        CompleteBookRunnable completeBookRunnable = new CompleteBookRunnable();
        new Thread(completeBookRunnable).start();
    }

    //prompt user to confirm completion of books, and react accordingly
    public void completionPopUp(View v) {
        if(selected.size() == 0) {
            Toast.makeText(getApplicationContext(), "Select One or More Books", Toast.LENGTH_SHORT).show();
            return;
        }

        //create window
        LayoutInflater inflater = (LayoutInflater) getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_confirm_completion, null);
        int dimension = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow completionWindow = new PopupWindow(view, dimension, dimension);
        completionWindow.setFocusable(true);
        //get anchor view
        CardView controlPanel = findViewById(R.id.control_panel);
        //show view
        completionWindow.showAtLocation(controlPanel, Gravity.TOP, 0, 0);
        
        //set listeners for window
        ImageButton confirm = view.findViewById(R.id.confirm_complete);
        ImageButton cancel = view.findViewById(R.id.cancel_complete);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeSelectedBooks();
                Toast.makeText(getApplicationContext(),    "         Book(s) Completed!\n" +
                                                                "Good Work! Now you can get a real job!", Toast.LENGTH_LONG).show();
                //dismiss popup
                completionWindow.dismiss();
                //relaunch page
                launchHome();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),    "         Never Mind, Then.\n" +
                                                                "Finishing things isn't for us either.", Toast.LENGTH_LONG).show();
                //dismiss popup
                completionWindow.dismiss();
                //relaunch page
                launchHome();
            }
        });
    }


    //prompt user to confirm report generation
    public void reportsPopUp(View v) {

        //create window
        LayoutInflater inflater = (LayoutInflater) getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_select_report, null);


        int dimension = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow completionWindow = new PopupWindow(view, dimension, dimension);
        completionWindow.setFocusable(true);
        //get anchor view
        CardView controlPanel = findViewById(R.id.control_panel);
        //show view
        completionWindow.showAtLocation(controlPanel, Gravity.TOP, 0, 0);




        //set listeners for window
        ImageButton confirm = view.findViewById(R.id.bt_confirm);

        //set on click listener
        //containers for radio buttons

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load new popup

                //get reference to radio group
                RadioGroup radioGroup = completionWindow.getContentView().findViewById(R.id.report_type_radio);
                        //findViewById(R.id.report_type_radio);

                if(radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Well, this isn't gonna be easy.\n Please make a selection!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //get reference to selected radio button
                RadioButton selectedButton = completionWindow.getContentView().findViewById(radioGroup.getCheckedRadioButtonId());

                //check which radio button was selected and react accordingly
                //share all books
                if(selectedButton == completionWindow.getContentView().findViewById(R.id.radio_all)) {
                    Log.d("PATH", "ONE");
                    ShareAllReportRunnable allReport = new ShareAllReportRunnable();
                    new Thread(allReport).start();
                }
                //share completed books only
                else if(selectedButton == completionWindow.getContentView().findViewById(R.id.radio_completed)) {
                    Log.d("PATH", "TWO");
                    ShareCompletedReportRunnable completedRunnable = new ShareCompletedReportRunnable();
                    new Thread(completedRunnable).start();
                }

                //dismiss popup
                completionWindow.dismiss();
            }
        });
    }

    //allow user to increase height goal
    public void hatPopUp() {
        //create window
        LayoutInflater inflater = (LayoutInflater) getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_increase_goal, null);


        int dimension = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow completionWindow = new PopupWindow(view, dimension, dimension);
        completionWindow.setFocusable(true);
        //get anchor view
        CardView controlPanel = findViewById(R.id.control_panel);
        //show view
        completionWindow.showAtLocation(controlPanel, Gravity.TOP, 0, 0);


        //set listeners for window
        //initialize seekbar references/values
        SeekBar feetBar = view.findViewById(R.id.seek_bar_feet);
        SeekBar inchesBar = view.findViewById(R.id.seek_bar_inches);
        feetBar.setMin(1);
        feetBar.setMax(12);
        inchesBar.setMin(0);
        inchesBar.setMax(11);

        //set listeners to notify user of seek bar value
        feetBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int selectedValue;
            String displayText;
            TextView feetSize = view.findViewById(R.id.feet_size);
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
            TextView inchesSize = view.findViewById(R.id.inches_size);
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


        //set click listeners
        ImageButton confirm = view.findViewById(R.id.bt_confirm);
        ImageButton cancel = view.findViewById(R.id.bt_cancel);
        //save user input
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDao userDao = TallTaleDatabase.getInstance(getApplicationContext()).userDao();
                //get progress bar references

                int feet = feetBar.getProgress();
                int inches = inchesBar.getProgress();
                int goalHeight = (feet * 12) + inches;
                int updateValue = goalHeight + User.userGoal;

                //update user height goal
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userDao.updateGoal(updateValue);
                        //pass information of logged in user to user
                        User newUser = userDao.getUser();
                        User.userId = newUser.getId();
                        User.userGoal = newUser.getGoal();
                        User.loggedInUser = newUser.getUsername();

                        //set work to main thread
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //dismiss popup
                                completionWindow.dismiss();
                                //reload page
                                launchHome();
                            }
                        });
                    }
                }).start();
            }
        });
        //dismiss window
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completionWindow.dismiss();
            }
        });
    }



    public void deleteSelectedBooks() {
        DeleteRunnable deleteRunnable = new DeleteRunnable();
        new Thread(deleteRunnable).start();
    }

    //prompt user to confirm deletion of books, and react accordingly
    public void deletionPopUp(View v) {
        if(selected.size() == 0) {
            Toast.makeText(getApplicationContext(), "Select One or More Books", Toast.LENGTH_SHORT).show();
            return;
        }
        //create window
        LayoutInflater inflater = (LayoutInflater) getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_confirm_completion, null);

        //set text for view
        TextView promptText = view.findViewById(R.id.prompt_text);
        promptText.setText("Delete All Selected Books?");

        int dimension = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow completionWindow = new PopupWindow(view, dimension, dimension);
        completionWindow.setFocusable(true);
        //get anchor view
        CardView controlPanel = findViewById(R.id.control_panel);
        //show view
        completionWindow.showAtLocation(controlPanel, Gravity.TOP, 0, 0);

        //set listeners for window
        ImageButton confirm = view.findViewById(R.id.confirm_complete);
        ImageButton cancel = view.findViewById(R.id.cancel_complete);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedBooks();
                Toast.makeText(getApplicationContext(),    "    Book(s) Deleted!\n" +
                                                                "We Didn't Like'em Anyways", Toast.LENGTH_LONG).show();
                //dismiss popup
                completionWindow.dismiss();
                //relaunch page
                launchHome();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),    "        Never Mind, Then.\n" +
                                                                "Spring Cleaning is for Losers", Toast.LENGTH_LONG).show();
                //dismiss popup
                completionWindow.dismiss();
                //relaunch page
                launchHome();
            }
        });
    }



    //launch add activity
    public void launchAddBook(View v) {
        Intent i = new Intent(this, AddBook.class);

        startActivity(i);
    }

    //launch edit activity
    public void launchEditActivity(View v) {
        if(selected.size() == 0) {
            Toast.makeText(getApplicationContext(), "Select A Book", Toast.LENGTH_SHORT).show();
            return;
        }
        if(selected.size() > 1) {
            Toast.makeText(getApplicationContext(),    "        Select ONLY One Book\n" +
                                                            "      Sorry about all the rules.\n" +
                                                            "We promise we aren't conformists", Toast.LENGTH_LONG).show();
            return;
        }

        //get info for update and launch edit activity
        SelectForUpdateRunnable selectForUpdateRunnable = new SelectForUpdateRunnable();
        new Thread(selectForUpdateRunnable).start();
    }

    //launch home activity
    public void launchHome() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(i);
    }


    //RUNNABLES


    //generate report including all books and share it
    class ShareAllReportRunnable implements Runnable {

        //String holding value of report to share
        String report = User.loggedInUser + " is sharing their reading list with you.";

        @Override
        public void run() {
            BookDao bookDao = TallTaleDatabase.getInstance(getApplicationContext()).bookDao();
            //query for values to be shared
            selectQueryResults = bookDao.getAllBooks();

            //if there is a note to share
            if (selectQueryResults.size() != 0) {


                for (int i = 0; i < selectQueryResults.size(); i++) {
                    report = report + "\nBook Title: \n    " + selectQueryResults.get(i).getTitle() + "\n" +
                            "Author: \n    " + selectQueryResults.get(i).getAuthor() + "\n" +
                            "Book Started On: \n    " + selectQueryResults.get(i).getStartDate() + "\n";
                    if(selectQueryResults.get(i).getCompleted()) {
                            report = report +  "Book Completed On: \n    " + selectQueryResults.get(i).getEndDate() + "\n\n";
                    }
                    else {
                        report = report + "\n";
                    }

                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent noteIntent = new Intent();
                        noteIntent.setAction(Intent.ACTION_SEND);
                        noteIntent.putExtra(Intent.EXTRA_TEXT, report);
                        noteIntent.setType("text/plain");
                        Intent shareReport = Intent.createChooser(noteIntent, "Share All Books");
                        startActivity(shareReport);
                    }
                });
            }
            //if there is not a note to share
            else {
                Log.d("null", "null");
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Add Some Books to Perform This Action", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }

    //generate report of completed books with stack height value and share
    class ShareCompletedReportRunnable implements Runnable {

        //String holding value of report to share


        String report = "If you stacked up all the books that " + User.loggedInUser + " has finished reading, you'd reach " + userHeight + " tall!\n" +
                        "Here are the books they've stacked so high..." ;

        @Override
        public void run() {
            Log.d("Runnable", "TWO");
            BookDao bookDao = TallTaleDatabase.getInstance(getApplicationContext()).bookDao();
            //query for values to be shared
            selectQueryResults = bookDao.getCompletedBooks();

            //if there is a note to share
            if (selectQueryResults.size() != 0) {


                for (int i = 0; i < selectQueryResults.size(); i++) {
                    report = report + "\nBook Title: \n    " + selectQueryResults.get(i).getTitle() + "\n" +
                            "Author: \n    " + selectQueryResults.get(i).getAuthor() + "\n" +
                            "Book Started On: \n    " + selectQueryResults.get(i).getStartDate() + "\n" +
                            "Book Completed On: \n    " + selectQueryResults.get(i).getEndDate() + "\n";
                            if(selectQueryResults.get(i).getThickness() == 1) {
                               report = report + "This book was " + selectQueryResults.get(i).getThickness() + " inch thick!\n\n";
                            }
                            else {
                                report = report + "This book was " + selectQueryResults.get(i).getThickness() + " inches thick!\n\n";
                            }

                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent noteIntent = new Intent();
                        noteIntent.setAction(Intent.ACTION_SEND);
                        noteIntent.putExtra(Intent.EXTRA_TEXT, report);
                        noteIntent.setType("text/plain");
                        Intent shareReport = Intent.createChooser(noteIntent, "Share Completed Books");
                        startActivity(shareReport);
                    }
                });
            }
            //if there is not a note to share
            else {
                Log.d("null", "null");
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Complete Some Books to Perform This Action", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }


    //get info for edit bundle and launch edit activity
    class SelectForUpdateRunnable implements  Runnable {

        @Override
        public void run() {

            BookDao bookDao = TallTaleDatabase.getInstance(getApplicationContext()).bookDao();
            //there will only every be one item in "selected" at this stage
            Book selectedBook = bookDao.getBookById(selected.get(0));

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    //add values to intent
                    Intent i = new Intent(getApplicationContext(), EditBookActivity.class);
                    i.putExtra("ID", selected.get(0));
                    i.putExtra("TITLE", selectedBook.getTitle());
                    i.putExtra("AUTHOR", selectedBook.getAuthor());
                    //launch edit activity
                    startActivity(i);
                }
            });
        }
    }

    //delete all selected books
    class DeleteRunnable implements  Runnable {

        @Override
        public void run() {
            for (int i = 0; i < selected.size(); i++) {
                BookDao bookDao = TallTaleDatabase.getInstance(getApplicationContext()).bookDao();
                bookDao.deleteById(selected.get(i));
            }
        }
    }

    //get user's current book stack height
    class CurrentStackHeightRunnable implements Runnable {
        //book height
        int totalHeight;
        //goal height
        int userGoal;
        //if user has reached goal
        boolean isGoalReached = false;

        @Override
        public void run() {
            BookDao bookDao = TallTaleDatabase.getInstance(getApplicationContext()).bookDao();
            UserDao userDao = TallTaleDatabase.getInstance(getApplicationContext()).userDao();

            //get list of all completed book heights
            List<Integer> bookHeightValues = bookDao.getStackHeight();

            //give height minimum value
            totalHeight = 0;
            //add all book heights together
            for (int i = 0; i < bookHeightValues.size(); i++) {
                //add each value to total height
                totalHeight += bookHeightValues.get(i);
            }

            //get user goal height
            userGoal = User.userGoal;
            Log.d("USER GOAL", String.valueOf(userGoal));



            //get work to main thread
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    //set progress bar position
                    //calculate progress percentage
                    int percentage =  (int)(((double)totalHeight / (double)userGoal) * 100);

                    //set value
                    ProgressBar progressBar = findViewById(R.id.progress_bar);

                    //if user has reached/passed user goal
                    if(totalHeight >= userGoal) {
                        CardView progressMarker = findViewById(R.id.user_current_card_view);
                        progressMarker.setCardBackgroundColor(getResources().getColor(R.color.Gold));
                        progressBar.setProgress(100);
                        //set progress marker to top
                        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(constraintLayout);
                        constraintSet.setGuidelinePercent(R.id.moving_guidline, (float)0.01);
                        constraintSet.connect(R.id.user_current_card_view, ConstraintSet.TOP, R.id.moving_guidline, ConstraintSet.TOP, 0);
                        constraintSet.applyTo(constraintLayout);

                        //notify method that user has reached their goal
                        isGoalReached = true;
                    }
                    //if user has not reached/passed user goal
                    else {
                        progressBar.setProgress(percentage);
                        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(constraintLayout);

                        //identify desired location of user progress marker
                        double target = ((85 - (((double) totalHeight / (double) userGoal) * 85)) / 100);

                        //check for minimum
                        if(totalHeight < 1) {
                            int minimumHeight = 1;
                            target = ((85 - (((double) minimumHeight / (double) userGoal) * 85)) / 100);
                        }

                        //set guideline to appropriate height
                        constraintSet.setGuidelinePercent(R.id.moving_guidline, (float) target);

                        constraintSet.connect(R.id.user_current_card_view, ConstraintSet.TOP, R.id.moving_guidline, ConstraintSet.TOP, 0);
                        constraintSet.applyTo(constraintLayout);
                    }

                    //assign current height value
                    TextView currentHeight = findViewById(R.id.user_current);
                    currentHeight.setText(formatInchValue(totalHeight).toString());
                    userHeight = formatInchValue(totalHeight);
                    //assign goal height value
                    TextView goalHeight = findViewById(R.id.user_goal);
                    goalHeight.setText(formatInchValue(userGoal).toString());

                    //if user has reached their goal
                    if(isGoalReached) {
                        //create popup to handle goal increase
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //delay pop up by three seconds
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        hatPopUp();
                                    }
                                });

                            }
                        }).start();
                    }

                }
            });
        }
    }


    //update a book's status to completed
    class CompleteBookRunnable implements Runnable {

        @Override
        public void run() {
            BookDao bookDao = TallTaleDatabase.getInstance(getApplicationContext()).bookDao();
            //get current endDateTime
            String endDateTime = sdf.format(new Date());
            for (int i = 0; i < selected.size(); i++) {
                bookDao.updateCompleted(selected.get(i), endDateTime);
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    launchHome();
                }
            });

        }
    }

    //query database for all books
    class SelectRunnable implements Runnable {

        @Override
        public void run() {
            BookDao bookDao = TallTaleDatabase.getInstance(getApplicationContext()).bookDao();

            //get all books into container
            List<Book> returnedQueryList = bookDao.getAllBooks();

            selectQueryResults = returnedQueryList;

            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    if(selectQueryResults != null ) {
                        for (int i = 0; i < selectQueryResults.size(); i++) {
                            //add book info
                            id.add(selectQueryResults.get(i).getId());
                            title.add(selectQueryResults.get(i).getTitle());
                            author.add(selectQueryResults.get(i).getAuthor());
                            startDateTime.add(selectQueryResults.get(i).getStartDate() + " -");
                            if(selectQueryResults.get(i).getEndDate() == null) {
                                endDateTime.add("...");
                            }
                            else {
                                endDateTime.add(selectQueryResults.get(i).getEndDate());
                            }

                            //notify adapter of change
                            adapter.notifyItemInserted(id.size()-1);
                            adapter.notifyItemInserted(title.size()-1);
                            adapter.notifyItemInserted(author.size()-1);
                            adapter.notifyItemInserted(startDateTime.size()-1);
                            adapter.notifyItemInserted(endDateTime.size()-1);
                        }
                    }
                }
            });
        }
    }

    //query database for all books
    class SearchRunnable implements Runnable {

        @Override
        public void run() {
            BookDao bookDao = TallTaleDatabase.getInstance(getApplicationContext()).bookDao();

            //abort if search has no value
            if(search == null) {
                return;
            }

            //get all books into container
            List<Book> returnedQueryList = bookDao.searchBooks(search);

            selectQueryResults = returnedQueryList;

            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    if(selectQueryResults != null ) {
                        for (int i = 0; i < selectQueryResults.size(); i++) {
                            //add book info
                            id.add(selectQueryResults.get(i).getId());
                            title.add(selectQueryResults.get(i).getTitle());
                            author.add(selectQueryResults.get(i).getAuthor());
                            startDateTime.add(selectQueryResults.get(i).getStartDate() + " -");
                            if(selectQueryResults.get(i).getEndDate() == null) {
                                endDateTime.add("...");
                            }
                            else {
                                endDateTime.add(selectQueryResults.get(i).getEndDate());
                            }

                            //notify adapter of change
                            adapter.notifyItemInserted(id.size()-1);
                            adapter.notifyItemInserted(title.size()-1);
                            adapter.notifyItemInserted(author.size()-1);
                            adapter.notifyItemInserted(startDateTime.size()-1);
                            adapter.notifyItemInserted(endDateTime.size()-1);
                        }
                    }
                }
            });
        }
    }
}

















