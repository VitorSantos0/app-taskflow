package com.example.todolistapp;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class TodoAdapter extends ArrayAdapter<Tarefa> {

    private final TarefaRepository repo;
    private final Runnable onChanged; // callback para o fragment recarregar o badge

    public TodoAdapter(Context context, ArrayList<Tarefa> lista,
                       TarefaRepository repo, Runnable onChanged) {
        super(context, 0, lista);
        this.repo      = repo;
        this.onChanged = onChanged;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_lista, parent, false);
        }

        Tarefa tarefa = getItem(position);
        if (tarefa == null) return convertView;

        TextView textView  = convertView.findViewById(R.id.textViewItem);
        TextView tvData    = convertView.findViewById(R.id.tvDataItem);
        CheckBox checkBox  = convertView.findViewById(R.id.checkBoxItem);

        textView.setText(tarefa.getDescricao());

        // Mostra a data-alvo apenas em visões multi-dia (semana/mês)
        if (tvData != null) {
            String d = tarefa.getDataAlvo();
            if (d != null && d.length() == 10) {
                tvData.setText(d.substring(8) + "/" + d.substring(5, 7));
                tvData.setVisibility(View.VISIBLE);
            } else {
                tvData.setVisibility(View.GONE);
            }
        }

        // Evita disparo do listener durante o bind
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(tarefa.isConcluida());
        aplicarEstilo(textView, tarefa.isConcluida());

        checkBox.setOnCheckedChangeListener((btn, isChecked) -> {
            tarefa.setConcluida(isChecked);
            repo.concluir(tarefa.getId(), isChecked);
            aplicarEstilo(textView, isChecked);
            if (onChanged != null) onChanged.run();
        });

        return convertView;
    }

    private void aplicarEstilo(TextView tv, boolean concluida) {
        if (concluida) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv.setAlpha(0.45f);
        } else {
            tv.setPaintFlags(tv.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            tv.setAlpha(1f);
        }
    }
}