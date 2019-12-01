package com.codesmugglers.booknerd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddBookActivity extends AppCompatActivity implements BookAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private BookAdapter mBookAdapter;
    private ArrayList<Book> mBookList;
    private RequestQueue mRequestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mBookList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(this);




        parseJson();
    }

    private void parseJson() {
        String url = "https://openlibrary.org/search.json?q=the+lord+of+the+rings";

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
                        Log.i("FOUND", error.toString());
                    }
                }

        );
        mRequestQueue.add(objectRequest);

    }

    @Override
    public void onItemClick(int position) {
        Log.e("PRESSED", "PRESSED");
    }
}
