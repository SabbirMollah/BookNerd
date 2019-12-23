package com.codesmugglers.booknerd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codesmugglers.booknerd.Adapter.BooksAdapter;
import com.codesmugglers.booknerd.Adapter.ChatAdapter;
import com.codesmugglers.booknerd.Model.Chat;
import com.codesmugglers.booknerd.Model.Connection;
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

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    private String mCurrentUserId;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private String mConnectionId;
    private String mChatId;

    private EditText mMessage;
    private Button mSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // If user is logged out, bring him to LoginActivity
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        mConnectionId = getIntent().getExtras().getString("connectionId");
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);

        mChatAdapter = new ChatAdapter(getChats(),ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mMessage = findViewById(R.id.message);
        mSend = findViewById(R.id.send);

        getChatId();

        mSend.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void getChatId(){
        DatabaseReference chatDB = FirebaseDatabase.getInstance().getReference();
    }

    private void sendMessage(){
        Query userDB = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mCurrentUserId).child("Connections").orderByKey().equalTo(mConnectionId);
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    mChatId = dataSnapshot.getKey();
                    Log.e("Hello",mChatId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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


    private ArrayList<Chat> chats = new ArrayList<Chat>();
    private List<Chat> getChats() {
        return chats;
    }
}
