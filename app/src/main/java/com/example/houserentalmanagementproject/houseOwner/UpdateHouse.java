package com.example.houserentalmanagementproject.houseOwner;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.houserentalmanagementproject.HouseModel;
import com.example.houserentalmanagementproject.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class UpdateHouse extends AppCompatActivity {

    private static final int IMAGE_REQUEST = 1;
    private ImageView iv_houseImage;
    private HouseModel house;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    private StorageReference storageReference;

    // Add EditText for search
    private EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_house);

        // Get the HouseModel object from the intent
        house = (HouseModel) getIntent().getSerializableExtra("house");

        // Get references to your EditTexts and ImageView
        EditText et_houseLocation = findViewById(R.id.et_houseLocation);
        EditText et_noOfRoom = findViewById(R.id.et_noOfRoom);
        EditText et_rentPerRoom = findViewById(R.id.et_rentPerRoom);
        EditText et_houseDescription = findViewById(R.id.et_houseDescription);
        et_search = findViewById(R.id.et_search); // Initialize EditText for search
        iv_houseImage = findViewById(R.id.iv_houseImage);

        // Populate the EditTexts and ImageView with the house's details
        et_houseLocation.setText(house.getHouseLocation());
        et_noOfRoom.setText(house.getNoOfRoom());
        et_rentPerRoom.setText(house.getRentPerRoom());
        et_houseDescription.setText(house.getHouseDescription());
        et_search.setText(house.getSearch()); // Populate the EditText for search with the house's search value
        Glide.with(this).load(house.getHouseImage()).into(iv_houseImage);

        // Initialize your StorageReference
        storageReference = FirebaseStorage.getInstance().getReference().child("Uploads");

        // Set OnClickListener for the ImageView
        iv_houseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        // Get reference to your button
        Button btn_updateHouse = findViewById(R.id.btn_updateHouse);

        // Set OnClickListener for the button
        btn_updateHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the house's details in the model
                house.setHouseLocation(et_houseLocation.getText().toString());
                house.setNoOfRoom(et_noOfRoom.getText().toString());
                house.setRentPerRoom(et_rentPerRoom.getText().toString());
                house.setHouseDescription(et_houseDescription.getText().toString());
                house.setSearch(et_search.getText().toString()); // Get search value from EditText

                // Upload the image if a new one was selected
                if (imageUri != null) {
                    uploadImage();
                } else {
                    // Otherwise, just update the house's details in Firebase
                    updateHouseDetails();
                }
            }
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Display the newly selected image immediately
            iv_houseImage.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(UpdateHouse.this);
        pd.setMessage("Uploading...");
        pd.show();
        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        uploadTask = fileReference.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();

                    // Update the house's image in the model
                    house.setHouseImage(mUri);

                    pd.dismiss();

                    // Update the house's details in Firebase
                    updateHouseDetails();
                } else {
                    Toast.makeText(UpdateHouse.this, "Failed!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateHouse.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = UpdateHouse.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void updateHouseDetails() {
        // Get search value from EditText
        String search = et_search.getText().toString();

        // Update the house's search value in the model
        house.setSearch(search);

        // Save the updated house's details in Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(RegisterOwner.HOUSES).child(house.getUserId()).child(house.getHouseId());
        ref.setValue(house).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UpdateHouse.this, "House updated", Toast.LENGTH_SHORT).show();
                    // Navigate back to the HouseDetails activity
                    finish();
                } else {
                    Toast.makeText(UpdateHouse.this, "Failed to update house", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
