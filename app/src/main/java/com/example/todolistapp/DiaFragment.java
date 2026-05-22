package com.example.todolistapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DiaFragment extends Fragment {

    private static final SimpleDateFormat SDF_KEY   =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat SDF_LABEL =
            new SimpleDateFormat("EEE, dd 'de' MMMM", new Locale("pt", "BR"));

    private Calendar        cal;
    private TarefaRepository repo;
    private ArrayList<Tarefa> lista;
    private TodoAdapter      adapter;

    private TextView  tvTitulo;
    private EditText  edtNova;
    private ListView  listView;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        repo   = TarefaRepository.get(requireContext());
        cal    = Calendar.getInstance();

        tvTitulo = view.findViewById(R.id.tvTituloDia);
        edtNova  = view.findViewById(R.id.edtNovaTarefa);
        listView = view.findViewById(R.id.listViewDia);

        view.findViewById(R.id.btnDiaAnterior).setOnClickListener(v -> navegar(-1));
        view.findViewById(R.id.btnDiaProximo ).setOnClickListener(v -> navegar(+1));
        view.findViewById(R.id.btnHoje       ).setOnClickListener(v -> irParaHoje());

        edtNova.setOnEditorActionListener((tv, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                adicionarTarefa();
                return true;
            }
            return false;
        });

        view.findViewById(R.id.btnAdicionarDia).setOnClickListener(v -> adicionarTarefa());

        carregarLista();
        configurarSwipeDelete();
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarLista();  // sincroniza ao voltar para esta aba
    }

    private void navegar(int dias) {
        cal.add(Calendar.DAY_OF_YEAR, dias);
        carregarLista();
    }

    private void irParaHoje() {
        cal = Calendar.getInstance();
        carregarLista();
    }

    private String chaveAtual() { return SDF_KEY.format(cal.getTime()); }

    private void carregarLista() {
        tvTitulo.setText(SDF_LABEL.format(cal.getTime()));

        lista   = repo.porDia(chaveAtual());
        adapter = new TodoAdapter(requireContext(), lista, repo, this::atualizarBadge);
        listView.setAdapter(adapter);
        atualizarBadge();
    }

    private void adicionarTarefa() {
        String texto = edtNova.getText().toString().trim();
        if (texto.isEmpty()) return;

        long id = repo.inserir(texto, chaveAtual());
        Tarefa nova = new Tarefa(id, texto, chaveAtual(), "", false);
        lista.add(nova);
        adapter.notifyDataSetChanged();
        edtNova.setText("");
        atualizarBadge();
    }

    private void atualizarBadge() {
        // notifica MainActivity para atualizar o badge da aba, se necessário
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).atualizarBadges();
        }
    }

    private void configurarSwipeDelete() {
        // ListView não usa ItemTouchHelper nativamente; usamos onItemLongClick
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Tarefa tarefa = lista.get(position);
            repo.deletar(tarefa.getId());
            lista.remove(position);
            adapter.notifyDataSetChanged();
            atualizarBadge();
            Snackbar.make(requireView(), "Tarefa removida", Snackbar.LENGTH_SHORT).show();
            return true;
        });
    }
}