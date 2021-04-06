package com.lucas7x.Nexu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.helper.HelperNavegacao;

public class FiltroActivity extends AppCompatActivity {

    private ImageView imageFotoEscolhida;
    private Bitmap imagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        //inicializar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);

        //recuperar a imagem selecionada pelo usuario
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            byte[] dadosImagem = bundle.getByteArray(HelperNavegacao.FOTO_PARA_POSTAGEM);

            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imageFotoEscolhida.setImageBitmap(imagem);

        }
    }
}