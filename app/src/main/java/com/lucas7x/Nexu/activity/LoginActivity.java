package com.lucas7x.Nexu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editSenha;
    private Button buttonEntrar;
    private ProgressBar progressLogin;

    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarComponentes();
        progressLogin.setVisibility(View.GONE);
    }

    public void inicializarComponentes() {
        editEmail = findViewById(R.id.editLoginEmail);
        editSenha = findViewById(R.id.editLoginSenha);
        buttonEntrar = findViewById(R.id.buttonLoginEntrar);
        progressLogin = findViewById(R.id.progressLogin);

        editEmail.requestFocus();
    }

    public void abrirCadastro(View view) {
        Intent i = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(i);
    }

    public void validarLogin(View view) {
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        if (!email.isEmpty() && !senha.isEmpty()) {
            usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setSenha(senha);

            loginUsuario(usuario);
        } else {
            Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
    }

    public void loginUsuario(Usuario u) {
        progressLogin.setVisibility(View.VISIBLE);

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressLogin.setVisibility(View.GONE);

                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    finish();

                                } else {
                                    progressLogin.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, "Erro ao fazer login.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
    }
}