package com.example.mysocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysocial.adapters.AdapterChat;
import com.example.mysocial.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView avatarIV, sendBT, backBT;
    TextView nameTV, statusTV;
    EditText msgET;

    FirebaseAuth mAuth;

    FirebaseDatabase database;
    DatabaseReference reference;

    ValueEventListener seenListener;
    DatabaseReference referenceForSeen;

    List<Chat> chatList;
    AdapterChat adapterChat;

    String uid, selectedUid;

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        referenceForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chats);
        avatarIV = findViewById(R.id.avatar);
        nameTV = findViewById(R.id.name);
        statusTV = findViewById(R.id.status);
        msgET = findViewById(R.id.msg);
        msgET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                updateTypingStatus(selectedUid);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTypingStatus("");
            }
        });
        sendBT = findViewById(R.id.send);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        backBT = findViewById(R.id.back);
        backBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        selectedUid = intent.getStringExtra("selectedUid");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        Query query = reference.orderByChild("Uid").equalTo(selectedUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    String name = (String) ds.child("Name").getValue();
                    String image = (String) ds.child("Image").getValue();
                    String signinTimestamp = (String) ds.child("SignInTimestamp").getValue();
                    String typingTo = (String) ds.child("TypingTo").getValue();

                    if(name.length()==0)
                        name = "No name";
                    nameTV.setText(name);
                    if(typingTo.length()==0)
                        statusTV.setText(signinTimestamp);
                    else
                        statusTV.setText("typing");
                    try {
                        Picasso.get().load(image).placeholder(R.drawable.avatar).into(avatarIV);
                    }
                    catch (Exception e) {}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msgET.getText().toString().trim();
                if(TextUtils.isEmpty(message)) {
                    Toast.makeText(ChatActivity.this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendMessage(message, uid, selectedUid);
                    msgET.setText("");
                }
            }
        });

        readMessages();

        seenMessage();

    }

    private void seenMessage() {
        referenceForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = referenceForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    if(chat.getReceiver().equals(uid) && chat.getSender().equals(selectedUid)) {
                        HashMap<String, Object> hasSeen = new HashMap<>();
                        hasSeen.put("Seen", true);
                        ds.getRef().updateChildren(hasSeen);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Chats");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    if((chat.getReceiver().equals(uid) && chat.getSender().equals(selectedUid)) || (chat.getReceiver().equals(selectedUid) && chat.getSender().equals(uid))) {
                        chatList.add(chat);
                    }
                    adapterChat = new AdapterChat(ChatActivity.this, chatList);
                    adapterChat.notifyDataSetChanged();

                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message, String senderUid, String receiverUid) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        String timestamp = DateFormat.format("dd-MM-yy  hh:mm aa", calendar).toString();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Message", message);
        hashMap.put("Receiver", receiverUid);
        hashMap.put("Sender", senderUid);
        hashMap.put("Timestamp", timestamp);
        hashMap.put("Seen", false);
        databaseReference.child("Chats").push().setValue(hashMap);
    }

    private void updateTypingStatus(String to) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("TypingTo", to);
        ref.updateChildren(hashMap);
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null) {
            uid = user.getUid();
        }
        else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

}
