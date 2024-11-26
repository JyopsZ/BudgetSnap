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

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.TransactionViewHolder> {

    private List<AccountList> AccountList;

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

    public AccountAdapter(List<AccountList> AccountList) {
        this.AccountList = AccountList;
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
        AccountList accountList = AccountList.get(position);
        holder.transactionName.setText(accountList.getName());
        holder.transactionDate.setText(accountList.getDate());
        holder.transactionAmount.setText(accountList.getAmount());
        holder.transactionCategory.setText(accountList.getCategory());
    }

    @Override
    public int getItemCount() {
        return AccountList.size();
    }
}
