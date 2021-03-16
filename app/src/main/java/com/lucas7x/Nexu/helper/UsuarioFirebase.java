package com.lucas7x.Nexu.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.lucas7x.Nexu.model.Usuario;

public class UsuarioFirebase {
    /*
    public static String getIdUsuario() {
        //pegar dados do usuario logado
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuth();
        String email = usuario.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificar(email);

        return idUsuario;
    }
    */

    public static FirebaseUser getUsuarioAtual() {
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuth();
        return usuario.getCurrentUser();
    } //fim getUsuarioAtual

    public static Usuario getDadosUsuarioLogado() {
        FirebaseUser firebaseUsuario = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail(firebaseUsuario.getEmail());
        usuario.setNome(firebaseUsuario.getDisplayName());
        if(firebaseUsuario.getPhotoUrl() == null) {
            usuario.setCaminhoFoto("");
        } else {
            usuario.setCaminhoFoto(firebaseUsuario.getPhotoUrl().toString());
        }

        return usuario;

    }

    /*
    public static boolean atualizarFotoUsuario(Uri url) {
        try {

            FirebaseUser usuario = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();

            usuario.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()) {
                        Log.d("Perfil", "Erro ao atualizar foto do usuário.");
                    }
                }
            });

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    } //fim atualizarFotoUsuario

     */
    public static boolean atualizarNomeUsuario(String nome) {
        try {
            //usuario logado no app
            FirebaseUser usuario = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();

            usuario.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()) {
                        Log.d("Perfil", "Erro ao atualizar nome do usuário.");
                    }
                }
            });

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    } //fim atualizarNomeUsuario


}
