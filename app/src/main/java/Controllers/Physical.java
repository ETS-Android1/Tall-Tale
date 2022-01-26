package Controllers;

import Database.Book;

//physical version of a book object
public class Physical extends Book {

    public Physical(String title, String author, String startDate, String endDate, int thickness, boolean isCompleted, int pages) {
        super(title, author, startDate, endDate, thickness, isCompleted);
        this.pages = pages;
    }

    //amount of pages in this physical book
    private int pages;

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }


    //get total thickness of a book based on page count
    //amount returned in fractions of an inch
    public int calculateThickness(Physical physicalBook) {
        int numPages = physicalBook.getPages();

        //get thickness of pages and round number
        int bookThickness = (int) Math.round(numPages * PAGE_THICKNESS);
        //make sure value is at least one
        if(bookThickness < 1) {
            bookThickness = 1;
        }

        return bookThickness;
    }

}


