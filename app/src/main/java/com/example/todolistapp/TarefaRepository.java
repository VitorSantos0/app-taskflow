package com.example.todolistapp;

import android.content.Context;

import java.util.ArrayList;

public class TarefaRepository {

    private static TarefaRepository instance;
    private final DatabaseHelper db;

    private TarefaRepository(Context ctx) {
        db = new DatabaseHelper(ctx.getApplicationContext());
    }

    public static TarefaRepository get(Context ctx) {
        if (instance == null) instance = new TarefaRepository(ctx);
        return instance;
    }

    public long inserir(String descricao, String dataAlvo) {
        return db.inserirTarefa(descricao, dataAlvo);
    }

    public ArrayList<Tarefa> porDia(String dataAlvo) {
        return db.listarPorDia(dataAlvo);
    }

    public ArrayList<Tarefa> porSemana(String inicio, String fim) {
        return db.listarEntreDatas(inicio, fim);
    }

    public ArrayList<Tarefa> porMes(String anoMes) {
        return db.listarPorMes(anoMes);
    }

    public void concluir(long id, boolean concluida) {
        db.atualizarConcluida(id, concluida);
    }

    public void deletar(long id) {
        db.deletarTarefa(id);
    }
}