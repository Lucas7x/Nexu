package com.lucas7x.Nexu.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lucas7x.Nexu.R;
import com.lucas7x.Nexu.helper.ConfiguracaoFirebase;
import com.lucas7x.Nexu.helper.HelperSTG;
import com.lucas7x.Nexu.helper.Permissao;
import com.lucas7x.Nexu.helper.UsuarioFirebase;
import com.lucas7x.Nexu.model.Usuario;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private String[] permissoesNecessarias = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private CircleImageView imageFotoEditarPerfil;
    private TextView textAlterarFoto;
    private TextInputEditText editNomePerfil, editEmailPerfil;
    private Button buttonSalvarAlteracoes;
    private ImageButton buttonCamera, buttonGaleria;

    private Usuario usuarioLogado;
    private StorageReference storageRef;
    private String idUsuario;

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //validar permissões
        Permissao.validarPermissões(permissoesNecessarias, this, 1);

        //configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        idUsuario = UsuarioFirebase.getIdUsuario();

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_email_preto);

        //inicializar componentes
        inicializarComponentes();


        //recuperar dados do usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNomePerfil.setText(usuarioPerfil.getDisplayName());
        editEmailPerfil.setText(usuarioPerfil.getEmail());

        buttonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(i.resolveActivity(getPackageManager()) == null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                } else {
                    Toast.makeText(
                            EditarPerfilActivity.this,
                            "Não conseguiu abrir a galeria",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(i.resolveActivity(getPackageManager()) == null) {
                    startActivityForResult(i, SELECAO_CAMERA);
                } else {
                    Toast.makeText(
                            EditarPerfilActivity.this,
                            "Não conseguiu abrir a câmera",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

    }

    public void inicializarComponentes() {
        imageFotoEditarPerfil = findViewById(R.id.imageFotoEditarPerfil);
        textAlterarFoto = findViewById(R.id.textAlterarFoto);
        editNomePerfil = findViewById(R.id.editNomeEditarPerfil);
        editEmailPerfil = findViewById(R.id.editEmailEditarPerfil);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarEditarPerfil);

        buttonCamera = findViewById(R.id.buttonCameraEditarPerfil);
        buttonGaleria = findViewById(R.id.buttonGaleriaEditarPerfil);

        editEmailPerfil.setFocusable(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void salvarAlteracoes(View view) {
        String nomeAtualizado = editNomePerfil.getText().toString();

        //atualizar nome no perfil
        UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

        //atualizar nome no banco de dados
        usuarioLogado.setNome(nomeAtualizado);
        usuarioLogado.atualizar();

        Toast.makeText(
                EditarPerfilActivity.this,
                R.string.sucesso_alteracao,
                Toast.LENGTH_SHORT
        ).show();
    }

    /*
    public void alterarFoto(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, SELECAO_CAMERA);

        }
    }

     */

    public void abrirCamera(View view) {

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(i.resolveActivity(getPackageManager()) == null) {
            startActivityForResult(i, SELECAO_CAMERA);
        } else {
            Toast.makeText(
                    EditarPerfilActivity.this,
                    "Não conseguiu abrir camera",
                    Toast.LENGTH_SHORT
            ).show();
        }

    } //fim abrirCameraConfiguracoes


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {

            Bitmap img = null;

            try {
                //seleção apenas da galeria
                switch (requestCode) {
                    case SELECAO_CAMERA:
                        img = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        if (android.os.Build.VERSION.SDK_INT >= 29) {
                            ImageDecoder.Source imageDecoder = ImageDecoder.createSource(getContentResolver(), localImagemSelecionada);
                            img = ImageDecoder.decodeBitmap(imageDecoder);
                            //Log.i("SDK", ">= 29");
                        } else {
                            img = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                            //Log.i("SDK", "< 29");
                        }
                        break;

                }

                //caso a imagem tenha sido selecionada
                if(img != null) {
                    //mostra imagem na tela
                    imageFotoEditarPerfil.setImageBitmap(img);

                    //recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //salvar imagem no firebase
                    StorageReference imagemRef = storageRef
                            .child(HelperSTG.IMAGENS)
                            .child(HelperSTG.IMAGENS_PERFIL)
                            .child(idUsuario + ".jpeg");
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(
                                    EditarPerfilActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //recuperar local da foto
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();

                                    //salvar foto no firebase
                                    atualizarFotoUsuario(url);
                                }
                            });

                        }
                    });


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(
                    EditarPerfilActivity.this,
                    "Nao foi possivel abrir a galeria",
                    Toast.LENGTH_SHORT
            ).show();
        }
    } // fim do onActivityResult

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //para verificar as permissões
        for(int permissaoResultado : grantResults) {
            if(permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }
    } //fim do método onRequestPermissionsResult

    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar o aplicativo é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    } //fim do método alertaValidacaoPermissao

    private void atualizarFotoUsuario(Uri url) {
        //atualizar foto no perfil
        UsuarioFirebase.atualizarFotoUsuario(url);

        //atualizar foto no firebase
        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.atualizar();

        Toast.makeText(
                EditarPerfilActivity.this,
                "Sua foto foi atualizada",
                Toast.LENGTH_SHORT
        ).show();
    }
}