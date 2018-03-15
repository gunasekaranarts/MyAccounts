package CustomAdapters;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spicasoft.myaccounts.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import CustomWidget.TextAwesome;
import Database.MyAccountsDatabase;
import POJO.Transaction;
import POJO.TransactionType;

/**
 * Created by USER on 24-11-2017.
 */

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.ViewHolder> {
    ArrayList<Transaction> transactions = new ArrayList<>();
    AppCompatActivity mContext;
    MyAccountsDatabase mSQLHelper;
    SQLiteDatabase dataBase;
    ArrayList<Integer> IncomeId,ExpenseId;
    ArrayList<TransactionType> transactionType;


    public StatementAdapter(ArrayList<Transaction> transactions, AppCompatActivity mContext,
                            ArrayList<Integer> incomeId, ArrayList<Integer> expenseId) {
        this.transactions = transactions;
        this.mContext = mContext;
        this.IncomeId = incomeId;
        this.ExpenseId = expenseId;
        transactionType=new ArrayList<>();
        mSQLHelper=new MyAccountsDatabase(this.mContext);
        transactionType=mSQLHelper.getTransactionTypes();

    }
    @Override
    public StatementAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.statement_item, parent, false);
        return new StatementAdapter.ViewHolder(view1);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Transaction transaction = transactions.get(position);
        mSQLHelper = new MyAccountsDatabase(mContext);
        if(transaction.getTransactionDate()!=null){
            holder.TransDate.setText(""+transaction.getTransactionDate());
        }else {
            holder.TransDate.setText("");
        }

        if(transaction.getTransactionName()!=null){
             String desc="";
             if(transaction.getTransactionDesc()!=null)
                 desc=transaction.getTransactionDesc();
            holder.TransactionName.setText(""+transaction.getTransactionName()+"\n"+desc);
        }else {
            holder.TransactionName.setText("");
        }
        if(transaction.getTransactionAmount()!=0){
            DecimalFormat format = new DecimalFormat("0.00");
            holder.TransAmt.setText("₹"+format.format(transaction.getTransactionAmount()));
            if(IncomeId.contains(transaction.getTransactionTypeID()))
                holder.TransAmt.setTextColor(ContextCompat.getColor(mContext, R.color.green));
           else
               holder.TransAmt.setTextColor(ContextCompat.getColor(mContext, R.color.red));


        }

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
    public class ViewHolder  extends RecyclerView.ViewHolder {
        TextView TransactionName,TransAmt,TransType,TransDate;

        public ViewHolder(View itemView) {
            super(itemView);
            TransDate=(TextView) itemView.findViewById(R.id.txt_date);
            TransactionName=(TextView)itemView.findViewById(R.id.txt_trans_details);
            TransAmt=(TextView) itemView.findViewById(R.id.txt_amt);


        }
    }
}
