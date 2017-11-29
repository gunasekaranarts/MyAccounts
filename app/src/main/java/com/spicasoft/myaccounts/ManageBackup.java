package com.spicasoft.myaccounts;

import android.app.ProgressDialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.drive.DriveFile;

import Database.MyAccountsDatabase;
import Utils.RestoreFile;

/**
 * Created by USER on 15-11-2017.
 */

public class ManageBackup extends Fragment {
    MyAccountsDatabase mHelper;
    Button btn_upload,btn_restore;
    private static final int REQUEST_CODE_SIGN_IN = 0;
    ProgressDialog progressDialog;


    DriveFile file;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.dbcloud, container, false);
        btn_upload=(Button) view.findViewById(R.id.btn_backup);
        btn_restore=(Button) view.findViewById(R.id.btn_restore);
        mHelper = new MyAccountsDatabase(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadData().execute();
            }
        });
        btn_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFileFromAppFolder();
            }
        });

        return view;
    }
    private class UploadData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            createFileInAppFolder();
            return "sucess full added";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void getFileFromAppFolder() {
        new RestoreFile(getActivity()).appFolder();
    }
    public void createFileInAppFolder() {
        ((MainActivity)getActivity()).uploadFile.UploadFile();
    }
    protected void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

}
