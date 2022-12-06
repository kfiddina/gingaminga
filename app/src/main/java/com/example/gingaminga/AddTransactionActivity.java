package com.example.gingaminga;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText titleEdit, categoryEdit, nominalEdit, descriptionEdit, dateEdit;
    private Spinner categorySpinner;
    private Button saveButton;
    private Transaction transaction;
    private String chosenCategory;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add New Transaction");
        setSupportActionBar(toolbar);

        transaction = new Transaction();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        titleEdit = (EditText) findViewById(R.id.edt_title);
//        categoryEdit = (EditText) findViewById(R.id.edt_category);
        categorySpinner = findViewById(R.id.edt_category);
        nominalEdit = (EditText) findViewById(R.id.edt_nominal);
        descriptionEdit = (EditText) findViewById(R.id.edt_description);
        dateEdit = (EditText) findViewById(R.id.edt_date);
        saveButton = (Button) findViewById(R.id.btn_add_transaction);

        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = ((view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String strFormatDefault = "d MMMM yyyy";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strFormatDefault, Locale.getDefault());
                    dateEdit.setText(simpleDateFormat.format(calendar.getTime()));
                });

                new DatePickerDialog(AddTransactionActivity.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
        categorySpinner.setOnItemSelectedListener(this);

//        saveButton.setOnClickListener(this);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTransaction();
            }
        });
    }

//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.btn_add_transaction) {
//            saveTransaction();
//        }
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenCategory = parent.getItemAtPosition(position).toString();
//        Toast.makeText(getApplicationContext(), chosenCategory, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void saveTransaction() {
        String title = titleEdit.getText().toString().trim();
//        String category = categoryEdit.getText().toString().trim();
        String category = chosenCategory;
        String nominal = nominalEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();
        String date = dateEdit.getText().toString().trim();
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

        if (TextUtils.isEmpty(date)) {
            isEmptyField = true;
            dateEdit.setError("This field cannot be empty!");
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
            transaction.setDate(date);

            // Insert data
            dbTransaction.child(id).setValue(transaction);
            finish();
        }
    }
}