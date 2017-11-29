package TableData;

import android.provider.BaseColumns;

/**
 * Created by USER on 08-11-2017.
 */

public class TransactionsTableData implements BaseColumns {
    public static String ID_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT, ";
    public static String TEXT = " TEXT,";
    public static String INTEGER = " INTEGER,";


    public static String TransactionTableName = "Transactions";
    public static String TransactionID="TransactionId";
    public static String TransactionTypeID="TransactionTypeId";
    public static String TransactionName="TransactionName";
    public static String TransactionDesc="TransactionDesc";
    public static String TransactionAmount="TransactionAmount";
    public static String TransactionDate="TransactionDate";
    public static String TransactionPersonID="TransactionPersonId";

}
