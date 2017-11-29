package com.spicasoft.myaccounts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;

import Database.MyAccountsDatabase;
import POJO.SecurityProfile;

/**
 * Created by USER on 08-11-2017.
 */

public class Password_Activity extends AppCompatActivity {
//    AppCompatEditText txtpassword;
//    AppCompatButton btnlogin;
    MyAccountsDatabase mHelper;
    SecurityProfile securityProfile;
    TextView lnkforgorpwd;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);
        lnkforgorpwd=(TextView) findViewById(R.id.lnk_forgot_password);
        mHelper=new MyAccountsDatabase(this);
        securityProfile=mHelper.getProfile();
        final PinEntryEditText pinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry);
        if (pinEntry != null) {
            pinEntry.requestFocus();
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence pwd) {
                    String pin = pwd.toString();
                  if(securityProfile.getPassword().equals(pin)){
                      imm.hideSoftInputFromWindow(pinEntry.getApplicationWindowToken(),0);
                      Intent send = new Intent(getApplicationContext(), MainActivity.class);
                      startActivity(send);
                    } else {
                        Toast.makeText(Password_Activity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        pinEntry.setText(null);
                    }
                }
            });
        }
         lnkforgorpwd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showAlertWithCancels("Your new pin has been sent to your email id : "
                        +securityProfile.getEmail());
            }
        });

    }
    public void showAlertWithCancels(String BuilderText) {
        android.support.v7.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("My Accounts - Password Reset");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
