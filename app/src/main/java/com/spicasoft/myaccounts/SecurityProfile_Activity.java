package com.spicasoft.myaccounts;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;


import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import Database.MyAccountsDatabase;
import POJO.SecurityProfile;
import TableData.SecurityTableData;

/**
 * Created by USER on 08-11-2017.
 */

public class SecurityProfile_Activity extends AppCompatActivity {
    AppCompatEditText txtName,txtEmail,txtMobile;
    PinEntryEditText txtPassword,txtConfirmPassword;
    Button btn_Save;
    SQLiteDatabase dataBase;
    MyAccountsDatabase mHelper;
    SecurityProfile securityProfile;
    LinearLayout layout_change_password;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_profile);
        txtName=(AppCompatEditText) findViewById(R.id.txt_Name);
        txtEmail=(AppCompatEditText) findViewById(R.id.txt_Email);
        GoogleSignInAccount acc= GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(acc!=null)
        {
            txtEmail.setText(acc.getEmail());
            txtName.setText(acc.getDisplayName());
        }
        txtMobile=(AppCompatEditText) findViewById(R.id.txt_Mobile);
        txtPassword=(PinEntryEditText) findViewById(R.id.txt_password);
        txtConfirmPassword=(PinEntryEditText) findViewById(R.id.txt_confirm_password);
        layout_change_password=(LinearLayout)findViewById(R.id.layout_change_password);
        layout_change_password.setVisibility(View.GONE);
        btn_Save=(Button) findViewById(R.id.btn_save);
        mHelper = new MyAccountsDatabase(this);
        btn_Save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (txtName.getText().toString().equals("")) {
                    txtName.requestFocus();
                    txtName.setError("Name cannot be empty");
                    btn_Save.setEnabled(true);
                }else if(txtEmail.getText().toString().equals("")){
                    txtEmail.requestFocus();
                    txtEmail.setError("Email cannot be empty");
                    btn_Save.setEnabled(true);
                }else if(txtPassword.getText().toString().equals("")){
                    txtPassword.requestFocus();
                    txtPassword.setError("Pin cannot be empty");
                    btn_Save.setEnabled(true);
                }else if(txtPassword.getText().toString().length()<=3){
                    txtPassword.requestFocus();
                    txtPassword.setError("Pin length must be 4 numbers");
                    btn_Save.setEnabled(true);
                }else if(!(txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString()))){
                    txtConfirmPassword.requestFocus();
                    txtConfirmPassword.setError("Pin does not match");
                    btn_Save.setEnabled(true);
                }else{
                    securityProfile=new SecurityProfile();
                    securityProfile.setName(txtName.getText().toString().trim());
                    securityProfile.setEmail(txtEmail.getText().toString().trim());
                    securityProfile.setMobile(txtMobile.getText().toString().trim());
                    securityProfile.setPassword(txtPassword.getText().toString().trim());
                    new SaveProfileSetup().execute();
                }
            }
        });
        txtPassword.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence pwd) {
                 txtConfirmPassword.requestFocus();
            }
        });
        txtConfirmPassword.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence pwd) {
                btn_Save.requestFocus();
            }
        });



    }

    private class SaveProfileSetup extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            SaveProfileSetupData();
            return "sucess full added";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            clearOperation();
        }
    }
    public void showAlertWithCancels(String BuilderText) {
        android.support.v7.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("My Accounts");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(SecurityProfile_Activity.this,Password_Activity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void clearOperation() {
        txtName.setText("");
        txtEmail.setText("");
        txtMobile.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        showAlertWithCancels("Profile has been created successfully.");

    }
    private void SaveProfileSetupData() {
        dataBase = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SecurityTableData.Name, securityProfile.getName());
        values.put(SecurityTableData.Email, securityProfile.getEmail());
        values.put(SecurityTableData.Mobile, securityProfile.getMobile());
        values.put(SecurityTableData.Password, securityProfile.getPassword());

        dataBase.insert(SecurityTableData.SecurityTableName, null, values);
        dataBase.close();

    }
}
