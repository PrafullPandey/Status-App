package com.example.p2_vaio.status;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.client.Firebase;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class credentials extends AppCompatActivity {
    private EditText name , email;
    private Button btn;
    private ImageView cannc, canne;
    private Firebase firebase;
    private static final String TAG = "credentials";
    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE=1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_READ_EXTERNAL_STORAGE:
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    permission was granted and do the contacts related task need to be done
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
//                    READ_CONTACTS_GRANTED = true;
                } else {
                    finish();
//                    disable the functionality that depends upon this permission
                }
//                fab.setEnabled(READ_CONTACTS_GRANTED);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);

        name= (EditText) findViewById(R.id.editText);
        email = (EditText) findViewById(R.id.editText2);
        btn = (Button)findViewById(R.id.submit);
        cannc= (ImageView)findViewById(R.id.cancn);
        canne=(ImageView)findViewById(R.id.cance);

        int hasReadPermissions = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        if (hasReadPermissions == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: permission grannted");
//            READ_CONTACTS_GRANTED = true;
        } else {
            Log.d(TAG, "onCreate: permission denied");
            ActivityCompat.requestPermissions(credentials.this, new String[]{READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        if(pref.getString("NAME","")==""){


            cannc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name.setText("");
                }
            });

            canne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    email.setText("");
                }
            });
            btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if( name.getText().toString().length() == 0 )
                            name.setError( "Name is required!" );

                        if( email.getText().toString().length() == 0 )
                            email.setError( "Email is required!" );

                        if(name.getText().toString().length()>0 && email.getText().toString().length()>0) {

                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("NAME", name.getText().toString());
                            editor.putString("EMAIL", email.getText().toString());
                            editor.commit();
                            String e = email.getText().toString().replaceAll("[.]", "-");
                            Firebase.setAndroidContext(credentials.this);
                            firebase = new Firebase("https://fir-sample-c1a4c.firebaseio.com/names");
                            firebase.child(e).setValue(name.getText().toString());
                            Intent i = new Intent(credentials.this, MainActivity.class);
                            i.putExtra("NAME", name.getText().toString());
                            i.putExtra("EMAIL", email.getText().toString());
                            finish();
                            startActivity(i);
                        }


                    }
                });
            }
        else{

            Intent i = new Intent(credentials.this , MainActivity.class);
            i.putExtra("NAME" , pref.getString("NAME",""));
            i.putExtra("EMAIL",pref.getString("EMAIL",""));
            finish();
            startActivity(i);
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
