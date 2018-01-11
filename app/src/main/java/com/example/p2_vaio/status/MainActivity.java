package com.example.p2_vaio.status;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements RecyclerItemClickListener.onRecyclerClickListener {

    private static final String TAG = "MainActivity";
    private ConstraintLayout constraintLayout;
    private StorageReference storageReference;
    private Firebase firebase , firebase1;
    private DatabaseReference databaseReference;
    private static final int GALLERY_INTENT=1;
    private String name,email,profile_url;
    private RecyclerView recyclerView;
    public static ArrayList<HashMap<String, String>> list ;
    private HashMap<String , String> e;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        name = getIntent().getStringExtra("NAME");
        email = getIntent().getStringExtra("EMAIL");
        email = email.replaceAll("[.]","-");

        setTitle(name);

        list = new ArrayList<HashMap<String, String>>();

        Log.d(TAG, "onCreate: "+email);
        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://fir-sample-c1a4c.firebaseio.com/users");
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

       constraintLayout = (ConstraintLayout)findViewById(R.id.mystatus);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,recyclerView,this));


        FirebaseRecyclerAdapter<Stat , StatViewHolder> adapter = new FirebaseRecyclerAdapter<Stat, StatViewHolder>(
                Stat.class,
                R.layout.row,
                StatViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(final StatViewHolder viewHolder, Stat model, int position) {

                final String post_key = getRef(position).getKey();
//                if(!post_key.equals(email)) {
                    firebase1 = new Firebase("https://fir-sample-c1a4c.firebaseio.com/names/" + post_key);
                    firebase1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            e = new HashMap<>();
                            String value = dataSnapshot.getValue(String.class);
                            e.put(value,post_key);
                            list.add(e);
                            viewHolder.setImageurl(value , post_key);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
//                }

//                viewHolder.setImageurl(post_key);
//                Log.d(TAG, "populateViewHolder: "+post_key);
            }
        };

        recyclerView.setAdapter(adapter);

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this , ProfileActivity.class);
                i.putExtra("NAME",name);
                i.putExtra("EMAIL",email);
                startActivity(i);
/*                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image*//*");
                startActivityForResult(i , GALLERY_INTENT);*/

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        Log.d(TAG, "onCreateOptionsMenu() returned: " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(id){
            case R.id.logout:
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

                pref.edit().remove("NAME").commit();
                pref.edit().remove("EMAIL").commit();
                pref.edit().clear().commit();
                finish();
                startActivity(new Intent(MainActivity.this , credentials.class));
                break;
            case R.id.aboutme:
                startActivity(new Intent(MainActivity.this , help.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
//        int position = getAdapterPosition();
        String k="";
        for ( String key : MainActivity.list.get(position).keySet() ) {
            k=key;
            Log.d(TAG, "onClick: "+k);
        }
        Intent i =new Intent(MainActivity.this, ProfileActivity.class);
        i.putExtra("KEY",MainActivity.list.get(position).get(k));
        i.putExtra("VALUE",k);
        startActivity(i);

        Log.d(TAG, "onClick: "+position+" "+MainActivity.list.get(position));
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    public static class StatViewHolder extends RecyclerView.ViewHolder {


        private TextView url ,txt;
        private Context context;
        private ImageView imgView;
        private GestureDetector gestureDetector;

        public StatViewHolder(View itemView) {
            super(itemView);
            context= itemView.getContext();
//            itemView.setOnClickListener(this);
            url = (TextView)itemView.findViewById(R.id.textView);
            txt = (TextView)itemView.findViewById(R.id.imageurl);
            imgView=(ImageView)itemView.findViewById(R.id.imageView);
        }




        public void setContext(Context ctx){
            context=ctx;
        }

        public void setImageurl(String imageurl , String key) {

            url.setText(imageurl);
        }

        public void setImageurl(String imageurl) {
            Log.d(TAG, "setImageurl: "+imageurl);
            url.setText(imageurl);
        }
        public void setImage(String url) {
            Log.d(TAG, "setImage: "+url);
//            URL myURL = new URL(url);
            txt.setText(url);
            Picasso.with(context).load(url)
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imgView);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
