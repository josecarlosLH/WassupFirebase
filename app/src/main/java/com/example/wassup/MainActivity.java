package com.example.wassup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.wassup.fragments.ChatsFragment;
import com.example.wassup.fragments.PerfilFragment;
import com.example.wassup.fragments.UsuariosFragment;
import com.example.wassup.modelos.Usuario;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //Firebase
    FirebaseUser firebaseUser;
    DatabaseReference mRef;

    //Componentes
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtenemos el usuario
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //Declaramos el tab layout  el viewpager junto a su adapter
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.view_pager);

        //NOTA: getSupportFragmentManager() se pone dentro del constructor porque lo que hace es devolver una objeto de tipo FragmentManager.
        //Gracias al FragmentManager, el ViewPager podrá cargar los distintos fragment disponibles en el View Pager.
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        //Añadimos los Fragment que hemos creado dentro del paquete fragments al ViewPager
        viewPagerAdapter.anadirFragment(new ChatsFragment(), "Chats");
        viewPagerAdapter.anadirFragment(new UsuariosFragment(), getString(R.string.usuarios));
        viewPagerAdapter.anadirFragment(new PerfilFragment(), getString(R.string.perfil));

        //Le añadimos el adapter al ViewPager y cargamos el ViewPager en el TabLayout
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    //Inflamos el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //Aquí le añadimos la funcionalidad al botón cerrar sesión
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Creamos un switch en el que cada caso representa la funcionalidad de uno de los botones del menú
        switch (item.getItemId()) {
            case R.id.cerrarsesion:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, InicioActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                return true;
        }
        return false;
    }

    //Clase del adapter del ViewPager
    class ViewPagerAdapter extends FragmentPagerAdapter {
        //En este ArrayList almacenaremos los fragments que va a contener el ViewPager
        private ArrayList<Fragment> fragments;
        //En este ArrayList almacenaraemos los títulos de las pestañas correspondientes a cada fragment del ViewPager
        private  ArrayList<String> titulos;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titulos = new ArrayList<>();
        }

        //Lo que devolvemos con este método es la posición dentro del ArrayList en la que está el fragment que queramos seleccionar.
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        //Devolvemos el número de fragments almacenados en el ArrayList
        @Override
        public int getCount() {
            return fragments.size();
        }

        //Método para añadir un fragment con su correspondiente título
        public void anadirFragment(Fragment fragment, String titulo) {
            fragments.add(fragment);
            titulos.add(titulo);
        }

        //Obtener número de páginas del ViewPager
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titulos.get(position);
        }
    }

    //Comprobamos si el usuario está conectado o desconectado
    private  void comprobarEstado(String estado) {
        mRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("estado", estado);

        mRef.updateChildren(hashMap);
    }

    //Si el usuario inicia la aplicación, está en línea
    @Override
    protected void onResume() {
        super.onResume();
        comprobarEstado("online");
    }

    //Si el usuario pone a la aplicación en segundo plano o cierra la aplicación, se desconecta
    @Override
    protected void onPause() {
        super.onPause();
        comprobarEstado("offline");
    }
}