package com.sdatakn.projeenqura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.util.List;

public class ClickItem extends AppCompatActivity implements LocationListener {

    String sehir;
    String ilce;
    String sube;
    String tipi;
    String bank_kod;
    String adresadi;
    String adres;
    String postakod;
    String of_line;
    String of_site;
    String bolge_koordinat;
    String en_yakin_atm;
    public String bestProvider;
    public Criteria criteria;
    TextView toolbar_title, sube1, ililce, btipi, enyakinatm;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LocationManager locationManager;
    TextView detaylsbut, locationbut;
    String provider;
    public double latitude;
    public double longitude;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_item);
        init();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        sehir = getIntent().getExtras().getString("sehir");
        ilce = getIntent().getExtras().getString("ilce");
        sube = getIntent().getExtras().getString("sube");
        tipi = getIntent().getExtras().getString("tipi");
        bank_kod = getIntent().getExtras().getString("bank_kod");
        adresadi = getIntent().getExtras().getString("adresadi");
        adres = getIntent().getExtras().getString("adres");
        postakod = getIntent().getExtras().getString("postakod");
        of_line = getIntent().getExtras().getString("of_line");
        of_site = getIntent().getExtras().getString("of_site");
        bolge_koordinat = getIntent().getExtras().getString("bolge_koordinat");
        en_yakin_atm = getIntent().getExtras().getString("en_yakin_atm");

        Bundle bundle = new Bundle();
        bundle.putString("sehir", sehir);
        bundle.putString("ilce", ilce);
        bundle.putString("sube", sube);
        bundle.putString("tipi", tipi);
        bundle.putString("bank_kod", bank_kod);
        bundle.putString("adresadi", adresadi);
        bundle.putString("adres", adres);
        bundle.putString("postakod", postakod);
        bundle.putString("of_line", of_line);
        bundle.putString("of_site", of_site);
        bundle.putString("bolge_koordinat", bolge_koordinat);
        bundle.putString("en_yakin_atm", en_yakin_atm);

mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        if (TextUtils.isEmpty(sube)) {
            sube = getString(R.string.jsonnull);
        }
        if (TextUtils.isEmpty(en_yakin_atm)) {
            en_yakin_atm = getString(R.string.jsonnull);
        }
        if (TextUtils.isEmpty(postakod)) {
            postakod = getString(R.string.jsonnull);
        }
        toolbar_title.setText(sube);
        sube1.setText(sube);
        ililce.setText(sehir + "/" + ilce);
        btipi.setText(tipi);
        enyakinatm.setText(en_yakin_atm);

        checkLocationPermission();

        detaylsbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
            }
        });

        locationbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the last know location from your location manager.
                if (ContextCompat.checkSelfPermission(ClickItem.this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(ClickItem.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {

                    getLocations();
                    double boylam = getLocation(ClickItem.this, adres).longitude;
                    double enlem = getLocation(ClickItem.this, adres).latitude;
                    String uri = "https://maps.google.com/maps?saddr=" + latitude + "," + longitude+ "&daddr=" + enlem + "," + boylam;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        ex.printStackTrace();
                        try {
                            Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            startActivity(unrestrictedIntent);
                        } catch (ActivityNotFoundException innerEx) {
                            Toast.makeText(ClickItem.this, "Please install a maps application", Toast.LENGTH_LONG).show();
                            innerEx.printStackTrace();
                        }
                    }
                } else {
                    checkLocationPermission();
                }
            }
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = findViewById(R.id.toolbar_title);
        sube1 = findViewById(R.id.sehir1);
        ililce = findViewById(R.id.ilce);
        btipi = findViewById(R.id.yatirma);
        enyakinatm = findViewById(R.id.atmdeger);
        detaylsbut = findViewById(R.id.detayls);
        locationbut = findViewById(R.id.location);
    }

    protected void getLocations() {
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

        //You can still do this if you like, you might get lucky:
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
            //    Log.e("TAG", "Hata");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
          //      Toast.makeText(ClickItem.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
            }
            else{
                locationManager.requestLocationUpdates(bestProvider, 1000, 0,  this);
            }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ClickItem.this,MainActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.backmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.backs:
                Intent intent = new Intent(ClickItem.this,MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPopup(){
        LayoutInflater inflater = (LayoutInflater) ClickItem.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.detaylspopup,null);
        ((TextView)layout.findViewById(R.id.sehirp)).setText(sehir +"/"+ilce);
        ((TextView)layout.findViewById(R.id.subep)).setText(sube);
        ((TextView)layout.findViewById(R.id.tipp)).setText(tipi);
        ((TextView)layout.findViewById(R.id.bankp)).setText(bank_kod);
        ((TextView)layout.findViewById(R.id.adresadip)).setText(adresadi);
        ((TextView)layout.findViewById(R.id.adresp)).setText(adres);
        ((TextView)layout.findViewById(R.id.postakodp)).setText(postakod);
        ((TextView)layout.findViewById(R.id.of_linep)).setText(of_line);
        ((TextView)layout.findViewById(R.id.of_sitep)).setText(of_site);
        ((TextView)layout.findViewById(R.id.bolge_koordinatp)).setText(bolge_koordinat);
        ((TextView)layout.findViewById(R.id.en_yakin_atmp)).setText(en_yakin_atm);

        float uzunluk=ClickItem.this.getResources().getDisplayMetrics().widthPixels;
        float uzunluk1=ClickItem.this.getResources().getDisplayMetrics().density;
        final PopupWindow pw = new PopupWindow(layout, (int)uzunluk, (int)uzunluk1*400, true);
        ((ImageView) layout.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        pw.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pw.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });
        pw.setOutsideTouchable(true);
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

    public LatLng getLocation(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }
        return p1;
    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.okey, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ActivityCompat.requestPermissions(ClickItem.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    startActivity(new Intent(ClickItem.this,MainActivity.class));

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(provider, 400, 1, (LocationListener) this);
                    }

                } else {
                    Toast.makeText(ClickItem.this,R.string.reddedildi,Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        locationManager.removeUpdates(this);

        //open the map:
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Toast.makeText(ClickItem.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);

    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}