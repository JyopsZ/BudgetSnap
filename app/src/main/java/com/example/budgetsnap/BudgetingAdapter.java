package com.example.budgetsnap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BudgetingAdapter extends RecyclerView.Adapter<BudgetingAdapter.BudViewHolder> {

    private List<Budget> budList;

    public static class BudViewHolder extends RecyclerView.ViewHolder {
        public TextView budTitle;
        public TextView budExpenses;
        public TextView budRemaining;
        public ImageView budImage;

        public BudViewHolder(View itemView) {
            super(itemView);
            budTitle = itemView.findViewById(R.id.bud_title);
            budExpenses = itemView.findViewById(R.id.bud_expense);
            budRemaining = itemView.findViewById(R.id.bud_remain);
            budImage = itemView.findViewById(R.id.bud_image); // Initialize budImage
        }
    }

    public BudgetingAdapter(List<Budget> budList) {
        this.budList = budList;
    }

    @Override
    public BudViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.template_budgeting, parent, false);
        return new BudViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BudViewHolder holder, int position) {
        Budget bud = budList.get(position);

        // Set text fields
        holder.budTitle.setText(bud.getTitle());
        holder.budExpenses.setText("Php: " + bud.getExpenses());
        holder.budRemaining.setText("Php: " + bud.getRemaining());

        // Set image based on category
        switch (bud.getCategory()) {
            case "Food":
                holder.budImage.setImageResource(R.drawable.c_food);
                break;
            case "Transportation":
                holder.budImage.setImageResource(R.drawable.c_transportation);
                break;
            case "Entertainment":
                holder.budImage.setImageResource(R.drawable.c_leisure);
                break;
            case "Utilities":
                holder.budImage.setImageResource(R.drawable.c_bills);
                break;
            case "Health":
                holder.budImage.setImageResource(R.drawable.c_health);
                break;
            case "Home":
                holder.budImage.setImageResource(R.drawable.c_home);
                break;
            case "Savings":
                holder.budImage.setImageResource(R.drawable.c_savings);
                break;
            case "Others":
                holder.budImage.setImageResource(R.drawable.c_others);
                break;
            default:
                holder.budImage.setImageResource(R.drawable.c_others); // Default image for unmatched categories
                break;
        }
    }

    @Override
    public int getItemCount() {
        return budList.size();
    }
}
