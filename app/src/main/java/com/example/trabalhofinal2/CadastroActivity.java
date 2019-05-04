package com.example.trabalhofinal2;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class CadastroActivity extends AppCompatActivity implements LocationListener {

    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private EditText edt_nome, edt_latitude, edt_longitude, edt_endereco;
    private ImageView imageview;
    private Button btn_foto, btn_salvar;
    private Bitmap imageBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        edt_latitude = findViewById(R.id.edt_latitude);
        edt_longitude = findViewById(R.id.edt_longitude);
        edt_nome = findViewById(R.id.edt_nome);
        edt_endereco = findViewById(R.id.edt_endereco);

        imageview = findViewById(R.id.imageview);

        btn_foto = findViewById(R.id.btn_foto);
        btn_salvar = findViewById(R.id.btn_salvar);

        btn_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 1);
            }
        });

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dao dao = new Dao(CadastroActivity.this);

                byte[] imgByte = null;
                if (imageBitmap != null) {
                    imgByte = getBytes(imageBitmap);
                }

                Ponto ponto = new Ponto(edt_nome.getText().toString(), edt_latitude.getText().toString(), edt_longitude.getText().toString(), edt_endereco.getText().toString(), imgByte);

                try {
                    //aqui ele salva o ponto, pode partir daqui pra fazer as outras partes.
                    dao.addPontoTuristico(ponto);
                    Toast.makeText(CadastroActivity.this, "Ponto turístico salvo!", Toast.LENGTH_LONG).show();

                    edt_nome.setText("");
                    edt_latitude.setText("");
                    edt_longitude.setText("");
                    edt_endereco.setText("");
                    imageview.setImageBitmap(null);
//                    dao.listarPontos();
                } catch (Exception e) {
                    e.printStackTrace();

                    new AlertDialog.Builder(CadastroActivity.this)
                            .setTitle("Erro!")
                            .setMessage("Houve um erro ao salvar esse ponto turístico!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Habilitar GPS!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            assert data != null;
            Bitmap foto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            imageBitmap = foto;
            imageview.setImageBitmap(foto);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        edt_latitude.setText(String.valueOf(location.getLatitude()));
        edt_longitude.setText(String.valueOf(location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public void abrirMapa(View view) {
        startActivity(new Intent(CadastroActivity.this, MapsActivity.class));
    }
}
