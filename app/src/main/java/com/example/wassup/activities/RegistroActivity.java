package com.example.wassup.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wassup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistroActivity extends AppCompatActivity {

    //Componentes del layout
    EditText usuarioET, contrasenaET, emailET;
    Button registroBT;

    //Firebase
    FirebaseAuth auth;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Inicializando componentes
        usuarioET = findViewById(R.id.usuarioEditText);
        contrasenaET = findViewById(R.id.passwordEditText);
        emailET = findViewById(R.id.emailEditText);
        registroBT = findViewById(R.id.registroButton);

        //Firebase Auth
        auth = FirebaseAuth.getInstance();

        //Añadimos el listener al botón de registro
        registroBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuario_texto = usuarioET.getText().toString();
                String email_texto = emailET.getText().toString();
                String contrasena_texto = contrasenaET.getText().toString();

                //Si los campos no son rellenados, hacemos un toast indicando el error. Si están correctos, creo el usuario
                if (TextUtils.isEmpty(usuario_texto) || TextUtils.isEmpty(email_texto) || TextUtils.isEmpty(contrasena_texto)) {
                    Toast.makeText(RegistroActivity.this, R.string.rellenarcampos, Toast.LENGTH_SHORT).show();
                } else {
                    registrarUsuario(usuario_texto, email_texto, contrasena_texto);
                }
            }
        });
    }

    private void registrarUsuario(final String usuario, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            mRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(userid);

                            //HashMap
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("usuario", usuario);
                            hashMap.put("imagenURL", "default");
                            hashMap.put("estado", "offline");

                            //Abrir la activity main tras registrarnos con éxito
                            mRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Vamos desde el activity de registro al main activity
                                        Intent i = new Intent(RegistroActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegistroActivity.this, R.string.emailinvalido, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}