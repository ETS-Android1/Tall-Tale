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
import android.widget.TextView;
import android.widget.Toast;

import com.example.tall_tale.R;

import Database.BookDao;
import Database.TallTaleDatabase;

//activity to help user edit books
public class EditBookActivity extends AppCompatActivity {

    //view values
    EditText editTitle;
    TextView titleSize;
    EditText editAuthor;
    TextView authorSize;
    //book values
    String title, author;
    int id;
    //get work to main thread
    Handler mainHandler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        //get view references
        editTitle = findViewById(R.id.edit_title);
        titleSize = findViewById(R.id.title_size);
        editAuthor = findViewById(R.id.edit_author);
        authorSize = findViewById(R.id.author_size);


        //get intent reference
        Intent intent = getIntent();
        if(intent != null) {

            //get title value
            editTitle.setText(intent.getStringExtra("TITLE"));
            //get author value
            editAuthor.setText(intent.getStringExtra("AUTHOR"));
            //get book id
            id = intent.getIntExtra("ID", 0);
        }

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

    //save user changes
    public void onClickConfirm(View v) {
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

        //update book record with new input
        UpdateRunnable updateRunnable = new UpdateRunnable();
        new Thread(updateRunnable).start();
    }

    //check if edit text is empty
    public boolean isEmpty(EditText editText){
        return editText.getText().toString().trim().length() == 0;
    }

    public void launchHome() {
        Intent i = new Intent(this, MainActivity.class);

        startActivity(i);
    }

    //update runnable
    public class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            BookDao bookDao = TallTaleDatabase.getInstance(getApplicationContext()).bookDao();

            //update user
            bookDao.updateBook(id, title, author);

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    launchHome();
                }
            });

        }
    }

}
























