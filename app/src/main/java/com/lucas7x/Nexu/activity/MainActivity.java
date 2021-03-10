package com.lucas7x.Nexu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.fragment.FeedFragment;
import com.lucas7x.Nexu.fragment.PerfilFragment;
import com.lucas7x.Nexu.fragment.PesquisaFragment;
import com.lucas7x.Nexu.fragment.PostagemFragment;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        //configuração de objetos
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        //configurar bottom navigation view
        configuraBottomNavigationView();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();

    } //fim do onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sair:
                deslogarUsuario();

                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*  método responsável por criar a bottomNaviation  */
    private void configuraBottomNavigationView() {
        BottomNavigationViewEx bottom = findViewById(R.id.bottomNavigation);

        //faz configurações iniciais do BottomNavigation
        bottom.enableAnimation(true);
        bottom.enableItemShiftingMode(false);
        bottom.enableShiftingMode(true);
        bottom.setTextVisibility(true);

        //habilitar navegação
        habilitarNavegacao(bottom);
        
        //configura item selecionado inicialmente
        Menu menu = bottom.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    /* método responsável por tratar eventos de clique no BottomNavigation */
    private void habilitarNavegacao(BottomNavigationViewEx viewEx) {
        viewEx.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.ic_inicio:
                                fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
                                return true;
                            case R.id.ic_pesquisa:
                                fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                                return true;
                            case R.id.ic_perfil:
                                fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                                return true;
                            case R.id.ic_postagem:
                                fragmentTransaction.replace(R.id.viewPager, new PostagemFragment()).commit();
                                return true;

                        }
                        return false;
                    }
                }
        );
    }

    private void deslogarUsuario() {
        try {
            autenticacao.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}