package com.codesmugglers.booknerd.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesmugglers.booknerd.R;

public class OwnedBookViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView mTitle;
    public TextView mAuthor;
    public TextView mIsbn;

    public OwnedBookViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mTitle = itemView.findViewById(R.id.title);
        mAuthor = itemView.findViewById(R.id.author);
        mIsbn = itemView.findViewById(R.id.isbn);
    }

    @Override
    public void onClick(View view) {

    }
}
