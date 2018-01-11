package com.example.p2_vaio.status;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements RecyclerItemClickListener.onRecyclerClickListener {

    private static final int GALLERY_INTENT=1;
    private static final String TAG = "ProfileActivity";
    private StorageReference storageReference;
    private String name,email,profile_url,key,value;
    private Firebase firebase , firebase1;
    private DatabaseReference databaseReference, db;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ImageButton del ,cancel ;
    private ImageView img;
    private TextView tv;
    public static int KEY_FLAG =0;
    public static ArrayList<HashMap<String, String>> arrayList ;
    private HashMap<String , String> e;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i , GALLERY_INTENT);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        name = getIntent().getStringExtra("NAME");
        email = getIntent().getStringExtra("EMAIL");
        key=getIntent().getStringExtra("KEY");
        value=getIntent().getStringExtra("VALUE");


        if(email==null) {
            profile_url = "https://fir-sample-c1a4c.firebaseio.com/users" + "/" + key;
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users/"+key);
            fab.setVisibility(View.GONE);
            Log.d(TAG, "onCreate: "+profile_url+"  "+true);
            setTitle(value);
            KEY_FLAG=1;
        }
        else {
            Log.d(TAG, "onCreate: "+profile_url+"  "+false);
            profile_url = "https://fir-sample-c1a4c.firebaseio.com/users" + "/" + email;
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users/"+email);
            setTitle(name);
            KEY_FLAG=0;
        }

        Firebase.setAndroidContext(this);
        firebase = new Firebase(profile_url);
        firebase1 = new Firebase("https://fir-sample-c1a4c.firebaseio.com/status");
        storageReference = FirebaseStorage.getInstance().getReference();


        recyclerView = (RecyclerView)findViewById(R.id.recyclerView2);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,recyclerView,this));

        arrayList = new ArrayList<HashMap<String, String>>();

        FirebaseRecyclerAdapter<Stat , MainActivity.StatViewHolder> adapter = new FirebaseRecyclerAdapter<Stat, MainActivity.StatViewHolder>(
                Stat.class,
                R.layout.row1,
                MainActivity.StatViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(MainActivity.StatViewHolder viewHolder, Stat model, int position) {
//                Log.d(TAG, "populateViewHolder: method called");
                viewHolder.setImage(model.getStatus());

                String post_key = getRef(position).getKey();
                Log.d(TAG, "populateViewHolder: "+model.getStatus()+"  "+post_key);
                e = new HashMap<>();
                e.put(model.getStatus(),post_key);
                arrayList.add(e);
                Log.d(TAG, "populateViewHolder: "+arrayList.size());

            }
        };

        recyclerView.setAdapter(adapter);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: in");
        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            Uri uri = data.getData();

            StorageReference filepath = storageReference.child("photos").child(uri.getLastPathSegment());
            Log.d(TAG, "onActivityResult: "+filepath);
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    firebase.push().child("status").setValue(downloadUrl.toString());
//                    firebase1.push().child("status").setValue(downloadUrl.toString());
                    Toast.makeText(ProfileActivity.this , "uploaded successfully",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "error",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: "+position);
//        Toast.makeText(ProfileActivity.this , "click",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        Log.d(TAG, "onItemLongClick: "+position);
            if(KEY_FLAG==0) {
            del = (ImageButton)view.findViewById(R.id.delete);
            cancel = (ImageButton)view.findViewById(R.id.cancel);
            img = (ImageView)view.findViewById(R.id.imageView);
            tv = (TextView)view.findViewById(R.id.imageurl);

            int opacity = 10; // from 0 to 255
            img.setBackgroundColor(opacity * 0x1000000);
            del.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    del.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                    int opacity = 255; // from 0 to 255
                    img.setBackgroundColor(opacity * 0x1000000);

                }
            });
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = arrayList.get(position).get(tv.getText().toString());
    //              Log.d(TAG, "onClick: to be deleted "+s);
                    String del_url = profile_url + "/" + s;
                    db = FirebaseDatabase.getInstance().getReference("users").child(email).child(s);
                    db.removeValue();
                }
            });
        }


//        Toast.makeText(ProfileActivity.this , "longclick",Toast.LENGTH_LONG).show();

    }
}
