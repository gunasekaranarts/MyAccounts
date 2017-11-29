package TableData;

import android.provider.BaseColumns;

/**
 * Created by USER on 08-11-2017.
 */

public class SecurityTableData implements BaseColumns {
    public static String ID_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT, ";
    public static String TEXT = " TEXT,";
    public static String INTEGER = " INTEGER,";


    public static String SecurityTableName = "Security";
    public static String ProfileId="ProfileId";
    public static String Name="Name";
    public static String Password="Password";
    public static String Email="Email";
    public static String Mobile="Mobile";
}
