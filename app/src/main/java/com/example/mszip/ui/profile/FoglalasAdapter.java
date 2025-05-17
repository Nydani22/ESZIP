package com.example.mszip.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mszip.model.FoglalasViewModel;
import com.example.mszip.R;
import java.util.List;

public class FoglalasAdapter extends RecyclerView.Adapter<FoglalasAdapter.ViewHolder> {

    private List<FoglalasViewModel> lista;
    private OnTorlesClickListener listener;

    public interface OnTorlesClickListener {
        void onTorlesClicked(String foglalasId);
    }

    public FoglalasAdapter(List<FoglalasViewModel> lista, OnTorlesClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDatum, textSzolgaltatas, textAr;
        Button btnTorles;

        public ViewHolder(View view) {
            super(view);
            textDatum = view.findViewById(R.id.textDatum);
            textSzolgaltatas = view.findViewById(R.id.textSzolgaltatas);
            textAr = view.findViewById(R.id.textAr);
            btnTorles = view.findViewById(R.id.btnTorles);
        }
    }


    public void updateList(List<FoglalasViewModel> newList) {
        this.lista = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public FoglalasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foglalas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FoglalasViewModel model = lista.get(position);
        holder.textDatum.setText(model.getDatum());
        holder.textSzolgaltatas.setText(model.getSzolgaltatasNev());
        holder.textAr.setText(model.getAr()+" Ft");

        holder.btnTorles.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTorlesClicked(model.getFoglalasId());
            }
        });
    }
}

