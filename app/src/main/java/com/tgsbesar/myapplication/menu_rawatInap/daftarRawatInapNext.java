package com.tgsbesar.myapplication.menu_rawatInap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tgsbesar.myapplication.API.transaksiLaboratoriumAPI;
import com.tgsbesar.myapplication.API.transaksiRInapAPI;
import com.tgsbesar.myapplication.R;
import com.tgsbesar.myapplication.database.Preferences;
import com.tgsbesar.myapplication.menu_laboratorium.laboratoriumNextActivity;
import com.tgsbesar.myapplication.menu_laboratorium.tampilLaboratorium;
import com.tgsbesar.myapplication.menu_rawatJalan.Dokter;
import com.tgsbesar.myapplication.menu_rawatJalan.Input;
import com.tgsbesar.myapplication.menu_rawatJalan.tampilRawatJalan;
import com.tgsbesar.myapplication.model.KelasKamar;
import com.tgsbesar.myapplication.profile.profileFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.android.volley.Request.Method.POST;

public class daftarRawatInapNext extends AppCompatActivity {

    String message,no_book,email,strDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_rawat_inap_next);
        Preferences preferences = new Preferences(daftarRawatInapNext.this.getApplicationContext());
        email = preferences.getEmailNorm();

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
                        strDate = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
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
                pesanKamar(email,kmr.tipe_kamar,kmr.fasilitas_kamar,String.valueOf(kmr.harga_kamar),strDate);
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

    public void pesanKamar(final String email, final String kelas_kamar, final String fasilitas, final String harga_kamar, final String tgl_rinap){
        //Tambahkan tambah buku disini
        RequestQueue queue = Volley.newRequestQueue(this);

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading....");
        progressDialog.setTitle("Menambahkan data transaksi");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(POST, transaksiRInapAPI.URL_ADD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    //Mengubah response string menjadi object
                    JSONObject obj = new JSONObject(response);

                    if (obj.getString("message").equals("Add Transaksi Success")) {
                        Intent intent = new Intent(daftarRawatInapNext.this, tampilRawatInap.class);
                        KelasKamar kelasKamar = new KelasKamar(kelas_kamar,fasilitas,Double.valueOf(harga_kamar));
                        intent.putExtra("KelasKamar",kelasKamar);
                        intent.putExtra("Tanggal",tgl_rinap);
                        startActivity(intent);
                    }

                    Toast.makeText(daftarRawatInapNext.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(daftarRawatInapNext.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String>  params = new HashMap<String, String>();
                params.put("kelas_kamar", kelas_kamar);
                params.put("harga_kamar", harga_kamar);
                params.put("tgl_rinap", tgl_rinap);
                params.put("email",email);

                return params;
            }

        };
        queue.add(stringRequest);
    }

}