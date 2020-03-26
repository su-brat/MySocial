package com.example.mysocial;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    ImageView avatarIV;
    TextView nameTV, emailTV;
    EditText aboutET, nameET, phoneET;
    ImageView editabout, editname, editphone;
    Button updateBT;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Users");

        avatarIV = view.findViewById(R.id.avatar);
        nameTV = view.findViewById(R.id.name);
        emailTV = view.findViewById(R.id.email);

        updateBT = view.findViewById(R.id.update);
        updateBT.setVisibility(View.GONE);
        updateBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the Edittext values to the database


                aboutET.setFocusable(true);
                aboutET.setFocusableInTouchMode(true);
                nameET.setFocusable(true);
                nameET.setFocusableInTouchMode(true);
                phoneET.setFocusable(true);
                phoneET.setFocusableInTouchMode(true);
                updateBT.setVisibility(View.GONE);
            }
        });

        aboutET = view.findViewById(R.id.aboutET);
        aboutET.setFocusableInTouchMode(false);
        aboutET.setFocusable(false);
        nameET = view.findViewById(R.id.nameET);
        nameET.setFocusable(false);
        nameET.setFocusableInTouchMode(false);
        phoneET = view.findViewById(R.id.phoneET);
        phoneET.setFocusableInTouchMode(false);
        phoneET.setFocusable(false);

        editabout = view.findViewById(R.id.edit1);
        editabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutET.setFocusable(true);
                aboutET.setFocusableInTouchMode(true);
                updateBT.setVisibility(View.VISIBLE);
            }
        });
        editname = view.findViewById(R.id.edit2);
        editname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameET.setFocusable(true);
                nameET.setFocusableInTouchMode(true);
                updateBT.setVisibility(View.VISIBLE);
            }
        });
        editphone = view.findViewById(R.id.edit3);
        editphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneET.setFocusable(true);
                phoneET.setFocusableInTouchMode(true);
                updateBT.setVisibility(View.VISIBLE);
            }
        });

        Query query = reference.orderByChild("Email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = "" + ds.child("Name").getValue();
                    String email = "" + ds.child("Email").getValue();
                    String image = "" + ds.child("Image").getValue();
                    String phone = "" + ds.child("Phone").getValue();
                    String about = "" + ds.child("About").getValue();
                    nameTV.setText(name);
                    emailTV.setText(email);
                    aboutET.setText(about);
                    nameET.setText(name);
                    phoneET.setText(phone);
                    try {
                        Picasso.get().load(image).into(avatarIV);
                    }
                    catch (Exception e) {
                        //Picasso.get().load(R.drawable.ic_avatar_white).into(avatarIV);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}
