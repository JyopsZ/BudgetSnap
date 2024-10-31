package com.example.budgetsnap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CatViewHolder> {

    private List<Categories> catList;

    public static class CatViewHolder extends RecyclerView.ViewHolder {
        public TextView catDate;
        public TextView catTitle;
        public TextView catPrice;
        public TextView catView;

        public CatViewHolder(View itemView) {
            super(itemView);
            catTitle = itemView.findViewById(R.id.cat_title);
            catDate = itemView.findViewById(R.id.cat_date);
            catPrice = itemView.findViewById(R.id.cat_price);
            catView = itemView.findViewById(R.id.cat_view);
        }
    }

    public CategoriesAdapter(List<Categories> Categories) {
        this.catList = Categories;
    }

    @Override
    public CatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.template_categories, parent, false);
        return new CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CatViewHolder holder, int position) {
        Categories cat = catList.get(position);
        holder.catDate.setText(cat.getDate());
        holder.catTitle.setText(cat.getTitle());
        holder.catPrice.setText(cat.getPrice());
       // holder.catView.setText(cat.getView());
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }
}

