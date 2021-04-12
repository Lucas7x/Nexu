package com.lucas7x.Nexu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperDB;
import com.lucas7x.Nexu.helper.UsuarioFirebase;
import com.lucas7x.Nexu.model.Comentario;
import com.lucas7x.Nexu.model.Usuario;

public class ComentariosActivity extends AppCompatActivity {

    private EditText editComentario;
    private RecyclerView recyclerComentarios;
    private String idPublicacao;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);


        //inicializar os componentes
        editComentario = findViewById(R.id.editComentario);


        //configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();


        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.comentarios);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //recuperar id da publicacao
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            idPublicacao = bundle.getString(HelperDB.ID_PUBLICACAO_CM);
        }





    }

    public void salvarComentario(View view) {
        String textoComentario = editComentario.getText().toString();

        if(textoComentario != null && !textoComentario.equals("")) {



            Comentario comentario = new Comentario();
            comentario.setComentario(textoComentario);
            comentario.setIdPublicacao(idPublicacao);
            comentario.setIdUsuario(usuarioLogado.getId());
            comentario.setCaminhoFotoUsuario(usuarioLogado.getCaminhoFoto());
            comentario.setNomeUsuario(usuarioLogado.getNome());

            if(comentario.salvar()) {
                Toast.makeText(
                        this,
                        R.string.comentario_salvo,
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(
                        this,
                        R.string.erro_salvar_comentario,
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {
            Toast.makeText(this, R.string.insira_um_comentario, Toast.LENGTH_SHORT).show();
        }

        //limpar o comentario digitado
        editComentario.setText("");


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}