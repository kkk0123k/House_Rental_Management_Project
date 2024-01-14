package com.example.houserentalmanagementproject.houseOwner;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.houserentalmanagementproject.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class AddHouse extends AppCompatActivity {

    ImageView iv_houseImage;
    EditText et_houseId, et_houseLocation, et_noOfRoom, et_rentPerRoom, et_houseDescription, et_search;
    Button btn_addHouse;

    private static final int STORAGE_PERMISSION_CODE = 100;
    StorageReference storageReference;
    public static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String imageString;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_house);

        iv_houseImage = findViewById(R.id.iv_houseImage);
        et_houseLocation = findViewById(R.id.et_houseLocation);
        et_noOfRoom = findViewById(R.id.et_noOfRoom);
        et_rentPerRoom = findViewById(R.id.et_rentPerRoom);
        et_houseDescription = findViewById(R.id.et_houseDescription);
        et_search = findViewById(R.id.et_search); // Initialize EditText for search
        btn_addHouse = findViewById(R.id.btn_addHouse);

        storageReference = FirebaseStorage.getInstance().getReference().child("Uploads");

        iv_houseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
            }
        });

        progressDialog = new ProgressDialog(AddHouse.this);

        btn_addHouse.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Publishing Your House");
                progressDialog.setTitle("Adding...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                String houseLocation = et_houseLocation.getText().toString();
                String noOfRoom = et_noOfRoom.getText().toString();
                String rentPerRoom = et_rentPerRoom.getText().toString();
                String houseDescription = et_houseDescription.getText().toString();
                String search = et_search.getText().toString(); // Get search value from EditText
                String image = imageString;

                createHouse(houseLocation, noOfRoom, rentPerRoom, houseDescription, image, search);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createHouse(String houseLocation, String noOfRoom, String rentPerRoom, String houseDescription, String image, String search) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(RegisterOwner.HOUSES).child(userId);

        DatabaseReference houseRef = reference.push(); // Push first to generate the key

        String houseId = houseRef.getKey(); // Get the generated key

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("houseId", houseId);
        hashMap.put("noOfRoom", noOfRoom);
        hashMap.put("houseDescription", houseDescription);
        hashMap.put("houseLocation", houseLocation);
        hashMap.put("rentPerRoom", rentPerRoom);
        hashMap.put("houseImage", image);
        hashMap.put("userId", userId);
        hashMap.put("search", search); // Add search value to hashMap

        houseRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(AddHouse.this, "House have been added", Toast.LENGTH_SHORT).show();
                    imageString = "";
                    Intent intent = new Intent(AddHouse.this, DashboardOwner.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AddHouse.this, "Fail to add house", Toast.LENGTH_SHORT).show();
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

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = AddHouse.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(AddHouse.this);
        pd.setMessage("Uploading...");
        pd.show();
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        try {
                            Uri downloadingUri = task.getResult();
                            Log.d("TAG", "onComplete: uri completed");
                            imageString = downloadingUri.toString();
                            Glide.with(AddHouse.this).load(imageUri).into(iv_houseImage);
                        } catch (Exception e) {
                            Log.d("TAG1", "error Message: " + e.getMessage());
                            Toast.makeText(AddHouse.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();
                    } else {
                        Toast.makeText(AddHouse.this, "Failed here", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddHouse.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(AddHouse.this, "No image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(AddHouse.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(AddHouse.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(AddHouse.this, new String[]{permission}, requestCode);
        } else {
            openImage();
            Toast.makeText(AddHouse.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImage();
                Toast.makeText(AddHouse.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddHouse.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}