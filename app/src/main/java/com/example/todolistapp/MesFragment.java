package com.example.todolistapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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

public class MesFragment extends Fragment {

    private static final SimpleDateFormat SDF_MES_KEY =
            new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    private static final SimpleDateFormat SDF_KEY     =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat SDF_LABEL   =
            new SimpleDateFormat("MMMM 'de' yyyy", new Locale("pt","BR"));

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
        return inflater.inflate(R.layout.fragment_mes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        repo = TarefaRepository.get(requireContext());
        cal  = Calendar.getInstance();

        tvTitulo = view.findViewById(R.id.tvTituloMes);
        edtNova  = view.findViewById(R.id.edtNovaTarefaMes);
        listView = view.findViewById(R.id.listViewMes);

        view.findViewById(R.id.btnMesAnterior).setOnClickListener(v -> navegar(-1));
        view.findViewById(R.id.btnMesProximo ).setOnClickListener(v -> navegar(+1));
        view.findViewById(R.id.btnMesAtual   ).setOnClickListener(v -> {
            cal = Calendar.getInstance();
            carregarLista();
        });

        edtNova.setOnEditorActionListener((tv, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) { adicionarTarefa(); return true; }
            return false;
        });
        view.findViewById(R.id.btnAdicionarMes).setOnClickListener(v -> adicionarTarefa());

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

    private void navegar(int meses) {
        cal.add(Calendar.MONTH, meses);
        carregarLista();
    }

    private void carregarLista() {
        String mesKey = SDF_MES_KEY.format(cal.getTime());
        tvTitulo.setText(capitalize(SDF_LABEL.format(cal.getTime())));
        lista   = repo.porMes(mesKey);
        adapter = new TodoAdapter(requireContext(), lista, repo, null);
        listView.setAdapter(adapter);
    }

    private void adicionarTarefa() {
        String texto = edtNova.getText().toString().trim();
        if (texto.isEmpty()) return;

        // Cria para hoje se for o mês atual, senão para o dia 1 do mês exibido
        String mesKey = SDF_MES_KEY.format(cal.getTime());
        String hoje   = DatabaseHelper.hoje();
        String alvo   = hoje.startsWith(mesKey) ? hoje
                : mesKey + "-01";

        long id = repo.inserir(texto, alvo);
        lista.add(new Tarefa(id, texto, alvo, "", false));
        adapter.notifyDataSetChanged();
        edtNova.setText("");
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}