package com.example.gingaminga;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateTransactionActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText titleEdit, categoryEdit, nominalEdit, desctiptionEdit;
    private Button updateButton;
    private String transactionId;
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
        categoryEdit = (EditText) findViewById(R.id.edt_category);
        nominalEdit = (EditText) findViewById(R.id.edt_nominal);
        desctiptionEdit = (EditText) findViewById(R.id.edt_description);
        updateButton = (Button) findViewById(R.id.btn_update_transaction);

        updateButton.setOnClickListener(this);
        transaction = getIntent().getParcelableExtra(EXTRA_TRANSACTION);

        if (transaction != null) {
            transactionId = transaction.getId();
        } else {
            transaction = new Transaction();
        }

        if (transaction != null) {
            titleEdit.setText(transaction.getTitle());
            categoryEdit.setText(transaction.getCategory());
            nominalEdit.setText(transaction.getNominal());
            desctiptionEdit.setText(transaction.getDescription());
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Transaction");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_update_transaction) {
            updateTransaction();
        }
    }

    private void updateTransaction() {
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
            Toast.makeText(UpdateTransactionActivity.this, "Updating data...", Toast.LENGTH_SHORT).show();

            transaction.setTitle(title);
            transaction.setCategory(category);
            transaction.setNominal(nominal);
            transaction.setDescription(description);

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
}