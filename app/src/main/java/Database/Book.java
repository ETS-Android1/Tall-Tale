package Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import Controllers.Audio;
import Controllers.Digital;
import Controllers.Physical;

//SQLite table for Book objects
@Entity(tableName = "books")
public class Book {

    //average thickness for a page in a book
    public static final double PAGE_THICKNESS = 0.004;
    //average number of words on a typical novel page
    public static final int WORDS_TO_PAGE = 300;
    //average number of words spoken per minute in audiobook format
    public static final int WORDS_PER_MINUTE = 155;

    //constructor
    public Book(String title, String author, String startDate, String endDate, int thickness, boolean isCompleted) {
        this.title = title;
        this.author = author;
        this.startDate = startDate;
        this.endDate = endDate;
        this.thickness = thickness;
        this.isCompleted = isCompleted;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "author")
    private String author;

    //the date/time a user starts a book
    @ColumnInfo(name = "start_date")
    private String startDate;

    //the date/time a user ends a book
    @ColumnInfo(name = "end_date")
    private String endDate;

    //book thickness in centimeters
    @ColumnInfo(name = "thickness")
    private int thickness;

    //records if user has finished a book
    @ColumnInfo(name = "is_completed")
    private Boolean isCompleted;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }
}
