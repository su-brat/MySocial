package com.example.mysocial;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    StorageReference storageReference;

    String storagePath = "Users_Avatars/";

    ImageView avatarIV;
    TextView nameTV, emailTV;
    EditText aboutET, nameET, phoneET;
    ImageView editabout, editname, editphone;
    Button updateBT;

    ProgressDialog pd;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[], storagePermissions[];

    Uri imageURI;

    String avatar;

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
        storageReference = FirebaseStorage.getInstance().getReference();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        avatarIV = view.findViewById(R.id.avatar);
        avatarIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update profile avatar
                avatar = "Image";
                showImagePicDialog();

                pd.setMessage("Updating avatar...");
                pd.show();
                // Update in database and reflect in the imageview

                pd.dismiss();
            }
        });
        nameTV = view.findViewById(R.id.name);
        emailTV = view.findViewById(R.id.email);

        updateBT = view.findViewById(R.id.update);
        updateBT.setVisibility(View.GONE);
        updateBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Updating to the database...");
                pd.show();
                // Update the Edittext values to the database
                updateDatabase("About", aboutET.getText().toString().trim());
                updateDatabase("Name", nameET.getText().toString().trim());
                updateDatabase("Phone", phoneET.getText().toString().trim());
                aboutET.setFocusable(false);
                aboutET.setFocusableInTouchMode(false);
                aboutET.setClickable(false);
                nameET.setFocusable(false);
                nameET.setFocusableInTouchMode(false);
                nameET.setClickable(false);
                phoneET.setFocusable(false);
                phoneET.setFocusableInTouchMode(false);
                phoneET.setClickable(false);
                updateBT.setVisibility(View.GONE);
                pd.dismiss();
            }
        });

        aboutET = view.findViewById(R.id.aboutET);
        aboutET.setFocusableInTouchMode(false);
        aboutET.setFocusable(false);
        aboutET.setClickable(false);
        nameET = view.findViewById(R.id.nameET);
        nameET.setFocusable(false);
        nameET.setFocusableInTouchMode(false);
        nameET.setClickable(false);
        phoneET = view.findViewById(R.id.phoneET);
        phoneET.setFocusableInTouchMode(false);
        phoneET.setFocusable(false);
        phoneET.setClickable(false);

        editabout = view.findViewById(R.id.edit1);
        editabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutET.setFocusable(true);
                aboutET.setFocusableInTouchMode(true);
                aboutET.setClickable(true);
                aboutET.requestFocus();
                updateBT.setVisibility(View.VISIBLE);
            }
        });
        editname = view.findViewById(R.id.edit2);
        editname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameET.setFocusable(true);
                nameET.setFocusableInTouchMode(true);
                nameET.setClickable(true);
                nameET.requestFocus();
                updateBT.setVisibility(View.VISIBLE);
            }
        });
        editphone = view.findViewById(R.id.edit3);
        editphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneET.setFocusable(true);
                phoneET.setFocusableInTouchMode(true);
                phoneET.requestFocus();
                phoneET.performClick();
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
        pd = new ProgressDialog(getContext());

        return view;
    }

    private void updateDatabase(String key, String value) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(key, value);

        reference.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Updated successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Updation failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkStoragePermission() {
        boolean result = (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result1 = (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        boolean result2 = (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0) {
                    boolean cameraAccepted = (grantResults[0]==PackageManager.PERMISSION_GRANTED);
                    boolean writeStorageAccepted = (grantResults[1]==PackageManager.PERMISSION_GRANTED);
                    if(cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(getActivity(), "Please enable camera and storage permissions", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0) {
                    boolean readStorageAccepted = (grantResults[0]==PackageManager.PERMISSION_GRANTED);
                    if(readStorageAccepted) {
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode==RESULT_OK) {
            if(requestCode==IMAGE_PICK_GALLERY_CODE) {
                imageURI = data.getData();
                uploadAvatar(imageURI);
            }
            if(requestCode==IMAGE_PICK_CAMERA_CODE) {
                uploadAvatar(imageURI);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadAvatar(Uri uri) {
        String filePathAndName = storagePath+""+avatar+"_"+user.getUid();

        StorageReference storageReference2 = storageReference.child(filePathAndName);
        storageReference2.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                if(uriTask.isSuccessful()) {
                    HashMap<String, Object> results = new HashMap<>();
                    results.put(avatar, downloadUri.toString());

                    reference.child(user.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Image updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Error updating image", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    pd.dismiss();
                    Toast.makeText(getActivity(), "Some error occured", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImagePicDialog() {
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0) {
                    if(!checkCameraPermission())
                        requestCameraPermission();
                    else
                        pickFromCamera();
                }
                else if(which==1) {
                    if(!checkStoragePermission())
                        requestStoragePermission();
                    else
                        pickFromGallery();
                }
            }
        });
        builder.create().show();
    }

    private void pickFromGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");


        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        imageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_search).setVisible(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null) {
            //Toast.makeText(this, ""+user.getEmail(), Toast.LENGTH_SHORT).show();
        }
        else {
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        }
    }
}
