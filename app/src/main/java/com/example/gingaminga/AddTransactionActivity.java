package com.example.gingaminga;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTransactionActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText titleEdit, categoryEdit, nominalEdit, desctiptionEdit;
    private Button saveButton;
    private Transaction transaction;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add New Transaction");
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        titleEdit = (EditText) findViewById(R.id.edt_title);
        categoryEdit = (EditText) findViewById(R.id.edt_category);
        nominalEdit = (EditText) findViewById(R.id.edt_nominal);
        desctiptionEdit = (EditText) findViewById(R.id.edt_description);
        saveButton = (Button) findViewById(R.id.btn_add_transaction);

        saveButton.setOnClickListener(this);
        transaction = new Transaction();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_transaction) {
            saveTransaction();
        }
    }

    private void saveTransaction() {
        String title = titleEdit.getText().toString().trim();
        String category = categoryEdit.getText().toString().trim();
        String nominal = nominalEdit.getText().toString().trim();
        String description = desctiptionEdit.getText().toString().trim();
        boolean isEmptyField = false;

        if (TextUtils.isEmpty(title)) {
            isEmptyField = true;
            titleEdit.setError("This field cannot be empty!");
        }

        if (TextUtils.isEmpty(category)) {
            isEmptyField = true;
            categoryEdit.setError("This field cannot be empty!");
        }

        if (TextUtils.isEmpty(nominal)) {
            isEmptyField = true;
            nominalEdit.setError("This field cannot be empty!");
        }

        if (!isEmptyField) {
            Toast.makeText(AddTransactionActivity.this, "Saving data...", Toast.LENGTH_SHORT).show();
            DatabaseReference dbTransaction = mDatabase.child("transaction");

            String id = dbTransaction.push().getKey();
            transaction.setId(id);
            transaction.setTitle(title);
            transaction.setCategory(category);
            transaction.setNominal(nominal);
            transaction.setDescription(description);

            // Insert data
            dbTransaction.child(id).setValue(transaction);
            finish();
        }
    }
}