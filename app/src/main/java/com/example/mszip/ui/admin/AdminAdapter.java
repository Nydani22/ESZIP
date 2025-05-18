package com.example.mszip.ui.admin;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mszip.R;
import com.example.mszip.model.idopont.Idopont;
import com.example.mszip.model.service.Service;
import com.example.mszip.service.AdminService;
import com.example.mszip.service.IdopontWithService;

import java.util.List;

public class AdminAdapter<T> extends RecyclerView.Adapter<AdminAdapter.ViewHolder> {
    public interface OnDeleteListener<T> {
        void onDelete(T item);
    }

    public interface OnEditListener<T> {
        void onEdit(T item);
    }
    private List<T> items;

    private final AdminService adminService = new AdminService();
    private final String format;
    private final OnDeleteListener<T> onDeleteListener;
    private final OnEditListener<T> onEditListener;

    public AdminAdapter(List<T> items, String format, OnDeleteListener<T> onDeleteListener, OnEditListener<T> onEditListener) {
        this.items = items;
        this.format = format;
        this.onDeleteListener = onDeleteListener;
        this.onEditListener=onEditListener;
    }

    public AdminAdapter(List<T> items, String format, OnDeleteListener<T> onDeleteListener) {
        this.items = items;
        this.format = format;
        this.onDeleteListener = onDeleteListener;
        this.onEditListener = item -> {};
    }

    public void updateList(List<T> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_idopont, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T item = items.get(position);

        if (item instanceof Idopont) {
            Idopont ido = (Idopont) item;
            holder.text1.setText("Betöltés...");
            holder.text2.setText("");

            adminService.getIdopontWithServiceNameById(ido.getId(), new AdminService.Callback<>() {
                @Override
                public void onSuccess(IdopontWithService result) {
                    holder.text1.setText("Dátum: " + ido.getDate());
                    holder.text2.setText("Szolgáltatás: " + result.getServiceName());
                    holder.buttonEdit.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Exception e) {
                    holder.text1.setText("Hiba történt");
                    holder.text2.setText("");
                    holder.buttonEdit.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            });
        } else if (item instanceof Service) {
            Service service = (Service) item;
            String text = String.format(format, service);
            holder.text1.setText(text);
            holder.text2.setText("");
            holder.buttonEdit.setVisibility(View.VISIBLE);


        } else {
            holder.text1.setText(item.toString());
            holder.text2.setText("");
        }

        holder.buttonDelete.setOnClickListener(v -> onDeleteListener.onDelete(item));
        holder.buttonEdit.setOnClickListener(v -> {
            if (item instanceof Service && onEditListener != null) {
                onEditListener.onEdit(item);
            }
        });

    }




    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        TextView text2;
        View buttonDelete;

        View buttonEdit;

        ViewHolder(View view) {
            super(view);
            text1 = view.findViewById(R.id.text1);
            text2 = view.findViewById(R.id.text2);
            buttonDelete = view.findViewById(R.id.buttonDelete);
            buttonEdit=view.findViewById(R.id.buttonEdit);
        }
    }
}
