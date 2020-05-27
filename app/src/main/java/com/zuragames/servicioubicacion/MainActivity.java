package com.zuragames.servicioubicacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zuragames.servicioubicacion.services.UbicacionService;
import com.zuragames.servicioubicacion.utils.LocationHelpers;

public class MainActivity extends AppCompatActivity {

    public static final int UBICACION_REQUEST_CODE = 7432;

    Button btnStartService, btnStopService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartService = findViewById(R.id.buttonStartService);
        btnStopService = findViewById(R.id.buttonStopService);

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
            }
        });
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });

        if (LocationHelpers.checkLocation(this)) {//comprobamos servicio de localización
            if (LocationHelpers.checkPermissions(this) == LocationHelpers.FINE_AND_COARSE_PERMISSION) {//comprueba permisos de ubicacion
                //startService();
                //TODO mostrar botones
            } else {
                LocationHelpers.requestPermissions(this);
            }
        } else {
            showAlert();
        }
    }

    public void startService() {
        Intent serviceIntent = new Intent(this, UbicacionService.class);
        serviceIntent.putExtra("inputExtra", "Ejemplo servicio foreground");
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(this, UbicacionService.class);
        stopService(serviceIntent);
    }

    private void showAlert() {

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this,R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.setTitle("No hay permisos de ubicación")
                .setMessage("Anda, ve a poner los permisos de ubicación")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(myIntent, UBICACION_REQUEST_CODE);

                    }
                })
                .setNegativeButton("Mas tarde", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();

    }
}
