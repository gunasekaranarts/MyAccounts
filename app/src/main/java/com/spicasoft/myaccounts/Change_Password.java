package com.spicasoft.myaccounts;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alimuzaffar.lib.pin.PinEntryEditText;

import Database.MyAccountsDatabase;
import POJO.SecurityProfile;
import TableData.SecurityTableData;

/**
 * Created by USER on 27-11-2017.
 */

public class Change_Password extends Fragment {
    LinearLayout layout_profile;
    AppCompatEditText txtName,txtEmail,txtMobile;
    Button btn_Save;
    SQLiteDatabase dataBase;
    MyAccountsDatabase mHelper;
    SecurityProfile securityProfile;
    PinEntryEditText oldPin,new_pin,confirm_pin;
    TextView  profile_title;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.security_profile, container, false);
        layout_profile=(LinearLayout)view.findViewById(R.id.layout_profile);
        layout_profile.setVisibility(View.GONE);
        profile_title=(TextView)view.findViewById(R.id.profile_Title);
        profile_title.setText("Change Pin");
        btn_Save=(Button) view.findViewById(R.id.btn_update);
        mHelper = new MyAccountsDatabase(getActivity());
        securityProfile=new SecurityProfile();
        securityProfile=mHelper.getProfile();
        oldPin = (PinEntryEditText) view.findViewById(R.id.txt_old_pin);
        new_pin = (PinEntryEditText) view.findViewById(R.id.txt_new_pin);
        confirm_pin = (PinEntryEditText) view.findViewById(R.id.txt_confirm_pin);
        oldPin.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence pwd) {
                new_pin.requestFocus();
            }
        });
        new_pin.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence pwd) {
                confirm_pin.requestFocus();
            }
        });
        confirm_pin.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence pwd) {
                btn_Save.requestFocus();
            }
        });
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(oldPin.getText().toString().equals("")){
                        oldPin.requestFocus();
                        oldPin.setError("Old pin can't be empty!");
                    }else if(!securityProfile.getPassword().equals(oldPin.getText().toString())){
                        oldPin.requestFocus();
                        oldPin.setError("Wrong pin!");
                    }else if(new_pin.getText().toString().equals("")) {
                        new_pin.requestFocus();
                        new_pin.setError("Pin can't be empty!");
                    }else if(new_pin.getText().toString().length()<=3){
                        new_pin.requestFocus();
                        new_pin.setError("Pin length must be 4 number");
                    } else if (!(new_pin.getText().toString().equals(confirm_pin.getText().toString().trim()))) {
                        confirm_pin.requestFocus();
                        new_pin.setError("Pin does not match!");
                        confirm_pin.setError("Pin does not match!");
                    }else if(new_pin.getText().toString().trim().equals(oldPin.getText().toString())){
                        new_pin.requestFocus();
                        new_pin.setError("Old pin and new pin can't be same!");
                    }else {
                        securityProfile.setPassword(new_pin.getText().toString().trim());
                        new Chnagepin().execute();
                    }
            }
        });
        return view;
    }
    private class Chnagepin extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            ChangePassword();
            return "sucess full added";
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            clearOperation();
            showAlertWithCancels("Pin has been changed successfully.");
        }
    }
    private void ChangePassword() {
        dataBase = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SecurityTableData.Password, securityProfile.getPassword());
        dataBase.beginTransaction();
        dataBase.update(SecurityTableData.SecurityTableName,values,SecurityTableData.ProfileId+"=?",new String[] {String.valueOf(securityProfile.getProfileId())});
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }
    private void clearOperation() {
        new_pin.setText("");
        oldPin.setText("");
        confirm_pin.setText("");
        oldPin.requestFocus();
        securityProfile=new SecurityProfile();

    }
    public void showAlertWithCancels(String BuilderText) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("My Accounts - Pin Changed");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               Fragment fragment = new Today();
                FloatingActionButton floatingActionButton = ((MainActivity) getActivity())
                        .getFloatingActionButton();
                if(floatingActionButton!=null)
                    floatingActionButton.show();
                FragmentTransaction fragmentTransaction=
                        getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,
                        R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                fragmentTransaction.commit();
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
