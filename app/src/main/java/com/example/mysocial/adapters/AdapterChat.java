package com.example.mysocial.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysocial.R;
import com.example.mysocial.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        String message = chatList.get(position).getMessage();
        String timestamp = chatList.get(position).getTimestamp();

        holder.msgTV.setText(message);
        holder.timeTV.setText(timestamp);

        holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Do you really want to delete this message?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(position);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });

        if(holder.isSeen!=null) {
            if (chatList.get(position).isSeen())
                holder.isSeen.setImageResource(R.drawable.ic_double_check_black);
            else
                holder.isSeen.setImageResource(R.drawable.ic_check_black);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void deleteMessage(final int position) {
        final String uid = user.getUid();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        long currentTime = calendar.getTimeInMillis();
        String msgTimestamp = chatList.get(position).getTimestamp();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy  hh:mm aa");
        try {
            Date date = simpleDateFormat.parse(msgTimestamp);
            long timestampInMilliseconds = date.getTime();
            long maxTimeLimit = 600000;
            if (currentTime - timestampInMilliseconds <= maxTimeLimit) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
                Query query = ref.orderByChild("Timestamp").equalTo(msgTimestamp);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (uid.equals(ds.child("Sender").getValue().toString())) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("Message", "%This message was deleted%");
                                ds.getRef().updateChildren(hashMap);
                                Toast.makeText(context, "Message deleted.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Cannot delete message.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } else {
                Toast.makeText(context, "Cannot delete message.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
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
        LinearLayout messageLayout;

        public Holder(@NonNull View itemView) {
            super(itemView);

            msgTV = itemView.findViewById(R.id.msg);
            timeTV = itemView.findViewById(R.id.timestamp);
            isSeen = itemView.findViewById(R.id.msgstatus);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }
}
