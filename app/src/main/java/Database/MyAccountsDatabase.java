package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import POJO.AnalysisSummary;
import POJO.CustomerTransaction;
import POJO.CustomerTransactionGroup;
import POJO.Customers;
import POJO.Persons;
import POJO.SecurityProfile;
import POJO.Transaction;
import POJO.TransactionFilter;
import POJO.TransactionType;
import TableData.CustomerTransactionTableData;
import TableData.CustomersTableData;
import TableData.PersonsTableData;
import TableData.SecurityTableData;
import TableData.TransactionTypeTableData;
import TableData.TransactionsTableData;

/**
 * Created by USER on 08-11-2017.
 */

public class MyAccountsDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "MyAccountsDatabase.db";
    public ArrayList<TransactionType> transactionTypes;
    public ArrayList<Persons> persons;
    public ArrayList<Customers> customers;
    public ArrayList<Transaction> transactions;
    public String databasePath = "";
    ArrayList<Integer> IncomeId,ExpenseId;
    Context context;
    String CreateSecurityQurey = "Create Table " + SecurityTableData.SecurityTableName + " ("+ SecurityTableData.ProfileId + SecurityTableData.ID_AUTOINCREMENT + SecurityTableData.Name + SecurityTableData.TEXT+
            SecurityTableData.Password + SecurityTableData.TEXT + SecurityTableData.Email + SecurityTableData.TEXT +
            SecurityTableData.Mobile + " TEXT);";

    String CreateTransactionTypeQurey = "Create Table " + TransactionTypeTableData.TransactionTypeTableName + " (" + TransactionTypeTableData.TransactionTypeID + TransactionTypeTableData.ID_AUTOINCREMENT+
            TransactionTypeTableData.TransactionTypeName +" TEXT);";

    String CreatePersionsQuery="Create Table "+ PersonsTableData.PersonTableName+" ("+ PersonsTableData.PersonID + PersonsTableData.ID_AUTOINCREMENT+
            PersonsTableData.PersonName + PersonsTableData.TEXT+PersonsTableData.PersonMobile + " TEXT);";

    String CreateTransactionsQurey = "Create Table " + TransactionsTableData.TransactionTableName + " (" + TransactionsTableData.TransactionID + TransactionsTableData.ID_AUTOINCREMENT+
            TransactionsTableData.TransactionTypeID + TransactionsTableData.INTEGER + TransactionsTableData.TransactionName + TransactionsTableData.TEXT + TransactionsTableData.TransactionDesc+ TransactionsTableData.TEXT+
            TransactionsTableData.TransactionAmount+TransactionsTableData.INTEGER+
            TransactionsTableData.TransactionDate+ TransactionsTableData.TEXT+ TransactionsTableData.TransactionPersonID +" INTEGER);";
    String CreateCustomerQurey = "Create Table " + CustomersTableData.CustomerTableName + " ("+ CustomersTableData.CustomerID + CustomersTableData.ID_AUTOINCREMENT + CustomersTableData.CustomerName + CustomersTableData.TEXT+
            CustomersTableData.CustomerMobile + CustomersTableData.TEXT + CustomersTableData.CustomerPlace +" TEXT);";
    String CreateCustTransactionsQurey = "Create Table " + CustomerTransactionTableData.CustomerTransactionTableName + " (" + CustomerTransactionTableData.CustomerTransactionId + CustomerTransactionTableData.ID_AUTOINCREMENT+
            CustomerTransactionTableData.TransactionType + CustomerTransactionTableData.INTEGER + CustomerTransactionTableData.TransactionDesc + CustomerTransactionTableData.TEXT + CustomerTransactionTableData.CustomerID+ CustomerTransactionTableData.INTEGER+
            CustomerTransactionTableData.TransactionAmt+TransactionsTableData.INTEGER+
            CustomerTransactionTableData.TransactionDate+" TEXT);";

    public MyAccountsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        databasePath = context.getDatabasePath("MyAccountsDatabase").getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateSecurityQurey);
        db.execSQL(CreateTransactionTypeQurey);
        db.execSQL(CreatePersionsQuery);
        db.execSQL(CreateTransactionsQurey);

        ContentValues contentValues=new ContentValues();
        contentValues.put(TransactionTypeTableData.TransactionTypeName,"Income");
        db.insert(TransactionTypeTableData.TransactionTypeTableName,null,contentValues);
        contentValues.put(TransactionTypeTableData.TransactionTypeName,"Expense");
        db.insert(TransactionTypeTableData.TransactionTypeTableName,null,contentValues);
        contentValues.put(TransactionTypeTableData.TransactionTypeName,"To Savings");
        db.insert(TransactionTypeTableData.TransactionTypeTableName,null,contentValues);
        contentValues.put(TransactionTypeTableData.TransactionTypeName,"Giving Credit");
        db.insert(TransactionTypeTableData.TransactionTypeTableName,null,contentValues);
        contentValues.put(TransactionTypeTableData.TransactionTypeName,"Credit Return");
        db.insert(TransactionTypeTableData.TransactionTypeTableName,null,contentValues);
        contentValues.put(TransactionTypeTableData.TransactionTypeName,"Getting Credit");
        db.insert(TransactionTypeTableData.TransactionTypeTableName,null,contentValues);
        contentValues.put(TransactionTypeTableData.TransactionTypeName,"Paying Credit");
        db.insert(TransactionTypeTableData.TransactionTypeTableName,null,contentValues);
        contentValues.put(TransactionTypeTableData.TransactionTypeName,"From Savings");
        db.insert(TransactionTypeTableData.TransactionTypeTableName,null,contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion!=newVersion) {
            if (oldVersion < 2) {
                db.execSQL(CreateCustomerQurey);
                db.execSQL(CreateCustTransactionsQurey);
            }
            if (oldVersion < 5) {
                db.execSQL("Alter table " + TransactionTypeTableData.TransactionTypeTableName + " ADD COLUMN " + TransactionTypeTableData.CashFlow + " TEXT");
                db.execSQL("Update " + TransactionTypeTableData.TransactionTypeTableName + " set " + TransactionTypeTableData.CashFlow + "='Inward' where " + TransactionTypeTableData.TransactionTypeID + " in (1,5,6,8)");
                db.execSQL("Update " + TransactionTypeTableData.TransactionTypeTableName + " set " + TransactionTypeTableData.CashFlow + "='Outward' where " + TransactionTypeTableData.TransactionTypeID + " in (2,3,4,7)");

//            db.execSQL("DROP TABLE IF EXISTS " + PersonsTableData.PersonTableName + ";");
//            db.execSQL("DROP TABLE IF EXISTS " + TransactionsTableData.TransactionTableName + ";");
//            onCreate(db);
            }
        }

    }
    public void SaveorUpdateTransype(TransactionType transactionType) {
        SQLiteDatabase dataBase = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(TransactionTypeTableData.TransactionTypeName, transactionType.getTransactionType());
        values.put(TransactionTypeTableData.CashFlow, transactionType.getCashFlow());
        if(transactionType.getTransactionTypeId()==0)
        dataBase.insert(TransactionTypeTableData.TransactionTypeTableName, null, values);
        else
            dataBase.update(TransactionTypeTableData.TransactionTypeTableName,values,TransactionTypeTableData.TransactionTypeID+"=?",new String[] {String.valueOf(transactionType.getTransactionTypeId())});
        dataBase.close();
    }
    public ArrayList<TransactionType> getTransactionTypes(){
        transactionTypes=new ArrayList<TransactionType>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TransactionTypeTableData.TransactionTypeTableName, null);
        if (mCursor.moveToFirst()){
            do{
                TransactionType transactionType=new TransactionType();
                transactionType.setTransactionTypeId(mCursor.getInt(mCursor.getColumnIndex(TransactionTypeTableData.TransactionTypeID)));
                transactionType.setTransactionType(mCursor.getString(mCursor.getColumnIndex(TransactionTypeTableData.TransactionTypeName)));
                transactionType.setCashFlow(mCursor.getString(mCursor.getColumnIndex(TransactionTypeTableData.CashFlow)));
                transactionTypes.add(transactionType);

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return transactionTypes;
    }

    public ArrayList<Persons> getPersons(){
        persons=new ArrayList<Persons>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + PersonsTableData.PersonTableName, null);
        if (mCursor.moveToFirst()){
            do{
                Persons person=new Persons();
                person.setPersonId(mCursor.getInt(mCursor.getColumnIndex(PersonsTableData.PersonID)));
                person.setPersonName(mCursor.getString(mCursor.getColumnIndex(PersonsTableData.PersonName)));
                person.setMobile(mCursor.getString(mCursor.getColumnIndex(PersonsTableData.PersonMobile)));
                persons.add(person);

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return persons;
    }
    public ArrayList<Customers> getCustomers(){
        customers=new ArrayList<Customers>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + CustomersTableData.CustomerTableName, null);
        if (mCursor.moveToFirst()){
            do{
                Customers customer=new Customers();
                customer.setCustomerID(mCursor.getInt(mCursor.getColumnIndex(CustomersTableData.CustomerID)));
                customer.setCustomerName(mCursor.getString(mCursor.getColumnIndex(CustomersTableData.CustomerName)));
                customer.setCustomerMobile(mCursor.getString(mCursor.getColumnIndex(CustomersTableData.CustomerMobile)));
                customer.setCustomerPlace(mCursor.getString(mCursor.getColumnIndex(CustomersTableData.CustomerPlace)));
                customers.add(customer);

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return customers;
    }
    public SecurityProfile getProfile(){
        SecurityProfile securityProfile= new SecurityProfile();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SecurityTableData.SecurityTableName, null);
        if (mCursor.moveToFirst()){
            do{
                securityProfile.setProfileId(mCursor.getInt(mCursor.getColumnIndex(SecurityTableData.ProfileId)));
                securityProfile.setName(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.Name)));
                securityProfile.setEmail(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.Email)));
                securityProfile.setMobile(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.Mobile)));
                securityProfile.setPassword(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.Password)));

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return securityProfile;
    }
    public void SavePersonData(Persons person) {
        SQLiteDatabase dataBase = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(PersonsTableData.PersonName, person.getPersonName());
        values.put(PersonsTableData.PersonMobile, person.getMobile());
        dataBase.insert(PersonsTableData.PersonTableName, null, values);
        dataBase.close();
    }
    public void SaveCustomerData(Customers customer) {
        SQLiteDatabase dataBase = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(CustomersTableData.CustomerName, customer.getCustomerName());
        values.put(CustomersTableData.CustomerMobile, customer.getCustomerMobile());
        values.put(CustomersTableData.CustomerPlace, customer.getCustomerPlace());
        dataBase.insert(CustomersTableData.CustomerTableName, null, values);
        dataBase.close();
    }
    public int getAvailableBalance(){
        SQLiteDatabase dataBase = this.getReadableDatabase();
        int availableBalance=0,Income=0,Expense=0;
        Cursor mCursor = dataBase.rawQuery("SELECT SUM("+TransactionsTableData.TransactionAmount+") FROM " + TransactionsTableData.TransactionTableName +" where "+TransactionsTableData.TransactionTypeID +" in (1,5,6,8)", null);
        if (mCursor.moveToFirst()){
            do{
                Income=(mCursor.getInt(0));
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        mCursor = dataBase.rawQuery("SELECT SUM("+TransactionsTableData.TransactionAmount+") FROM " + TransactionsTableData.TransactionTableName +" where "+TransactionsTableData.TransactionTypeID +" in (2,3,4,7)", null);
        if (mCursor.moveToFirst()){
            do{
                Expense=(mCursor.getInt(0));
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        dataBase.close();
        availableBalance=Income-Expense;
        return availableBalance;
    }
    public void InsertTransaction(Transaction transaction){
        SQLiteDatabase dataBase = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(TransactionsTableData.TransactionTypeID, transaction.getTransactionTypeID());
        values.put(TransactionsTableData.TransactionDate, transaction.getTransactionDate().replace("-",""));
        values.put(TransactionsTableData.TransactionName, transaction.getTransactionName());
        values.put(TransactionsTableData.TransactionDesc, transaction.getTransactionDesc());
        values.put(TransactionsTableData.TransactionAmount, transaction.getTransactionAmount());
        values.put(TransactionsTableData.TransactionPersonID, transaction.getTransactionPersonID());
        dataBase.beginTransaction();
        if(transaction.getTransactionID()==0)
            dataBase.insert(TransactionsTableData.TransactionTableName, null, values);
        else{
            dataBase.update(TransactionsTableData.TransactionTableName,values,
                    TransactionsTableData.TransactionID+"=?",new String[] {String.valueOf(transaction.getTransactionID())});
        }
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }
    public ArrayList<Transaction> getTransactions(TransactionFilter transactionFilter)
    {
        SQLiteDatabase dataBase = this.getReadableDatabase();
        transactions=new ArrayList<>();
        String query="select * from "+ TransactionsTableData.TransactionTableName
                +" where "+TransactionsTableData.TransactionID+" is not null ";
        if((transactionFilter.getTransactionDate()!=null))
            query+=" and "+TransactionsTableData.TransactionDate+"='"+transactionFilter.getTransactionDate().replace("-","")+"'";
        if(!(transactionFilter.getTransactionId()==0))
            query+=" and "+TransactionsTableData.TransactionID+"='"+transactionFilter.getTransactionId()+"'";
        if(transactionFilter.getFromDate()!=null)
            query+=" and "+TransactionsTableData.TransactionDate+">='"+transactionFilter.getFromDate().replace("-","")+"'";
        if(transactionFilter.getToDate()!=null)
            query+=" and "+TransactionsTableData.TransactionDate+"<='"+transactionFilter.getToDate().replace("-","")+"'";
        if(transactionFilter.getTransactionTypeId()!=0) {
            query += " and " + TransactionsTableData.TransactionTypeID + "=" + transactionFilter.getTransactionTypeId();
        }
        if(transactionFilter.getPersonId()!=0)
            query+=" and "+TransactionsTableData.TransactionPersonID+"="+transactionFilter.getPersonId();
        if((transactionFilter.getKeyword()!=null))
            query+=" and "+TransactionsTableData.TransactionName+" LIKE '%"+transactionFilter.getKeyword()+"%'";
        query+= " order by "+ TransactionsTableData.TransactionDate+" ";

        Cursor mCursor = dataBase.rawQuery(query, null);
        if (mCursor.moveToFirst()){
            do{
                Transaction transaction=new Transaction();
                transaction.setTransactionID(mCursor.getInt(mCursor.getColumnIndex(TransactionsTableData.TransactionID)));
                transaction.setTransactionTypeID(mCursor.getInt(mCursor.getColumnIndex(TransactionsTableData.TransactionTypeID)));
                String TransDate=mCursor.getString(mCursor.getColumnIndex(TransactionsTableData.TransactionDate));
                StringBuilder transdates = new StringBuilder(TransDate);
                transdates.insert(4,"-");
                transdates.insert(7,"-");
                transaction.setTransactionDate(transdates.toString());
                transaction.setTransactionName(mCursor.getString(mCursor.getColumnIndex(TransactionsTableData.TransactionName)));
                transaction.setTransactionDesc(mCursor.getString(mCursor.getColumnIndex(TransactionsTableData.TransactionDesc)));
                transaction.setTransactionAmount(mCursor.getInt(mCursor.getColumnIndex(TransactionsTableData.TransactionAmount)));
                transaction.setTransactionPersonID(mCursor.getInt(mCursor.getColumnIndex(TransactionsTableData.TransactionPersonID)));
                transactions.add(transaction);

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        dataBase.close();
        return transactions;
    }

    public ArrayList<CustomerTransactionGroup> getCustomersGroupTransaction(){
        ArrayList<CustomerTransactionGroup> customerTransGroup=new ArrayList<CustomerTransactionGroup>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query="select distinct C."+CustomersTableData.CustomerID+","
                +CustomersTableData.CustomerName+"|| '-' || "+CustomersTableData.CustomerPlace+" as CustomerName,"+CustomersTableData.CustomerMobile
                +", ifnull((select SUM("+CustomerTransactionTableData.TransactionAmt+") from "+CustomerTransactionTableData.CustomerTransactionTableName+" where  "+CustomerTransactionTableData.CustomerID+"=T."+CustomersTableData.CustomerID+" and "+CustomerTransactionTableData.TransactionType+"=0),0) as Totalamt,"
                +" ifnull((select SUM("+CustomerTransactionTableData.TransactionAmt+") from "+CustomerTransactionTableData.CustomerTransactionTableName+" where  "+CustomerTransactionTableData.CustomerID+"=T."+CustomersTableData.CustomerID+" and "+CustomerTransactionTableData.TransactionType+"=1),0) as Revceived,"
                +" ifnull((select SUM("+CustomerTransactionTableData.TransactionAmt+") from "+CustomerTransactionTableData.CustomerTransactionTableName+" where  "+CustomerTransactionTableData.CustomerID+"=T."+CustomersTableData.CustomerID+" and "+CustomerTransactionTableData.TransactionType+"=0),0)- ifnull((select SUM("
                +CustomerTransactionTableData.TransactionAmt+") from "+CustomerTransactionTableData.CustomerTransactionTableName+" where  "+CustomerTransactionTableData.CustomerID+"=T."+CustomersTableData.CustomerID+"  and "+CustomerTransactionTableData.TransactionType+"=1),0) as PendingAmt"
                +" FROM " + CustomersTableData.CustomerTableName +" C  left join "+ CustomerTransactionTableData.CustomerTransactionTableName +" T on C."+CustomersTableData.CustomerID+"=T."+CustomerTransactionTableData.CustomerID;
        Cursor mCursor = sqLiteDatabase.rawQuery(query, null);
        if (mCursor.moveToFirst()){
            do{
                CustomerTransactionGroup customertrans=new CustomerTransactionGroup();
                customertrans.setCustomerID(mCursor.getInt(mCursor.getColumnIndex(CustomersTableData.CustomerID)));
                customertrans.setCustomerName(mCursor.getString(mCursor.getColumnIndex(CustomersTableData.CustomerName)));
                customertrans.setCustomerMobile(mCursor.getString(mCursor.getColumnIndex(CustomersTableData.CustomerMobile)));
                customertrans.setTotalAmt(mCursor.getInt(mCursor.getColumnIndex("Totalamt")));
                customertrans.setReceivedAmt(mCursor.getInt(mCursor.getColumnIndex("Revceived")));
                customertrans.setPendingAmt(mCursor.getInt(mCursor.getColumnIndex("PendingAmt")));
                customerTransGroup.add(customertrans);

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return customerTransGroup;
    }
    public void InsertCustomerTransaction(CustomerTransaction transaction){
        SQLiteDatabase dataBase = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(CustomerTransactionTableData.TransactionDate, transaction.getTransactionDate().replace("-",""));
        values.put(CustomerTransactionTableData.TransactionType, transaction.getTransactionType());
        values.put(CustomerTransactionTableData.TransactionDesc, transaction.getTransactionDesc());
        values.put(CustomerTransactionTableData.TransactionAmt, transaction.getTransactionAmt());
        values.put(CustomerTransactionTableData.CustomerID, transaction.getCustomerId());
        dataBase.beginTransaction();
        if(transaction.getCustomerTransactionId()==0)
            dataBase.insert(CustomerTransactionTableData.CustomerTransactionTableName, null, values);
        else{
            dataBase.update(CustomerTransactionTableData.CustomerTransactionTableName,values,
                    CustomerTransactionTableData.CustomerTransactionId+"=?",new String[] {String.valueOf(transaction.getCustomerTransactionId())});
        }
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }
    public ArrayList<CustomerTransaction> getCustomerTransactions(int CustomerId){
        ArrayList<CustomerTransaction> cTransactions=new ArrayList<CustomerTransaction>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + CustomerTransactionTableData.CustomerTransactionTableName+" Where "+CustomerTransactionTableData.CustomerID+"="+CustomerId + " order by "+CustomerTransactionTableData.CustomerTransactionId+" desc", null);
        if (mCursor.moveToFirst()){
            do{
                CustomerTransaction Customertrans=new CustomerTransaction();
                Customertrans.setCustomerTransactionId(mCursor.getInt(mCursor.getColumnIndex(CustomerTransactionTableData.CustomerTransactionId)));
                Customertrans.setCustomerId(mCursor.getInt(mCursor.getColumnIndex(CustomerTransactionTableData.CustomerID)));
                Customertrans.setTransactionType(mCursor.getInt(mCursor.getColumnIndex(CustomerTransactionTableData.TransactionType)));
                Customertrans.setTransactionAmt(mCursor.getInt(mCursor.getColumnIndex(CustomerTransactionTableData.TransactionAmt)));
                String TransDate=mCursor.getString(mCursor.getColumnIndex(CustomerTransactionTableData.TransactionDate));
                StringBuilder transdates = new StringBuilder(TransDate);
                transdates.insert(4,"-");
                transdates.insert(7,"-");
                Customertrans.setTransactionDate(transdates.toString());

                cTransactions.add(Customertrans);

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return cTransactions;
    }
    public AnalysisSummary getGraphData(TransactionFilter filter,ArrayList<String> keywords){
        AnalysisSummary summary=new AnalysisSummary();
        SQLiteDatabase dataBase = this.getReadableDatabase();

        String condition=" ";
        if(filter.getFromDate()!=null)
            condition+=" and "+TransactionsTableData.TransactionDate+">='"+filter.getFromDate().replace("-","")+"'";
        if(filter.getToDate()!=null)
            condition+=" and "+TransactionsTableData.TransactionDate+"<='"+filter.getToDate().replace("-","")+"'";
        int Income=0,Expense=0,tosavings=0,fromsavings=0, creditgiven=0,creditreturn=0;
        ArrayList<Integer> ListValues=new ArrayList<>();
        Cursor mCursor = dataBase.rawQuery("SELECT SUM("+TransactionsTableData.TransactionAmount+") FROM " + TransactionsTableData.TransactionTableName +" where "+TransactionsTableData.TransactionTypeID +" = 1 "+condition, null);
        if (mCursor.moveToFirst()){
            do{
                Income=(mCursor.getInt(0));
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        mCursor = dataBase.rawQuery("SELECT SUM("+TransactionsTableData.TransactionAmount+") FROM " + TransactionsTableData.TransactionTableName +" where "+TransactionsTableData.TransactionTypeID +" = 2 "+condition, null);
        if (mCursor.moveToFirst()){
            do{
                Expense=(mCursor.getInt(0));
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        mCursor = dataBase.rawQuery("SELECT SUM("+TransactionsTableData.TransactionAmount+") FROM " + TransactionsTableData.TransactionTableName +" where "+TransactionsTableData.TransactionTypeID +" = 4 "+condition, null);
        if (mCursor.moveToFirst()){
            do{
                creditgiven=(mCursor.getInt(0));
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        mCursor = dataBase.rawQuery("SELECT SUM("+TransactionsTableData.TransactionAmount+") FROM " + TransactionsTableData.TransactionTableName +" where "+TransactionsTableData.TransactionTypeID +" = 5 "+condition, null);
        if (mCursor.moveToFirst()){
            do{
                creditreturn=(mCursor.getInt(0));
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        mCursor = dataBase.rawQuery("SELECT SUM("+TransactionsTableData.TransactionAmount+") FROM " + TransactionsTableData.TransactionTableName +" where "+TransactionsTableData.TransactionTypeID +" = 3 "+condition, null);
        if (mCursor.moveToFirst()){
            do{
                tosavings=(mCursor.getInt(0));
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        mCursor = dataBase.rawQuery("SELECT SUM("+TransactionsTableData.TransactionAmount+") FROM " + TransactionsTableData.TransactionTableName +" where "+TransactionsTableData.TransactionTypeID +" = 8 "+condition, null);
        if (mCursor.moveToFirst()){
            do{
                fromsavings=(mCursor.getInt(0));
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        for (String keyword:
             keywords) {
            mCursor = dataBase.rawQuery("SELECT SUM("+TransactionsTableData.TransactionAmount+") FROM " + TransactionsTableData.TransactionTableName +" where "+TransactionsTableData.TransactionName +" like '%"+keyword+"%' "+condition, null);
            if (mCursor.moveToFirst()){
                do{
                    ListValues.add(mCursor.getInt(0));
                }while (mCursor.moveToNext());
                mCursor.close();
            }
        }
        summary.setKeywords(ListValues);


        dataBase.close();
        summary.setIncome(Income);
        summary.setExpense(Expense);
        summary.setSaving(tosavings-fromsavings);
        summary.setCredit(creditgiven-creditreturn);
        return summary;
    }
    public ArrayList<Integer> getIncomeIds()
    {
        IncomeId=new ArrayList<Integer>();
        SQLiteDatabase dataBase = this.getReadableDatabase();
        Cursor mCursor = dataBase.rawQuery("SELECT "+TransactionTypeTableData.TransactionTypeID+" FROM " + TransactionTypeTableData.TransactionTypeTableName +" where "+TransactionTypeTableData.CashFlow +" = 'Inward' ", null);
        if (mCursor.moveToFirst()){
            do{
                IncomeId.add(mCursor.getInt(0));
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        return IncomeId;
    }
    public ArrayList<Integer> getExpenseIds()
    {
        ExpenseId=new ArrayList<>();
        SQLiteDatabase dataBase = this.getReadableDatabase();
        Cursor mCursor = dataBase.rawQuery("SELECT "+TransactionTypeTableData.TransactionTypeID+" FROM " + TransactionTypeTableData.TransactionTypeTableName +" where "+TransactionTypeTableData.CashFlow +" = 'Outward' ", null);
        if (mCursor.moveToFirst()){
            do{
                ExpenseId.add(mCursor.getInt(0));
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        return ExpenseId;
    }

}
