package Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tall_tale.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import Controllers.Audio;
import Controllers.Digital;
import Controllers.Physical;
import Database.Book;
import Database.BookDao;
import Database.TallTaleDatabase;

//this activity helps the user add a new book object to the database
//each form in this activity is styled in the layout file activity_add_book, so not additional fragments/popups are required for normal operation
public class AddBook extends AppCompatActivity {

    //references to forms
    CardView cardViewOne;
    CardView cardViewTwo;
    CardView cardViewThree;
    CardView cardViewFour;
    //track currently visible form
    int userProgress = 1;
    //Book object
    Book newBook;
    enum Type {PHYSICAL, DIGITAL, AUDIO}
    Type selectedType;
    //reference for views on second form, used for getting book thickness value
    TextView inputPrompt;
    EditText editCount;
    //values for new book object
    String title, author, startDateTime, endDateTime;
    int thickness;
    boolean isCompleted;
    //container for user number input before it is processed into page thickness
    int userInput;
    //date format
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        //instantiate references
        cardViewOne = findViewById(R.id.form_one);
        cardViewTwo = findViewById(R.id.form_two);
        cardViewThree = findViewById(R.id.form_three);
        cardViewFour = findViewById(R.id.form_four);
        inputPrompt = findViewById(R.id.book_input_prompt);
        editCount = findViewById(R.id.edit_count);

        //set listeners for buttons in forms
        ImageButton formOneConfirm = findViewById(R.id.form_one_confirm);
        ImageButton formTwoConfirm = findViewById(R.id.form_two_confirm);
        ImageButton formThreeConfirm = findViewById(R.id.form_three_confirm);
        ImageButton formFourConfirm = findViewById(R.id.form_four_confirm);
        ImageButton formFourCancel = findViewById(R.id.form_four_cancel);

