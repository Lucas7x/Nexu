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

public class CadastroActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editSenha;
    private Button buttonCadastrar;
    private ProgressBar progressCadastro;

    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        verificarUsuarioLogado();
        inicializarComponentes();
        progressCadastro.setVisibility(View.GONE);

    }

    public void inicializarComponentes() {
        editNome = findViewById(R.id.editCadastroNome);
        editEmail = findViewById(R.id.editCadastroEmail);
        editSenha = findViewById(R.id.editCadastroSenha);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);
        progressCadastro = findViewById(R.id.progressCadastrar);

        editNome.requestFocus();
    }

    public void verificarUsuarioLogado() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        if (autenticacao.getCurrentUser() != null) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void validarCadastro(View view) {
                String nome = editNome.getText().toString();
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        if (!nome.isEmpty() && !email.isEmpty() && !senha.isEmpty()) {
            usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(senha);

            cadastrarUsuario(usuario);
        } else {
            Toast.makeText(CadastroActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
    }

    public void cadastrarUsuario(Usuario u) {
        progressCadastro.setVisibility(View.VISIBLE);
        
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
        .addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                         if (task.isSuccessful()) {
                             progressCadastro.setVisibility(View.GONE);

                             Toast.makeText(CadastroActivity.this, "Usuário cadastrado com sucesso.", Toast.LENGTH_SHORT).show();

                             Intent i = new Intent(getApplicationContext(), MainActivity.class);
                             startActivity(i);
                             finish();

                         } else {
                             progressCadastro.setVisibility(View.GONE);

                             String excessao = "";
                             try {
                                 throw task.getException();
                             } catch (FirebaseAuthWeakPasswordException e) {
                                 excessao = "Digite uma senha mais forte.";
                             } catch (FirebaseAuthInvalidCredentialsException e) {
                                 excessao = "Digite um e-mail válido.";
                             } catch (FirebaseAuthUserCollisionException e) {
                                 excessao = "Esta conta já foi cadastrada.";
                             }catch (Exception e) {
                                 excessao = "Erro ao cadastrar usuário: " + e.getMessage();
                             }

                             Toast.makeText(CadastroActivity.this, excessao, Toast.LENGTH_SHORT).show();
                         }
                    }
                }
        );

    }
}