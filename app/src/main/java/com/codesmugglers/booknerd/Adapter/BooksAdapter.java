package com.codesmugglers.booknerd.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesmugglers.booknerd.Model.Book;
import com.codesmugglers.booknerd.R;
import com.codesmugglers.booknerd.ViewHolders.OwnedBookViewHolders;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<OwnedBookViewHolders> {
    private List<Book> books;
    private Context context;

    public BooksAdapter(List<Book> books, Context context){
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public OwnedBookViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.owned_book_item,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        OwnedBookViewHolders rcv = new OwnedBookViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull OwnedBookViewHolders holder, int position) {
        holder.mTitle.setText(books.get(position).getTitle());
        holder.mAuthor.setText(books.get(position).getAuthor());
        holder.mIsbn.setText(books.get(position).getIsbn());
    }

    @Override
    public int getItemCount() {
        return this.books.size();
    }
}
