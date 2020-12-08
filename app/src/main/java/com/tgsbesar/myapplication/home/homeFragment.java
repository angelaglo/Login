package com.tgsbesar.myapplication.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;
import com.tgsbesar.myapplication.R;
import com.tgsbesar.myapplication.database.Preferences;
import com.tgsbesar.myapplication.menu_rawatInap.daftarRawatInap;
import com.tgsbesar.myapplication.menu_laboratorium.laboratoriumActivity;
import com.tgsbesar.myapplication.menu_rawatJalan.rawatJalan;
import com.tgsbesar.myapplication.splashScreen;


public class homeFragment extends Fragment {

    CardView card_rawatJalan, card_rawatInap, card_Laboratorium;
    String norek;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        final MaterialTextView Norekam = view.findViewById(R.id.noRekam);
        Preferences preferences = new Preferences(homeFragment.this.getContext());
        norek = preferences.getEmailNorm();
        Norekam.setText("Halo, "+norek);


        //menu rawat jalan
        card_rawatJalan = view.findViewById(R.id.card_rawatJalan);
        card_rawatJalan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), rawatJalan.class);
                startActivity(i);
            }
        });

        //menu rawat inap
        card_rawatInap = view.findViewById(R.id.card_rawatInap);
        card_rawatInap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), daftarRawatInap.class);
                startActivity(i);
            }
        });

        //menu laboratorium
        card_Laboratorium = view.findViewById(R.id.card_laboratorium);
        card_Laboratorium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), laboratoriumActivity.class);
                startActivity(i);
            }
        });
        return view;
    }
}