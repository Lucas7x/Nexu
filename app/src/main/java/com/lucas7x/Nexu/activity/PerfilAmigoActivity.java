package com.lucas7x.Nexu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperDB;
import com.lucas7x.Nexu.helper.HelperNavegacao;
import com.lucas7x.Nexu.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;

    private Button buttonSeguir;
    private CircleImageView imageFotoPerfilAmigo;
    private ValueEventListener valueEventListenerPerfilAmigo;
    private TextView textPublicacoes, textSeguidores, textSeguindo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //configurações iniciais
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child(HelperDB.USUARIOS);

        //inicializar os componentes
        inicializarComponentes();

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

            //recuperar foto do usuario selecionado
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();

            if(caminhoFoto != null) {
                Uri url = Uri.parse(caminhoFoto);
                Glide.with(PerfilAmigoActivity.this)
                        .load(url)
                        .into(imageFotoPerfilAmigo);
            } else {
                imageFotoPerfilAmigo.setImageResource(R.drawable.avatar);
            }
        }
    }

    private void inicializarComponentes() {
        buttonSeguir = findViewById(R.id.buttonPerfilAcao);
        buttonSeguir.setText(R.string.seguir);
        imageFotoPerfilAmigo = findViewById(R.id.imageFotoPerfil);
        textSeguidores = findViewById(R.id.textSeguidoresPerfil);
        textSeguindo = findViewById(R.id.textSeguindoPerfil);
        textPublicacoes = findViewById(R.id.textPublicacoesPerfil);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosPerfilAmigo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioAmigoRef.removeEventListener(valueEventListenerPerfilAmigo);
    }

    private void recuperarDadosPerfilAmigo() {
        usuarioAmigoRef = usuariosRef.child(usuarioSelecionado.getId());
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Usuario usuario = snapshot.getValue(Usuario.class);

                        String publicacoes = String.valueOf(usuario.getPublicacoes());
                        String seguidores = String.valueOf(usuario.getSeguidores());
                        String seguindo = String.valueOf(usuario.getSeguindo());

                        //configura valores recuperados
                        textPublicacoes.setText(publicacoes);
                        textSeguidores.setText(seguidores);
                        textSeguindo.setText(seguindo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}