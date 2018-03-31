package com.example.ostap.popularmovie.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ostap.popularmovie.R;
import com.example.ostap.popularmovie.model.Review;

import java.util.List;

/**
 * Created by ostap on 30/03/2018.
 */

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    private List<Review> reviewList;
    private Context context;

    public ReviewListAdapter(Context context, List<Review> reviewList) {
        this.reviewList = reviewList;
        this.context = context;
    }

    @Override
    public ReviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate the custom layout
        View view = inflater.inflate(R.layout.review_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewListAdapter.ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        TextView author = holder.author;
        TextView content = holder.content;

        author.setText(review.getAuthor());
        content.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (reviewList == null) return 0;
        return reviewList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView author;
        public TextView content;

        public ViewHolder(View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.author_tv);
            content = itemView.findViewById(R.id.content_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String reviewUrl = reviewList.get(position).getUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewUrl));
            context.startActivity(intent);
        }
    }
}
