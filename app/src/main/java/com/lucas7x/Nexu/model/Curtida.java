package com.lucas7x.Nexu.model;

import com.google.firebase.database.DatabaseReference;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperDB;

import java.util.HashMap;

public class Curtida {

    private Usuario usuario;

    private String idPublicacao;
    private int qtdCurtidas = 0;
    private String idUsuario;
    private String nomeUsuario;
    private String caminhoFoto;

    public Curtida() {
    }


    public void salvar() {

        DatabaseReference curtidaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child(HelperDB.CURTIDAS)
                .child(idPublicacao)
                .child(getIdUsuario());

        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put(HelperDB.NOME_USUARIO_CTD, usuario.getNome());
        dadosUsuario.put(HelperDB.CAMINHO_FOTO_CTD, usuario.getCaminhoFoto());
        dadosUsuario.put(HelperDB.ID_USUARIO_CTD, usuario.getId());

        curtidaRef.setValue(dadosUsuario);
        //atualizar quantidade de curtidas
        atualizarQtdCurtidas(1);

    }

    public void remover() {

        DatabaseReference curtidaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child(HelperDB.CURTIDAS)
                .child(idPublicacao)
                .child(getIdUsuario());

        curtidaRef.removeValue();
        //atualizar quantidade de curtidas
        atualizarQtdCurtidas(-1);

    }

    public void atualizarQtdCurtidas(int valor) {
        DatabaseReference curtidaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child(HelperDB.CURTIDAS)
                .child(idPublicacao)
                .child(HelperDB.QTD_CURTIDAS_CTD);
        setQtdCurtidas(getQtdCurtidas() + valor);

        curtidaRef.setValue(getQtdCurtidas());

    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getIdPublicacao() {
        return idPublicacao;
    }

    public void setIdPublicacao(String idPublicacao) {
        this.idPublicacao = idPublicacao;
    }

    public int getQtdCurtidas() {
        return qtdCurtidas;
    }

    public void setQtdCurtidas(int qtdCurtidas) {
        this.qtdCurtidas = qtdCurtidas;
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

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}
