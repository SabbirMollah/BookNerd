package com.codesmugglers.booknerd.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesmugglers.booknerd.R;

public class ConnectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mName;
    public TextView mGender;
    public TextView mCity;
    public TextView mConnectionBookTitle;
    public TextView mUserBookTitle;

    public ConnectionViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mName = itemView.findViewById(R.id.name);
        mGender = itemView.findViewById(R.id.gender);
        mCity = itemView.findViewById(R.id.city);
        mUserBookTitle = itemView.findViewById(R.id.user_book);
        mConnectionBookTitle = itemView.findViewById(R.id.connection_book);

    }

    @Override
    public void onClick(View view) {

    }
}
