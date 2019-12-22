package com.codesmugglers.booknerd.ViewHolders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesmugglers.booknerd.ChatActivity;
import com.codesmugglers.booknerd.Model.Connection;
import com.codesmugglers.booknerd.R;

import java.util.List;

public class ConnectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mName;
    public TextView mGender;
    public TextView mCity;
    public TextView mConnectionBookTitle;
    public TextView mUserBookTitle;

    private List<Connection> connections;

    public ConnectionViewHolder(@NonNull View itemView, List<Connection> connections) {
        super(itemView);
        itemView.setOnClickListener(this);

        this.connections = connections;

        mName = itemView.findViewById(R.id.name);
        mGender = itemView.findViewById(R.id.gender);
        mCity = itemView.findViewById(R.id.city);
        mUserBookTitle = itemView.findViewById(R.id.user_book);
        mConnectionBookTitle = itemView.findViewById(R.id.connection_book);


    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("connectionId",connections.get(getAdapterPosition()).getConnectedUserId());
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);
    }
}
