package com.example.todolistapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME    = "todolist_v3.db";
    public static final int    DATABASE_VERSION = 1;
    public static final String TABLE_TAREFAS    = "tarefas";

    private static final SimpleDateFormat SDF_DATE =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat SDF_DATETIME =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_TAREFAS + " (" +
                        "id            INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "descricao     TEXT    NOT NULL, " +
                        "data_alvo     TEXT    NOT NULL, " +   // "yyyy-MM-dd"
                        "data_registro TEXT    NOT NULL, " +
                        "concluida     INTEGER NOT NULL DEFAULT 0" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREFAS);
        onCreate(db);
    }

    public long inserirTarefa(String descricao, String dataAlvo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("descricao",     descricao);
        v.put("data_alvo",     dataAlvo);
        v.put("data_registro", SDF_DATETIME.format(new Date()));
        v.put("concluida",     0);
        long id = db.insert(TABLE_TAREFAS, null, v);
        db.close();
        return id;
    }

    public ArrayList<Tarefa> listarPorDia(String dataAlvo) {
        return query("data_alvo = ?", new String[]{dataAlvo});
    }

    public ArrayList<Tarefa> listarEntreDatas(String inicio, String fim) {
        return query("data_alvo >= ? AND data_alvo <= ?", new String[]{inicio, fim});
    }

    public ArrayList<Tarefa> listarPorMes(String anoMes) {
        return query("data_alvo LIKE ?", new String[]{anoMes + "-%"});
    }

    private ArrayList<Tarefa> query(String selection, String[] args) {
        ArrayList<Tarefa> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_TAREFAS, null, selection, args, null, null, "data_alvo ASC, id ASC");
        if (c.moveToFirst()) {
            do {
                lista.add(fromCursor(c));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return lista;
    }

    public void atualizarConcluida(long id, boolean concluida) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("concluida", concluida ? 1 : 0);
        db.update(TABLE_TAREFAS, v, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deletarTarefa(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TAREFAS, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    private Tarefa fromCursor(Cursor c) {
        long    id       = c.getLong(c.getColumnIndexOrThrow("id"));
        String  desc     = c.getString(c.getColumnIndexOrThrow("descricao"));
        String  dataAlvo = c.getString(c.getColumnIndexOrThrow("data_alvo"));
        String  dataReg  = c.getString(c.getColumnIndexOrThrow("data_registro"));
        boolean concl    = c.getInt(c.getColumnIndexOrThrow("concluida")) == 1;
        return new Tarefa(id, desc, dataAlvo, dataReg, concl);
    }

    public static String hoje() {
        return SDF_DATE.format(new Date());
    }
}