package com.lucas7x.Nexu.model;

import com.google.firebase.database.DatabaseReference;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperDB;

import java.util.HashMap;
import java.util.Map;

public class Comentario {

    private String idComentario;
    private String comentario;
    private String idPublicacao;
    private String idUsuario;
    private String nomeUsuario;
    private String caminhoFotoUsuario;


    public Comentario() {}


    public boolean salvar() {
        /*
        estrutura de um comentario
        comentarios
            id_publicacao
                id_comentario
                    comentario

         */
        DatabaseReference comentariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child(HelperDB.COMENTARIOS)
                .child(getIdPublicacao());

        String idComent = comentariosRef.push().getKey();
        setIdComentario(idComent);

        comentariosRef
                .child(getIdComentario())
                .setValue(this);

        return true;
    }


    public String getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getIdPublicacao() {
        return idPublicacao;
    }

    public void setIdPublicacao(String idPublicacao) {
        this.idPublicacao = idPublicacao;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getCaminhoFotoUsuario() {
        return caminhoFotoUsuario;
    }

    public void setCaminhoFotoUsuario(String caminhoFotoUsuario) {
        this.caminhoFotoUsuario = caminhoFotoUsuario;
    }


}
