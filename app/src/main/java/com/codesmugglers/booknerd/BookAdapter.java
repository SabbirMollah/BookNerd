package com.codesmugglers.booknerd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<Book> bookList;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public BookAdapter(Context context, ArrayList<Book> bookList){
        this.mContext = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.book_search_item, parent, false);
        return new BookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Book book = bookList.get(position);

        String coverUrl = book.getCoverUrl();
        String title = book.getTitle();
        String authorName = book.getAuthorName();

        ((BookViewHolder)holder).mAuthor.setText((authorName));
        ((BookViewHolder)holder).mTitle.setText((title));
        Picasso.get().load(book.getCoverUrl()).fit().centerInside().into(((BookViewHolder)holder).mCover);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        public ImageView mCover;
        public TextView mTitle;
        public TextView mAuthor;


        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            mCover = itemView.findViewById(R.id.cover);
            mTitle = itemView.findViewById(R.id.title);
            mAuthor = itemView.findViewById(R.id.author_name);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(position);
                    }
                }
            });
        }
    }

}
