package com.lucas7x.Nexu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;

import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.helper.HelperNavegacao;
import com.lucas7x.Nexu.model.Usuario;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;

    private Button buttonSeguir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //inicializar os componentes
        buttonSeguir = findViewById(R.id.buttonPerfilAcao);
        buttonSeguir.setText(R.string.seguir);


        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_email_preto);


        //recuperar o usuario selecionado na tela de pesquisa
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            usuarioSelecionado = (Usuario) bundle.getSerializable(HelperNavegacao.AMIGO_SELECIONADO);

            //configura o nome do usuário na toolbar
            toolbar.setTitle(usuarioSelecionado.getNome());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}