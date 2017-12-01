package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import POJO.Persons;
import POJO.SecurityProfile;
import POJO.Transaction;
import POJO.TransactionFilter;
import POJO.TransactionType;
import TableData.PersonsTableData;
import TableData.SecurityTableData;
import TableData.TransactionTypeTableData;
import TableData.TransactionsTableData;

/**
 * Created by USER on 08-11-2017.
 */

public class MyAccountsDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MyAccountsDatabase.db";
    public ArrayList<TransactionType> transactionTypes;
    public ArrayList<Persons> persons;
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
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + SecurityTableData.SecurityTableName + ";");
            db.execSQL("DROP TABLE IF EXISTS " + TransactionTypeTableData.TransactionTypeTableName + ";");
            db.execSQL("DROP TABLE IF EXISTS " + PersonsTableData.PersonTableName + ";");
            db.execSQL("DROP TABLE IF EXISTS " + TransactionsTableData.TransactionTableName + ";");
            onCreate(db);
        }
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
        values.put(TransactionsTableData.TransactionDate, transaction.getTransactionDate());
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
            query+=" and "+TransactionsTableData.TransactionDate+"='"+transactionFilter.getTransactionDate()+"'";
        if(!(transactionFilter.getTransactionId()==0))
            query+=" and "+TransactionsTableData.TransactionID+"='"+transactionFilter.getTransactionId()+"'";
        if(transactionFilter.getFromDate()!=null)
            query+=" and "+TransactionsTableData.TransactionDate+">='"+transactionFilter.getFromDate()+"'";
        if(transactionFilter.getToDate()!=null)
            query+=" and "+TransactionsTableData.TransactionDate+"<='"+transactionFilter.getToDate()+"'";
        if(transactionFilter.getTransactionTypeId()!=0) {
            query += " and " + TransactionsTableData.TransactionTypeID + "=" + transactionFilter.getTransactionTypeId();
        }
        if(transactionFilter.getPersonId()!=0)
            query+=" and "+TransactionsTableData.TransactionPersonID+"="+transactionFilter.getPersonId();
        if((transactionFilter.getKeyword()!=null))
            query+=" and "+TransactionsTableData.TransactionName+" LIKE '%"+transactionFilter.getKeyword()+"%'";


        Cursor mCursor = dataBase.rawQuery(query, null);
        if (mCursor.moveToFirst()){
            do{
                Transaction transaction=new Transaction();
                transaction.setTransactionID(mCursor.getInt(mCursor.getColumnIndex(TransactionsTableData.TransactionID)));
                transaction.setTransactionTypeID(mCursor.getInt(mCursor.getColumnIndex(TransactionsTableData.TransactionTypeID)));
                transaction.setTransactionDate(mCursor.getString(mCursor.getColumnIndex(TransactionsTableData.TransactionDate)));
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
    public ArrayList<Integer> getIncomeIds()
    {
        IncomeId=new ArrayList<Integer>();
        IncomeId.add(1);
        IncomeId.add(5);
        IncomeId.add(6);
        IncomeId.add(8);
        return IncomeId;
    }
    public ArrayList<Integer> getExpenseIds()
    {
        ExpenseId=new ArrayList<>();
        ExpenseId.add(2);
        ExpenseId.add(3);
        ExpenseId.add(4);
        ExpenseId.add(7);
        return ExpenseId;
    }

}
