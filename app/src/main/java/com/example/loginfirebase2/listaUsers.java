package com.example.loginfirebase2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class listaUsers extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthState;
    private DatabaseReference mDatabase;
    EditText titulo;
    EditText correo, pass, userName;
    Button mostrar, editar, eliminar;
    ListView listView;
    UserModel userSelected;
    private List<UserModel> listaU = new ArrayList<UserModel>();
    ArrayAdapter<UserModel> arrayAdapterUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_users2);

        titulo = (EditText) findViewById(R.id.titulo);
        mostrar = (Button) findViewById(R.id.mostrar);
        editar = (Button) findViewById(R.id.editar);
        eliminar = (Button) findViewById(R.id.eliminar);
        correo = (EditText) findViewById(R.id.correo);
        pass = (EditText) findViewById(R.id.pass);
        userName = (EditText) findViewById(R.id.userName);
        listView = findViewById(R.id.lista);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        stateUpdate();
        userData();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //METODO PARA SELECCIONAR UN ITEM
                userSelected = (UserModel) parent.getItemAtPosition(position);
                userName.setText(userSelected.getName());
                correo.setText(userSelected.getEmail());
                pass.setText(userSelected.getPassword());
            }
        });
        mostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mostrarDatos();
            }
        });
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarDatos();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eliminarDatos();
            }
        });




    }

    private void mostrarDatos() {
//SELECCIONAR DATOS DE LA BD Y LISTARLOS EN UN LISTVIEW
        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaU.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    listaU.add(user);




                }arrayAdapterUser = new ArrayAdapter<UserModel>(listaUsers.this, R.layout.support_simple_spinner_dropdown_item, listaU);
                listView.setAdapter(arrayAdapterUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void editarDatos(){

        UserModel userx = new UserModel();
        userx.setUid(userSelected.getUid());
        userx.setName(userName.getText().toString().trim());
        userx.setEmail(correo.getText().toString().trim());
        userx.setPassword(pass.getText().toString().trim());
        mDatabase.child("Users").child(userx.getUid()).setValue(userx);
        Toast.makeText(listaUsers.this, "Actualizado", Toast.LENGTH_LONG).show();
        limpiar();
    }

    private void eliminarDatos(){
        UserModel userx = new UserModel();
        userx.setUid(userSelected.getUid());
        mDatabase.child("Users").child(userx.getUid()).removeValue();
        Toast.makeText(listaUsers.this, "Elimindado", Toast.LENGTH_LONG).show();


        limpiar();

    }

    public void limpiar(){

        userName.setText("");
        correo.setText("");
        pass.setText("");

    }
    private void userData() {

        String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue().toString();
                    String correox = snapshot.child("email").getValue().toString();

                    titulo.setText("User: " + name + "\n" + "Email: " + correox);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void stateUpdate() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {


           /* onLinee.setText("");
            onLinee.setBackgroundColor(Color.GREEN);
*/
            //reload();
        } else {
            /*onLinee.setText("");
            onLinee.setBackgroundColor(Color.RED);
*/

        }


    }


    private void cerrarLog() {

        mAuth.signOut();
        stateUpdate();
        Intent cerrar = new Intent(listaUsers.this, MainActivity.class);
        startActivity(cerrar);

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