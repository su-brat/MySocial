package com.example.mysocial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {


    Context context;
    List<User> userList;

    public AdapterUsers(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String userimage = userList.get(position).getImage();
        String username = userList.get(position).getName();
        final String useremail = userList.get(position).getEmail();

        holder.nameTV.setText(username);
        holder.emailTV.setText(useremail);
        try {
            Picasso.get().load(userimage).placeholder(R.drawable.ic_face_black).into(holder.avatarIV);
        }
        catch (Exception e) {

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, useremail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView avatarIV;
        TextView nameTV, emailTV;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            avatarIV = itemView.findViewById(R.id.avatar);
            nameTV = itemView.findViewById(R.id.name);
            emailTV = itemView.findViewById(R.id.email);
        }
    }
}
