package com.tgsbesar.myapplication.menu_laboratorium;

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
import com.tgsbesar.myapplication.R;
import com.tgsbesar.myapplication.database.Preferences;
import com.tgsbesar.myapplication.menu_rawatInap.daftarRawatInapNext;
import com.tgsbesar.myapplication.menu_rawatJalan.Dokter;
import com.tgsbesar.myapplication.menu_rawatJalan.Input;
import com.tgsbesar.myapplication.menu_rawatJalan.tampilRawatJalan;
import com.tgsbesar.myapplication.model.Laboratorium;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.android.volley.Request.Method.POST;

public class laboratoriumNextActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    String jam, no_booking,email,strDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laboratorium_next);
        Preferences preferences = new Preferences(laboratoriumNextActivity.this.getApplicationContext());
        email = preferences.getEmailNorm();

        Laboratorium lab = (Laboratorium) getIntent().getSerializableExtra("Laboratorium") ;


        Calendar calendar = Calendar.getInstance();
        int Day = calendar.get(Calendar.DAY_OF_MONTH);
        int Month = calendar.get(Calendar.MONTH);
        int Year = calendar.get(Calendar.YEAR);

        Button btnDate = findViewById(R.id.buttonPickDateCheckUp);
        TextView tanggal = findViewById(R.id.tv_dateCheckUp);
        Button btnSend = findViewById(R.id.buttonSend);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        strDate = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                        tanggal.setText(strDate);
                    }
                },Year, Month, Day);

                dialog.show(getFragmentManager(),"DatePickerDialog");
            }
        });

        AutoCompleteTextView drop = findViewById(R.id.dropdown_fill_CheckUp);
        List<String> categories = new ArrayList<String>();
        categories.add("09:00 - 11:00");
        categories.add("13:00 - 15:00");
        categories.add("17:00 - 19:00");
        categories.add("21:00 - 22:00");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, categories);
        drop.setAdapter(adapter);
        drop.setOnItemClickListener(this);



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pesanLab(email,lab.getKategori(),String.valueOf(lab.harga_test),strDate,jam, lab.getDeskripsi());
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        jam = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), "Jam " +jam, Toast.LENGTH_SHORT).show();
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

    public void pesanLab(final String email, final String paket_checkUp, final String harga_paket, final String tgl_checkUp, final String jam_checkUp, final String deskripsi_checkUp){
        //Tambahkan tambah buku disini
        RequestQueue queue = Volley.newRequestQueue(this);

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading....");
        progressDialog.setTitle("Menambahkan data transaksi");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(POST, transaksiLaboratoriumAPI.URL_ADD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println(response.toString());
                progressDialog.dismiss();
                try {
                    //Mengubah response string menjadi object
                    JSONObject obj = new JSONObject(response);

                    if (obj.getString("message").equals("Add TransaksiLaboratorium Success")) {
                        Laboratorium lab = new Laboratorium(paket_checkUp,deskripsi_checkUp,Double.valueOf(harga_paket));
                        Intent intent = new Intent(laboratoriumNextActivity.this, tampilLaboratorium.class);
                        intent.putExtra("Laboratorium", lab);
                        intent.putExtra("Tanggal",tgl_checkUp);
                        intent.putExtra("Jam",jam_checkUp);
                        startActivity(intent);
                    }

                    Toast.makeText(laboratoriumNextActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(laboratoriumNextActivity.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String>  params = new HashMap<String, String>();
                params.put("paket_checkUp", paket_checkUp);
                params.put("harga_paket", harga_paket);
                params.put("tgl_checkUp", String.valueOf(tgl_checkUp));
                params.put("jam_checkUp",jam_checkUp);
                params.put("email",email);
                params.put("deskripsi_checkUp",deskripsi_checkUp);



                return params;
            }

        };
        queue.add(stringRequest);
    }
}