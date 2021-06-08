package com.example.loginfirebase2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
     EditText correo, pass, userName;
     TextView onLine;
     Button registrar, iniciarSesion, cerrarSesion;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthState;
    private DatabaseReference mDatabase;

    public String userNames = "";
    public String email = "";
    public String password = "";

    public String key="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // ...
        mDatabase = FirebaseDatabase.getInstance().getReference();
        correo = (EditText) findViewById(R.id.correo);
        pass = (EditText) findViewById(R.id.pass);
        userName = (EditText) findViewById(R.id.userName);
        registrar = (Button) findViewById(R.id.registrar);
        iniciarSesion = (Button) findViewById(R.id.inicioSesion);

        onLine = (TextView) findViewById(R.id.onLine);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent screenLog = new Intent(MainActivity.this,login.class);
                startActivity(screenLog);
            }
        });


    }

    private void createUser() {
        String email, password,userNames;
        key = UUID.randomUUID().toString();
        if (!verifyData()) {

            Toast.makeText(MainActivity.this, "Error al crear al usuario", Toast.LENGTH_LONG).show();
        } else {
            email = correo.getText().toString();
            password = pass.getText().toString();
            userNames = userName.getText().toString();


            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        Map<String, Object> map = new HashMap<>();
                        map.put("uid",key);
                        map.put("name", userNames);
                        map.put("email", email);
                        map.put("password", password);

                        String id = mAuth.getCurrentUser().getUid();
                        mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> taskTwo) {

                                if (taskTwo.isSuccessful()) {

                                    Toast.makeText(MainActivity.this, "Usuario regisstrado", Toast.LENGTH_LONG).show();

                                    startActivity(new Intent(MainActivity.this,listaUsers.class));
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, "No se pudo registrar", Toast.LENGTH_LONG).show();

                                }
                            }
                        });

                    } else {

                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }


                }
            });
        }

    }

    private boolean verifyData() {

        String cor = correo.getText().toString();
        String pas = pass.getText().toString();


        if (cor.isEmpty()) {

            correo.setText("");
            return false;
        }

        if (pas.isEmpty()) {

            pass.setText("");
            return false;
        }

        return true;

    }
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){

            Intent restaurar = new Intent(MainActivity.this,listaUsers.class);
            startActivity(restaurar);
            finish();

        }
    }

}







