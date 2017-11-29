package com.spicasoft.myaccounts;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import CustomAdapters.PersonsAdapter;
import CustomWidget.TextAwesome;
import Database.MyAccountsDatabase;
import POJO.Persons;
import TableData.PersonsTableData;

import static android.app.Activity.RESULT_OK;

/**
 * Created by USER on 09-11-2017.
 */

public class ManagePersons extends Fragment {
    public ManagePersons(){

    }
    AppCompatEditText txtName,txtMobile;
    Button btnSave;
    MyAccountsDatabase mSQLHelper;
    SQLiteDatabase dataBase;
    Persons person;
    RecyclerView personList;
    RecyclerView.LayoutManager mLayoutManager;
    PersonsAdapter personsAdapter;
    ArrayList<Persons> addPersons = new ArrayList<>();
    TextAwesome txt_pick_contact;
    private static final int REQUEST_CODE = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.manage_persons, container, false);
        txtName=(AppCompatEditText) view.findViewById(R.id.txt_personName);
        txtMobile=(AppCompatEditText) view.findViewById(R.id.txt_personMobile);
        btnSave=(Button) view.findViewById(R.id.btn_person);
        txt_pick_contact=(TextAwesome) view.findViewById(R.id.txt_pick_contact);
        mSQLHelper=new MyAccountsDatabase(getContext());
        personList = (RecyclerView) view.findViewById(R.id.personslist);
        personList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        personList.setLayoutManager(mLayoutManager);
        displayPersons();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(person==null){
                    if(txtName.getText().toString().equals("")){
                        txtName.requestFocus();
                        txtName.setError("Name can't be empty");
                    }else if(txtMobile.getText().toString().equals("")){
                        txtMobile.requestFocus();
                        txtMobile.setError("Mobile no can't be empty");
                    }else{
                        person=new Persons();
                        person.setPersonName(txtName.getText().toString().trim());
                        person.setMobile(txtMobile.getText().toString().trim());
                        new AddPerson().execute();

                    }
                }else{
                    person.setPersonName(txtName.getText().toString().trim());
                    person.setMobile(txtMobile.getText().toString().trim());
                    new EditPerson().execute();
                }

            }
        });
        txt_pick_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                getActivity().startActivityForResult(intent, REQUEST_CODE);
            }
        });

        return  view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor =getActivity().getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);
                txtName.setText(name);
                txtMobile.setText(number.replace(" ",""));


            }
        }
    };

    private void displayPersons() {
        addPersons=mSQLHelper.getPersons();
        personsAdapter = new PersonsAdapter((AppCompatActivity) getActivity(), addPersons,ManagePersons.this);
        personList.setAdapter(personsAdapter);
        personsAdapter.notifyDataSetChanged();
        person=null;

    }
    public void EditPerson(Persons p){
        person=new Persons();
        person.setPersonId(p.getPersonId());
        person.setPersonName(p.getPersonName());
        person.setMobile(p.getMobile());
        txtName.setText(p.getPersonName());
        txtMobile.setText(p.getMobile());
    }

    public void showAlertWithCancels(String BuilderText) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("My Accounts - Manage Person");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ReloadOperation();
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void ReloadOperation() {
        addPersons.clear();
        displayPersons();
    }
    private class AddPerson extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mSQLHelper.SavePersonData(person);
            return "sucess full added";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            clearOperation();
            showAlertWithCancels("Person has been added successfully.");
        }
    }
    private class EditPerson extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            UpdatePersonData();
            return "sucess full added";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            clearOperation();
            showAlertWithCancels("Person has been updated successfully.");
        }
    }

    private void UpdatePersonData() {
        ContentValues values=new ContentValues();
        dataBase=mSQLHelper.getWritableDatabase();
        dataBase.beginTransaction();
        values.put(PersonsTableData.PersonName, person.getPersonName());
        values.put(PersonsTableData.PersonMobile, person.getMobile());
        dataBase.update(PersonsTableData.PersonTableName,values,PersonsTableData.PersonID+"=?",new String[] {String.valueOf(person.getPersonId())});
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }

    private void clearOperation() {
        txtName.setText("");
        txtMobile.setText("");
        txtName.requestFocus();
        person=null;
    }
}
