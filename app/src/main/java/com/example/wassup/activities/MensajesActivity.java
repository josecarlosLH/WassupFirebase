package com.example.wassup.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.wassup.R;
import com.example.wassup.adapters.MensajesAdapter;
import com.example.wassup.modelos.Chat;
import com.example.wassup.modelos.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MensajesActivity extends AppCompatActivity {

    //Componentes
    TextView nombreUsuario;
    ImageView imageView;
    EditText mensajeET;
    ImageButton enviarBT;

    //Firebase
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Intent intent;

    //Chat
    MensajesAdapter mensajesAdapter;
    List<Chat> listaChat;
    RecyclerView recyclerView;
    String usuarioid;

    ValueEventListener mensajeLeidoEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes);

        //Componentes del layout
        imageView = findViewById(R.id.perfilIV);
        nombreUsuario = findViewById(R.id.nombreUsuarioTV);
        enviarBT = findViewById(R.id.enviarBT);
        mensajeET = findViewById(R.id.mensajeET);

        //RecyclerView
        recyclerView = findViewById(R.id.mensajesRV);
        recyclerView.setHasFixedSize(true);
        //Con LinearLayoutManager establecemos un layout para mostrar el contenido de forma organizada
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true); //Con este método, los mensajes empezarán a mostrarse de abajo a arriba
        recyclerView.setLayoutManager(linearLayoutManager);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("");
        //setDisplayHomeAsUpEnabled(true) añade el icono > al toolbar para poder clicarlo y regresar a la pantalla principal. Mismo funcionamiento que
        //al pulsar el botón atrás del dispositivo
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Cuando pulsamos el botón de atrás del dispositivo, finalizará la activity de los mensajes para volver a la pantalla principal
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Recibimos la id del usuario que seleccionamos en el RecyclerView de usuarios
        intent = getIntent();
        usuarioid = intent.getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance()
                .getReference("Usuarios")
                .child(usuarioid);

        //Cargamos el nombre, la imagen de perfil y los mensajes que hemos intercambiado con ese usuario
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                nombreUsuario.setText(usuario.getNombreUsuario());


                if (usuario.getImagenURL().equals("default")) {
                    Glide.with(MensajesActivity.this)
                            .load(R.drawable.ic_user)
                            .circleCrop()
                            .into(imageView);
                } else {
                    Glide.with(MensajesActivity.this)
                            .load(usuario.getImagenURL())
                            .circleCrop()
                            .into(imageView);
                }

                cargarMensajes(firebaseUser.getUid(), usuarioid, usuario.getImagenURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        enviarBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensaje = mensajeET.getText().toString();
                //Comprobamos que el mensaje no esté vacío para poder enviarlo
                if (!mensaje.equals("")) {
                    enviarMensaje(firebaseUser.getUid(), usuarioid, mensaje);
                }

                mensajeET.setText("");
            }
        });

        estadoMensaje(usuarioid);
    }

    private void estadoMensaje(String usuarioid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        mensajeLeidoEvent = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if (chat.getReceptor().equals(firebaseUser.getUid()) && chat.getEmisor().equals(usuarioid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("visto", true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void enviarMensaje(String emisor, String receptor, String mensaje) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("emisor", emisor);
        hashMap.put("receptor", receptor);
        hashMap.put("mensaje", mensaje);
        hashMap.put("visto", false);

        //Con esta línea, guardamos los datos del mensaje en el nodo Chats indicando el mensaje, el receptor y el emisor.
        reference.child("Chats").push().setValue(hashMap);

        //Obtenemos el historial ordenado de los usuarios con los que YA hemos contactado
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ListaChats")
                .child(firebaseUser.getUid())
                .child(usuarioid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef.child("id").setValue(usuarioid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    //Cargamos los mensajes de un chat concreto y obtenemos los datos del usuario con el que estamos chateando
    private void cargarMensajes(String miId, String usuarioid, String imgURL) {
        listaChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaChat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    //Con este if comprobamos si los mensajes están siendo enviados por mí o por la otra persona
                    if (chat.getReceptor().equals(miId) && chat.getEmisor().equals(usuarioid)
                            || chat.getReceptor().equals(usuarioid) && chat.getEmisor().equals(miId)) {
                        listaChat.add(chat);
                    }
                    //Cargamos los mensajes en el RV
                    mensajesAdapter = new MensajesAdapter(MensajesActivity.this, listaChat, imgURL);
                    recyclerView.setAdapter(mensajesAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private  void comprobarEstado(String estado) {
        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("estado", estado);

        reference.updateChildren(hashMap);
    }

    //Si el usuario inicia la aplicación, está en línea
    @Override
    protected void onResume() {
        super.onResume();
        comprobarEstado("online");
    }

    //Si el usuario pone a la aplicación en segundo plano o cierra la aplicación, se desconecta y paramos el listener para marcar los mensajes como vistos
    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(mensajeLeidoEvent);
        comprobarEstado("offline");
    }
}