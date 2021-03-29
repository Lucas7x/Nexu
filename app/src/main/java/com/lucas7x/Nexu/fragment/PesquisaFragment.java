package com.lucas7x.Nexu.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.adapter.AdapterPesquisa;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperDB;
import com.lucas7x.Nexu.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class PesquisaFragment extends Fragment {


    private SearchView searchViewPesquisa;
    private RecyclerView recyclerPesquisa;

    private List<Usuario> listaUsuarios;
    private DatabaseReference usuariosRef;

    private AdapterPesquisa adapterPesquisa;


    public PesquisaFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerPesquisa = view.findViewById(R.id.recyclerPesquisa);

        //configurações iniciais
        listaUsuarios = new ArrayList<>();
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child(HelperDB.USUARIOS);

        //configurar o recycler view
        recyclerPesquisa.setHasFixedSize(true);
        recyclerPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));
        //adapter
        adapterPesquisa = new AdapterPesquisa(listaUsuarios, getActivity());
        recyclerPesquisa.setAdapter(adapterPesquisa);

        //configurar search view
        searchViewPesquisa.setQueryHint(getString(R.string.buscar_usuarios));
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toUpperCase();
                pesquisarUsuarios(textoDigitado);
                return true;
            }
        });

        return view;
    } //fim onCreateView

    private void pesquisarUsuarios(String texto) {
        //limpar a lista
        listaUsuarios.clear();

        //pesquisar usuarios caso tenha texto na caixa de pesquisa
        if(texto.length() >= 2) {
            Query query = usuariosRef.orderByChild(HelperDB.NOME_PESQUISA_US)
                    .startAt(texto)
                    .endAt(texto + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //limpar a lista
                    listaUsuarios.clear();

                    for (DataSnapshot ds: snapshot.getChildren()) {
                        listaUsuarios.add(ds.getValue(Usuario.class));
                    }

                    adapterPesquisa.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    } //fim pesquisarUsuarios
}