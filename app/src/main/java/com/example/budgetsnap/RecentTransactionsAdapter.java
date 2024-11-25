package com.example.budgetsnap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionName = itemView.findViewById(R.id.name);
            transactionDate = itemView.findViewById(R.id.date);
            transactionAmount = itemView.findViewById(R.id.amount);
            transactionCategory = itemView.findViewById(R.id.category);
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
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}
