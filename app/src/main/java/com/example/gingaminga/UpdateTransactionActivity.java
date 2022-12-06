package com.example.gingaminga;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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

public class UpdateTransactionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText titleEdit, categoryEdit, nominalEdit, descriptionEdit, dateEdit;
    private Button updateButton;
    private Spinner categorySpinner;
    private String transactionId, chosenCategory;
    private Transaction transaction;

    public static final String EXTRA_TRANSACTION = "extra_transaction";
    public final int ALERT_DIALOG_CLOSE = 10;
    public final int ALERT_DIALOG_DELETE = 20;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_transaction);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        titleEdit = (EditText) findViewById(R.id.edt_title);
//        categoryEdit = (EditText) findViewById(R.id.edt_category);
        categorySpinner = findViewById(R.id.edt_category);
        nominalEdit = (EditText) findViewById(R.id.edt_nominal);
        descriptionEdit = (EditText) findViewById(R.id.edt_description);
        dateEdit = (EditText) findViewById(R.id.edt_date);
        updateButton = (Button) findViewById(R.id.btn_update_transaction);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Transaction");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

                new DatePickerDialog(UpdateTransactionActivity.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
        categorySpinner.setOnItemSelectedListener(this);

        transaction = getIntent().getParcelableExtra(EXTRA_TRANSACTION);
        if (transaction != null) {
            transactionId = transaction.getId();
        } else {
            transaction = new Transaction();
        }

        if (transaction != null) {
            titleEdit.setText(transaction.getTitle());
//            categoryEdit.setText(transaction.getCategory());
            nominalEdit.setText(transaction.getNominal());
            descriptionEdit.setText(transaction.getDescription());
            dateEdit.setText(transaction.getDate());
            categorySpinner.setSelection(spinnerAdapter.getPosition(transaction.getCategory()));
        }
//        updateButton.setOnClickListener(this);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTransaction();
            }
        });
    }

//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.btn_update_transaction) {
//            updateTransaction();
//        }
//    }

    private void updateTransaction() {
        String title = titleEdit.getText().toString().trim();
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
            Toast.makeText(UpdateTransactionActivity.this, "Updating data...", Toast.LENGTH_SHORT).show();

            transaction.setTitle(title);
            transaction.setCategory(category);
            transaction.setNominal(nominal);
            transaction.setDescription(description);
            transaction.setDate(date);

            DatabaseReference dbTransaction = mDatabase.child("transaction");

            // Update data
            dbTransaction.child(transactionId).setValue(transaction);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Choose menu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case android.R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;

        if (isDialogClose) {
            dialogTitle = "Cancel";
            dialogMessage = "Do you want to cancel the changes made to the form?";
        } else {
            dialogTitle = "Delete Item";
            dialogMessage = "Are you sure you want to delete this item?";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder.setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isDialogClose) {
                            finish();
                        } else {
                            // Delete record
                            DatabaseReference dbTransaction = mDatabase.child("transaction").child(transactionId);
                            dbTransaction.removeValue();

                            Toast.makeText(UpdateTransactionActivity.this, "Deleting data...", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenCategory = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}