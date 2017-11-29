package com.spicasoft.myaccounts;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import CustomAdapters.PersonsAdapter;
import CustomWidget.TextAwesome;
import Database.MyAccountsDatabase;
import POJO.Persons;
import POJO.Transaction;
import POJO.TransactionFilter;
import POJO.TransactionType;
import Utils.DateUtil;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by USER on 07-11-2017.
 */

public class AddTransaction extends Fragment {
    int TransactionId=0;
    public Fragment Initialize(int transactionid) {
        this.TransactionId = transactionid;
        return this;
    }
    public AddTransaction(){

    }
    AppCompatEditText transDate,transName,transDesc,transAmt,txtName,txtMobile;
    MaterialSpinner transType;
    SearchableSpinner transPerson;
    TextAwesome lnk_back,lnk_addperson;
    Button savetrans;
    ArrayList<TransactionType> transactionType;
    ArrayList<Persons> persons;
    MyAccountsDatabase mSqlHelper;
    ArrayAdapter TransTypeAdapter;
    ArrayAdapter PersonAdapter;
    TransactionType selectedTransType;
    LinearLayout personlayout;
    Persons selectedPerson,person;
    Fragment fragment;
    List<Integer> personTypeIdlist,IncomeId,ExpenseId;
    Transaction transaction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.add_new_transaction, container, false);
        transDate=(AppCompatEditText) view.findViewById(R.id.transdate);
        transName=(AppCompatEditText) view.findViewById(R.id.txt_transactionName);
        transDesc=(AppCompatEditText) view.findViewById(R.id.txt_transactionDesc);
        transAmt=(AppCompatEditText) view.findViewById(R.id.transamount);
        transType=(MaterialSpinner) view.findViewById(R.id.sprTransaction_Type);
        transPerson=(SearchableSpinner) view.findViewById(R.id.spnr_Persons);
        lnk_back=(TextAwesome)view.findViewById(R.id.lnk_back);
        lnk_addperson=(TextAwesome)view.findViewById(R.id.lnk_addperson);
        savetrans=(Button)view.findViewById(R.id.savetrans);
        personlayout=(LinearLayout) view.findViewById(R.id.personlayout);
        mSqlHelper=new MyAccountsDatabase(getContext());
        savetrans.setText("Save");
        IncomeId=new ArrayList<>();
        IncomeId=mSqlHelper.getIncomeIds();
        ExpenseId=new ArrayList<>();
        ExpenseId=mSqlHelper.getExpenseIds();
        setTransactionTypeAdapter();
        personTypeIdlist = new ArrayList<Integer>();
        personTypeIdlist.add(4);
        personTypeIdlist.add(5);
        personTypeIdlist.add(6);
        personTypeIdlist.add(7);
        if(TransactionId==0) {
            personlayout.setVisibility(View.GONE);
            transDate.setText("" + DateUtil.getCurrentDateHuman());
        }else{
            LoadTransactionDetails();
        }
        transDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogTransDate("Transaction Date", 1);
            }
        });

        transType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=-1) {

                    selectedTransType = new TransactionType();
                    selectedTransType = (TransactionType) TransTypeAdapter.getItem(position);
                    if (personTypeIdlist.contains(selectedTransType.getTransactionTypeId())) {
                        setPersonAdapter();
                        personlayout.setVisibility(View.VISIBLE);
                        transName.setVisibility(View.GONE);
                    } else {
                        personlayout.setVisibility(View.GONE);
                        transName.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lnk_back.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                fragment = new Today();
                FloatingActionButton floatingActionButton = ((MainActivity) getActivity())
                        .getFloatingActionButton();
                if(floatingActionButton!=null)
                    floatingActionButton.show();
                FragmentTransaction fragmentTransaction=
                        getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,
                        R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                fragmentTransaction.commit();
            }
        });
        lnk_addperson.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ShowAddPersonDialog();
            }
        });
        savetrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveTransaction();
            }
        });
        return view;
    }

    private void LoadTransactionDetails() {
        TransactionFilter transactionFilter=new TransactionFilter();
        transactionFilter.setTransactionId(TransactionId);
        transaction=mSqlHelper.getTransactions(transactionFilter).get(0);
        transDate.setText(transaction.getTransactionDate());
        selectedTransType=new TransactionType();
        selectedTransType=TransactionType.getItemById(transactionType,transaction.getTransactionTypeID());
        int id=TransTypeAdapter.getPosition(selectedTransType)+1;
        transType.setSelection(id,true);
        if(personTypeIdlist.contains(selectedTransType.getTransactionTypeId())) {
            selectedPerson=new Persons();
            setPersonAdapter();
            selectedPerson=Persons.getItemById(persons,transaction.getTransactionPersonID());
            if(selectedPerson!=null) {
                int pos=PersonAdapter.getPosition(selectedPerson);
                transPerson.setSelection(pos);

            }
        }else{
            transName.setText(transaction.getTransactionName());
        }
        transDesc.setText(transaction.getTransactionDesc());
        transAmt.setText(String.valueOf(transaction.getTransactionAmount()));
    }

    private void SaveTransaction() {
        boolean is_error=false;
        if(transDate.getText().equals(""))
        {
            is_error=true;
            transDate.requestFocus();
            transDate.setError("Transaction date can't be empty!");
        }else if(transType.getSelectedItemPosition()==-1){
            is_error=true;
            transType.requestFocus();
            transType.setError("Transaction type can't be empty!");
        }else if(transAmt.getText().toString().equals("")){
            is_error=true;
            transAmt.requestFocus();
            transAmt.setError("Amount can't be empty");
        }else if(transType.getSelectedItemPosition()>=0){

            if(personTypeIdlist.contains(selectedTransType.getTransactionTypeId())){
                if(transPerson.getSelectedItemPosition()==-1){
                    is_error=true;
                    transPerson.requestFocus();
                }
            }else{
                if(transName.getText().equals("")){
                    is_error=true;
                    transName.requestFocus();
                    transName.setError("Transaction name can't be empty");
                }
            }

        }
        if(!is_error){
            if(transaction==null)
                transaction=new Transaction();
            transaction.setTransactionDate(transDate.getText().toString());

            transaction.setTransactionTypeID(selectedTransType.transactionTypeId);
            if(personTypeIdlist.contains(selectedTransType.getTransactionTypeId())){
                selectedPerson=(Persons) PersonAdapter.getItem(transPerson.getSelectedItemPosition());
                transaction.setTransactionName(selectedPerson.getPersonName());
                transaction.setTransactionPersonID(selectedPerson.getPersonId());
            }else{
                transaction.setTransactionName(transName.getText().toString());
            }
            transaction.setTransactionDesc(transDesc.getText().toString());
            transaction.setTransactionAmount(Integer.parseInt(transAmt.getText().toString()));

            mSqlHelper.InsertTransaction(transaction);
            Toast.makeText(getContext(),"Transaction saved",Toast.LENGTH_SHORT).show();
              fragment = new Today().Initialize(transaction.getTransactionDate());
            FloatingActionButton floatingActionButton = ((MainActivity) getActivity())
                    .getFloatingActionButton();
            if(floatingActionButton!=null)
                floatingActionButton.show();
            FragmentTransaction fragmentTransaction=
                    getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,
                    R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }
    }

    private void ShowAddPersonDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_update_person);

        Button Ok = (Button) dialog.findViewById(R.id.btn_person);
        txtName=(AppCompatEditText) dialog.findViewById(R.id.txt_personName);
        txtMobile=(AppCompatEditText) dialog.findViewById(R.id.txt_personMobile);
        TextView title=(TextView) dialog.findViewById(R.id.txt_manage);
        title.setText("Add Person");
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtName.getText().toString().equals("")){
                    txtName.requestFocus();
                    txtName.setError("Name can't be empty");
                }else if(txtMobile.getText().toString().equals("")){
                    txtMobile.requestFocus();
                    txtMobile.setError("Mobile no can't be empty");
                }else{
                    person=new Persons();
                    person.setPersonName(txtName.getText().toString().trim());
                    person.setMobile(txtMobile.getText().toString().trim());
                    new AddPerson().execute();
                }
                dialog.dismiss();
            }
        });

        dialog.show();

    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



    private void setPersonAdapter() {
        if(PersonAdapter!=null)
            PersonAdapter.clear();
        persons=new ArrayList<Persons>();
        persons=mSqlHelper.getPersons();
        PersonAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, persons);
        transPerson.setAdapter(PersonAdapter);
    }

    private void setTransactionTypeAdapter() {
        transactionType=new ArrayList<TransactionType>();
        transactionType=mSqlHelper.getTransactionTypes();
        TransTypeAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, transactionType);
        transType.setAdapter(TransTypeAdapter);
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
                        transDate.setText("" + strDateTime);
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
    private class AddPerson extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mSqlHelper.SavePersonData(person);
            return "sucess full added";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            setPersonAdapter();
        }
    }
}
