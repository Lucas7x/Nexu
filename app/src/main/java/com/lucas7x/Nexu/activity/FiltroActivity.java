package com.lucas7x.Nexu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.adapter.AdapterMiniaturas;
import com.lucas7x.Nexu.helper.HelperNavegacao;
import com.lucas7x.Nexu.helper.RecyclerItemClickListener;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    //bloco de inicialização estático para carregar a biblioteca que aplica os filtros
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private Bitmap imagemFiltrada;
    private List<ThumbnailItem> listaFiltros;

    private RecyclerView recyclerFiltros;
    private AdapterMiniaturas adapterMiniaturas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        //configurações iniciais
        listaFiltros = new ArrayList<>();

        //inicializar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFiltros = findViewById(R.id.recyclerFiltros);

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

            //teste do filtro
            /*


             */


        }
    } //fim do onCreate

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


    //ações nos itens do menu


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_salvar_postagem:

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