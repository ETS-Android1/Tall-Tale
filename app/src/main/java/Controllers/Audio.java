package Controllers;

import Database.Book;

//audio version of a Book
public class Audio extends Book {

    public Audio(String title, String author, String startDate, String endDate, int thickness, boolean isCompleted, int minutes) {
        super(title, author, startDate, endDate, thickness, isCompleted);
        this.minutes = minutes;
    }

    private int minutes;

    //amount of minutes to this audio book
    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    //get total thickness of a book based on time
    public int calculateThickness(Audio audioBook) {
        int numMinutes = audioBook.getMinutes();
        //convert minutes of book to number of words
        int numWords = numMinutes * WORDS_PER_MINUTE;
        //convert words to page count
        int numPages = numWords / WORDS_TO_PAGE;
        //convert page count to page thickness
        //get thickness of pages and round number
        int bookThickness = (int) Math.round(numPages * PAGE_THICKNESS);
        //make sure value is at least one
        if(bookThickness < 1) {
            bookThickness = 1;
        }

        return bookThickness;
    }
}
