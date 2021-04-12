package com.lucas7x.Nexu.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperDB;
import com.lucas7x.Nexu.helper.UsuarioFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Publicacao implements Serializable {

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

    public boolean salvar(DataSnapshot seguidoresSnapshot) {

        Map objeto = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        DatabaseReference dbRef = ConfiguracaoFirebase.getFirebaseDatabase();

        //referencia para publicacao
        String caminhoPublicacao = "/" + getIdUsuario() + "/" + getIdPublicacao();
        objeto.put("/" + HelperDB.PUBLICACOES + caminhoPublicacao, this);

        //referencia para feed
        for(DataSnapshot seguidores: seguidoresSnapshot.getChildren()) {
            /*
            estrutura feed
            feed
                id_seguidor
                    id_publicacao
                        publicacao
             */

            String idSeguidor = seguidores.getKey();

            //monta um objeto publicacao para salvar no feed dos seguidores
            HashMap<String, Object> dadosPublicacao = new HashMap<>();
            dadosPublicacao.put(HelperDB.ID_PB, getIdPublicacao());
            dadosPublicacao.put(HelperDB.DESCRICAO_PB, getDescricao());
            dadosPublicacao.put(HelperDB.CAMINHO_FOTO_PB, getCaminhoFoto());

            dadosPublicacao.put(HelperDB.NOME_USUARIO_PB, usuarioLogado.getNome());
            dadosPublicacao.put(HelperDB.FOTO_USUARIO_PB, usuarioLogado.getCaminhoFoto());

            String caminhoFeed = "/" + idSeguidor + "/" + getIdPublicacao();
            objeto.put("/" + HelperDB.FEEDS + caminhoFeed, dadosPublicacao);
        }


        dbRef.updateChildren(objeto);
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
