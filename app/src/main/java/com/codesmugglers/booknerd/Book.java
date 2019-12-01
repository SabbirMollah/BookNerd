package com.codesmugglers.booknerd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Book {
    private String title;
    private String authorName;
    private String isbn;


    public Book(String title, String authorName, String isbn) {
        this.title = title;
        this.authorName = authorName;
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCoverUrl() {
        return "http://covers.openlibrary.org/b/isbn/"+isbn+"-S.jpg";
    }

    public boolean equals(Book book){
        // Two books are regarded as equal if they have same title and same author
        return this.title.equals(book.title) && this.authorName.equals(book.authorName);
    }
}
