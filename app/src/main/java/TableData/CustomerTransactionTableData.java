package TableData;

import android.provider.BaseColumns;

/**
 * Created by USER on 31-01-2018.
 */

public class CustomerTransactionTableData implements BaseColumns {
    public static String ID_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT, ";
    public static String TEXT = " TEXT,";
    public static String INTEGER = " INTEGER,";


    public static String CustomerTransactionTableName = "CustomerTransactionTableData";
    public static String CustomerTransactionId="CustomerTransactionId";
    public static String CustomerID="CustomerId";
    public static String TransactionDesc="TransactionDesc";
    public static String TransactionType="TransactionType";
    public static String TransactionAmt="TransactionAmt";
    public static String TransactionDate="TransactionDate";
}
