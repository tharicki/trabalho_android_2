package com.example.trabalhofinal2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class Dao extends SQLiteOpenHelper {

    private SQLiteDatabase db = getReadableDatabase();

    public Dao(Context context) {
        super(context, "pontos", null, 1);
    }

    void addPontoTuristico(Ponto pontoTuristico) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put("nome", pontoTuristico.getNome());
        values.put("latitude", pontoTuristico.getLatitude());
        values.put("longitude", pontoTuristico.getLongitude());
        values.put("endereco", pontoTuristico.getEndereco());
        values.put("imagem", pontoTuristico.getImage());

        db.insert("pontos", null, values);
    }

    List<Ponto> listarPontos() {
        String sql = "SELECT * FROM pontos";
        Cursor c = db.rawQuery(sql, null);
        List<Ponto> pontos = new ArrayList<>();
        while (c.moveToNext()) {
            Ponto pontoTuristico = new Ponto();
            pontoTuristico.setNome(c.getString(c.getColumnIndex("nome")));
            pontoTuristico.setLatitude(c.getString(c.getColumnIndex("latitude")));
            pontoTuristico.setLongitude(c.getString(c.getColumnIndex("longitude")));
            pontoTuristico.setEndereco(c.getString(c.getColumnIndex("endereco")));
            pontoTuristico.setImage(c.getBlob(c.getColumnIndex("imagem")));

            pontos.add(pontoTuristico);
        }
        c.close();

        return pontos;
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDb) {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS pontos(nome VARCHAR, latitude VARCHAR, longitude VARCHAR, endereco VARCHAR, imagem BLOB);";
        if (db == null) {
            db = sqlDb;
        }
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
