package com.spicasoft.myaccounts;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import CustomAdapters.StatementAdapter;
import CustomWidget.TextAwesome;
import Database.MyAccountsDatabase;
import POJO.Persons;
import POJO.SecurityProfile;
import POJO.Transaction;
import POJO.TransactionFilter;
import POJO.TransactionType;
import fr.ganfra.materialspinner.MaterialSpinner;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


/**
 * Created by USER on 24-11-2017.
 */

public class Statement extends Fragment {
    TextAwesome lnk_save,lnk_share;
    AppCompatEditText fromDate,toDate,transKey;
    MaterialSpinner type;
    MaterialSpinner person;
    ArrayList<Transaction> transactions;
    ArrayList<TransactionType> transactionType;
    ArrayList<Persons> persons;
    MyAccountsDatabase mSqlHelper;
    ArrayAdapter TransTypeAdapter;
    TransactionType selectedTransType;
    Persons selectedPerson;
    ArrayAdapter PersonAdapter;
    ArrayList<Integer> personTypeIdlist,IncomeId,ExpenseId;
    AppCompatImageButton btn_search,btn_clear;
    int tot_income=0,tot_debit=0,tot_credit=0,tot_savings=0;
    StatementAdapter statementAdapter;
    RecyclerView statementList;
    RecyclerView.LayoutManager mLayoutManager;
    TransactionFilter filter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_statement, container, false);
        lnk_save=(TextAwesome)view.findViewById(R.id.lnk_save);
        lnk_share=(TextAwesome)view.findViewById(R.id.lnk_share);
        fromDate=(AppCompatEditText)view.findViewById(R.id.from_date);
        toDate=(AppCompatEditText)view.findViewById(R.id.to_date);
        transKey=(AppCompatEditText)view.findViewById(R.id.trans_key);
        type=(MaterialSpinner)view.findViewById(R.id.spr_Type);
        person=(MaterialSpinner)view.findViewById(R.id.spr_persons);
        btn_search=(AppCompatImageButton)view.findViewById(R.id.btn_search);
        btn_clear=(AppCompatImageButton)view.findViewById(R.id.btn_clear);
        mSqlHelper=new MyAccountsDatabase(getActivity());
        statementList=(RecyclerView)view.findViewById(R.id.statements);
        statementList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        statementList.setLayoutManager(mLayoutManager);
        setAdapter();
        IncomeId=new ArrayList<>();
        IncomeId=mSqlHelper.getIncomeIds();
        ExpenseId=new ArrayList<>();
        ExpenseId=mSqlHelper.getExpenseIds();
        personTypeIdlist = new ArrayList<Integer>();
        personTypeIdlist.add(4);
        personTypeIdlist.add(5);
        personTypeIdlist.add(6);
        personTypeIdlist.add(7);
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogTransDate("From Date", 1);
            }
        });
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogTransDate("To Date", 2);
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                Calendar cal1 = null, cal2 = null;
                if(!fromDate.getText().toString().equals("")) {
                    try {
                        Date dates = format.parse(fromDate.getText().toString());
                        cal1 = Calendar.getInstance();
                        cal1.setTime(dates);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(!toDate.getText().toString().equals("")) {
                    try {
                        Date datess = format.parse(toDate.getText().toString());
                        cal2 = Calendar.getInstance();
                        cal2.setTime(datess);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if((!fromDate.getText().toString().equals(""))&&(!toDate.getText().toString().equals(""))) {
                    if (cal1.after(cal2)) {
                        showAlertWithCancel("To date should be greater than the From date");
                    }
                }
                SearchTransactions();
            }
        });
        btn_clear.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                fromDate.setText("");
                toDate.setText("");
                transKey.setText("");;
                type.setSelection(0);
                person.setSelection(0);
                if(transactions!=null)
                transactions.clear();
                if(statementAdapter!=null)
                    statementAdapter.notifyDataSetChanged();

            }
        });
        lnk_save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showAlertFileType("Do you want to save this report as ?" ,0);
            }
        });
        lnk_share.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showAlertFileType("Do you want to share this report as ?" ,1);
            }
        });
        return view;
    }
    public void showAlertWithCancel(String BuilderText) {
        android.support.v7.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("My Accounts");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }



    public void showAlertFileType(String BuilderText,final int i) {
        android.support.v7.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("My Accounts");
        builder.setMessage(BuilderText);
        builder.setPositiveButton("PDF", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(i==0)
                    SavePDF(false);
                else if (i==1)
                    SavePDF(true);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Excel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(i==0)
                    SaveExcel(false);
                else if (i==1)
                    SaveExcel(true);

            }
        });
        builder.show();
    }

    private void SearchTransactions() {
        filter=new TransactionFilter();
        if(!fromDate.getText().toString().equals(""))
            filter.setFromDate(fromDate.getText().toString());
        if(!toDate.getText().toString().equals(""))
            filter.setToDate(toDate.getText().toString());
        if(!transKey.getText().toString().equals(""))
            filter.setKeyword(transKey.getText().toString());
        if(type.getSelectedItemPosition()!=0) {
            int s=type.getSelectedItemPosition();
            selectedTransType = new TransactionType();
            selectedTransType = (TransactionType) TransTypeAdapter.getItem(type.getSelectedItemPosition()-1);
            filter.setTransactionTypeId(selectedTransType.getTransactionTypeId());
        }
        if(person.getSelectedItemPosition()!=0){
            selectedPerson=new Persons();
            selectedPerson=(Persons) PersonAdapter.getItem(person.getSelectedItemPosition()-1);
            filter.setPersonId(selectedPerson.getPersonId());
        }
        transactions=new ArrayList<>();
        transactions=mSqlHelper.getTransactions(filter);
        if(transactions.size()<=0) {
            Toast.makeText(getActivity(), "No transaction found. Please change filters.",
                    Toast.LENGTH_LONG).show();
        }else {
            setTransactionAdapter();
        }
    }

    private void setTransactionAdapter() {
        statementAdapter = new StatementAdapter(transactions,(AppCompatActivity) getActivity(), IncomeId,ExpenseId);
        statementList.setAdapter(statementAdapter);
        statementAdapter.notifyDataSetChanged();
        ShowSummaryDialog();
    }

    public void ShowSummaryDialog() {
        DecimalFormat format = new DecimalFormat("0.00");
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_statement_summary);
        TextView txt_tot_credit=(TextView) dialog.findViewById(R.id.txt_tot_credit);
        TextView txt_tot_debit=(TextView) dialog.findViewById(R.id.txt_tot_debit);
        TextView txt_col_bal=(TextView) dialog.findViewById(R.id.txt_col_bal);
        int TotIncome=0,TotExpense=0;
        for (Transaction transaction:
        transactions) {
            if(IncomeId.contains(transaction.getTransactionTypeID()))
                TotIncome+=transaction.getTransactionAmount();
            else
                TotExpense+=transaction.getTransactionAmount();
        }
        txt_tot_credit.setText("₹"+format.format(TotIncome));
        txt_tot_debit.setText("₹"+format.format(TotExpense));
        txt_col_bal.setText("₹"+format.format(TotIncome-TotExpense));
        dialog.show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=-1) {

                    selectedTransType = new TransactionType();
                    selectedTransType = (TransactionType) TransTypeAdapter.getItem(position);
                    if (personTypeIdlist.contains(selectedTransType.getTransactionTypeId())) {
                        person.setVisibility(View.VISIBLE);

                    } else {
                        person.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                String strDateTime = ( dp.getYear()+ "-" + (((month.length() == 1 ? "0" + month : month))) + "-" + (day.length() == 1 ? "0" + day : day));

                switch (i) {
                    case 1:
                        fromDate.setText("" + strDateTime);
                        break;
                    case 2:
                        toDate.setText("" + strDateTime);
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

    private void setAdapter() {

        transactionType=new ArrayList<TransactionType>();
        transactionType=mSqlHelper.getTransactionTypes();
        TransTypeAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, transactionType);
        type.setAdapter(TransTypeAdapter);

        if(PersonAdapter!=null)
            PersonAdapter.clear();
        persons=new ArrayList<Persons>();
        persons=mSqlHelper.getPersons();
        PersonAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, persons);
        person.setAdapter(PersonAdapter);
    }
    private void SavePDF(boolean is_Share) {
        SearchTransactions();
        tot_debit=tot_credit=0;
        if(transactions.size()>0){
            File myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My Accounts/");
            if (!myDir.exists()) {
                myDir.mkdir();
            }
            myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My Accounts/PDF");
            if (!myDir.exists()) {
                myDir.mkdir();
            }
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmm");
            String formattedDate = df.format(c.getTime());
            String pdfFile = "My Accounts "+formattedDate+".pdf";
            File file = new File(myDir, pdfFile);
             Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
                    Font.BOLD);
            Font greenFont = new Font(Font.FontFamily.TIMES_ROMAN, 22,
                    Font.BOLDITALIC, BaseColor.GREEN);
            Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.NORMAL, BaseColor.RED);
            Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.NORMAL, BaseColor.BLUE);
             Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
                    Font.BOLD);
             Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.BOLD);

            try{
                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                addMetaData(document);
                document.newPage();
                Paragraph preface = new Paragraph();
                addEmptyLine(preface, 1);
                Paragraph heading=new Paragraph("My Accounts App - Statement", greenFont);
                heading.setAlignment(Element.ALIGN_CENTER);
                document.add(heading);

                addEmptyLine(preface, 1);
                SecurityProfile securityProfile = mSqlHelper.getProfile();
                preface.add(new Paragraph(
                        "Profile",
                        subFont));
                addEmptyLine(preface, 1);
                preface.add(new Paragraph(
                        "Name\t:\t "+securityProfile.getName(),
                        subFont));
                preface.add(new Paragraph(
                        "Email\t:\t "+securityProfile.getEmail(),
                        subFont));
                preface.add(new Paragraph(
                        "Mobile\t:\t "+securityProfile.getMobile(),
                        subFont));

                SimpleDateFormat dfs = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
                String Date = dfs.format(c.getTime());
                preface.add(new Paragraph(
                        "Statement generated on\t:\t "+ Date,
                        subFont));
                addEmptyLine(preface,1);
                setupFilter(preface,subFont);
                preface.setAlignment(Element.ALIGN_LEFT);
                document.add(preface);
                document.newPage();
                Paragraph preface1=new Paragraph();
                addEmptyLine(preface1,2);
                document.add(preface1);
                addTransactionTable(document,smallBold,blueFont,redFont);
                Paragraph thks=new Paragraph("* * * Thanks * * *");
                thks.setAlignment(Element.ALIGN_CENTER);
                thks.setFont(greenFont);
                document.add(thks);
                document.close();
                Toast.makeText(getActivity(), "Data Exported as a PDF file at - "+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                if(is_Share)
                {
                    if(file.exists())
                    Share(file.getAbsolutePath());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }else{
            showAlertWithCancel("No transaction found to save report");
        }
    }

    private void addTransactionTable(Document document,Font normal,Font blue,Font red ) {
        PdfPTable table = new PdfPTable(5);
        try {
            table.setWidths(new int[]{1,2, 1, 1,1});

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        PdfPCell c1 = new PdfPCell(new Phrase("Date"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Narration"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Type"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Debit"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Credit"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        table.setHeaderRows(1);
        DecimalFormat format = new DecimalFormat("0.00");
        tot_debit=tot_credit=tot_income=tot_savings=0;
        for (Transaction t:
             transactions) {
            c1 = new PdfPCell(new Phrase(t.getTransactionDate()));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
            table.addCell(t.getTransactionName()+" \n"+t.getTransactionDesc());
            table.addCell(TransactionType.getItemById(transactionType
                    ,t.getTransactionTypeID()).getTransactionType());
            if(ExpenseId.contains(t.getTransactionTypeID())) {
                c1=new PdfPCell(new Phrase("₹" + format.format(t.getTransactionAmount()),red));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);
                table.addCell("");
                tot_debit+=t.getTransactionAmount();
            }else{
                table.addCell("");
                c1=new PdfPCell(new Phrase("₹" + format.format(t.getTransactionAmount()),blue));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);
                tot_credit+=t.getTransactionAmount();
            }
            if(t.getTransactionTypeID()==1)
                tot_income+=t.getTransactionAmount();
            if(t.getTransactionTypeID()==3)
                tot_savings+=t.getTransactionAmount();
        }
        try {
            document.add(table);
            Paragraph preface = new Paragraph();
            addEmptyLine(preface,1);
            preface.add(new Paragraph("Total Debits : "
                    +new Phrase("₹" + format.format(tot_debit),red)));
            preface.add(new Paragraph("Total Credits : "
                    +new Phrase("₹" + format.format(tot_credit),blue)));
            preface.add(new Paragraph("Total Income : "
                    +new Phrase("₹" + format.format(tot_income),normal)));
            preface.add(new Paragraph("Total Savings : "
                    +new Phrase("₹" + format.format(tot_savings),blue)));
            document.add(preface);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
    public void setupFilter(Paragraph preface,Font subFont){
        preface.add(new Paragraph(
                "Statement filter",
                subFont));
        if(filter.getFromDate()!=null)
            preface.add(new Paragraph(
                    "From Date : "+filter.getFromDate().toString(),
                    subFont));
        if(filter.getToDate()!=null)
            preface.add(new Paragraph(
                    "To Date : "+filter.getToDate().toString(),
                    subFont));
        if(filter.getTransactionDate()!=null)
            preface.add(new Paragraph(
                    "Date : "+filter.getTransactionDate().toString(),
                    subFont));
        if(filter.getKeyword()!=null)
            preface.add(new Paragraph(
                    "Keyword : "+filter.getKeyword().toString(),
                    subFont));
        if(filter.getTransactionTypeId()!=0)
            preface.add(new Paragraph(
                    "Transaction Type : "+TransactionType.getItemById(transactionType
                            ,filter.getTransactionTypeId()).getTransactionType(),
                    subFont));
        if(filter.getPersonId()!=0)
            preface.add(new Paragraph(
                    "Beneficiary Name : "+Persons.getItemById(persons,
                            filter.getPersonId()).getPersonName(),
                    subFont));
        if(filter.getPersonId()!=0)
            preface.add(new Paragraph(
                    "Beneficiary Mobile : "+Persons.getItemById(persons,
                            filter.getPersonId()).getMobile(),
                    subFont));
    }
    private static void addMetaData(Document document) {
        document.addTitle("My Accounts Statement");
        document.addSubject("Created by My Account app");
        document.addKeywords("Java, PDF, iText, My Accounts, Guna,");
        document.addAuthor("Guna");
        document.addCreator("Guna");
    }
    private void SaveExcel(Boolean is_Share) {
        {
            SearchTransactions();
            tot_debit=tot_credit=tot_income=tot_savings=0;
            if(transactions.size()>0){
                File myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My Accounts/");
                if (!myDir.exists()) {
                    myDir.mkdir();
                }
                myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My Accounts/Excel");
                if (!myDir.exists()) {
                    myDir.mkdir();
                }
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmm");
                String formattedDate = df.format(c.getTime());
                String csvFile = "My Accounts "+formattedDate+".xls";
                try {
                    File file = new File(myDir, csvFile);
                    WorkbookSettings wbSettings = new WorkbookSettings();
                    wbSettings.setLocale(new Locale("en", "EN"));
                    wbSettings.setTemplate(true);
                    WritableWorkbook workbook;
                    workbook = Workbook.createWorkbook(file, wbSettings);
                    WritableSheet sheet = workbook.createSheet("Statement", 0);
                    sheet.setColumnView(0,20);
                    sheet.setColumnView(1,30);
                    sheet.setColumnView(2,30);
                    sheet.setColumnView(3,20);
                    sheet.setColumnView(4,20);
                    sheet.setColumnView(5,20);
                    sheet.addCell(new Label(0, 0, "Date"));
                    sheet.addCell(new Label(1, 0, "Narration"));
                    sheet.addCell(new Label(2, 0, "Description"));
                    sheet.addCell(new Label(3, 0, "Type"));
                    sheet.addCell(new Label(4, 0, "Debit"));
                    sheet.addCell(new Label(5, 0, "Credit"));
                    DecimalFormat format = new DecimalFormat("0.00");
                    for(int i=0;i<transactions.size();i++) {
                        sheet.addCell(new Label(0, i+1, transactions.get(i).getTransactionDate()));
                        sheet.addCell(new Label(1, i+1, transactions.get(i).getTransactionName()));
                        sheet.addCell(new Label(2, i+1, transactions.get(i).getTransactionDesc()));
                        selectedTransType=new TransactionType();
                        selectedTransType=TransactionType.getItemById(transactionType,transactions.get(i).getTransactionTypeID());
                        if(selectedTransType!=null)
                            sheet.addCell(new Label(3, i+1, selectedTransType.getTransactionType()));


                        if(ExpenseId.contains(selectedTransType.getTransactionTypeId())) {
                            sheet.addCell(new Label(4,
                                    i + 1, ("₹" + format.format(transactions.get(i).getTransactionAmount()))));
                            tot_debit+=transactions.get(i).getTransactionAmount();
                        }
                        else {
                            sheet.addCell(new Label(5,
                                    i + 1, ("₹" + format.format(transactions.get(i).getTransactionAmount()))));
                            tot_credit+=transactions.get(i).getTransactionAmount();
                        }
                        if(selectedTransType.getTransactionTypeId()==1)
                            tot_income+=transactions.get(i).getTransactionAmount();
                        if(selectedTransType.getTransactionTypeId()==3)
                            tot_savings+=transactions.get(i).getTransactionAmount();
                    }

                    sheet.addCell(new Label(3,
                            transactions.size()+2, ("Total Debits")));
                    sheet.addCell(new Label(4,
                            transactions.size()+2, ("₹" + format.format(tot_debit))));
                    sheet.addCell(new Label(3,
                            transactions.size()+3, ("Total Credits")));
                    sheet.addCell(new Label(4,
                            transactions.size()+3, ("₹" + format.format(tot_credit))));
                    sheet.addCell(new Label(3,
                            transactions.size()+4, ("Total Earnings")));
                    sheet.addCell(new Label(4,
                            transactions.size()+4, ("₹" + format.format(tot_income))));
                    sheet.addCell(new Label(3,
                            transactions.size()+5, ("Total Savings")));
                    sheet.addCell(new Label(4,
                            transactions.size()+5, ("₹" + format.format(tot_savings))));
                    sheet.addCell(new Label(3,
                            transactions.size()+6, ("Balance")));
                    sheet.addCell(new Label(4,
                            transactions.size()+6, ("₹" + format.format(tot_credit-tot_debit))));
                    workbook.write();
                    workbook.close();
                    Toast.makeText(getActivity(), "Data Exported as a Excel Sheet at - "+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    if(is_Share)
                    {
                        if(file.exists())
                            Share(file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                showAlertWithCancel("No transaction found to save report");
            }

        }
    }

    public void Share(String path){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        String aEmailList[] = { "" };
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Accounts Statement");
        emailIntent.setType("plain/text");
        File fileIn = new File(path);
        String message = "My Accounts app statement \n\n Attachment Name: "+fileIn.getName()+"\n PFA";
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,message);
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileIn));
        startActivity(Intent.createChooser(emailIntent, "Send via..."));
    }
}
