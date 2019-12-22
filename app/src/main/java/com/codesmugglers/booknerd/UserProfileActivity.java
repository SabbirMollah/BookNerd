package com.codesmugglers.booknerd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    private TextView mName;
    private TextView mDob;
    private TextView mCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // If user is logged out, bring him to LoginActivity
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        mName = findViewById(R.id.name);
        mDob = findViewById(R.id.dob);
        mCity = findViewById(R.id.city);

        getPersonalInfo();
    }

    private void getPersonalInfo() {

        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid());
        //Show User Information
        currentUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String name = snapshot.child("Name").getValue().toString();
                String dob = snapshot.child("DOB").getValue().toString();
                String city = snapshot.child("City").getValue().toString();

                mName.setText(name);
                mDob.setText(dob);
                mCity.setText(city);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERROR", "Couldn't connect to server");
                finish();
            }
        });
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

    public void onCLickOwnedBooks(View view) {
        Intent intent = new Intent(UserProfileActivity.this, OwnedBooksActivity.class);
        startActivity(intent);
    }

    public void onClickAddBook(View view) {
        Intent intent = new Intent(UserProfileActivity.this, AddBookActivity.class);
        startActivity(intent);
    }

    public void onClickMyConnections(View view) {
        Intent intent = new Intent(UserProfileActivity.this, ConnectionsActivity.class);
        startActivity(intent);
    }

    public void onClickLogOut(View view) {
        mAuth.signOut();
    }
}
