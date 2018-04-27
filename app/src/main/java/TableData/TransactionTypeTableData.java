package TableData;

import android.provider.BaseColumns;

/**
 * Created by USER on 08-11-2017.
 */

public class TransactionTypeTableData implements BaseColumns {
    public static String ID_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT, ";
    public static String TEXT = " TEXT,";
    public static String INTEGER = " INTEGER,";


    public static String TransactionTypeTableName = "TransactionType";
    public static String TransactionTypeID="TransactionTypeId";
    public static String TransactionTypeName="TransactionTypeName";
    public static String CashFlow="CashFlow";

}

