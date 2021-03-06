package com.tgsbesar.myapplication.menu_rawatInap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tgsbesar.myapplication.R;
import com.tgsbesar.myapplication.menu_rawatJalan.Dokter;
import com.tgsbesar.myapplication.menu_rawatJalan.Input;
import com.tgsbesar.myapplication.menu_rawatJalan.tampilRawatJalan;
import com.tgsbesar.myapplication.model.KelasKamar;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class daftarRawatInapNext extends AppCompatActivity {

    String message,no_book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_rawat_inap_next);

        KelasKamar kmr = (KelasKamar) getIntent().getSerializableExtra("KelasKamar"); //1

        Calendar calendar = Calendar.getInstance();
        int Day = calendar.get(Calendar.DAY_OF_MONTH);
        int Month = calendar.get(Calendar.MONTH);
        int Year = calendar.get(Calendar.YEAR);

        Button btnDate = findViewById(R.id.buttonPickDate_rawatInap);
        TextView text = findViewById(R.id.tv_date_rawatInap);
        Button btnSend = findViewById(R.id.buttonSend);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        String strDate = dayOfMonth + "/" + (monthOfYear+1) + "/" + year;
                        text.setText(strDate);
                    }
                },Year, Month, Day);

                dialog.show(getFragmentManager(),"DatePickerDialog");
            }
        });

        no_book=String.valueOf(getRandomNumberInRange(1000,3000));
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(daftarRawatInapNext.this, tampilRawatInap.class);
                intent.putExtra("KelasKamar",kmr);
                intent.putExtra("Tanggal",text.getText().toString());
                intent.putExtra("no_book",no_book);
                startActivity(intent);
            }
        });
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

    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

}