package com.lucas7x.Nexu.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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
import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.activity.EditarPerfilActivity;
import com.lucas7x.Nexu.helper.UsuarioFirebase;

import de.hdodenhof.circleimageview.CircleImageView;


public class PerfilFragment extends Fragment {

    private ProgressBar progressPerfil;
    private CircleImageView imagePerfil;
    public GridView gridViewPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private Button buttonPerfilEditar;

    /*// TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    */
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



        //configurações dos componentes
        progressPerfil = view.findViewById(R.id.progressPerfil);
        imagePerfil = view.findViewById(R.id.imageFotoPerfil);
        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
        buttonPerfilEditar = view.findViewById(R.id.buttonPerfilAcao);
        textPublicacoes = view.findViewById(R.id.textPublicacoesPerfil);
        textSeguidores = view.findViewById(R.id.textSeguidoresPerfil);
        textSeguindo = view.findViewById(R.id.textSeguindoPerfil);

        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        //pega imagem do perfil e mostra no imageview
        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null) {
            Glide.with(PerfilFragment.this)
                    .load(url)
                    .into(imagePerfil);
        } else {
            imagePerfil.setImageResource(R.drawable.avatar);
        }

        //abre
        buttonPerfilEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        progressPerfil.setVisibility(View.GONE);

        return view;
    }

    public void abrirEdicaoPerfil(View view) {
        Intent i = new Intent(getContext(), EditarPerfilActivity.class);
        startActivity(i);
    }
}