package com.example.budgetsnap;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecentTransactionsAdapter extends RecyclerView.Adapter<RecentTransactionsAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView transactionName;
        public TextView transactionDate;
        public TextView transactionAmount;
        public TextView transactionCategory;
        public ImageView transactionImage;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionName = itemView.findViewById(R.id.name);
            transactionDate = itemView.findViewById(R.id.date);
            transactionAmount = itemView.findViewById(R.id.amount);
            transactionCategory = itemView.findViewById(R.id.category);
            transactionImage = itemView.findViewById(R.id.imageView); // Ensure this matches your XML
        }
    }

    public RecentTransactionsAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.template_transaction_profile, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.transactionName.setText(transaction.getName());
        holder.transactionDate.setText(transaction.getDate());
        holder.transactionAmount.setText(transaction.getAmount());
        holder.transactionCategory.setText(transaction.getCategory());
        holder.transactionImage.setImageBitmap(BitmapFactory.decodeByteArray(
                transaction.getImage(), 0, transaction.getImage().length));

        // Set text color based on transaction type
        holder.transactionAmount.setTextColor(
                transaction.isPositive() ? Color.GREEN : Color.RED);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}
