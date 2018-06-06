package CustomAdapters;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.spicasoft.myaccounts.ManageCustomerGroupTrans;
import com.spicasoft.myaccounts.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import CustomWidget.TextAwesome;
import Database.MyAccountsDatabase;
import POJO.CustomerTransaction;
import POJO.CustomerTransactionGroup;

/**
 * Created by USER on 31-01-2018.
 */

public class CustomerTransactionGroupAdapter  extends RecyclerView.Adapter<CustomerTransactionGroupAdapter.ViewHolder> {

    ArrayList<CustomerTransactionGroup> addCustomerGroupTrans = new ArrayList<>();
    MyAccountsDatabase mSQLHelper;
    AppCompatActivity mContext;

    public CustomerTransactionGroupAdapter(ArrayList<CustomerTransactionGroup> addCustomer, AppCompatActivity mContext) {
        this.addCustomerGroupTrans = addCustomer;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_trans_group_item, parent, false);
        return new ViewHolder(view1);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CustomerTransactionGroup customergroup = addCustomerGroupTrans.get(position);
        mSQLHelper = new MyAccountsDatabase(mContext);
        DecimalFormat format = new DecimalFormat("0.00");
        if(customergroup.getCustomerName()!=null){
            holder.customerName.setText(""+customergroup.getCustomerName());
        }else {
            holder.customerName.setText("-");
        }
        if(customergroup.getCustomerMobile()!=null){
            holder.customerMobile.setText(""+customergroup.getCustomerMobile());
        }else {
            holder.customerMobile.setText("-");
        }
        if(customergroup.getTotalAmt()!=0){
            holder.TotalAmt.setText("Total : ₹"+format.format(customergroup.getTotalAmt()));
        }else {
            holder.TotalAmt.setText("Total :₹0.00");
        }
        if(customergroup.getReceivedAmt()!=0){
            holder.ReceiveAmt.setText("Received : ₹"+format.format(customergroup.getReceivedAmt()));
        }else {
            holder.ReceiveAmt.setText("Received : ₹0.00");
        }
        if(customergroup.getPendingAmt()!=0){
            holder.PendingAmt.setText("Pending : ₹"+format.format(customergroup.getPendingAmt()));
        }else {
            holder.PendingAmt.setText("Pending : ₹0.00");
        }
        if(customergroup.getStatus().equals("Active")){
            holder.txt_acc_status.setText("Status : Active");
            holder.txt_acc_status.setTextColor(Color.GREEN);
        }else {
            holder.txt_acc_status.setText("Status : Closed");
            holder.txt_acc_status.setTextColor(Color.RED);
            holder.btn_close.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogTransHistory("Transaction History",1,customergroup);
            }
        });
        holder.btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("My Accounts");
                builder.setMessage("Are you sure want to close?" );
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSQLHelper.CloseCustomerAcc(customergroup);
                        addCustomerGroupTrans=mSQLHelper.getCustomersGroupTransaction();
                        notifyDataSetChanged();
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
    private void ShowDialogTransHistory(String Titile, final int i,CustomerTransactionGroup customergroup) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle(Titile);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(500,200);
        DecimalFormat format = new DecimalFormat("0.00");
        dialog.setContentView(R.layout.dialog_customer_transactions);
        TextView txt_cust_name = (TextView) dialog.findViewById(R.id.txt_cust_name);
        txt_cust_name.setText(customergroup.getCustomerName());
        TextView txt_cust_total = (TextView) dialog.findViewById(R.id.txt_cust_total);
        txt_cust_total.setText("Total Amount: ₹"+ format.format(customergroup.getTotalAmt()));
        TextView txt_cust_received = (TextView) dialog.findViewById(R.id.txt_cust_received);
        txt_cust_received.setText("Received Amount: ₹"+ format.format(customergroup.getReceivedAmt()));
        TextView txt_cust_pending = (TextView) dialog.findViewById(R.id.txt_cust_pending);
        txt_cust_pending.setText("Pending Amount: ₹"+ format.format(customergroup.getPendingAmt()));
        RecyclerView customerTransList;
        RecyclerView.LayoutManager mLayoutManager;
        ArrayList<CustomerTransaction> customerTransactions = new ArrayList<>();
        customerTransList = (RecyclerView) dialog.findViewById(R.id.history);
        customerTransList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext);
        customerTransList.setLayoutManager(mLayoutManager);
        CustomerHistoryAdapter customersTransAdapter;
        customerTransactions=mSQLHelper.getCustomerTransactions(customergroup.getCustomerID());
        customersTransAdapter = new CustomerHistoryAdapter(customerTransactions,(AppCompatActivity) mContext);
        customerTransList.setAdapter(customersTransAdapter);
        customersTransAdapter.notifyDataSetChanged();

        dialog.show();

    }
    @Override
    public int getItemCount() {return addCustomerGroupTrans.size();
    }
    public class ViewHolder  extends RecyclerView.ViewHolder {
        TextView customerName, customerMobile,TotalAmt,ReceiveAmt,PendingAmt,txt_acc_status,btn_close;

        public ViewHolder(View itemView) {
            super(itemView);
            customerName=(TextView) itemView.findViewById(R.id.txt_customerview);
            customerMobile=(TextView) itemView.findViewById(R.id.txt_custmobileview);
            TotalAmt=(TextView) itemView.findViewById(R.id.txt_total);
            ReceiveAmt=(TextView) itemView.findViewById(R.id.txt_received);
            PendingAmt=(TextView) itemView.findViewById(R.id.txt_remaining);
            txt_acc_status=(TextView) itemView.findViewById(R.id.txt_acc_status);
            btn_close=(TextView) itemView.findViewById(R.id.btn_close);
        }
    }
}
