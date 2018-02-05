package TableData;

import android.provider.BaseColumns;

/**
 * Created by USER on 31-01-2018.
 */

public class CustomersTableData implements BaseColumns {
    public static String ID_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT, ";
    public static String TEXT = " TEXT,";
    public static String INTEGER = " INTEGER,";


    public static String CustomerTableName = "Customers";
    public static String CustomerID="CustomerId";
    public static String CustomerName="CustomerName";
    public static String CustomerMobile="CustomerMobile";
    public static String CustomerPlace="CustomerPlace";
}
