package com.ujjwalkumar.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

public class ScannerActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 642;

    private CodeScannerView scannerView;
    private ImageView imageViewBack;
    private TextView textViewResult;

    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        imageViewBack = findViewById(R.id.imageViewBack);
        textViewResult = findViewById(R.id.textViewResult);
        scannerView = findViewById(R.id.scannerView);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        }

        mCodeScanner = new CodeScanner(this, scannerView);

        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());

        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            String res = result.getText();
            textViewResult.setText(res);
            int i = res.indexOf("#");
            if(i!=-1) {
                String id = res.substring(0, i);
                String password = res.substring(i+1);
                Intent in = new Intent();
                in.setAction(Intent.ACTION_VIEW);
                in.putExtra("id", id);
                in.putExtra("password", password);
                in.setClass(getApplicationContext(), RoomActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
                finish();
            }
            else {
                Toast.makeText(ScannerActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
            }
        }));

        mCodeScanner.setErrorCallback(error -> Toast.makeText(ScannerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show());

        imageViewBack.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), RoomActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE && grantResults.length>0 && grantResults[0]!=-1) {
            mCodeScanner.startPreview();
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}