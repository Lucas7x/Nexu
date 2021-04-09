package com.lucas7x.Nexu.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperDB;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String id;
    private String nome;
    private String nomePesquisa;
    private String email;
    private String senha;
    private String caminhoFoto;
    private int seguidores = 0;
    private int seguindo = 0;
    private int publicacoes = 0;

    public Usuario() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomePesquisa() { return nomePesquisa; }

    public void setNomePesquisa(String nomePesquisa) { this.nomePesquisa = nomePesquisa; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public int getSeguidores() { return seguidores; }

    public void setSeguidores(int seguidores) { this.seguidores = seguidores; }

    public int getSeguindo() { return seguindo; }

    public void setSeguindo(int seguindo) { this.seguindo = seguindo; }

    public int getPublicacoes() { return publicacoes; }

    public void setPublicacoes(int publicacoes) { this.publicacoes = publicacoes; }

    public void salvar() {
        DatabaseReference dbRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuariosRef = dbRef.child(HelperDB.USUARIOS).child(getId());
        usuariosRef.setValue(this);
    }

    public void atualizar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuariosRef = firebaseRef.child(HelperDB.USUARIOS).child(getId());

        Map<String,Object> valoresUsuario = converterParaMap();
        usuariosRef.updateChildren(valoresUsuario);
    }

    public void atualizarQtdPublicacoes() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuariosRef = firebaseRef.child(HelperDB.USUARIOS).child(getId());

        HashMap<String, Object> dados = new HashMap<>();
        dados.put(HelperDB.PUBLICACOES, getPublicacoes());

        usuariosRef.updateChildren(dados);
    }

    public Map<String,Object> converterParaMap() {
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put(HelperDB.ID_US, getId());
        usuarioMap.put(HelperDB.NOME_US, getNome());
        usuarioMap.put(HelperDB.NOME_PESQUISA_US, getNomePesquisa());
        usuarioMap.put(HelperDB.EMAIL_US, getEmail());
        usuarioMap.put(HelperDB.CAMINHO_FOTO_US, getCaminhoFoto());
        usuarioMap.put(HelperDB.SEGUIDORES, getSeguidores());
        usuarioMap.put(HelperDB.SEGUINDO, getSeguindo());
        usuarioMap.put(HelperDB.PUBLICACOES, getPublicacoes());

        return usuarioMap;
    }
}
