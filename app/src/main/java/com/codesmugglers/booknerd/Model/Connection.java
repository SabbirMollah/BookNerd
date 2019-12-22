package com.codesmugglers.booknerd.Model;

public class Connection {
    private String connectedUserId;
    private User connectedUser;
    private Book usersBook;
    private Book connectedUsersBook;

    public Connection(String connectedUserId, User connectedUser, Book usersBook, Book connectedUsersBook) {
        this.connectedUserId = connectedUserId;
        this.connectedUser = connectedUser;
        this.usersBook = usersBook;
        this.connectedUsersBook = connectedUsersBook;
    }

    public User getConnectedUser() {
        return connectedUser;
    }

    public void setConnectedUser(User connectedUser) {
        this.connectedUser = connectedUser;
    }

    public Book getUsersBook() {
        return usersBook;
    }

    public void setUsersBook(Book usersBooks) {
        this.usersBook = usersBooks;
    }

    public Book getConnectedUsersBook() {
        return connectedUsersBook;
    }

    public void setConnectedUsersBook(Book connectedUsersBook) {
        this.connectedUsersBook = connectedUsersBook;
    }

    public String getConnectedUserId() {
        return connectedUserId;
    }

    public void setConnectedUserId(String connectedUserId) {
        this.connectedUserId = connectedUserId;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "connectedUser=" + connectedUser +
                ", usersBooks=" + usersBook +
                ", connectedUsersBook=" + connectedUsersBook +
                '}';
    }


}
