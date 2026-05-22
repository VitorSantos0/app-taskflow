package com.example.todolistapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SemanaFragment extends Fragment {

    private static final SimpleDateFormat SDF_KEY   =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat SDF_LABEL =
            new SimpleDateFormat("dd/MM", Locale.getDefault());

    private Calendar         cal;
    private TarefaRepository repo;
    private ArrayList<Tarefa> lista;
    private TodoAdapter       adapter;

    private TextView tvTitulo;
    private EditText edtNova;
    private ListView listView;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_semana, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        repo = TarefaRepository.get(requireContext());
        cal  = Calendar.getInstance();
        irParaSegundaDaSemana();

        tvTitulo = view.findViewById(R.id.tvTituloSemana);
        edtNova  = view.findViewById(R.id.edtNovaTarefaSemana);
        listView = view.findViewById(R.id.listViewSemana);

        view.findViewById(R.id.btnSemanaAnterior).setOnClickListener(v -> navegar(-1));
        view.findViewById(R.id.btnSemanaProxima ).setOnClickListener(v -> navegar(+1));
        view.findViewById(R.id.btnSemanaAtual   ).setOnClickListener(v -> {
            cal = Calendar.getInstance();
            irParaSegundaDaSemana();
            carregarLista();
        });

        edtNova.setOnEditorActionListener((tv, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) { adicionarTarefa(); return true; }
            return false;
        });
        view.findViewById(R.id.btnAdicionarSemana).setOnClickListener(v -> adicionarTarefa());

        carregarLista();

        listView.setOnItemLongClickListener((parent, v, position, id) -> {
            Tarefa t = lista.get(position);
            repo.deletar(t.getId());
            lista.remove(position);
            adapter.notifyDataSetChanged();
            Snackbar.make(requireView(), "Tarefa removida", Snackbar.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarLista();
    }

    private void irParaSegundaDaSemana() {
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    }

    private void navegar(int semanas) {
        cal.add(Calendar.WEEK_OF_YEAR, semanas);
        irParaSegundaDaSemana();
        carregarLista();
    }

    private String[] intervalo() {
        Calendar inicio = (Calendar) cal.clone();
        inicio.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Calendar fim = (Calendar) inicio.clone();
        fim.add(Calendar.DAY_OF_YEAR, 6);
        return new String[]{ SDF_KEY.format(inicio.getTime()), SDF_KEY.format(fim.getTime()) };
    }

    private void carregarLista() {
        String[] iv = intervalo();
        String labelInicio = SDF_LABEL.format(cal.getTime());
        Calendar fimCal = (Calendar) cal.clone();
        fimCal.add(Calendar.DAY_OF_YEAR, 6);
        String labelFim = SDF_LABEL.format(fimCal.getTime());
        tvTitulo.setText(labelInicio + " – " + labelFim);

        lista   = repo.porSemana(iv[0], iv[1]);
        adapter = new TodoAdapter(requireContext(), lista, repo, null);
        listView.setAdapter(adapter);
    }

    /** Tarefa adicionada na semana é criada para hoje se hoje está na semana,
     *  senão é criada para a segunda-feira da semana exibida. */
    private void adicionarTarefa() {
        String texto = edtNova.getText().toString().trim();
        if (texto.isEmpty()) return;

        String[] iv = intervalo();
        String hoje = DatabaseHelper.hoje();
        String alvo = (hoje.compareTo(iv[0]) >= 0 && hoje.compareTo(iv[1]) <= 0)
                ? hoje : iv[0];

        long id = repo.inserir(texto, alvo);
        lista.add(new Tarefa(id, texto, alvo, "", false));
        adapter.notifyDataSetChanged();
        edtNova.setText("");
    }
}