        //get radio input and progress to next form
        formOneConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get reference to radio group
                RadioGroup bookType = findViewById(R.id.book_type_radio);
                //validate that a selection has been made
                if(bookType.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Well, this isn't gonna be easy.\n Please make a selection!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //get reference to selected radio button
                RadioButton selectedButton = findViewById(bookType.getCheckedRadioButtonId());

                //identify button and perform appropriate action
                if(selectedButton == findViewById(R.id.radio_physical)) {
                    //notify activity of selection
                    selectedType = Type.PHYSICAL;
                    //change values for next form
                    physicalSelected();
                }
                else if (selectedButton == findViewById(R.id.radio_digital)) {
                    //notify activity of selection
                    selectedType = Type.DIGITAL;
                    //change values for next form
                    digitalSelected();
                }
                 else if (selectedButton == findViewById(R.id.radio_audio)) {
                    //notify activity of selection
                    selectedType = Type.AUDIO;
                    //change values for next form
                    audioSelected();
                }

                //progress user to next form
                nextForm();
            }
        });

        formTwoConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get user input and sanitize it
                if(isEmpty(editCount)) {
                    Toast.makeText(getApplicationContext(), "   Is the UI that bad?\nEnter a number, my guy", Toast.LENGTH_LONG).show();
                    return;
                }
                //catch user input in temporary container
                String unprocessedUserInput = editCount.getText().toString();
                if(editCount.getText().toString().length() > 9) {
                    Toast.makeText(getApplicationContext(),
                            "Hmm... That input is high and wild.\nCool Story Bro. We don't believe you.", Toast.LENGTH_LONG).show();
                    return;
                }
                userInput = Integer.parseInt(removeSpecialCharacters(unprocessedUserInput));

                //invalidation for people insane enough to be reading a book like "Marcel Proust's elephantine Remembrance of Things Past" (a 9,609,000 word book)
                if(userInput > 1000000) {
                    Toast.makeText(getApplicationContext(), "    Wow! That's a long book.\nThis app deals in numbers less than a million.", Toast.LENGTH_LONG).show();
                    return;
                }

                //progress user to next form
                nextForm();
            }
        });

        //values for tracking user title/author input and input size
        EditText editTitle = findViewById(R.id.edit_title);
        TextView titleSize = findViewById(R.id.title_size);
        EditText editAuthor = findViewById(R.id.edit_author);
        TextView authorSize = findViewById(R.id.author_size);

        formThreeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check values have been entered by user
                if(isEmpty(editTitle) || isEmpty(editAuthor)) {
                    Toast.makeText(getApplicationContext(),
                            "          These fields are required\nDon't like it? Complain on the internet.", Toast.LENGTH_LONG).show();
                    return;
                }
                //catch user input
                title = editTitle.getText().toString();
                author = editAuthor.getText().toString();
                //check that values are within size limit
                if(title.length() > 40 || author.length() > 20) {
                    Toast.makeText(getApplicationContext(),
                                       "Input is too long     \n\n" +
                                            "If you can't read our form,\n" +
                                            "you're gonna have a tough \n" +
                                            "time with that book", Toast.LENGTH_LONG).show();
                    return;
                }

                //progress user to next form
                nextForm();
            }
        });

        formFourConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if the user has completed the book already
                isCompleted = true;
                Toast.makeText(getApplicationContext(), "Man, what an overachiever.", Toast.LENGTH_SHORT).show();

                //get current startDateTime
                startDateTime = sdf.format(new Date());
                //set end date to the same
                endDateTime = startDateTime;

                //establish book type
                if(selectedType == Type.PHYSICAL) {
                    //create physical book object and assign all values obtained up to this point
                    //Note: the 0 for thickness is a placeholder
                    Physical book = new Physical(title, author, startDateTime, endDateTime, 0, isCompleted, userInput);
                    //calculate book thickness based on page count
                    book.setThickness(book.calculateThickness(book));

                    //load book to insert
                    newBook = book;
                    
                    //insert book
                    InsertRunnable insertRunnable = new InsertRunnable();
                    new Thread(insertRunnable).start();

                    Toast.makeText(getApplicationContext(), "Success! Book Inserted.", Toast.LENGTH_SHORT).show();
                }
                else if(selectedType == Type.DIGITAL) {
                    //create digital book object and assign all values obtained up to this point
                    //Note: the 0 for thickness is a placeholder
                    Digital book = new Digital(title, author, startDateTime, endDateTime, 0, isCompleted, userInput);
                    //calculate book thickness based on word count
                    book.setThickness(book.calculateThickness(book));

                    //load book to insert
                    newBook = book;

                    //insert book
                    InsertRunnable insertRunnable = new InsertRunnable();
                    new Thread(insertRunnable).start();

                    Toast.makeText(getApplicationContext(), "Success! Book Inserted.", Toast.LENGTH_SHORT).show();
                }
                else if(selectedType == Type.AUDIO) {
                    //create audio book object and assign all values obtained up to this point
                    //Note: the 0 for thickness is a placeholder
                    Audio book = new Audio(title, author, startDateTime, endDateTime, 0, isCompleted, userInput);
                    //calculate book thickness based on minute count
                    book.setThickness(book.calculateThickness(book));


                    //load book to insert
                    newBook = book;

                    //insert book
                    InsertRunnable insertRunnable = new InsertRunnable();
                    new Thread(insertRunnable).start();

                    Toast.makeText(getApplicationContext(), "Success! Book Inserted.", Toast.LENGTH_SHORT).show();
                }

                //take user to home
                nextForm();
            }
        });
        formFourCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if the user has not completed the book yet
                isCompleted = false;

                //get current startDateTime
                startDateTime = sdf.format(new Date());


                //establish book type
                if(selectedType == Type.PHYSICAL) {
                    //create physical book object and assign all values obtained up to this point
                    //Note: the 0 for thickness is a placeholder
                    Physical book = new Physical(title, author, startDateTime, null, 0, isCompleted, userInput);
                    //calculate book thickness based on page count
                    book.setThickness(book.calculateThickness(book));

                    //load book to insert
                    newBook = book;

                    //insert book
                    InsertRunnable insertRunnable = new InsertRunnable();
                    new Thread(insertRunnable).start();

                    Toast.makeText(getApplicationContext(), "Success! Book Inserted.", Toast.LENGTH_SHORT).show();
                }
                else if(selectedType == Type.DIGITAL) {
                    //create digital book object and assign all values obtained up to this point
                    //Note: the 0 for thickness is a placeholder
                    Digital book = new Digital(title, author, startDateTime, null, 0, isCompleted, userInput);
                    //calculate book thickness based on word count
                    book.setThickness(book.calculateThickness(book));

                    //load book to insert
                    newBook = book;

                    //insert book
                    InsertRunnable insertRunnable = new InsertRunnable();
                    new Thread(insertRunnable).start();

                    Toast.makeText(getApplicationContext(), "Success! Book Inserted.", Toast.LENGTH_SHORT).show();
                }
                else if(selectedType == Type.AUDIO) {
                    //create audio book object and assign all values obtained up to this point
                    //Note: the 0 for thickness is a placeholder
                    Audio book = new Audio(title, author, startDateTime, null, 0, isCompleted, userInput);
                    //calculate book thickness based on minute count
                    book.setThickness(book.calculateThickness(book));

                    //load book to insert
                    newBook = book;

                    //insert book
                    InsertRunnable insertRunnable = new InsertRunnable();
                    new Thread(insertRunnable).start();

                    Toast.makeText(getApplicationContext(), "Success! Book Inserted.", Toast.LENGTH_SHORT).show();
                }
                //take user to home
                nextForm();
            }
        });




        //set listeners for edit text fields to validate input
        editTitle.addTextChangedListener(new TextWatcher() {
            int characterCount;
            String displayText;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                characterCount = editTitle.getText().toString().length();
                displayText = characterCount + "/40";
                titleSize.setText(displayText);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editAuthor.addTextChangedListener(new TextWatcher() {
            int characterCount;
            String displayText;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                characterCount = editAuthor.getText().toString().length();
                displayText = characterCount + "/20";
                authorSize.setText(displayText);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });



    }



    //methods to change input prompt/edit text hint based on used book type selection
    public void physicalSelected() {
        inputPrompt.setText("How many pages does your book have?");
        editCount.setHint("250");
    }
    public void digitalSelected() {
        inputPrompt.setText("How many words does your book have?");
        editCount.setHint("70,000");
    }
    public void audioSelected() {
        inputPrompt.setText("How long is your book in minutes?");
        editCount.setHint("744");
    }

    //remove all special characters from user with a "number" keyboard
    public String removeSpecialCharacters(String string) {
        String sanitizedString = string.replace(",", "")
                .replace(".", "")
                .replace("-", "")
                .replace(" ", "");
        return sanitizedString;
    }

    //reset user progress onDestroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        userProgress = 1;
    }

    //this method takes the user to the next form, and if the user has visited them all, calls the launchHome() method
    public void nextForm() {
        switch (userProgress) {
            case 1:
                cardViewOne.setVisibility(View.GONE);
                cardViewTwo.setVisibility(View.VISIBLE);
                userProgress = 2;
                break;
            case 2:
                cardViewTwo.setVisibility(View.GONE);
                cardViewThree.setVisibility(View.VISIBLE);
                userProgress = 3;
                break;
            case 3:
                cardViewThree.setVisibility(View.GONE);
                cardViewFour.setVisibility(View.VISIBLE);
                userProgress = 4;
                break;
            case 4:
                userProgress = 1;
                launchHome();
                break;

        }
    }

    //check if edit text is empty
    public boolean isEmpty(EditText editText){
        return editText.getText().toString().trim().length() == 0;
    }

    //launch the home activity
    public void launchHome() {
        Intent i = new Intent(this, MainActivity.class);

        startActivity(i);
    }

    //insert book object into database
    public class InsertRunnable implements Runnable {

        @Override
        public void run() {
            BookDao bookDao = TallTaleDatabase.getInstance(getApplicationContext()).bookDao();
            if(newBook != null) {
                bookDao.insertBook(newBook);
            }
        }
    }




}

