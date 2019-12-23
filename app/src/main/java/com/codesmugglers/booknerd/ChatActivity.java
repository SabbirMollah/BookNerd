package com.codesmugglers.booknerd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codesmugglers.booknerd.Adapter.ChatAdapter;
import com.codesmugglers.booknerd.Model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    private String mCurrentUserId;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private String mConnectedUserId;
    private String mChatId;
    private String mConnectionId;

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
        mConnectedUserId = getIntent().getExtras().getString("connectionId");
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
        Query connectionsDB = FirebaseDatabase.getInstance().getReference()
                .child("Connections").orderByChild("ConnectedUserId").equalTo(mConnectedUserId);
        connectionsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        mChatId = ds.child("ChatId").getValue().toString();

                    }
                    getChatMessages();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void sendMessage(){
        String message = mMessage.getText().toString();
        if(mChatId != null && !message.isEmpty()){
            DatabaseReference newMessageDB = FirebaseDatabase.getInstance().getReference()
                    .child("Chats").child(mChatId);
            String chatKey = newMessageDB.push().getKey();

            Map newMessage = new HashMap();
            newMessage.put("CreatedByUserId", mCurrentUserId);
            newMessage.put("Text", message);

            newMessageDB.child(chatKey).setValue(newMessage);

        }
        mMessage.setText(null);
    }

    private void getChatMessages(){
        Log.e("Hey", "Getting messages");
        DatabaseReference chatDB = FirebaseDatabase.getInstance().getReference()
                .child("Chats").child(mChatId);
        chatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String message = "";
                    boolean createdFromUser = false;

                    if(dataSnapshot.child("CreatedByUserId").getValue()!=null){
                        if(dataSnapshot.child("CreatedByUserId").getValue().toString().equals(mCurrentUserId)){
                            createdFromUser = true;
                        }
                    }
                    if(dataSnapshot.child("Text").getValue()!=null){
                        message = dataSnapshot.child("Text").getValue().toString();
                    }

                    Chat chat = new Chat(message, createdFromUser);
                    chats.add(chat);
                    mChatAdapter.notifyDataSetChanged();
                    Log.e(message, String.valueOf(createdFromUser));
                    Log.e("Array", chats.toString());
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
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
