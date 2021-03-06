package com.hanium.greenduks;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationInterface, NavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private GoogleMap map;
    private LocationManager locMan;
    private Location lastLocation;
    private Marker centerMarker;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ImageView iv_menu = findViewById(R.id.iv_menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        TextView toolbar_name = findViewById(R.id.tvToolbar_name);
        initializeLayout(iv_menu, drawerLayout, toolbar_name, "????????? ??????");
        setNavigationViewListener();
        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageView iv_qr = findViewById(R.id.iv_qr);
        iv_qr.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), QrScanActivity.class);
            startActivity(intent);
            finish();
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        // xml ???????????? ???????????? header ??????
        View header = navigationView.getHeaderView(0);
        // header??? ?????? ????????? ????????????
        ImageView logoutBtn = (ImageView) header.findViewById(R.id.ivNavi_logout);

        logoutBtn.setOnClickListener(v -> {
            AWSMobileClient.getInstance().signOut();
            Intent i = new Intent(MapActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });
    }

     @Override
     public void onMapReady (@NonNull GoogleMap googleMap){
        map = googleMap;
        LatLng current;
        if (getLastLocation() != null) {
            current = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
        else {
            current = new LatLng(37.56, 126.97);
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 17));

        MarkerOptions options = new MarkerOptions();
        options.position(current)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

        centerMarker = map.addMarker(options);
        //addMarker with snippet for box address
    }

    private void locationUpdate() {
        if (checkPermission()) {
            locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        }
    }

    private Location getLastLocation() {
        if (checkPermission()) {
            lastLocation = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (lastLocation != null) {
            return lastLocation;
        }
        else {
            return null;
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 17));
            centerMarker.setPosition(current);
        }
    };

    private boolean checkPermission () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grandResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationUpdate();
            } else {
                Toast.makeText(this, "?????? ?????? ????????? ???????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        startActivity(nextIntent(item, this, drawerLayout));
        return true;
    }

    public void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
    }
}