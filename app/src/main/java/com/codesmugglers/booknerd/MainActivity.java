package com.codesmugglers.booknerd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.codesmugglers.booknerd.Adapter.SuggestedBooksArrayAdapter;
import com.codesmugglers.booknerd.Model.Book;
import com.codesmugglers.booknerd.Model.SuggestedBook;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    private SuggestedBook suggestedBooks[];
    private SuggestedBooksArrayAdapter arrayAdapter;
    private int i;

    private String mCurrentUserId;

    ListView listView;
    List<SuggestedBook> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // If user is logged out, bring him to LoginActivity
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        rowItems= new ArrayList<SuggestedBook>();
        arrayAdapter = new SuggestedBooksArrayAdapter(this, R.layout.suggested_book_item, rowItems );

        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                //Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                SuggestedBook suggestedBook = ((SuggestedBook)dataObject);
                DatabaseReference leftSwipeDb = FirebaseDatabase.getInstance().getReference()
                        .child("Books").child(suggestedBook.getBookId()).child("Left").child(mCurrentUserId);
                DatabaseReference rightSwipeDb = FirebaseDatabase.getInstance().getReference()
                        .child("Books").child(suggestedBook.getBookId()).child("Right").child(mCurrentUserId);
                leftSwipeDb.setValue("True");
                rightSwipeDb.setValue("False");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                SuggestedBook suggestedBook = ((SuggestedBook)dataObject);
                DatabaseReference leftSwipeDb = FirebaseDatabase.getInstance().getReference()
                        .child("Books").child(suggestedBook.getBookId()).child("Left").child(mCurrentUserId);
                DatabaseReference rightSwipeDb = FirebaseDatabase.getInstance().getReference()
                        .child("Books").child(suggestedBook.getBookId()).child("Right").child(mCurrentUserId);
                leftSwipeDb.setValue("False");
                rightSwipeDb.setValue("True");

                setConnection(mCurrentUserId, suggestedBook.getOwnerId(), suggestedBook);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                //Toast.makeText(MainActivity.this, "Book suggestions is empty!, Try later.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Clicked!!!", Toast.LENGTH_SHORT).show();
            }
        });


        getSuggestedBooks();
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

    private void getSuggestedBooks() {
        DatabaseReference booksDb = FirebaseDatabase.getInstance().getReference().child("Books");
        rowItems.clear();
        arrayAdapter.notifyDataSetChanged();
        booksDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot book: dataSnapshot.getChildren()){
                        String author = dataSnapshot.child(book.getKey()).child("Author").getValue().toString();
                        String title = dataSnapshot.child(book.getKey()).child("Title").getValue().toString();
                        String isbn = dataSnapshot.child(book.getKey()).child("Isbn").getValue().toString();
                        String ownerId = dataSnapshot.child(book.getKey()).child("Owner").getValue().toString();
                        String bookId = book.getKey();

                        if(!ownerId.equals(mCurrentUserId)){
                            Book _book = new Book(title,author,isbn);
                            SuggestedBook suggestedBook = new SuggestedBook(_book,ownerId, bookId);
                            //Log.e("Owner " + ownerId, mCurrentUserId );
                            rowItems.add(suggestedBook);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setConnection(final String userId, final String ownerId, final SuggestedBook suggestedBook){
        Query ownersBooksDB = FirebaseDatabase.getInstance().getReference()
                .child("Books").orderByChild("Owner").equalTo(mCurrentUserId);
        ownersBooksDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot book: dataSnapshot.getChildren()){
                        for(DataSnapshot swipeId: book.child("Right").getChildren()){
                            Log.e(ownerId,swipeId.getValue().toString()+ " " + swipeId.getKey());
                            if(swipeId.getKey().equals(ownerId) && swipeId.getValue().toString().equals("True")){
                                //There is a connection

                                String title = book.child("Title").getValue().toString();
                                String author = book.child("Author").getValue().toString();
                                String isbn = book.child("Isbn").getValue().toString();

                                DatabaseReference chatDB = FirebaseDatabase.getInstance().getReference()
                                        .child("Chats");
                                String chatKey = FirebaseDatabase.getInstance().getReference()
                                        .child("Chats").push().getKey();

                                DatabaseReference connectionsDB = FirebaseDatabase.getInstance().getReference()
                                        .child("Connections");
                                String connectionsKey = FirebaseDatabase.getInstance().getReference()
                                        .child("Connections").push().getKey();


                                Map<String,String> usersBook = new HashMap<String,String>();
                                usersBook.put("Title", title);
                                usersBook.put("Author", author);
                                usersBook.put("Isbn", isbn);
                                usersBook.put("Owner", userId);
                                connectionsDB.child(connectionsKey).child("UsersBook").setValue(usersBook);
                                connectionsDB.child(connectionsKey).child("UserId").setValue(userId);

                                Map<String,String> connectedUsersBook = new HashMap<String,String>();
                                connectedUsersBook.put("Title", suggestedBook.getBook().getTitle());
                                connectedUsersBook.put("Author", suggestedBook.getBook().getAuthor());
                                connectedUsersBook.put("Isbn", suggestedBook.getBook().getIsbn());
                                connectedUsersBook.put("Owner", ownerId);
                                connectionsDB.child(connectionsKey).child("ConnectedUsersBook").setValue(connectedUsersBook);
                                connectionsDB.child(connectionsKey).child("ConnectedUserId").setValue(ownerId);

                                connectionsDB.child(connectionsKey).child("ChatId").setValue(chatKey);

                                //Second ConnectionsID
                                DatabaseReference secondConnectionsDB = FirebaseDatabase.getInstance().getReference()
                                        .child("Connections");
                                String secondConnectionsKey = FirebaseDatabase.getInstance().getReference()
                                        .child("Connections").push().getKey();
                                connectionsDB.child(secondConnectionsKey).child("UsersBook").setValue(connectedUsersBook);
                                connectionsDB.child(secondConnectionsKey).child("UserId").setValue(ownerId);
                                connectionsDB.child(secondConnectionsKey).child("ConnectedUsersBook").setValue(usersBook);
                                connectionsDB.child(secondConnectionsKey).child("ConnectedUserId").setValue(userId);
                                connectionsDB.child(secondConnectionsKey).child("ChatId").setValue(chatKey);


                                DatabaseReference userConnectionDB = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child(userId).child("Connections").child(ownerId);
                                DatabaseReference otherUserConnectionDB = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child(ownerId).child("Connections").child(userId);
                                userConnectionDB.setValue("True");
                                otherUserConnectionDB.setValue("True");

                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void onClickProfile(View view) {
        Intent ownedBooksIntent = new Intent(MainActivity.this, UserProfileActivity.class);
        startActivity(ownedBooksIntent);
    }

}