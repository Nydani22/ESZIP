package com.example.mszip.ui.info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.mszip.R;
import com.example.mszip.databinding.FragmentInfoBinding;
import com.example.mszip.model.service.Service;

public class InfoFragment extends Fragment {
    private FragmentInfoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        LinearLayout containerLayout = binding.linearLayoutContainer;

        InfoViewModel viewModel = new ViewModelProvider(this).get(InfoViewModel.class);
        viewModel.getServices().observe(getViewLifecycleOwner(), services -> {
            //containerLayout.removeAllViews();
            int delay = 0;
            for (Service service : services) {
                View cardView = inflater.inflate(R.layout.item_service_card, containerLayout, false);

                TextView nameText = cardView.findViewById(R.id.serviceName);
                TextView priceText = cardView.findViewById(R.id.servicePrice);
                TextView timeText = cardView.findViewById(R.id.serviceTime);
                Button bookButton = cardView.findViewById(R.id.bookButton);

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_slide_in_left);
                animation.setStartOffset(delay);
                cardView.startAnimation(animation);

                nameText.setText(service.name);
                priceText.setText("Ár: " + service.price);
                timeText.setText("Időtartam: " + service.time);
                bookButton.setOnClickListener(v -> {
                    //Toast.makeText(getContext(), "Foglalva: " + service.name, Toast.LENGTH_SHORT).show();
                });
                delay += 100;

                containerLayout.addView(cardView);
            }
        });




        return root;
    }
}
