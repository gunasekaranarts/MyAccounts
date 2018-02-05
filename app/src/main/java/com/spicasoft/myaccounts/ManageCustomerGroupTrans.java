package com.spicasoft.myaccounts;

import android.app.Dialog;
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
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;

import CustomAdapters.CustomerTransactionGroupAdapter;
import Database.MyAccountsDatabase;
import POJO.CustomerTransaction;
import POJO.CustomerTransactionGroup;
import POJO.Customers;
import Utils.DateUtil;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by USER on 01-02-2018.
 */

public class ManageCustomerGroupTrans extends Fragment {
    AppCompatEditText transdate,txt_transAmt,txt_transDesc;
    MaterialSpinner sprTransaction_Type,spr_customerlist;

    Button btn_Save_Cust_Trans;
    MyAccountsDatabase mSQLHelper;
    SQLiteDatabase dataBase;
    RecyclerView customerGroupList;
    CustomerTransactionGroupAdapter cusGroupAdapter;
    ArrayAdapter customerAdapter;
    ArrayList<CustomerTransactionGroup> addCustomersGroup = new ArrayList<>();
    ArrayList<Customers> customers = new ArrayList<>();
    RecyclerView.LayoutManager mLayoutManager;
    CustomerTransaction customerTransaction;
    Customers selectedCustomer;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.manage_pending_salary, container, false);
        customerGroupList = (RecyclerView) view.findViewById(R.id.customertransgrouplist);
        spr_customerlist=(MaterialSpinner) view.findViewById(R.id.spr_customerlist);
        sprTransaction_Type=(MaterialSpinner) view.findViewById(R.id.sprTransaction_Type);
        transdate=(AppCompatEditText) view.findViewById(R.id.transdate);
        txt_transAmt=(AppCompatEditText) view.findViewById(R.id.txt_transAmt);
        txt_transDesc=(AppCompatEditText) view.findViewById(R.id.txt_transDesc);
        btn_Save_Cust_Trans =(Button) view.findViewById(R.id.btn_Save_Cust_Trans);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mSQLHelper=new MyAccountsDatabase(getContext());
        customerGroupList.setLayoutManager(mLayoutManager);
        transdate.setText("" + DateUtil.getCurrentDateHuman());
        transdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogTransDate("Transaction Date", 1);
            }
        });
        setCustomerAdapter();
        displayCustomerGroupTrans();

        btn_Save_Cust_Trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error=false;
                if(spr_customerlist.getSelectedItemPosition()==-1){
                    spr_customerlist.requestFocus();
                    error=true;
                }
                if(sprTransaction_Type.getSelectedItemPosition()==-1)
                {
                    error=true;
                    sprTransaction_Type.requestFocus();
                    sprTransaction_Type.setError("Transaction type should not empty");
                }
                if(txt_transAmt.getText().toString().equals(""))
                {
                    txt_transAmt.requestFocus();
                    txt_transAmt.setError("Amount should not empty");
                    error=true;
                }
                if(!error){
                    SaveTransaction();
                }
            }
        });
        return  view;
    }

    private void SaveTransaction() {
        if(customerTransaction==null)
            customerTransaction=new CustomerTransaction();
        customerTransaction.setTransactionDate(transdate.getText().toString());
        selectedCustomer=customers.get(spr_customerlist.getSelectedItemPosition()-1);
        customerTransaction.setCustomerId(selectedCustomer.getCustomerID());
        customerTransaction.setTransactionDesc(txt_transDesc.getText().toString());
        if(sprTransaction_Type.getSelectedItem().toString().equals("Salary Pending"))
            customerTransaction.setTransactionType(0);
        else if(sprTransaction_Type.getSelectedItem().toString().equals("Received"))
            customerTransaction.setTransactionType(1);
        customerTransaction.setTransactionAmt(Integer.parseInt(txt_transAmt.getText().toString()));
        mSQLHelper.InsertCustomerTransaction(customerTransaction);
        Toast.makeText(getContext(),"Transaction saved",Toast.LENGTH_SHORT).show();
        displayCustomerGroupTrans();
        ClearAll();
    }

    private void ClearAll() {
        spr_customerlist.setSelection(0);
        sprTransaction_Type.setSelection(0,true);
        txt_transAmt.setText("");
        txt_transDesc.setText("");
        customerTransaction=null;
    }

    private void displayCustomerGroupTrans() {
        addCustomersGroup=mSQLHelper.getCustomersGroupTransaction();
        cusGroupAdapter = new CustomerTransactionGroupAdapter( addCustomersGroup,(AppCompatActivity) getActivity());
        customerGroupList.setAdapter(cusGroupAdapter);
        cusGroupAdapter.notifyDataSetChanged();
    }
    private void setCustomerAdapter() {
        customers=new ArrayList<Customers>();
        customers=mSQLHelper.getCustomers();
        customerAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, customers);
        spr_customerlist.setAdapter(customerAdapter);
    }
    private void ShowDialogTransDate(String Titile, final int i) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_datetimepicker);
        TextView title = (TextView) dialog.findViewById(R.id.dialog_datetime_title);
        title.setText("" + Titile);
        Button Ok = (Button) dialog.findViewById(R.id.setdatetime_ok);
        final DatePicker dp = (DatePicker) dialog.findViewById(R.id.date_picker);
        Button cancel = (Button) dialog.findViewById(R.id.setdatetime_cancel);
        dp.setMaxDate((System.currentTimeMillis() - 1000));
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = String.valueOf(dp.getMonth() + 1);
                String day = String.valueOf(dp.getDayOfMonth());
                String strDateTime = ( dp.getYear() + "-" + (((month.length() == 1 ? "0" + month : month))) + "-" + (day.length() == 1 ? "0" + day : day));

                switch (i) {
                    case 1:
                        transdate.setText("" + strDateTime);
                        break;
                    default:
                }
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }


}
