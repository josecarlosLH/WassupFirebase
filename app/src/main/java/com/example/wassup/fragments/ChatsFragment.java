package com.example.wassup.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wassup.R;
import com.example.wassup.adapters.UsuarioAdapter;
import com.example.wassup.modelos.ListaChats;
import com.example.wassup.modelos.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private UsuarioAdapter usuarioAdapter;
    private List<Usuario> listaUsuarios;
    private List<ListaChats> listaChats;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.chatRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        listaChats = new ArrayList<>();

        //Aquí nos situamos en el nodo ListaChats y dentro de este, en las conversaciones que ha tenido el usuario
        reference = FirebaseDatabase.getInstance().getReference("ListaChats")
                .child(firebaseUser.getUid());
        //Controlamos los cambios internos en el nodo ListaChats para que sea actualizado al instante
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaChats.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListaChats listaChatsc = dataSnapshot.getValue(ListaChats.class);
                    listaChats.add(listaChatsc);
                }
                listarChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return view;
    }

    //Este método nos devuelve la lista de contctos con los que hemos chateado recientemente
    private void listarChats() {
        listaUsuarios = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Usuarios");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaUsuarios.clear();

                //Obtenemos todos los usuarios contactados y los almacenamos en el array
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    for (ListaChats chatlist : listaChats) {
                        if (usuario.getId().equals(chatlist.getId())) {
                            listaUsuarios.add(usuario);
                        }
                    }
                }
                //Mostramos la lista de usuarios en el RV
                usuarioAdapter = new UsuarioAdapter(getContext(), listaUsuarios, true);
                recyclerView.setAdapter(usuarioAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}