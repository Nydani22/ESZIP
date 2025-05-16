package com.example.mszip.ui.foglal;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.mszip.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Set;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    public interface OnDayClickListener {
        void onDayClick(String day);
    }

    private List<String> days;
    private Set<String> foglaltNapok;
    private String selectedDate = null;
    private final OnDayClickListener listener;

    public DayAdapter(List<String> days, Set<String> foglaltNapok, OnDayClickListener listener) {
        this.days = days;
        this.foglaltNapok = foglaltNapok;
        this.listener = listener;
    }

    public void setFoglaltNapok(Set<String> foglaltNapok) {
        this.foglaltNapok = foglaltNapok;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        String day = days.get(position);
        holder.bind(day, foglaltNapok.contains(day), day.equals(selectedDate));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    class DayViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_day);
        }

        void bind(String day, boolean foglalt, boolean selected) {
            String dayNumber = day.substring(day.length() - 2);
            textView.setText(dayNumber);

            if (foglalt) {
                textView.setTextColor(Color.RED);
                textView.setEnabled(false);
            } else {
                textView.setTextColor(selected ? Color.WHITE : Color.BLACK);
                textView.setBackgroundColor(selected ? Color.BLUE : Color.TRANSPARENT);
                textView.setEnabled(true);
            }

            itemView.setOnClickListener(v -> {
                if (!foglalt) {
                    listener.onDayClick(day);
                }
            });
        }
    }
}


