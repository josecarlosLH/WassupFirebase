package com.example.wassup.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wassup.MensajesActivity;
import com.example.wassup.R;
import com.example.wassup.modelos.Usuario;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {
    private Context context;
    private List<Usuario> listaUsuarios;
    private boolean estaConectado;

    public UsuarioAdapter(Context context, List<Usuario> listaUsuarios, boolean estaConectado) {
        this.context = context;
        this.listaUsuarios = listaUsuarios;
        this.estaConectado = estaConectado;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.usuario_item, parent, false);
        return new UsuarioAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);
        holder.usuario.setText(usuario.getNombreUsuario());

        //Si el usuario no tiene una imagen determinado, cargamos una por defecto
        if (usuario.getImagenURL().equals("default")) {
            Glide.with(context)
                    .load(R.drawable.ic_user)
                    .circleCrop()
                    .into(holder.usuarioIV);
        //Si el usuario tiene una imagen guardada, usamos la librería de Glide (previamente vista con Luis) para cargar nuestra imagen
        //en un marco circular.
        } else {
            Glide.with(context)
                    .load(usuario.getImagenURL())
                    .circleCrop()
                    .into(holder.usuarioIV);
        }

        //Estado del usuario
        if (estaConectado) {
            if (usuario.getEstado().equals("online")) {
                holder.onlineIV.setVisibility(View.VISIBLE);
                holder.offlineIV.setVisibility(View.GONE);
            } else {
                holder.onlineIV.setVisibility(View.GONE);
                holder.offlineIV.setVisibility(View.VISIBLE);
            }
        // Si no está conectado, ocultamos las IV de estado de conexión
        } else {
            holder.onlineIV.setVisibility(View.GONE);
            holder.offlineIV.setVisibility(View.GONE);
        }

        //Listener para cuando seleccionemos un usuario del RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MensajesActivity.class);
                i.putExtra("userid", usuario.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView usuario;
        public ImageView usuarioIV, onlineIV, offlineIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            usuario = itemView.findViewById(R.id.usuarioTV);
            usuarioIV = itemView.findViewById(R.id.usuarioIV);
            onlineIV = itemView.findViewById(R.id.onlineIV);
            offlineIV = itemView.findViewById(R.id.offlineIV);
        }
    }
}