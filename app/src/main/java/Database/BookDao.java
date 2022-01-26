package Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

//Handle queries for Book Objects
@Dao
public interface BookDao {

    //return a list of all books
    @Query("SELECT * FROM books")
    List<Book> getAllBooks();

    //get a book by Id
    @Query("SELECT * FROM books WHERE id = :id")
    Book getBookById(int id);

    //get completed books
    @Query("SELECT * FROM books WHERE is_completed = 1")
    List<Book> getCompletedBooks();

    //search for books by String value (the string value must be formatted to %String%
    @Query("SELECT * FROM books WHERE title LIKE :search OR author LIKE :search OR start_date LIKE :search OR end_date LIKE :search")
    List<Book> searchBooks(String search);

    //insert a new book object
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBook(Book book);

    //mark a book as completed
    @Query("UPDATE books SET is_completed = 1, end_date = :end WHERE id = :id")
    void updateCompleted(int id, String end);

    //update book values
    @Query("UPDATE books SET title = :title, author = :author WHERE id = :id")
    void updateBook(int id, String title, String author);

    //get current user stack height
    @Query("SELECT thickness FROM books WHERE is_completed = 1")
    List<Integer> getStackHeight();

    //delete book entry by ID
    @Query("DELETE FROM books WHERE id = :id")
    void deleteById(int id);
}
