package com.example.gingaminga;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView pemasukanTV, pengeluaranTV, notFoundTV;
    private ListView listView;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuth;
    private FloatingActionButton addFAB;
    private ArrayList<Transaction> transactionList;
    private TransactionAdapter adapter;

    DatabaseReference dbTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Transactions");
//        toolbar.setLogo(R.drawable.anggap_saja_logo3);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        dbTransaction = FirebaseDatabase.getInstance().getReference("transaction");
        pemasukanTV = (TextView) findViewById(R.id.tv_total_masuk);
        pengeluaranTV = (TextView) findViewById(R.id.tv_total_keluar);
        notFoundTV = (TextView) findViewById(R.id.tv_not_found);
        listView = findViewById(R.id.lv_list);
        addFAB = findViewById(R.id.fab_add);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddTransactionActivity.class));
                Toast.makeText(MainActivity.this, "FAB Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        // List of Transaction
        transactionList = new ArrayList<>();


        // Listener for items in listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, UpdateTransactionActivity.class);
                intent.putExtra(UpdateTransactionActivity.EXTRA_TRANSACTION, transactionList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //choose menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mn_logout:
                Toast.makeText(this, "Bye Bye !!", Toast.LENGTH_SHORT).show();
                signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void signOut() {
        mAuth.signOut();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        progressBar.setVisibility(View.GONE);
//    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);

        dbTransaction.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionList.clear();

                for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {
                    Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                    transactionList.add(transaction);
                }

                TransactionAdapter adapter = new TransactionAdapter(MainActivity.this);
                adapter.setTransactionList(transactionList);
                listView.setAdapter(adapter);
                if (transactionList.isEmpty()) {
                    notFoundTV.setVisibility(View.VISIBLE);
                } else {
                    notFoundTV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Problems occurred: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }
}