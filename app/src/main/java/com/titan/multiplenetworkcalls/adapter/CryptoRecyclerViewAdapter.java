package com.titan.multiplenetworkcalls.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.titan.multiplenetworkcalls.R;
import com.titan.multiplenetworkcalls.models.Crypto;

import java.util.ArrayList;
import java.util.List;

public class CryptoRecyclerViewAdapter extends RecyclerView.Adapter<CryptoRecyclerViewAdapter.CryptoViewHolder> {

    private List<Crypto.Market> marketList;


    public CryptoRecyclerViewAdapter() {
        marketList = new ArrayList<>();
    }

    @Override
    public CryptoViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_layout, parent, false);

        CryptoViewHolder cryptoViewHolder = new CryptoViewHolder(view);
        return cryptoViewHolder;
    }

    @Override
    public void onBindViewHolder(CryptoViewHolder holder, int position) {
        Crypto.Market market = marketList.get(position);

        holder.txtMarket.setText(market.market);
        holder.txtPrice.setText("$" + String.format("%.2f", Double.parseDouble(market.price)));

        if(market.coinName != null) {

            holder.txtCoin.setText(market.coinName);
            if (market.coinName.equalsIgnoreCase("eth")) {
                holder.cardView.setCardBackgroundColor(Color.GRAY);
            } else {
                holder.cardView.setCardBackgroundColor(Color.GREEN);
            }
        }
        else{
            holder.txtCoin.setText("No coin name");
            holder.cardView.setCardBackgroundColor(Color.LTGRAY);
        }

    }

    @Override
    public int getItemCount() {
        return marketList.size();
    }

    public void setData(List<Crypto.Market> data) {
        this.marketList.addAll(data);
        notifyDataSetChanged();
    }

    public class CryptoViewHolder extends RecyclerView.ViewHolder {

        public TextView txtCoin;
        public TextView txtMarket;
        public TextView txtPrice;
        public CardView cardView;

        public CryptoViewHolder(View view) {
            super(view);

            txtCoin = view.findViewById(R.id.txtCoin);
            txtMarket = view.findViewById(R.id.txtMarket);
            txtPrice = view.findViewById(R.id.txtPrice);
            cardView = view.findViewById(R.id.cardView);
        }
    }
}