package com.example.mysocial.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysocial.R;
import com.example.mysocial.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.Holder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<Chat> chatList;

    FirebaseUser user;

    public AdapterChat(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new Holder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new Holder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String message = chatList.get(position).getMessage();
        String timestamp = chatList.get(position).getTimestamp();

        holder.msgTV.setText(message);
        holder.timeTV.setText(timestamp);

        if(holder.isSeen!=null) {
            if (chatList.get(position).isSeen())
                holder.isSeen.setImageResource(R.drawable.ic_double_check_black);
            else
                holder.isSeen.setImageResource(R.drawable.ic_check_black);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().endsWith(user.getUid()))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView isSeen;
        TextView msgTV, timeTV;

        public Holder(@NonNull View itemView) {
            super(itemView);

            msgTV = itemView.findViewById(R.id.msg);
            timeTV = itemView.findViewById(R.id.timestamp);
            isSeen = itemView.findViewById(R.id.msgstatus);
        }
    }
}
