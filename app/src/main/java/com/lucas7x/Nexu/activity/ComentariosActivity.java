package com.lucas7x.Nexu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.adapter.AdapterComentario;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperDB;
import com.lucas7x.Nexu.helper.UsuarioFirebase;
import com.lucas7x.Nexu.model.Comentario;
import com.lucas7x.Nexu.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity {

    private EditText editComentario;
    private RecyclerView recyclerComentarios;
    private AdapterComentario adapterComentario;

    private String idPublicacao;
    private Usuario usuarioLogado;
    private List<Comentario> listaComentarios;
    private ValueEventListener valueEventListenerComentarios;
    private DatabaseReference databaseRef;
    private DatabaseReference comentariosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);


        //inicializar os componentes
        editComentario = findViewById(R.id.editComentario);
        recyclerComentarios = findViewById(R.id.recyclerComentarios);


        //configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        listaComentarios = new ArrayList<>();
        databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();


        //recuperar id da publicacao
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            idPublicacao = (String) bundle.getSerializable(HelperDB.ID_PUBLICACAO_CTD);
        }


        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.comentarios);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        //configurar recycler view
        recyclerComentarios.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerComentarios.setLayoutManager(layoutManager);
        //configurar adapter do recycler view
        adapterComentario = new AdapterComentario(listaComentarios, getApplicationContext());
        recyclerComentarios.setAdapter(adapterComentario);


    }


    public void listarComentarios() {

        listaComentarios.clear();

        comentariosRef = databaseRef
                .child(HelperDB.COMENTARIOS)
                .child(idPublicacao);

        //Log.d("idPublicacao", idPublicacao);
        //if(comentariosRef.hasChil)


        valueEventListenerComentarios = comentariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaComentarios.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Comentario comentario = ds.getValue(Comentario.class);
                    listaComentarios.add(comentario);
                }
                adapterComentario.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public void salvarComentario(View view) {
        String textoComentario = editComentario.getText().toString();
        Log.d("idPublicacao", idPublicacao);

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
    protected void onStart() {
        super.onStart();
        listarComentarios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentariosRef.removeEventListener(valueEventListenerComentarios);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}