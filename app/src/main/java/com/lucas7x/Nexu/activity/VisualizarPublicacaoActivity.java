package com.lucas7x.Nexu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.helper.HelperNavegacao;
import com.lucas7x.Nexu.model.Publicacao;
import com.lucas7x.Nexu.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPublicacaoActivity extends AppCompatActivity {

    CircleImageView imagePerfil;
    ImageView imagePublicacaoSelecionada;
    TextView textNomePublicacao, textQtdCurtidas, textDescricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_publicacao);

        //inicializar componentes
        inicializarComponentes();


        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.visualizar_publicacao);
        setSupportActionBar(toolbar);


        //recuperar dados da activity anterior
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {

            Publicacao publicacao = (Publicacao) bundle.getSerializable(HelperNavegacao.PUBLICACAO_SELECIONADA);
            Usuario usuario = (Usuario) bundle.getSerializable(HelperNavegacao.AMIGO_SELECIONADO);

            //exibe dados de usuario
            Uri url = Uri.parse(usuario.getCaminhoFoto());
            if (url != null) {
                Glide.with(VisualizarPublicacaoActivity.this)
                        .load(url)
                        .into(imagePerfil);
            }

            textNomePublicacao.setText(usuario.getNome());
            //textQtdCurtidas.setText(publicacao.getQtdCurtidas);
            textDescricao.setText(publicacao.getDescricao());

            //exibe dados da postagem
            Uri postagem = Uri.parse(publicacao.getCaminhoFoto());
            if (postagem != null) {
                Glide.with(VisualizarPublicacaoActivity.this)
                        .load(postagem)
                        .into(imagePublicacaoSelecionada);
            }

        }

    }


    private void inicializarComponentes() {

        imagePerfil = findViewById(R.id.imagePerfilPublicacao);
        imagePublicacaoSelecionada = findViewById(R.id.imagePublicacaoSelecionada);
        textQtdCurtidas = findViewById(R.id.textQtdCurtidasPublicacao);
        textDescricao = findViewById(R.id.textDescricaoPublicacao);
        textNomePublicacao = findViewById(R.id.textNomePublicacao);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }


}