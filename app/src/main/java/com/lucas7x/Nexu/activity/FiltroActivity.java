package com.lucas7x.Nexu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.adapter.AdapterMiniaturas;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperDB;
import com.lucas7x.Nexu.helper.HelperNavegacao;
import com.lucas7x.Nexu.helper.HelperSTG;
import com.lucas7x.Nexu.helper.RecyclerItemClickListener;
import com.lucas7x.Nexu.helper.UsuarioFirebase;
import com.lucas7x.Nexu.model.Publicacao;
import com.lucas7x.Nexu.model.Usuario;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    //bloco de inicialização estático para carregar a biblioteca que aplica os filtros
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private TextInputEditText textDescricaoFiltro;

    private Bitmap imagem;
    private Bitmap imagemFiltrada;
    private List<ThumbnailItem> listaFiltros;
    private String idUsuarioLogado;
    private AlertDialog dialog;

    private RecyclerView recyclerFiltros;
    private AdapterMiniaturas adapterMiniaturas;

    private DatabaseReference firebaseDbRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private Usuario usuarioLogado;
    private DataSnapshot seguidoresSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        //configurações iniciais
        listaFiltros = new ArrayList<>();
        firebaseDbRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child(HelperDB.USUARIOS);


        //inicializar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFiltros = findViewById(R.id.recyclerFiltros);
        textDescricaoFiltro = findViewById(R.id.textDescricaoFiltro);

        //recuperar os dados para uma nova publicacao
        recuperarDadosPublicacao();


        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.filtros);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //recuperar a imagem selecionada pelo usuario
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            byte[] dadosImagem = bundle.getByteArray(HelperNavegacao.FOTO_PARA_POSTAGEM);

            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imageFotoEscolhida.setImageBitmap(imagem);
            imagemFiltrada = imagem.copy(imagem.getConfig(), true);


            //configura recyclerView de filtros
            adapterMiniaturas = new AdapterMiniaturas(listaFiltros, getApplicationContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerFiltros.setLayoutManager(layoutManager);
            recyclerFiltros.setAdapter(adapterMiniaturas);


            //adiciona evento de clique no recycler view
            recyclerFiltros.addOnItemTouchListener(
                    new RecyclerItemClickListener(
                            getApplicationContext(),
                            recyclerFiltros,
                            new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    //recuperar filtro clicado
                                    ThumbnailItem item = listaFiltros.get(position);

                                    imagemFiltrada = imagem.copy(imagem.getConfig(), true);
                                    Filter filtro = item.filter;
                                    //Filter filtro = FilterPack.getAmazonFilter( getApplicationContext());
                                    imageFotoEscolhida.setImageBitmap(filtro.processFilter(imagemFiltrada));

                                }

                                @Override
                                public void onLongItemClick(View view, int position) {

                                }

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            }
                    )
            );

            //recupera filtros
            recuperarFiltros();


        }
    } //fim do onCreate


    private void abrirDialogCarregamento(String titulo) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setCancelable(false);
        alert.setView(R.layout.carregamento);

        dialog = alert.create();
        dialog.show();

    }

    //recuperarDadosUsuarioLogado
    private void recuperarDadosPublicacao() {

        abrirDialogCarregamento(getString(R.string.recuperando_dados));

        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //recuperar dados do usuario logado
                        usuarioLogado = snapshot.getValue(Usuario.class);

                        //recuperar seguidores
                        DatabaseReference seguidoresRef = firebaseDbRef
                                .child(HelperDB.SEGUIDORES)
                                .child(usuarioLogado.getId());
                        seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                seguidoresSnapshot = snapshot;
                                dialog.cancel();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    } //fim do recuperarDadosUsuarioLogado



    private void recuperarFiltros() {
        //limpar itens
        ThumbnailsManager.clearThumbs();
        listaFiltros.clear();

        //configurar filtro normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = imagem;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb(item);


        //lista todos os filtros
        List<Filter> filtros = FilterPack.getFilterPack(getApplicationContext());
        for (Filter filtro : filtros) {
            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = imagem;
            itemFiltro.filter = filtro;
            itemFiltro.filterName = filtro.getName();

            ThumbnailsManager.addThumb(itemFiltro);

        }


        listaFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));

        adapterMiniaturas.notifyDataSetChanged();


    } //fim do recuperarFiltros


    //adicionar menu na toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);

        return super.onCreateOptionsMenu(menu);
    }


    private void publicar() {

        abrirDialogCarregamento(getString(R.string.salvando_publicacao));

        Publicacao publicacao = new Publicacao();
        publicacao.setIdUsuario(idUsuarioLogado);
        publicacao.setDescricao(textDescricaoFiltro.getText().toString());

        ////converter imagem em byteArray para enviar ao firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagemFiltrada.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        //salvar imagem no firebase storage
        StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        StorageReference imagemRef = storageRef
                .child(HelperSTG.IMAGENS)
                .child(HelperSTG.PUBLICACOES)
                .child(publicacao.getIdPublicacao() + ".jpeg");


        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(
                        FiltroActivity.this,
                        "Erro ao fazer upload da imagem",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //recuperar local da foto
                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {

                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri url = task.getResult();
                        publicacao.setCaminhoFoto(url.toString());

                        //salvar foto no firebase
                        if(publicacao.salvar(seguidoresSnapshot)) {

                            //atualizar a quantidade de publicacoes
                            int qtdPublicacoes = usuarioLogado.getPublicacoes() + 1;
                            usuarioLogado.setPublicacoes(qtdPublicacoes);
                            usuarioLogado.atualizarQtdPublicacoes();

                            Toast.makeText(
                                    FiltroActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT
                            ).show();

                            dialog.cancel();

                            finish();
                        }
                    }
                });
            }
        });


    }


    //ações nos itens do menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_salvar_postagem:
                publicar();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}