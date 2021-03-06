package com.lucas7x.Nexu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.adapter.AdapterGrid;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperDB;
import com.lucas7x.Nexu.helper.HelperNavegacao;
import com.lucas7x.Nexu.helper.UsuarioFirebase;
import com.lucas7x.Nexu.model.Publicacao;
import com.lucas7x.Nexu.model.Usuario;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private String idUsuarioLogado;
    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private List<Publicacao> publicacoes;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference seguindoRef;
    private DatabaseReference seguidoresRef;
    private ValueEventListener valueEventListenerPerfilAmigo;
    private DatabaseReference publicacoesUsuarioRef;

    private Button buttonSeguir;
    private CircleImageView imageFotoPerfilAmigo;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private GridView gridViewPerfil;
    private AdapterGrid adapterGrid;

    private ProgressBar progressPerfilAmigo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        usuariosRef = firebaseRef.child(HelperDB.USUARIOS);
        seguindoRef = firebaseRef.child(HelperDB.SEGUINDO);
        seguidoresRef = firebaseRef.child(HelperDB.SEGUIDORES);
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //inicializar os componentes
        inicializarComponentes();

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.perfil);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_email_preto);


        //recuperar o usuario selecionado na tela de pesquisa
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            usuarioSelecionado = (Usuario) bundle.getSerializable(HelperNavegacao.AMIGO_SELECIONADO);

            //configurar referencia para postagens do usuario
            publicacoesUsuarioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child(HelperDB.PUBLICACOES)
                    .child(usuarioSelecionado.getId());



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

        //inicializar imageLoader
        inicializarImageLoader();

        //carregar as fotos das publicações de um usuário
        carregarFotosPublicacao();

        //abrir foto quando clicar na miniatura
        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Publicacao publicacao = publicacoes.get(position);
                Intent i = new Intent(getApplicationContext(), VisualizarPublicacaoActivity.class);
                i.putExtra(HelperNavegacao.AMIGO_SELECIONADO, usuarioSelecionado);
                i.putExtra(HelperNavegacao.PUBLICACAO_SELECIONADA, publicacao);

                startActivity(i);

            }
        });


    } // fim do onCreate


    /*
    * Instancia a Universal Image Loader
    * */
    public void inicializarImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .build();
        ImageLoader.getInstance().init(config);

    }


    public void carregarFotosPublicacao() {

        //recuperar as fotos postadas pelo usuario
        publicacoesUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                publicacoes = new ArrayList<>();

                //configurar tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth(tamanhoImagem);

                List<String> urlFotos = new ArrayList<>();
                for(DataSnapshot ds: snapshot.getChildren()) {
                    Publicacao publicacao = ds.getValue(Publicacao.class);
                    publicacoes.add(publicacao);
                    urlFotos.add(publicacao.getCaminhoFoto());
                }

                int qtdPublicacoes = urlFotos.size();
                textPublicacoes.setText(String.valueOf(qtdPublicacoes));

                //configurar adapter
                adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_postagem, urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    } //fim do carregarFotosPublicacao


    private void recuperarDadosUsuarioLogado() {
        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //recuperar dados do usuario logado
                        usuarioLogado = snapshot.getValue(Usuario.class);

                        //verificar se usuario logado já está seguindo amigo selecionado
                        verificaSeguindoUsuarioAmigo();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }


    private void verificaSeguindoUsuarioAmigo() {
        DatabaseReference seguidorRef = seguindoRef
                .child(idUsuarioLogado)
                .child(usuarioSelecionado.getId());

        seguidorRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            //já está seguindo
                            configurarBotaoSeguir(true);
                        } else {
                            //ainda não está seguindo
                            configurarBotaoSeguir(false);

                            //adiciona evento para seguir usuário
                            buttonSeguir.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //salvar seguidor
                                    salvarSeguidor(usuarioLogado, usuarioSelecionado);
                                }
                            });
                        }
                    }
                    

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    } //fim do verificaSeguindoUsuarioAmigo


    private void configurarBotaoSeguir(boolean segueUsuario) {
        if(segueUsuario) {
            buttonSeguir.setText(R.string.seguindo);
        } else {
            buttonSeguir.setText(R.string.seguir);
        }
    } //fim do configurarBotaoSeguir


    private void salvarSeguidor(Usuario uLogado, Usuario uAmigo) {

        /*
        usuario X começa a seguir amigo Y
            novo seguindo X -> Y
            novo seguidor Y -> X
         */

        //salva o amigo na lista de seguindo do usuario
        HashMap<String, Object> dadosAmigo = new HashMap<>();
        dadosAmigo.put(HelperDB.NOME_US, uAmigo.getNome());
        dadosAmigo.put(HelperDB.CAMINHO_FOTO_US, uAmigo.getCaminhoFoto());


        DatabaseReference seguindo = seguindoRef
                .child(uLogado.getId())
                .child(uAmigo.getId());
        seguindo.setValue(dadosAmigo);


        //salva o usuario logado como seguidor do amigo
        HashMap<String, Object> dadosLogado = new HashMap<>();
        dadosLogado.put(HelperDB.NOME_US, uLogado.getNome());
        dadosLogado.put(HelperDB.CAMINHO_FOTO_US, uLogado.getCaminhoFoto());

        //salvar seguidor
        DatabaseReference seguidor = seguidoresRef
                .child(uAmigo.getId())
                .child(uLogado.getId());
        seguidor.setValue(dadosLogado);




        //alterar texto e ação do botão para seguindo
        configurarBotaoSeguir(true);

        //retirando ação do botão de seguir
        //-------------------------------------------------------------alterar para deixar de seguir---------------------------------
        buttonSeguir.setOnClickListener(null);



        //incrementar seguindo do usuário logado
        int qtdSeguindoLogado = uLogado.getSeguindo() + 1;

        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put(HelperDB.SEGUINDO, qtdSeguindoLogado);

        DatabaseReference usuarioSeguindo = usuariosRef
                .child(uLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);



        //incrementar seguidores do amigo
        int qtdSeguidorAmigo = uAmigo.getSeguidores() + 1;

        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put(HelperDB.SEGUIDORES, qtdSeguidorAmigo);

        DatabaseReference usuarioSeguido = usuariosRef
                .child(uAmigo.getId());
        usuarioSeguido.updateChildren(dadosSeguidores);


    } //fim do salvarSeguidor


    private void inicializarComponentes() {
        buttonSeguir = findViewById(R.id.buttonPerfilAcao);
        buttonSeguir.setText(R.string.carregando);
        imageFotoPerfilAmigo = findViewById(R.id.imageFotoPerfil);
        textSeguidores = findViewById(R.id.textSeguidoresPerfil);
        textSeguindo = findViewById(R.id.textSeguindoPerfil);
        textPublicacoes = findViewById(R.id.textPublicacoesPerfil);
        gridViewPerfil = findViewById(R.id.gridViewPerfil);
        progressPerfilAmigo = findViewById(R.id.progressPerfil);
        progressPerfilAmigo.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //recuperar dados do usuario pesquisado (amigo)
        recuperarDadosPerfilAmigo();

        //recuperar dados do usuário logado
        recuperarDadosUsuarioLogado();
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
    } // fim do recuperarDadosPerfilAmigo

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}