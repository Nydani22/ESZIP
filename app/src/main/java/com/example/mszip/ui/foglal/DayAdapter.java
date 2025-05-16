package com.example.mszip.ui.foglal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.mszip.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private final List<String> napok;

    public DayAdapter(List<String> napok) {
        this.napok = napok;
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        public TextView napText;

        public DayViewHolder(View view) {
            super(view);
            napText = view.findViewById(R.id.day_text); // item_day.xml fájlban kell léteznie
        }
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        String nap = napok.get(position);
        holder.napText.setText(nap);
    }

    @Override
    public int getItemCount() {
        return napok.size();
    }
}

