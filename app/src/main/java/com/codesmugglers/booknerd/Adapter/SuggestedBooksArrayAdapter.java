package com.codesmugglers.booknerd.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codesmugglers.booknerd.Model.SuggestedBook;
import com.codesmugglers.booknerd.R;

import java.util.List;

public class SuggestedBooksArrayAdapter extends ArrayAdapter<SuggestedBook> {
    Context context;

    public SuggestedBooksArrayAdapter(Context context, int resourceId, List<SuggestedBook> books) {
        super(context, resourceId, books);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        SuggestedBook bookItem = getItem(position);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.suggested_book_item, parent, false);
        }
        TextView title = convertView.findViewById(R.id.title);
        TextView author = convertView.findViewById(R.id.author);

        title.setText(bookItem.getBook().getTitle());
        author.setText(bookItem.getBook().getAuthor());

        return convertView;
    }
}
