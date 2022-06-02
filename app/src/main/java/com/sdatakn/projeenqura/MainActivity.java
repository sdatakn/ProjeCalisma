package com.sdatakn.projeenqura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.sdatakn.projeenqura.adapter.AdapterData;
import com.sdatakn.projeenqura.model.BankModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    AdapterData adapterData;
    RecyclerView recyclerView;
    List<BankModel> bankModels;
    ProgressBar progressBar;
    AppBarLayout appBarLayout;
    SearchView menu_search_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appBarLayout = findViewById(R.id.appbar);
        Toolbar toolbar  = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        menu_search_view = findViewById(R.id.menu_search_view);

        bankModels = new ArrayList<>();

        progressBar = findViewById(R.id.simpleProgressBar);
        parsJson();
        recyclerView = findViewById(R.id.recy);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    appBarLayout.setVisibility(View.GONE);
                } else {
                    appBarLayout.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        adapterData = new AdapterData(this, bankModels);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterData);

        menu_search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<BankModel>filterModList=filter(bankModels,newText);
                adapterData.searchdata(filterModList);
                return true;
            }
        });

        KontrolInternet(this);

    }
    void parsJson(){

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "https://raw.githubusercontent.com/fatiha380/mockjson/main/bankdata", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null) {
                    for (int i=0;i<response.length();i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            BankModel bankModel = new BankModel();
                            bankModel.setSehir(jsonObject.getString("dc_SEHIR"));
                            bankModel.setIlce(jsonObject.getString("dc_ILCE"));
                            bankModel.setSube(jsonObject.getString("dc_BANKA_SUBE"));
                            bankModel.setTipi(jsonObject.getString("dc_BANKA_TIPI"));
                            bankModel.setBank_kod(jsonObject.getString("dc_BANK_KODU"));
                            bankModel.setAdresadi(jsonObject.getString("dc_ADRES_ADI"));
                            bankModel.setAdres(jsonObject.getString("dc_ADRES"));
                            bankModel.setPostakod(jsonObject.getString("dc_POSTA_KODU"));
                            bankModel.setOf_line(jsonObject.getString("dc_ON_OFF_LINE"));
                            bankModel.setOf_site(jsonObject.getString("dc_ON_OFF_SITE"));
                            bankModel.setBolge_koordinat(jsonObject.getString("dc_BOLGE_KOORDINATORLUGU"));
                            bankModel.setEn_yakin_atm(jsonObject.getString("dc_EN_YAKIM_ATM"));
                            bankModels.add(bankModel);
                            progressBar.setVisibility(View.GONE);
                            adapterData.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    Toast.makeText(MainActivity.this,R.string.errorjson,Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this,R.string.errorjson,Toast.LENGTH_LONG).show();
                error.printStackTrace();

            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    private List<BankModel>filter(List<BankModel>hi, String query){
        query=query.toLowerCase();
        final List<BankModel>filterModeList=new ArrayList<>();
        for (BankModel modal:hi){
            final String text=modal.getSehir().toLowerCase();
            if (text.startsWith(query)){
                filterModeList.add(modal);
            }
        }
        return filterModeList;
    }

    public static void KontrolInternet(Context context) {

        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        boolean isOnline = (networkInfo != null && networkInfo.isConnected());
        if(!isOnline)
            Toast.makeText(context, R.string.checkinternet, Toast.LENGTH_SHORT).show();

    }

    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}