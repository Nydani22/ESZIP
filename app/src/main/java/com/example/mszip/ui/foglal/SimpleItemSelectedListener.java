package com.example.mszip.ui.foglal;
import android.view.View;
import android.widget.AdapterView;

public abstract class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        onItemSelected(position);
    }
    public abstract void onItemSelected(int position);
}