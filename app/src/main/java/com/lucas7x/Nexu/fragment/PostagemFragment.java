package com.lucas7x.Nexu.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.activity.FiltroActivity;
import com.lucas7x.Nexu.helper.HelperNavegacao;
import com.lucas7x.Nexu.helper.Permissao;

import java.io.ByteArrayOutputStream;


public class PostagemFragment extends Fragment {

    private String[] permissoesNecessarias = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    private ImageButton buttonAbrirGaleria;
    private ImageButton buttonAbrirCamera;

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    public PostagemFragment() {
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
        View view = inflater.inflate(R.layout.fragment_postagem, container, false);

        //inicializar componentes
        buttonAbrirCamera = view.findViewById(R.id.buttonAbrirCamera);
        buttonAbrirGaleria = view.findViewById(R.id.buttonAbrirGaleria);


        //validar permissões
        Permissao.validarPermissões(permissoesNecessarias, getActivity(), 1);


        //adicionar listener para o botão da câmera
        buttonAbrirCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(i.resolveActivity(getActivity().getPackageManager()) == null) {
                    startActivityForResult(i, SELECAO_CAMERA);
                } else {
                    Toast.makeText(
                            getActivity(),
                            R.string.nao_foi_possivel_abrir_a_camera,
                            Toast.LENGTH_SHORT
                    ).show();


                }
            }
        });


        //adicionar listener para botão da galeria
        buttonAbrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(i.resolveActivity(getActivity().getPackageManager()) == null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                } else {
                    Toast.makeText(
                            getActivity(),
                            R.string.nao_foi_possivel_abrir_a_galeria,
                            Toast.LENGTH_SHORT
                    ).show();


                }
            }
        });




        return view;
    } //fim do onCreateView


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK) {
            Bitmap img = null;

            try {

                //pega a imagem de acordo com o método selecionado
                switch (requestCode) {

                    case SELECAO_CAMERA:
                        img = (Bitmap) data.getExtras().get("data");
                        break;

                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();

                        if (android.os.Build.VERSION.SDK_INT >= 29) {
                            ImageDecoder.Source imageDecoder = ImageDecoder.createSource(getActivity().getContentResolver(), localImagemSelecionada);
                            img = ImageDecoder.decodeBitmap(imageDecoder);
                            //Log.i("SDK", ">= 29");
                        } else {
                            img = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);
                        }

                        break;

                }

                //caso a imagem tenha sido selecionada
                if(img != null) {

                    //converter imagem em byteArray
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //envia imagem escolhida para aplicação de filtros
                    Intent i = new Intent(getActivity(), FiltroActivity.class);
                    i.putExtra(HelperNavegacao.FOTO_PARA_POSTAGEM, dadosImagem);
                    startActivity(i);

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}