package com.lucas7x.Nexu.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.activity.EditarPerfilActivity;
import com.lucas7x.Nexu.activity.PerfilAmigoActivity;
import com.lucas7x.Nexu.adapter.AdapterGrid;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperDB;
import com.lucas7x.Nexu.helper.UsuarioFirebase;
import com.lucas7x.Nexu.model.Publicacao;
import com.lucas7x.Nexu.model.Usuario;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class PerfilFragment extends Fragment {

    private ProgressBar progressPerfil;
    private CircleImageView imagePerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private Button buttonPerfilEditar;
    private Usuario usuarioLogado;

    private ValueEventListener valueEventListenerPerfil;
    private DatabaseReference firebaseRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference usuariosRef;

    private DatabaseReference publicacoesUsuarioRef;
    private GridView gridViewPerfil;
    private AdapterGrid adapterGrid;

    public PerfilFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        usuariosRef = firebaseRef.child(HelperDB.USUARIOS);

        //configurar referencia postagens do usuario
        publicacoesUsuarioRef = firebaseRef
                .child(HelperDB.PUBLICACOES)
                .child(usuarioLogado.getId());


        //configurações dos componentes
        inicializarComponentes(view);



        //recupera usuario logado
        //recuperar foto do usuario selecionado
        String caminhoFoto = usuarioLogado.getCaminhoFoto();

        if(caminhoFoto != null) {
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(getActivity())
                    .load(url)
                    .into(imagePerfil);
        } else {
            imagePerfil.setImageResource(R.drawable.avatar);
        }

        /*FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        //pega imagem do perfil e mostra no imageview
        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null) {
            Glide.with(PerfilFragment.this)
                    .load(url)
                    .into(imagePerfil);
        } else {
            imagePerfil.setImageResource(R.drawable.avatar);
        }*/

        //abre tela para editar o perfil
        buttonPerfilEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        progressPerfil.setVisibility(View.GONE);

        //inicializar imageLoader
        inicializarImageLoader();

        //carregar as fotos das publicações de um usuário
        carregarFotosPublicacao();

        return view;
    } //fim do onCreateView



    private void inicializarComponentes(View view) {
        progressPerfil = view.findViewById(R.id.progressPerfil);
        imagePerfil = view.findViewById(R.id.imageFotoPerfil);
        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
        buttonPerfilEditar = view.findViewById(R.id.buttonPerfilAcao);
        textPublicacoes = view.findViewById(R.id.textPublicacoesPerfil);
        textSeguidores = view.findViewById(R.id.textSeguidoresPerfil);
        textSeguindo = view.findViewById(R.id.textSeguindoPerfil);
    }


    public void abrirEdicaoPerfil(View view) {
        Intent i = new Intent(getContext(), EditarPerfilActivity.class);
        startActivity(i);
    }

    @Override
    public void onStart() {
        super.onStart();

        //recuperar dados do usuario logado
        recuperarDadosUsuarioLogado();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioLogadoRef.removeEventListener(valueEventListenerPerfil);
    }

    private void recuperarDadosUsuarioLogado() {
        usuarioLogadoRef = usuariosRef.child(usuarioLogado.getId());
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(
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
    } // fim do recuperarDadosUsuarioLogado


    /*
     * Instancia a Universal Image Loader
     * */
    public void inicializarImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getActivity())
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

                //configurar tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth(tamanhoImagem);

                List<String> urlFotos = new ArrayList<>();
                for(DataSnapshot ds: snapshot.getChildren()) {
                    Publicacao publicacao = ds.getValue(Publicacao.class);
                    urlFotos.add(publicacao.getCaminhoFoto());
                }

                int qtdPublicacoes = urlFotos.size();
                textPublicacoes.setText(String.valueOf(qtdPublicacoes));

                //configurar adapter
                adapterGrid = new AdapterGrid(getActivity(), R.layout.grid_postagem, urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    } //fim do carregarFotosPublicacao


}