package CustomAdapters;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.spicasoft.myaccounts.ManagePersons;
import com.spicasoft.myaccounts.R;

import java.util.ArrayList;

import CustomWidget.TextAwesome;
import Database.MyAccountsDatabase;
import POJO.Persons;
import TableData.PersonsTableData;

/**
 * Created by USER on 09-11-2017.
 */

public class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.ViewHolder> {
    ArrayList<Persons> addPerson = new ArrayList<>();
    String personID, pName, Name,Mobile;
    AppCompatActivity mContext;
    ProgressDialog progressDialog;
    MyAccountsDatabase mSQLHelper;
    SQLiteDatabase dataBase;
    int personsID,positions;
    ManagePersons manageperson;

    public PersonsAdapter(AppCompatActivity mContext, ArrayList<Persons> addPerson, ManagePersons manageperson) {
        this.mContext = mContext;
        this.addPerson = addPerson;
        this.manageperson=manageperson;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item, parent, false);
        return new ViewHolder(view1);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Persons person = addPerson.get(position);
        mSQLHelper = new MyAccountsDatabase(mContext);
        if(person.getPersonName()!=null){
            holder.personName.setText(""+person.getPersonName());
        }else {
            holder.personName.setText("-");
        }
        if(person.getMobile()!=null){
            holder.personMobile.setText(""+person.getMobile());
        }else {
            holder.personMobile.setText("-");
        }
        holder.Edit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                manageperson.EditPerson(addPerson.get(position));
            }
        });
        holder.Delete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                positions=position;
                pName = addPerson.get(position).getPersonName();
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("My Accounts");
                builder.setMessage("Are you sure want to Remove " +pName+ " ?" );
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        personsID = addPerson.get(position).getPersonId();

                        new DeleteAsync().execute();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {return addPerson.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {
        TextView personName, personMobile;
        TextAwesome Edit, Delete;
        public ViewHolder(View itemView) {
            super(itemView);
            personName=(TextView) itemView.findViewById(R.id.txt_personview);
            personMobile=(TextView) itemView.findViewById(R.id.txt_mobileview);
            Edit = (TextAwesome) itemView.findViewById(R.id.txt_edit);
            Delete = (TextAwesome) itemView.findViewById(R.id.txt_delete);

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
            DeleteRow(personsID);
            return "sucess full deleted";
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("My Accounts");
            builder.setMessage("Person " + pName + " details deleted successfully.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialo, int which) {
                   ReloadData(positions);
                    dialo.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    private void ReloadData(int positions) {
        addPerson.remove(positions);
        notifyDataSetChanged();
    }


    private void DeleteRow(int personID) {
        dataBase = mSQLHelper.getWritableDatabase();
        dataBase.beginTransaction();
        dataBase.delete(PersonsTableData.PersonTableName, PersonsTableData.PersonID + "=?",new String[] {String.valueOf(personID)});
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }
}
