package com.tgsbesar.myapplication.menu_laboratorium;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import com.tgsbesar.myapplication.R;
import com.tgsbesar.myapplication.databinding.ActivityLaboratoriumBinding;
import com.tgsbesar.myapplication.menu_rawatJalan.SpesialisAdapter;
import com.tgsbesar.myapplication.menu_rawatJalan.Tampil_Dokter;
import com.tgsbesar.myapplication.menu_rawatJalan.rawatJalan;
import com.tgsbesar.myapplication.model.Laboratorium;

import java.util.ArrayList;

public class laboratoriumActivity extends AppCompatActivity {
    private ArrayList<Laboratorium> listLab;
    private RecyclerView recyclerView;
    private recyclerViewLaboratorium adapter;
    private static ActivityLaboratoriumBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_laboratorium);

        //get data laboratorium
        listLab = new daftarLaboratorium().LABORATORIUM;

        //recycler view
        RecyclerView recyclerView = binding.recyclerViewLaboratorium;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new recyclerViewLaboratorium(listLab);
        recyclerView.setAdapter(adapter);


    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
}