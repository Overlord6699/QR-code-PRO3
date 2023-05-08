package com.example.qr_code;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    final int QR_WIDTH = 800, QR_HEIGHT = 800;
    final String ERROR_NAME = "Ошибка!", ERROR_TEXT = "Введите текст";

    EditText editInput;
    Button GenerateBtn;
    FloatingActionButton scanBtn;
    ImageView QR_Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editInput = findViewById(R.id.edit_input);
        GenerateBtn = findViewById(R.id.bt_generate);
        scanBtn = findViewById(R.id.fab_scan);
        QR_Image = findViewById(R.id.iv_qr);

        //генерация
        GenerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateQR();
            }
        });

        //скан
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQR();
            }
        });
    }

    private void scanQR() {
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(true);
        options.setBeepEnabled(false);
        options.setCaptureActivity(CaptureAct.class);

        barLauncher.launch(options);
    }

    //отображение результата
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Scan result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("COPY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ClipboardManager clipboard = (ClipboardManager) MainActivity.this.getSystemService(MainActivity.this.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", result.getContents().toString());
                    clipboard.setPrimaryClip(clip);
                    dialogInterface.dismiss();

                    Toast toast = Toast.makeText(getApplicationContext(), "Text is saved!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }).show();
        }
    });

    //окно с ошибкой
    private Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(ERROR_NAME)
                .setMessage(ERROR_TEXT)
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Закрываем диалоговое окно
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    private void generateQR() {
        String text = editInput.getText().toString().trim();

        if(text.length()  == 0){
           Dialog error = onCreateDialog();
            error.show();
            return;
        }

        MultiFormatWriter writer = new MultiFormatWriter();

        try {
            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            QR_Image.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}