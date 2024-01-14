package com.example.houserentalmanagementproject.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.houserentalmanagementproject.FullScreenImage;
import com.example.houserentalmanagementproject.HouseModel;
import com.example.houserentalmanagementproject.R;
import com.example.houserentalmanagementproject.houseOwner.RegisterOwner;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HouseDetails extends AppCompatActivity {

    TextView tv_noOfRoom, tv_rentPerRoom, tv_houseDescription, tv_houseLocation;
    PhotoView photoView;
    Button btn_viewMember, btn_viewLocation, btn_call, btn_message;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.putExtra("address", "8950562633");
                smsIntent.putExtra("sms_body", "Message");
                startActivity(smsIntent);
            } else {
                // Permission denied, Disable the functionality that depends on this permission.
            }
            // Other 'case' lines to check for other permissions this app might request.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_details2);

        Intent intent = getIntent();
        String houseId = intent.getStringExtra("houseId");
        String userId = intent.getStringExtra("userId");
        tv_noOfRoom = findViewById(R.id.et_noOfRoom);
        tv_rentPerRoom = findViewById(R.id.et_rentPerRoom);
        tv_houseDescription = findViewById(R.id.et_houseDescription);
        tv_houseLocation = findViewById(R.id.et_houseLocation);
        photoView = findViewById(R.id.iv_houseImage);
        String noOfRoom = intent.getStringExtra("noOfRoom");
        String rentPerRoom = intent.getStringExtra("rentPerRoom");
        String houseDescription = intent.getStringExtra("houseDescription");
        String houseLocation = intent.getStringExtra("houseLocation");
        String houseImage = intent.getStringExtra("houseImage");


        // Append the static text to the dynamic content
        tv_noOfRoom.setText(getBoldLabel("No. of Rooms: ", noOfRoom));
        tv_rentPerRoom.setText(getBoldLabel("Rent per Room: ", rentPerRoom));
        tv_houseDescription.setText(getBoldLabel("House Description: ", houseDescription));
        tv_houseLocation.setText(getBoldLabel("Location: ", houseLocation));

        Glide.with(HouseDetails.this).load(houseImage).into(photoView);
        btn_call = findViewById(R.id.btn_call);
        btn_message = findViewById(R.id.btn_message);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HouseDetails.this, FullScreenImage.class);
                intent.putExtra("image_url",houseImage);
                startActivity(intent);
            }
        });
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:8950562633"));
                startActivity(intent);
            }
        });

        btn_message.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(HouseDetails.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(HouseDetails.this, Manifest.permission.SEND_SMS)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response!
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(HouseDetails.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            } else {
                // Permission has already been granted
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setData(Uri.parse("sms:"));
                smsIntent.putExtra("address", "8950562633");
                startActivity(smsIntent);
            }
        });


        Glide.with(HouseDetails.this).load(houseImage).into(photoView);

        /*
        btn_viewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HouseDetails.this, ViewMembers.class);
                intent1.putExtra("houseId", houseId);
                intent1.putExtra("noOfRoom", noOfRoom);
                intent1.putExtra("rentPerRoom", rentPerRoom);
                intent1.putExtra("houseDescription", houseDescription);
                intent1.putExtra("houseLocation", houseLocation);
                intent1.putExtra("houseImage", houseImage);
                intent1.putExtra("userId", userId);
                startActivity(intent1);
            }
        });*/

    }
    private SpannableStringBuilder getBoldLabel(String label, String text) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder(label + text);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}