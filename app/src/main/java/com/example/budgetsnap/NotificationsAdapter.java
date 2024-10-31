package com.example.budgetsnap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView reminderDate;
        public TextView reminderTitle;
        public TextView reminderMessage;

        public ReminderViewHolder(View itemView) {
            super(itemView);
            reminderDate = itemView.findViewById(R.id.reminder_date);
            reminderTitle = itemView.findViewById(R.id.cat_title);
            reminderMessage = itemView.findViewById(R.id.cat_date);
        }
    }

    public NotificationsAdapter(List<Reminder> reminders) {
        this.reminderList = reminders;
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.reminderDate.setText(reminder.getDate());
        holder.reminderTitle.setText(reminder.getTitle());
        holder.reminderMessage.setText(reminder.getMessage());
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }
}

