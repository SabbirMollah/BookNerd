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

import com.codesmugglers.booknerd.Model.Chat;
import com.codesmugglers.booknerd.R;

import java.util.List;

public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {



    private List<Chat> chats;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

    }
}
