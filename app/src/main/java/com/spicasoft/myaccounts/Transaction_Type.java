package com.spicasoft.myaccounts;

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
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;

import CustomAdapters.TransactionTypeAdapter;
import Database.MyAccountsDatabase;
import POJO.TransactionType;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by USER on 27-04-2018.
 */

public class Transaction_Type extends Fragment {
    TransactionTypeAdapter transactionTypeAdapter;
    RecyclerView rcvtranstypelst;
    RecyclerView.LayoutManager mLayoutManager;
    MyAccountsDatabase mSqlHelper;
    TransactionType transactionType;
    ArrayList<TransactionType> transactionTypes;
    AppCompatEditText txt_tranactiontype;
    MaterialSpinner spr_Type;
    Button btn_trans_type;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_transactiontype, container, false);
        mSqlHelper=new MyAccountsDatabase(getActivity());
        rcvtranstypelst=(RecyclerView) view.findViewById(R.id.rcvtranstypelst);
        txt_tranactiontype=(AppCompatEditText)  view.findViewById(R.id.txt_tranactiontype);
        spr_Type=(MaterialSpinner) view.findViewById(R.id.spr_Type);
        btn_trans_type=(Button) view.findViewById(R.id.btn_trans_type);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rcvtranstypelst.setLayoutManager(mLayoutManager);
        LoadTransactionTypes();
        transactionType=new TransactionType();
        btn_trans_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_tranactiontype.getText().toString().equals(""))
                {
                    txt_tranactiontype.setError("Transaction Type Name should not be null!");
                    txt_tranactiontype.requestFocus();
                }
                else if(spr_Type.getSelectedItemPosition()==0){
                    spr_Type.requestFocus();
                    spr_Type.setError("Cash flow can't be empty!");
                }else{
                    transactionType.setTransactionType(txt_tranactiontype.getText().toString());
                    transactionType.setCashFlow(spr_Type.getSelectedItem().toString());
                    mSqlHelper.SaveorUpdateTransype(transactionType);
                    LoadTransactionTypes();
                    ClearFields();
                }
            }
        });
        return view;
    }

    private void ClearFields() {
        txt_tranactiontype.setText("");
        spr_Type.setSelection(0);
        transactionType=new TransactionType();
    }

    private void LoadTransactionTypes() {

        transactionTypes=new ArrayList<TransactionType>();
        transactionTypes=mSqlHelper.getTransactionTypes();
        transactionTypeAdapter = new TransactionTypeAdapter(transactionTypes,(AppCompatActivity) getActivity(),Transaction_Type.this);
        rcvtranstypelst.setAdapter(transactionTypeAdapter);
    }


    public void EditTransactionType(TransactionType transaction){
        txt_tranactiontype.setText(transaction.getTransactionType());
        if(transaction.getCashFlow().equals("Inward"))
            spr_Type.setSelection(1);
        else
            spr_Type.setSelection(2);
        transactionType.setTransactionTypeId(transaction.getTransactionTypeId());
    }
}
