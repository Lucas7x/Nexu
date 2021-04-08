package com.lucas7x.Nexu.model;

import com.google.firebase.database.DatabaseReference;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperDB;

public class Publicacao {

    /*
    * Modelo de publicação
    * publicacao
    *   <idUsuario>
    *       <idPostagemFirebase
    *           descricao
    *           caminho foto
    *           idusuario
    * */

    private String idPublicacao;
    private String idUsuario;
    private String descricao;
    private String caminhoFoto;

    public Publicacao() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference publicacaoRef = firebaseRef
                .child(HelperDB.PUBLICACOES);
        String idPub = publicacaoRef.push().getKey();
        setIdPublicacao(idPub);
    }

    public boolean salvar() {
        DatabaseReference dbRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference publicacoesRef = dbRef
                .child(HelperDB.PUBLICACOES)
                .child(getIdUsuario())
                .child(getIdPublicacao());
        publicacoesRef.setValue(this);

        return true;
    }

    public String getIdPublicacao() {
        return idPublicacao;
    }

    public void setIdPublicacao(String idPostagem) {
        this.idPublicacao = idPostagem;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}
