package com.lucas7x.Nexu.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {
    public static boolean validarPermissões(String[] permissoes, Activity contexto, int requestCode) {
        if(Build.VERSION.SDK_INT >= 23) {       //versão maior q marshmallow
            List<String> listaPermissoes = new ArrayList<>();

            //percorre a lista de permissões verificando se já estão liberadas
            for (String permissao:permissoes) {
                Boolean temPermissao = ContextCompat.checkSelfPermission(contexto, permissao) == PackageManager.PERMISSION_GRANTED;
                if (!temPermissao) listaPermissoes.add(permissao);
            }
            //se a lista estiver vazia, nao precisa solicitar novas permissões
            if(listaPermissoes.isEmpty()) return true;
            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            //solicita permissões
            ActivityCompat.requestPermissions(contexto, novasPermissoes, requestCode);
        }

        return true;
    }
}
