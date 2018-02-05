package CustomAdapters;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spicasoft.myaccounts.R;

import java.util.ArrayList;

import Database.MyAccountsDatabase;
import POJO.CustomerTransaction;

/**
 * Created by USER on 05-02-2018.
 */

public class CustomerHistoryAdapter  extends RecyclerView.Adapter<CustomerHistoryAdapter.ViewHolder> {
    ArrayList<CustomerTransaction> customerTransactions = new ArrayList<>();
    MyAccountsDatabase mSQLHelper;
    AppCompatActivity mContext;

    public CustomerHistoryAdapter(ArrayList<CustomerTransaction> customerTransactions, AppCompatActivity mContext) {
        this.customerTransactions = customerTransactions;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_history_item, parent, false);
        return new ViewHolder(view1);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CustomerTransaction customertrans = customerTransactions.get(position);
        mSQLHelper = new MyAccountsDatabase(mContext);
        if(customertrans.getTransactionDate()!=null) {
            holder.history_date.setText(customertrans.getTransactionDate());
        }else{
            holder.history_date.setText("-");
        }
        if(customertrans.getTransactionDesc()!=null) {
            holder.history_desc.setText(customertrans.getTransactionDesc());
        }else{
            if(customertrans.getTransactionType()==0)
                holder.history_desc.setText("Pending");
            else
                holder.history_desc.setText("Received");
        }
        if(customertrans.getTransactionAmt()!=0) {
            if(customertrans.getTransactionType()==0)
                holder.history_Amt.setTextColor(Color.RED);
            else  if(customertrans.getTransactionType()==1)
                holder.history_Amt.setTextColor(Color.BLUE);
            holder.history_Amt.setText("₹ "+String.valueOf(customertrans.getTransactionAmt()));
        }else{
            holder.history_Amt.setText("₹ 0");
        }
    }

    @Override
    public int getItemCount() {
        return customerTransactions.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {
        TextView history_date, history_desc,history_Amt;

        public ViewHolder(View itemView) {
            super(itemView);
            history_date=(TextView) itemView.findViewById(R.id.txt_hist_date);
            history_desc=(TextView) itemView.findViewById(R.id.txt_hist_desc);
            history_Amt=(TextView) itemView.findViewById(R.id.txt_hist_amt);

        }
    }
}
