package com.spicasoft.myaccounts;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import Database.MyAccountsDatabase;
import POJO.SecurityProfile;
import TableData.SecurityTableData;

/**
 * Created by USER on 27-11-2017.
 */

public class Profile extends Fragment {
    LinearLayout passwordLayout,layout_change_password;
    AppCompatEditText txtName,txtEmail,txtMobile;
    Button btn_Save;
    SQLiteDatabase dataBase;
    MyAccountsDatabase mHelper;
    SecurityProfile securityProfile;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.security_profile, container, false);
        passwordLayout=(LinearLayout)view.findViewById(R.id.layout_password);
        layout_change_password=(LinearLayout)view.findViewById(R.id.layout_change_password);
        passwordLayout.setVisibility(View.GONE);
        layout_change_password.setVisibility(View.GONE);
        txtName=(AppCompatEditText)view.findViewById(R.id.txt_Name);
        txtEmail=(AppCompatEditText)view.findViewById(R.id.txt_Email);
        txtMobile=(AppCompatEditText)view.findViewById(R.id.txt_Mobile);
        btn_Save=(Button) view.findViewById(R.id.btn_save);
        mHelper = new MyAccountsDatabase(getActivity());


        Initialize();
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtName.getText().toString().equals("")) {
                    txtName.requestFocus();
                    txtName.setError("Name cannot be empty");
                    btn_Save.setEnabled(true);
                } else if (txtEmail.getText().toString().equals("")) {
                    txtEmail.requestFocus();
                    txtEmail.setError("Email cannot be empty");
                    btn_Save.setEnabled(true);
                }else{
                    securityProfile.setName(txtName.getText().toString().trim());
                    securityProfile.setEmail(txtEmail.getText().toString().trim());
                    securityProfile.setMobile(txtMobile.getText().toString().trim());
                    new UpdateProfileSetup().execute();
                }
            }
        });
        return view;
    }

    private void Initialize() {
        securityProfile=new SecurityProfile();
        securityProfile=mHelper.getProfile();
        txtName.setText(securityProfile.getName());
        txtEmail.setText(securityProfile.getEmail());
        txtMobile.setText(securityProfile.getMobile());
    }
    public void showAlertWithCancels(String BuilderText) {
        android.support.v7.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("My Accounts");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private class UpdateProfileSetup extends AsyncTask<String, String, String> {
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
            updateView();
        }
    }
    private void updateView() {

        showAlertWithCancels("Profile has been updated successfully.");

    }
    private void SaveProfileSetupData() {
        dataBase = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SecurityTableData.Name, securityProfile.getName());
        values.put(SecurityTableData.Email, securityProfile.getEmail());
        values.put(SecurityTableData.Mobile, securityProfile.getMobile());
        dataBase.beginTransaction();
        dataBase.update(SecurityTableData.SecurityTableName,values,SecurityTableData.ProfileId+"=?",new String[] {String.valueOf(securityProfile.getProfileId())});
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }
}
