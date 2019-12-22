package com.codesmugglers.booknerd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.codesmugglers.booknerd.Adapter.BooksAdapter;
import com.codesmugglers.booknerd.Model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OwnedBooksActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    private String mCurrentUserId;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mBooksAdapter;
    private RecyclerView.LayoutManager mBooksLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owned_books);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // If user is logged out, bring him to LoginActivity
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(OwnedBooksActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };



        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mBooksLayoutManager = new LinearLayoutManager(OwnedBooksActivity.this);
        mRecyclerView.setLayoutManager(mBooksLayoutManager);

        mBooksAdapter = new BooksAdapter(getOwnedBooks(),OwnedBooksActivity.this);
        mRecyclerView.setAdapter(mBooksAdapter);

        getOwnedBooksIDs();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(firebaseAuthStateListener != null){
            mAuth.removeAuthStateListener(firebaseAuthStateListener);
        }
    }

    private void getOwnedBooksIDs() {
        DatabaseReference booksDB = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mCurrentUserId).child("OwnedBooks");
        booksDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot bookId: dataSnapshot.getChildren()){
                        fetchBookInformation(bookId.getValue().toString());
                        //Log.e("Book",bookId.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchBookInformation(String bookId) {
        DatabaseReference bookDB = FirebaseDatabase.getInstance().getReference()
                .child("Books").child(bookId);
        bookDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String title = dataSnapshot.child("Title").getValue().toString();;
                    String author = dataSnapshot.child("Author").getValue().toString();
                    String isbn = dataSnapshot.child("Isbn").getValue().toString();


                    Book book = new Book(title,author,isbn);

                    books.add(book);
                    mBooksAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<Book> books = new ArrayList<Book>();
    private List<Book> getOwnedBooks() {
        return books;
    }

}
