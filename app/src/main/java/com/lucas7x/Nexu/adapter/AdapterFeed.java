package com.lucas7x.Nexu.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.activity.ComentariosActivity;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperDB;
import com.lucas7x.Nexu.helper.UsuarioFirebase;
import com.lucas7x.Nexu.model.Curtida;
import com.lucas7x.Nexu.model.Feed;
import com.lucas7x.Nexu.model.Usuario;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder>{


    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView fotoPerfil;
        ImageView fotoPublicacao;
        TextView nome, descricao, qtdCurtidas;
        ImageView visualizarComentario;
        LikeButton likeButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fotoPerfil = itemView.findViewById(R.id.imagePerfilPublicacao);
            fotoPublicacao = itemView.findViewById(R.id.imagePublicacaoSelecionada);
            nome = itemView.findViewById(R.id.textNomePublicacao);
            descricao = itemView.findViewById(R.id.textDescricaoPublicacao);
            qtdCurtidas = itemView.findViewById(R.id.textQtdCurtidasPublicacao);
            visualizarComentario = itemView.findViewById(R.id.imageComentarioFeed);
            likeButton = itemView.findViewById(R.id.likeButtonFeed);

        }
    }

    private List<Feed> listaFeed;
    private Context context;

    public AdapterFeed(List<Feed> listaFeed, Context context) {
        this.listaFeed = listaFeed;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_feed, parent, false);
        return new AdapterFeed.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final Feed feed = listaFeed.get(position);
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        /*
        estrutura do feed
        feeds
            id_publicacao

         */
        //carregar dados do feed
        Uri uriFotoUsuario = Uri.parse(feed.getCaminhoFotoUsuario());
        Uri uriFotoPublicacao = Uri.parse(feed.getCaminhoFoto());

        if(uriFotoUsuario != null) {
            Glide.with(context)
                    .load(uriFotoUsuario)
                    .into(holder.fotoPerfil);
        }

        if(uriFotoPublicacao != null) {
            Glide.with(context)
                    .load(uriFotoPublicacao)
                    .into(holder.fotoPublicacao);
        }

        holder.nome.setText(feed.getNomeUsuario());
        holder.descricao.setText(feed.getDescricao());

        //adicionar evento de clique nos comentarios
        holder.visualizarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ComentariosActivity.class);
                i.putExtra(HelperDB.ID_PUBLICACAO_CTD, feed.getIdPublicacao());
                context.startActivity(i);
            }
        });


        /*
        Estrutura de curtidas
        curtidas
            id_publicacao
                id_usuario
                    nome_usuario
                    caminho_foto
        */
        //recuperar dados das curtidas
        DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child(HelperDB.CURTIDAS)
                .child(feed.getIdPublicacao());
        curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int qtdCurtidas = 0;

                //verifica se já foi setada uma quantidade de curtidas, a fim de não haver erros por NullPointer
                if(snapshot.hasChild(HelperDB.QTD_CURTIDAS_CTD)) {
                    Curtida curtida = snapshot.getValue(Curtida.class);
                    qtdCurtidas = curtida.getQtdCurtidas();
                }


                //verifica se ja foi curtido
                if(snapshot.hasChild(usuarioLogado.getId())) {
                    holder.likeButton.setLiked(true);
                } else {
                    holder.likeButton.setLiked(false);
                }



                //monta objeto para curtida ***********************************************************************
                Curtida curtida = new Curtida();
                curtida.setQtdCurtidas(qtdCurtidas);
                curtida.setUsuario(usuarioLogado);
                curtida.setIdUsuario(usuarioLogado.getId());
                curtida.setIdPublicacao(feed.getIdPublicacao());

                //adicionar eventos para curtir uma foto
                holder.likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {

                        curtida.salvar();
                        holder.qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");

                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {

                        curtida.remover();
                        holder.qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");

                    }
                });

                holder.qtdCurtidas.setText(qtdCurtidas + " curtidas");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return listaFeed.size();
    }


}
