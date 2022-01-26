package Controllers;

import Database.Book;

//digital version of a book object
public class Digital extends Book {

    public Digital(String title, String author, String startDate, String endDate, int thickness, boolean isCompleted, int words) {
        super(title, author, startDate, endDate, thickness, isCompleted);
        this.words = words;
    }

    //amount of words in this digital book
    private int words;

    public int getWords() {
        return words;
    }

    public void setWords(int words) {
        this.words = words;
    }

    //get total thickness of a book based on word count
    public int calculateThickness(Digital digitalBook) {
        int numWords = digitalBook.getWords();
        //convert words to page count
        int numPages = numWords / WORDS_TO_PAGE;
        //convert page count to page thickness
        //get thickness of pages and round number
        int bookThickness = (int) Math.round(numPages * PAGE_THICKNESS);
        //make sure value is at least one
        if (bookThickness < 1) {
            bookThickness = 1;
        }

        return bookThickness;
    }
}
