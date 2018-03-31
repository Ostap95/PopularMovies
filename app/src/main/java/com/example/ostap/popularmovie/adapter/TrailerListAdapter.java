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

import java.util.List;


/**
 * Created by ostap on 23/03/2018.
 */

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.ViewHolder> {

    private List<String> mTrailersList;
    private Context mContext;

    public TrailerListAdapter(Context context, List<String> trailersList) {
        mContext = context;
        mTrailersList = trailersList;
    }


    @Override
    public TrailerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate the custom layout
        View view = inflater.inflate(R.layout.trailer_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerListAdapter.ViewHolder holder, int position) {
        TextView number = holder.trailer_number;
        number.setText(mContext.getString(R.string.trailer_string) + " " + Integer.toString(position + 1));

    }

    @Override
    public int getItemCount() {
        if (mTrailersList == null) return 0;
        return mTrailersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView trailer_number;

        public ViewHolder(View itemView) {
            super(itemView);

            trailer_number = itemView.findViewById(R.id.play_trailer_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailersList.get(position)));
            mContext.startActivity(intent);
        }
    }

}
