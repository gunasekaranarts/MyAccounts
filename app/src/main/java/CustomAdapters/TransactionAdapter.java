package CustomAdapters;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spicasoft.myaccounts.R;
import com.spicasoft.myaccounts.Today;

import java.util.ArrayList;

import CustomWidget.TextAwesome;
import Database.MyAccountsDatabase;
import POJO.Transaction;
import TableData.PersonsTableData;
import TableData.TransactionsTableData;

/**
 * Created by USER on 21-11-2017.
 */

public class TransactionAdapter  extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    ArrayList<Transaction> transactions = new ArrayList<>();
    AppCompatActivity mContext;
    ProgressDialog progressDialog;
    MyAccountsDatabase mSQLHelper;
    SQLiteDatabase dataBase;
    ArrayList<Integer> IncomeId,ExpenseId;
    int transactionID,positions;
    Today today;
    public TransactionAdapter(ArrayList<Transaction> transactions, AppCompatActivity mContext, ArrayList<Integer>IncomeID,ArrayList<Integer>ExpenseId,Today today) {
        this.transactions = transactions;
        this.mContext = mContext;
        this.IncomeId=IncomeID;
        this.ExpenseId =ExpenseId;
        this.today=today;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction, parent, false);
        return new TransactionAdapter.ViewHolder(view1);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Transaction transaction = transactions.get(position);
        mSQLHelper = new MyAccountsDatabase(mContext);
        if(transaction.getTransactionName()!=null){
            holder.TransactionName.setText(""+transaction.getTransactionName());
        }else {
            holder.TransactionName.setText("-");
        }
        if(transaction.getTransactionDesc()!=null){
            holder.TransDesc.setText(""+transaction.getTransactionDesc());
        }else {
            holder.TransDesc.setText("");
        }
        if(transaction.getTransactionAmount()!=0){
            if(IncomeId.contains(transaction.getTransactionTypeID()))
            {
                holder.TransAmt.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                holder.TransType.setText(R.string.fa_download);
                holder.TransType.setTextColor(ContextCompat.getColor(mContext,R.color.green));
            }else
            {
                holder.TransAmt.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                holder.TransType.setText(R.string.fa_upload);
                holder.TransType.setTextColor(ContextCompat.getColor(mContext,R.color.red));
            }
            holder.TransAmt.setText("₹"+transaction.getTransactionAmount());
        }else {
            holder.TransAmt.setText("₹0");
        }
        holder.Edit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                today.EditTransaction(transactions.get(position).getTransactionID());
            }
        });
        holder.Delete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                positions=position;
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("My Accounts");
                builder.setMessage("Are you sure want to delete?" );
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        transactionID = transactions.get(position).getTransactionID();

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
    public int getItemCount() {
        return transactions.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {
        TextView TransactionName, TransDesc,TransAmt;
        TextAwesome TransType, Edit, Delete;
        public ViewHolder(View itemView) {
            super(itemView);
            TransactionName=(TextView)itemView.findViewById(R.id.txt_narration);
            TransDesc=(TextView)itemView.findViewById(R.id.txt_TransactionDesc);
            TransAmt=(TextView)itemView.findViewById(R.id.txt_Transaction_Amt);
            Edit = (TextAwesome) itemView.findViewById(R.id.edit);
            Delete = (TextAwesome) itemView.findViewById(R.id.delete);
            TransType= (TextAwesome) itemView.findViewById(R.id.imageTransaction);
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
            DeleteRow(transactionID);
            return "sucess full deleted";
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("My Accounts");
            builder.setMessage("Transaction details deleted successfully.");
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
        transactions.remove(positions);
        today.ShowBalance();
        notifyDataSetChanged();
    }
    private void DeleteRow(int transactionID) {
        dataBase = mSQLHelper.getWritableDatabase();
        dataBase.beginTransaction();
        dataBase.delete(TransactionsTableData.TransactionTableName, TransactionsTableData.TransactionID + "=?",new String[] {String.valueOf(transactionID)});
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }
}
