package CustomAdapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spicasoft.myaccounts.R;
import com.spicasoft.myaccounts.Transaction_Type;

import java.util.ArrayList;

import CustomWidget.TextAwesome;
import Database.MyAccountsDatabase;
import POJO.TransactionType;

/**
 * Created by USER on 27-04-2018.
 */

public class TransactionTypeAdapter extends RecyclerView.Adapter<TransactionTypeAdapter.ViewHolder> {
    ArrayList<TransactionType> transactionstype = new ArrayList<>();
    AppCompatActivity mContext;
    MyAccountsDatabase mSQLHelper;
    Transaction_Type transaction_type;

    public TransactionTypeAdapter(ArrayList<TransactionType> transactionstype, AppCompatActivity mContext, Transaction_Type transaction_type) {
        this.transactionstype = transactionstype;
        this.mContext = mContext;
        this.transaction_type=transaction_type;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.transactiontype_item, parent, false);
        return new TransactionTypeAdapter.ViewHolder(view1);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TransactionType transaction = transactionstype.get(position);
        mSQLHelper = new MyAccountsDatabase(mContext);
        holder.txt_trans_type.setText(transaction.getTransactionType());
        if(transaction.getCashFlow().equals("Inward")) {
            holder.imageTransaction.setText(R.string.fa_download);
            holder.imageTransaction.setTextColor(ContextCompat.getColor(mContext,R.color.green));
        }else{
            holder.imageTransaction.setText(R.string.fa_upload);
            holder.imageTransaction.setTextColor(ContextCompat.getColor(mContext,R.color.red));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               transaction_type.EditTransactionType(transaction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionstype.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {
        TextView txt_trans_type;
        TextAwesome imageTransaction;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_trans_type=(TextView)itemView.findViewById(R.id.txt_trans_type);
            imageTransaction = (TextAwesome) itemView.findViewById(R.id.imageTransaction);
        }
    }
}
