package com.spicasoft.myaccounts;

import android.app.Dialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import CustomAdapters.TransactionAdapter;
import Database.MyAccountsDatabase;
import POJO.Transaction;
import POJO.TransactionFilter;
import Utils.DateUtil;

/**
 * Created by USER on 07-11-2017.
 */

public class Today extends Fragment {
    String TransactionDate;
    public Fragment Initialize(String TransactionDate) {
        this.TransactionDate = TransactionDate;
        return this;
    }
    public Today(){

    }
    AppCompatEditText transdate;
    TextView txt_Total_Savings,txt_Earnings,txt_Debits,txt_Credits,txt_Savings;
    MyAccountsDatabase mSqlHelper;
    ArrayList<Transaction> transactions;
    Transaction transaction;
    ArrayList<Integer> incomeId,expenseId;
    int tot_income=0,tot_debit=0,tot_credit=0,tot_savings=0;
    TransactionAdapter transactionAdapter;
    RecyclerView transactionList;
    RecyclerView.LayoutManager mLayoutManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_today, container, false);
        transdate=(AppCompatEditText) view.findViewById(R.id.transdate);
        txt_Total_Savings=(TextView) view.findViewById(R.id.txt_Total_Balance);
        txt_Earnings=(TextView) view.findViewById(R.id.txt_Earnings);
        txt_Debits=(TextView) view.findViewById(R.id.txt_Debits);
        txt_Credits=(TextView) view.findViewById(R.id.txt_Credits);
        txt_Savings=(TextView) view.findViewById(R.id.txt_Savings);
        if(TransactionDate!=null) {
            transdate.setText(TransactionDate);
        }else {
            transdate.setText("" + DateUtil.getCurrentDateHuman());
        }

        transdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogTransDate("Transaction Date", 1);
            }
        });
        mSqlHelper=new MyAccountsDatabase(getActivity());

        ShowBalance();
        incomeId=new ArrayList<>();
        incomeId=mSqlHelper.getIncomeIds();
        expenseId=new ArrayList<>();
        expenseId=mSqlHelper.getExpenseIds();
        transactionList = (RecyclerView) view.findViewById(R.id.transactions);
        transactionList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        transactionList.setLayoutManager(mLayoutManager);
        getTransactions();
        return view;

    }
    public void EditTransaction(int TransactionId){
        FloatingActionButton floatingActionButton = ((MainActivity) getActivity())
                .getFloatingActionButton();
        if(floatingActionButton!=null)
            floatingActionButton.hide();
        Fragment fragment = new AddTransaction().Initialize(TransactionId);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom,
                R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void ShowBalance() {
        DecimalFormat format = new DecimalFormat("0.00");
        txt_Total_Savings.setText("₹"+format.format(mSqlHelper.getAvailableBalance()));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    public void getTransactions(){
        tot_income=tot_savings=tot_credit=tot_debit=tot_credit=0;
        transactions=new ArrayList<Transaction>();
        TransactionFilter transactionFilter=new TransactionFilter();
        transactionFilter.setTransactionDate(transdate.getText().toString());
        transactions=mSqlHelper.getTransactions(transactionFilter);
        for (Transaction transaction:transactions) {
            if(transaction.getTransactionTypeID()==1){
                tot_income+=transaction.getTransactionAmount();
            }else if(transaction.getTransactionTypeID()==3)
            {
                tot_savings+=transaction.getTransactionAmount();
            }
            if(incomeId.contains(transaction.getTransactionTypeID())){
                tot_credit+=transaction.getTransactionAmount();
            }else{
                tot_debit+=transaction.getTransactionAmount();
            }

        }
        DecimalFormat format = new DecimalFormat("0.00");
        txt_Earnings.setText("₹"+format.format(tot_income));
        txt_Debits.setText("₹"+format.format(tot_debit));
        txt_Credits.setText("₹"+format.format(tot_credit));
        txt_Savings.setText("₹"+format.format(tot_savings));
        setAdapter();

    }

    private void setAdapter() {
        transactionAdapter = new TransactionAdapter(transactions,(AppCompatActivity) getActivity(), incomeId,expenseId,Today.this);
        transactionList.setAdapter(transactionAdapter);
        transactionAdapter.notifyDataSetChanged();
        transaction=null;
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
                String strDateTime = ((day.length() == 1 ? "0" + day : day)) + "-" + (((month.length() == 1 ? "0" + month : month))) + "-" + dp.getYear();

                switch (i) {
                    case 1:
                        transdate.setText("" + strDateTime);
                        getTransactions();
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
