package com.example.mszip.ui.foglal;

import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.mszip.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.mszip.model.idopont.Idopont;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    public interface OnDayClickListener {
        void onDayClick(String day, Idopont idopont);
    }

    private List<String> days;
    private Set<String> foglaltNapok;
    private Set<String> elerhetoNapok = new HashSet<>();
    private String selectedDate = null;
    private final OnDayClickListener listener;


    private Map<String, Idopont> napIdopontMap = new HashMap<>();

    public DayAdapter(List<String> days, Set<String> foglaltNapok, OnDayClickListener listener) {
        this.days = days;
        this.foglaltNapok = foglaltNapok != null ? foglaltNapok : new HashSet<>();
        this.listener = listener;
    }

    public void setElerhetoNapok(Set<String> elerhetoNapok) {
        this.elerhetoNapok = elerhetoNapok != null ? elerhetoNapok : new HashSet<>();
    }

    public void setFoglaltNapok(Set<String> foglaltNapok) {
        this.foglaltNapok = foglaltNapok != null ? foglaltNapok : new HashSet<>();
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void setNapIdopontMap(Map<String, Idopont> map) {
        this.napIdopontMap = map != null ? map : new HashMap<>();
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
        boolean foglalt = foglaltNapok.contains(day);
        boolean elerheto = elerhetoNapok.contains(day);
        boolean selected = day.equals(selectedDate);
        holder.bind(day, elerheto, foglalt, selected);
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

        void bind(String fullDate, boolean elerheto, boolean foglalt, boolean selected) {
            String napSzam = fullDate.substring(fullDate.length() - 2); // "dd"
            textView.setText(napSzam);

            boolean kattinthato = elerheto && !foglalt;

            textView.setEnabled(kattinthato);
            boolean darkMode = (itemView.getContext().getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

            int normalColor = darkMode ? Color.WHITE : Color.BLACK;

            textView.setTextColor(kattinthato ? (selected ? Color.WHITE : normalColor) : Color.LTGRAY);
            textView.setBackgroundColor(selected ? Color.BLUE : Color.TRANSPARENT);

            itemView.setOnClickListener(v -> {
                if (kattinthato) {
                    Idopont idopont = napIdopontMap.get(fullDate);
                    listener.onDayClick(fullDate, idopont);
                }
            });
        }
    }
}
