package com.codesmugglers.booknerd.Model;

public class SuggestedBook {
    private Book book;
    private String ownerId;
    private String bookId;

    public SuggestedBook(Book book, String ownerId, String bookId) {
        this.book = book;
        this.ownerId = ownerId;
        this.bookId = bookId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public String toString() {
        return "SuggestedBook{" +
                "book=" + book +
                ", ownerId='" + ownerId + '\'' +
                ", bookId='" + bookId + '\'' +
                '}';
    }
}
