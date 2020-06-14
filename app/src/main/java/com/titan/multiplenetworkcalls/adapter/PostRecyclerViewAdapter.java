package com.titan.multiplenetworkcalls.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.titan.multiplenetworkcalls.R;
import com.titan.multiplenetworkcalls.models.Post;

import java.util.ArrayList;
import java.util.List;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder> {

    private List<Post> registers;


    public PostRecyclerViewAdapter() {
        registers = new ArrayList<>();
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_layout, parent, false);

        PostViewHolder postViewHolder = new PostViewHolder(view);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post item = registers.get(position);

        holder.txtId.setText(item.getId());
        holder.txtuserId.setText(item.getUserId());
        holder.txtTitle.setText(item.getTitle());

    }

    @Override
    public int getItemCount() {
        return registers.size();
    }

    public void setData(List<Post> data) {
        this.registers.addAll(data);
        notifyDataSetChanged();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView txtuserId;
        public TextView txtId;
        public TextView txtTitle;
        public CardView cardView;

        public PostViewHolder(View view) {
            super(view);

            txtuserId = view.findViewById(R.id.txtuserId);
            txtId = view.findViewById(R.id.txtId);
            txtTitle = view.findViewById(R.id.txtTitle);
            cardView = view.findViewById(R.id.cardView);
        }
    }
}