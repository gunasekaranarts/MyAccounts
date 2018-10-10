package CustomAdapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.spicasoft.myaccounts.PasswordManagement;
import com.spicasoft.myaccounts.R;

import java.util.ArrayList;

import CustomWidget.TextAwesome;
import Database.MyAccountsDatabase;
import POJO.PasswordManager;
import POJO.SecurityProfile;
import TableData.PasswordManagerTableData;
import Utils.Crypting;

/**
 * Created by USER on 10-10-2018.
 */

public class PasswordManagerAdapter extends RecyclerView.Adapter<PasswordManagerAdapter.ViewHolder> {
    ArrayList<PasswordManager> passwords=new ArrayList<>();
    AppCompatActivity mContext;
    ProgressDialog progressDialog;
    MyAccountsDatabase mSQLHelper;
    SQLiteDatabase dataBase;
    SecurityProfile securityProfile;
    PasswordManagement passwordManagement;
    int accountId,pos;
    public PasswordManagerAdapter(AppCompatActivity mContext, ArrayList<PasswordManager> passwords, PasswordManagement passwordManagement) {
        this.mContext = mContext;
        this.passwords = passwords;
        this.passwordManagement=passwordManagement;

    }
    @Override
    public PasswordManagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.password_mgt_item, parent, false);
        return new ViewHolder(view1);
    }

    @Override
    public void onBindViewHolder(PasswordManagerAdapter.ViewHolder holder, final int position) {
        final PasswordManager password=passwords.get(position);
        mSQLHelper=new MyAccountsDatabase(mContext);
        holder.txt_account_name.setText(password.getAccountName());
        holder.btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_pin);
                final PinEntryEditText pinEntry = (PinEntryEditText) dialog.findViewById(R.id.txt_app_pin);
                final TextView username= dialog.findViewById(R.id.txt_UserName);
                final TextView passwords= dialog.findViewById(R.id.txt_Password);
                final TextView txt_header= dialog.findViewById(R.id.txt_header);

                final Button Ok = (Button) dialog.findViewById(R.id.btn_ok);
                final Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                final Button copy = (Button) dialog.findViewById(R.id.btn_copy);
                pinEntry.requestFocus();
                final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                Ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        securityProfile=mSQLHelper.getProfile();

                        if(pinEntry.getText().toString().equals(securityProfile.getPassword())){
                            txt_header.setText("User Name & Password");
                            pinEntry.setVisibility(View.GONE);
                            Ok.setVisibility(View.GONE);
                            copy.setVisibility(View.VISIBLE);
                            username.setVisibility(View.VISIBLE);
                            passwords.setVisibility(View.VISIBLE);
                            try {
                                username.setText("User Name: "+Crypting.deCryptText(password.getUserName()));
                                passwords.setText("Password : "+ Crypting.deCryptText(password.getPassword()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(mContext,"Invalid Pin",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imm.hideSoftInputFromWindow(pinEntry.getApplicationWindowToken(),0);
                        dialog.dismiss();
                    }
                });
                copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
                            ClipData clip = null;
                            clip = ClipData.newPlainText("text", Crypting.deCryptText(password.getPassword()));
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(mContext,"Password copied to Clipboard!",Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                dialog.show();
            }
        });
        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_pin);
                final PinEntryEditText pinEntry = (PinEntryEditText) dialog.findViewById(R.id.txt_app_pin);
                final TextView username= dialog.findViewById(R.id.txt_UserName);
                final TextView passwordss= dialog.findViewById(R.id.txt_Password);
                final TextView txt_header= dialog.findViewById(R.id.txt_header);

                final Button Ok = (Button) dialog.findViewById(R.id.btn_ok);
                final Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                final Button copy = (Button) dialog.findViewById(R.id.btn_copy);
                pinEntry.requestFocus();
                final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                Ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        securityProfile=mSQLHelper.getProfile();

                        if(pinEntry.getText().toString().equals(securityProfile.getPassword())){
                            passwordManagement.editPassword(passwords.get(position));
                            dialog.dismiss();
                        }else{
                            Toast.makeText(mContext,"Invalid Pin",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imm.hideSoftInputFromWindow(pinEntry.getApplicationWindowToken(),0);
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos=position;

                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_pin);
                final PinEntryEditText pinEntry = (PinEntryEditText) dialog.findViewById(R.id.txt_app_pin);
                final TextView username= dialog.findViewById(R.id.txt_UserName);
                final TextView passwordss= dialog.findViewById(R.id.txt_Password);
                final TextView txt_header= dialog.findViewById(R.id.txt_header);
                txt_header.setText("Are you sure want to delete? (Enter App Pin to delete)");
                final Button Ok = (Button) dialog.findViewById(R.id.btn_ok);
                final Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                final Button copy = (Button) dialog.findViewById(R.id.btn_copy);
                pinEntry.requestFocus();
                final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                Ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        securityProfile=mSQLHelper.getProfile();

                        if(pinEntry.getText().toString().equals(securityProfile.getPassword())){
                            accountId = passwords.get(position).getAccountId();

                            new DeleteAsync().execute();
                            dialog.dismiss();
                        }else{
                            Toast.makeText(mContext,"Invalid Pin",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imm.hideSoftInputFromWindow(pinEntry.getApplicationWindowToken(),0);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return passwords.size();
    }
    public class ViewHolder  extends RecyclerView.ViewHolder {
        TextView txt_account_name;
        TextAwesome btn_view, btn_edit, btn_delete;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_account_name=(TextView) itemView.findViewById(R.id.txt_account_name);
            btn_view = (TextAwesome) itemView.findViewById(R.id.btn_view);
            btn_edit = (TextAwesome) itemView.findViewById(R.id.btn_edit);
            btn_delete = (TextAwesome) itemView.findViewById(R.id.btn_delete);

        }
    }
    private class DeleteAsync extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            DeleteRow(accountId);
            return "sucess full deleted";
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("My Accounts");
            builder.setMessage("Password deleted successfully.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialo, int which) {
                    ReloadData(pos);
                    dialo.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    private void ReloadData(int positions) {
        passwords.remove(positions);
        notifyDataSetChanged();
    }


    private void DeleteRow(int accountId) {
        dataBase = mSQLHelper.getWritableDatabase();
        dataBase.beginTransaction();
        dataBase.delete(PasswordManagerTableData.PasswordManagerTableName, PasswordManagerTableData.AccountId + "=?",new String[] {String.valueOf(accountId)});
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }
}
