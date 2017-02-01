package com.example.android.mybook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.R.attr.author;
import static android.R.attr.childIndicatorRight;


/**
 * Created by gongkai on 2017/1/31.
 */

public class BookListAdapter extends ArrayAdapter<Book> {
    private Context mContext = null;

    public BookListAdapter(Context context, ArrayList<Book> objects) {
        super(context, 0, objects);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_item, parent, false);
        }

        LinearLayout bookItem = (LinearLayout) convertView.findViewById(R.id.book_item);
        ImageView bookCover = (ImageView) bookItem.findViewById(R.id.book_cover);
        TextView bookTitle = (TextView) bookItem.findViewById(R.id.book_title);
        TextView bookPublishTime = (TextView) bookItem.findViewById(R.id.book_publish_time);
        TextView bookAuthor = (TextView) bookItem.findViewById(R.id.book_authors);

        Book currentBook = getItem(position);

        // Set Thumbnail for book
        if (currentBook.hasThumbnail()) {
            // Set Cover for Book
            bookCover.setImageBitmap(currentBook.getThumbnail());
        } else {
            if (!currentBook.getTitle().equals(mContext.getString(R.string.error_notice_title))) {
                // Set Default Cover Image for Book
                bookCover.setImageResource(R.drawable.default_cover);
            }
            else{
                // Error Notice
                // Set Image Size to Zero
                ViewGroup.LayoutParams layoutParams = bookCover.getLayoutParams();
                layoutParams.width = 0;
                layoutParams.height = 0;
                bookCover.setLayoutParams(layoutParams);
            }
        }

        // Set Title for Book
        if (currentBook.hasTitle())
            bookTitle.setText(currentBook.getTitle());

        // Set Publish Time for Book
        if (currentBook.hasPublishTime())
            bookPublishTime.setText(currentBook.getPublishTime());

        // Author List
        if (currentBook.hasAuthor())
            bookAuthor.setText(currentBook.getAuthors().get(0));

        return convertView;
    }
}
