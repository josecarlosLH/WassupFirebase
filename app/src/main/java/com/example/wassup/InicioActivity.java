package com.example.wassup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InicioActivity extends AppCompatActivity {

    //Componentes
    EditText usuarioET, contrasenaET;
    Button conectarBT, registrarBT;

    //Firebase
    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Comprobamos si el usuario se conecta por primera vez
        if (firebaseUser != null) {
            //Si ya se ha conectado previamente, lo enviamos a la pantalla principal directamente
            Intent i = new Intent(InicioActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        //Inicializamos componentes
        usuarioET = findViewById(R.id.usuarioET);
        contrasenaET = findViewById(R.id.contrasenaET);
        conectarBT = findViewById(R.id.conectrBT);
        registrarBT = findViewById(R.id.registrarahoraBT);

        //Firebase Auth
        auth = FirebaseAuth.getInstance();

        //Listener al botón registarse ahora
        registrarBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InicioActivity.this, RegistroActivity.class);
                startActivity(i);

            }
        });

        //Listener al botón conectarse
        conectarBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_texto = usuarioET.getText().toString();
                String contrasena_texto = contrasenaET.getText().toString();

                //Comprobamos si los campos están vacíos
               if (TextUtils.isEmpty(email_texto) || TextUtils.isEmpty(contrasena_texto)) {
                   Toast.makeText(InicioActivity.this, R.string.rellenar, Toast.LENGTH_SHORT).show();
               } else {
                   auth.signInWithEmailAndPassword(email_texto, contrasena_texto)
                   //Tenemos que comprobar si el usuario existe en la base de datos
                           .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Si los datos son correctos, pasamos a la pantalla principal
                                Intent i = new Intent(InicioActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            } else {
                                //Si no son correctos, le damos un toast indicando que hay error
                                Toast.makeText(InicioActivity.this, R.string.credencialesincorrectas, Toast.LENGTH_SHORT).show();
                            }
                       }
                   });
               }
            }
        });
    }
}