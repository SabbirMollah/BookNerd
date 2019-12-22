package com.codesmugglers.booknerd.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesmugglers.booknerd.Model.Connection;
import com.codesmugglers.booknerd.R;
import com.codesmugglers.booknerd.ViewHolders.ConnectionViewHolder;
import com.codesmugglers.booknerd.ViewHolders.OwnedBookViewHolders;

import java.util.List;

public class ConnectionAdapter  extends RecyclerView.Adapter<ConnectionViewHolder> {
    private List<Connection> connections;
    private Context context;

    public ConnectionAdapter(List<Connection> connections, Context context) {
        this.connections = connections;
        this.context = context;
    }

    @NonNull
    @Override
    public ConnectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.connected_user_item,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ConnectionViewHolder rcv = new ConnectionViewHolder(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectionViewHolder holder, int position) {
        holder.mName.setText(connections.get(position).getConnectedUser().getName());
        holder.mGender.setText(connections.get(position).getConnectedUser().getGender());
        holder.mCity.setText(connections.get(position).getConnectedUser().getCity());
        holder.mConnectionBookTitle.setText(connections.get(position).getConnectedUsersBook().getTitle());
        holder.mUserBookTitle.setText(connections.get(position).getUsersBook().getTitle());
    }

    @Override
    public int getItemCount() {
        return this.connections.size();
    }
}
