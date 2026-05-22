package com.example.todolistapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ViewPager2          viewPager;
    private TabLayout           tabLayout;
    private RoutinePagerAdapter adapter;

    private static final String[] TITULOS = {"Dia", "Semana", "Mês"};
    private static final int[]    ICONES  = {
            R.drawable.ic_dia,
            R.drawable.ic_semana,
            R.drawable.ic_mes
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        adapter = new RoutinePagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Mantém os 3 fragments em memória para sincronização instantânea
        viewPager.setOffscreenPageLimit(2);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(TITULOS[position]);
            tab.setIcon(ICONES[position]);
        }).attach();
    }

    /** Chamado pelos fragments para atualizar badges futuros (extensível). */
    public void atualizarBadges() {
        // Placeholder para futuras badges de contagem nas abas
    }
}