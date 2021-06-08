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

public class login extends AppCompatActivity {

    EditText pass, correo;
    Button inicioSes;
    TextView onLinee;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthState;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        mAuth = FirebaseAuth.getInstance();
        pass = (EditText) findViewById(R.id.pass);
        correo = (EditText) findViewById(R.id.correo);
        onLinee = (TextView) findViewById(R.id.onLinee);
        inicioSes = (Button) findViewById(R.id.inicioSes);


        mAuthState = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //se revisa el estado de el inicio de sesion
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Log.d("TAG", "Logueado" + user.getUid());
                    //reload();
                } else {
                    Log.d("TAG", "No Logueado" + user.getUid());
                }
            }
        };
        inicioSes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inicioSesion();
            }
        });


        stateUpdate();

    }

    public void enviarDatos(String email, String password) {

        email = correo.getText().toString();
        password = pass.getText().toString();

        Intent sig = new Intent(login.this, listaUsers.class);
        sig.putExtra("correo", email);
        sig.putExtra("pass", password);
        startActivity(sig);


    }


    private boolean verificarDatos() {

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


    private void inicioSesion() {
        String email, password;


        if (!verificarDatos()) {

            Toast.makeText(login.this, "Fallo al iniciar sesion", Toast.LENGTH_LONG).show();
        } else {

            email = correo.getText().toString();
            password = pass.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(login.this, "Logueado", Toast.LENGTH_LONG).show();

                                enviarDatos(email, password);

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(login.this, "Fallo al iniciar sesion", Toast.LENGTH_LONG).show();

                            }
                            stateUpdate();
                        }
                    });
        }


    }

    private void stateUpdate() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {


            onLinee.setText("");
            onLinee.setBackgroundColor(Color.GREEN);

            //reload();
        } else {
            onLinee.setText("");
            onLinee.setBackgroundColor(Color.RED);



        }


    }




    private void cerrarLog() {

        mAuth.signOut();
        stateUpdate();
        correo.setText("");
        pass.setText("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.disconect) {

            cerrarLog();
            return true;

        }
        return super.onOptionsItemSelected(item);


    }




}








