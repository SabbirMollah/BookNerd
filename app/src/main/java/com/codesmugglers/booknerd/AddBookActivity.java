package com.codesmugglers.booknerd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddBookActivity extends AppCompatActivity implements BookAdapter.OnItemClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    private RecyclerView mRecyclerView;
    private BookAdapter mBookAdapter;
    private ArrayList<Book> mBookList;
    private RequestQueue mRequestQueue;
    private SearchView mSearchView;

    private EditText mTitle;
    private EditText mAuthor;
    private EditText mIsbn;

    private static final int CAMERA_PERMISSION_CODE = 1;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // If user is logged out, bring him to LoginActivity
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(AddBookActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        mTitle = findViewById(R.id.title);
        mAuthor = findViewById(R.id.author);
        mIsbn = findViewById(R.id.isbn);

//        mRecyclerView = findViewById(R.id.recycler_view);
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        mBookList = new ArrayList<>();
//
//        mRequestQueue = Volley.newRequestQueue(this);


    }

    private void parseJson(String query) {
        mBookList.clear();
        query = query.replace(" ", "+");
        String url = "https://openlibrary.org/search.json?q=" + query;

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray docsArray = response.getJSONArray("docs");

                            for (int i=0; i<docsArray.length(); i++) {
                                JSONObject hit = docsArray.getJSONObject(i);
                                String title;
                                String authorName;
                                String isbn;

                                try{
                                    title = hit.getString("title");
                                }
                                catch(JSONException ex){
                                    title = "Unknown";
                                }

                                try{
                                    authorName = hit.getJSONArray("author_name").get(0).toString();
                                }
                                catch(JSONException ex){
                                    authorName = "Unknown";
                                }

                                try{
                                    isbn = hit.getJSONArray("isbn").get(0).toString();
                                }
                                catch(JSONException ex){
                                    //If book has no ISBN then skip
                                    continue;
                                }

                                mBookList.add(new Book(title, authorName,isbn));
                            }
                            mBookAdapter = new BookAdapter(AddBookActivity.this, mBookList);
                            mRecyclerView.setAdapter(mBookAdapter);

                            mBookAdapter.setOnItemClickListener(AddBookActivity.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("FOUND", error.toString());
                        Toast.makeText(AddBookActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );
        mRequestQueue.add(objectRequest);

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

    @Override
    public void onItemClick(int position) {
        String userId = mAuth.getCurrentUser().getUid();

        DatabaseReference booksDb = FirebaseDatabase.getInstance().getReference().child("Books");
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("Books");

        Map<String,String> userData = new HashMap<String,String>();
        userData.put("Name", mBookList.get(position).getTitle());
        userData.put("ISBN", mBookList.get(position).getIsbn());
        userData.put("Owner", userId);

        String key = booksDb.push().getKey();

        booksDb.child(key).setValue(userData);
        userDb.push().setValue(key);
    }

    public void onClickScan(View view) {
        if(ContextCompat.checkSelfPermission(AddBookActivity.this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            scannerView = new ZXingScannerView(this);
            scannerView.setResultHandler(new ZXingScannerResultHandler());
            setContentView(scannerView);
            scannerView.startCamera();
        }
        else{
            requestCameraPermission();
        }
    }

    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler{
        @Override
        public void handleResult(Result result) {
            String resultCode = result.getText();
            Toast.makeText(AddBookActivity.this, resultCode, Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_add_book);
            
            scannerView.stopCamera();
        }
    }

    private void requestCameraPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed").
                    setMessage("Hellow")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }
        else {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void onClickAddBook(View view) {

        String userId = mAuth.getCurrentUser().getUid();

        DatabaseReference booksDb = FirebaseDatabase.getInstance().getReference().child("Books");
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("Books");

        Map<String,String> userData = new HashMap<String,String>();
        userData.put("Name", mTitle.getText().toString());
        userData.put("ISBN", mIsbn.getText().toString());
        userData.put("Author", mAuthor.getText().toString());
        userData.put("Owner", userId);

        String key = booksDb.push().getKey();

        booksDb.child(key).setValue(userData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Toast.makeText(AddBookActivity.this, "Book Added Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        userDb.push().setValue(key);


    }
}
