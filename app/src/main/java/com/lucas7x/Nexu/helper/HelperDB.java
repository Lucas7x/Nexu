package com.lucas7x.Nexu.helper;

public class HelperDB {

    //nós iniciais (classes)
    public static final String USUARIOS = "usuarios";
    public static final String MENSAGENS = "mensagens";
    public static final String SEGUIDORES = "seguidores";
    public static final String SEGUINDO = "seguindo";
    public static final String PUBLICACOES = "publicacoes";
    public static final String FEEDS = "feeds";
    public static final String CURTIDAS = "curtidas";
    public static final String COMENTARIOS = "comentarios";


    //referentes aos usuarios
    public static final String ID_US = "id";
    public static final String NOME_US = "nome";
    public static final String NOME_PESQUISA_US = "nomePesquisa";
    public static final String EMAIL_US = "email";
    public static final String CAMINHO_FOTO_US = "caminhoFoto";

    //referentes as publicacoes
    public static final String ID_PB = "idPublicacao";
    public static final String DESCRICAO_PB = "descricao";
    public static final String NOME_USUARIO_PB = "nomeUsuario";
    public static final String CAMINHO_FOTO_PB = "caminhoFoto";
    public static final String FOTO_USUARIO_PB = "caminhoFotoUsuario";


    //referentes as curtidas
    public static final String ID_PUBLICACAO_CTD = "id";
    public static final String ID_USUARIO_CTD = "idUsuario";
    public static final String QTD_CURTIDAS_CTD = "qtdCurtidas";
    public static final String CAMINHO_FOTO_CTD = "caminhoFoto";
    public static final String NOME_USUARIO_CTD = "nomeUsuario";

    //referentes aos comentarios
    public static final String ID_COMENTARIO_CM = "idComentario";
    public static final String ID_PUBLICACAO_CM = "idComentario";
    public static final String ID_USUARIO_CM = "idComentario";
    public static final String CAMINHO_FOTO_USUARIO_CM = "idComentario";
    public static final String COMENTARIO_CM = "idComentario";


}
