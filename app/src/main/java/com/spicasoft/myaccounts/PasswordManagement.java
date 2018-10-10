package com.spicasoft.myaccounts;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import CustomAdapters.PasswordManagerAdapter;
import Database.MyAccountsDatabase;
import POJO.PasswordManager;
import Utils.Crypting;

/**
 * Created by USER on 10-10-2018.
 */

public class PasswordManagement extends Fragment {
    AppCompatEditText txt_acc_name,txt_acc_user_name,txt_acc_password;
    Button btn_Save;
    MyAccountsDatabase mSQLHelper;
    SQLiteDatabase dataBase;
    PasswordManager passwordManager;
    RecyclerView passwordList;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<PasswordManager> passwords = new ArrayList<>();
    PasswordManagerAdapter managerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.password_manager, container, false);
        txt_acc_name=view.findViewById(R.id.txt_acc_name);
        txt_acc_user_name=view.findViewById(R.id.txt_acc_user_name);
        txt_acc_password=view.findViewById(R.id.txt_acc_password);
        btn_Save =view.findViewById(R.id.btn_save);
        passwordList=view.findViewById(R.id.rcv_pwd_list);
        mLayoutManager = new LinearLayoutManager(getActivity());
        passwordList.setLayoutManager(mLayoutManager);
        mSQLHelper=new MyAccountsDatabase(getContext());
        displayPasswords();
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid=true;
                if(txt_acc_password.getText().toString().equals("")){
                    txt_acc_password.setError("Password should not be empty!");
                    txt_acc_password.requestFocus();
                    isValid=false;
                }
                if(txt_acc_user_name.getText().toString().equals("")){
                    txt_acc_user_name.setError("User name/id should not be empty!");
                    txt_acc_user_name.requestFocus();
                    isValid=false;
                }
                if(txt_acc_name.getText().toString().equals("")){
                    txt_acc_name.setError("Account name should not be empty!");
                    txt_acc_name.requestFocus();
                    isValid=false;
                }
                if(isValid){
                    if(passwordManager==null)
                        passwordManager=new PasswordManager();
                    try {
                        passwordManager.setAccountName(txt_acc_name.getText().toString());
                        passwordManager.setUserName(Crypting.enCryptText(txt_acc_user_name.getText().toString().trim()));
                        passwordManager.setPassword(Crypting.enCryptText(txt_acc_password.getText().toString().trim()));
                        mSQLHelper.insertPwdMgr(passwordManager);
                        Toast.makeText(getContext(),"Password saved",Toast.LENGTH_SHORT).show();
                        displayPasswords();
                        ClearAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


            }
        });

        return view;
    }

    private void displayPasswords() {
        passwords=mSQLHelper.getPasswords();
        managerAdapter = new PasswordManagerAdapter((AppCompatActivity) getActivity(), passwords, this );
        passwordList.setAdapter(managerAdapter);
        managerAdapter.notifyDataSetChanged();
    }

    private void ClearAll() {
        txt_acc_password.setText("");
        txt_acc_user_name.setText("");
        txt_acc_name.setText("");
        passwordManager=null;
    }
    public void editPassword(PasswordManager p){
        passwordManager=new PasswordManager();
        passwordManager.setAccountId(p.getAccountId());
        passwordManager.setAccountName(p.getAccountName());
        passwordManager.setUserName(p.getUserName());
        passwordManager.setPassword(p.getPassword());

        try {
            txt_acc_name.setText(p.getAccountName());
            txt_acc_user_name.setText(Crypting.deCryptText(p.getUserName()));
            txt_acc_password.setText(Crypting.deCryptText(p.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
