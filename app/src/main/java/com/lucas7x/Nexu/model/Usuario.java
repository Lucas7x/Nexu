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

    public Map<String,Object> converterParaMap() {
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put(HelperDB.ID_US, getId());
        usuarioMap.put(HelperDB.NOME_US, getNome());
        usuarioMap.put(HelperDB.NOME_PESQUISA_US, getNomePesquisa());
        usuarioMap.put(HelperDB.EMAIL_US, getEmail());
        usuarioMap.put(HelperDB.CAMINHO_FOTO_US, getCaminhoFoto());

        return usuarioMap;
    }
}
