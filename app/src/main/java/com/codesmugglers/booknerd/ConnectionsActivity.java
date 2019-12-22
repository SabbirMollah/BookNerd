package com.codesmugglers.booknerd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.codesmugglers.booknerd.Adapter.ConnectionAdapter;

import com.codesmugglers.booknerd.Model.Book;
import com.codesmugglers.booknerd.Model.Connection;
import com.codesmugglers.booknerd.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConnectionsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    private String mCurrentUserId;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mConnectionAdapter;
    private RecyclerView.LayoutManager mConnectionsLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // If user is logged out, bring him to LoginActivity
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(ConnectionsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mConnectionsLayoutManager = new LinearLayoutManager(ConnectionsActivity.this);
        mRecyclerView.setLayoutManager(mConnectionsLayoutManager);

        mConnectionAdapter = new ConnectionAdapter(getConnections(), ConnectionsActivity.this);
        mRecyclerView.setAdapter(mConnectionAdapter);

        fetchConnectionsInformation();

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


    private void fetchConnectionsInformation(){
        Query connectionsQuery = FirebaseDatabase.getInstance().getReference()
                .child("Connections").orderByChild("UserId").equalTo(mCurrentUserId);
        connectionsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot connection: dataSnapshot.getChildren()){
                        String connectedUsersId = connection.child("ConnectedUsersId").getValue().toString();
                        String connectedUsersBookAuthor = connection.child("ConnectedUsersBook").child("Author").getValue().toString();
                        String connectedUsersBookTitle = connection.child("ConnectedUsersBook").child("Title").getValue().toString();
                        String connectedUsersBookIsbn = connection.child("ConnectedUsersBook").child("Isbn").getValue().toString();

                        String usersBookAuthor = connection.child("UsersBook").child("Author").getValue().toString();
                        String usersBookTitle = connection.child("UsersBook").child("Title").getValue().toString();
                        String usersBookIsbn = connection.child("UsersBook").child("Isbn").getValue().toString();

                        Book userBook = new Book(usersBookTitle,usersBookAuthor,usersBookIsbn);
                        Book connectedUsersBoook = new Book(connectedUsersBookTitle, connectedUsersBookAuthor, connectedUsersBookTitle);

                        fetchUserInformation(connectedUsersId, userBook, connectedUsersBoook);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchUserInformation(String connectedUserId, final Book userBook, final Book connectedUserBook){
        Query connectedUsersQuery = FirebaseDatabase.getInstance().getReference()
                .child("Users").orderByKey().equalTo(connectedUserId);
        connectedUsersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot user: dataSnapshot.getChildren()){
                        String name = user.child("Name").getValue().toString();
                        String dob = user.child("DOB").getValue().toString();
                        String gender = user.child("Gender").getValue().toString();
                        String city = user.child("City").getValue().toString();

                        User connectedUser = new User(name, dob, gender, city);

                        Connection connection = new Connection(connectedUser, userBook, connectedUserBook);

                        connections.add(connection);
                        mConnectionAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private ArrayList<Connection> connections = new ArrayList<Connection>();
    private List<Connection> getConnections() {
        return connections;
    }
}
