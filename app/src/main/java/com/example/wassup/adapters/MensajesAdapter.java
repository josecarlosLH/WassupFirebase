package com.example.wassup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wassup.R;
import com.example.wassup.modelos.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.ViewHolder> {
    private Context context;
    private List<Chat> listaChats;
    private String imgURL;

    // CREAMOS VARIABLES FINALES PARA SABER SI EL MENSAJE CON EL QUE TRABAJAMOS ES ENVIADO O RECIBIDO
    public static final int MSJ_RECIBIDO = 0;
    public static final int MSJ_ENVIADO = 1;

    //Firebase
    FirebaseUser firebaseUser;

    public MensajesAdapter(Context context, List<Chat> listaChats, String imgURL) {
        this.context = context;
        this.listaChats = listaChats;
        this.imgURL = imgURL;
    }

    @NonNull
    @Override
    public MensajesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Dependiendo del tipo de mensaje, usaremos el layout de enviado o recibido para mostrar (inflar) el mensaje
        if (viewType == MSJ_ENVIADO) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_enviado, parent, false);
            return new MensajesAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_recibido, parent, false);
            return new MensajesAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MensajesAdapter.ViewHolder holder, int position) {
        Chat chat = listaChats.get(position);
        holder.mensajeTV.setText(chat.getMensaje());

        //Si el usuario no tiene una imagen determinado, cargamos una por defecto
        if (imgURL.equals("default")) {
            Glide.with(context)
                    .load(R.drawable.ic_user)
                    .circleCrop()
                    .into(holder.perfilIV);
        //Si el usuario tiene una imagen guardada, usamos la librería de Glide (previamente vista con Luis)
        //para cargar nuestra imagen cargar nuestra imagen para cargar nuestra imagen en un marco circular.
        } else {
            Glide.with(context)
                    .load(imgURL)
                    .circleCrop()
                    .into(holder.perfilIV);
        }

        if (position == listaChats.size() - 1) {
            if (chat.isEsLeido()) {
                holder.leidoTV.setText(R.string.leido);
            } else {
                holder.leidoTV.setText(R.string.enviado);
            }
        } else {
            holder.leidoTV.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listaChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mensajeTV;
        public ImageView perfilIV;
        public TextView leidoTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mensajeTV = itemView.findViewById(R.id.mensajeTV);
            perfilIV = itemView.findViewById(R.id.imagenPerfilChatIV);
            leidoTV = itemView.findViewById(R.id.leidoTV);
        }

    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Con esto comprobamos si el usuario que ha enviado el mensaje es el mismo usuario que está conectado en nuestro dispositivo.
        //De esta forma, sabremos que un mensaje está siendo ENVIADO
        if (listaChats.get(position).getEmisor().equals(firebaseUser.getUid())) {
            return MSJ_ENVIADO;
        } else {
            //Si el usuario del mensaje no está conectado en nuestro dispositivo, podemos saber que el mensaje es RECIBIDO
            return MSJ_RECIBIDO;
        }
    }
}