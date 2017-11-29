package TableData;

import android.provider.BaseColumns;

/**
 * Created by USER on 08-11-2017.
 */

public class PersonsTableData implements BaseColumns {
    public static String ID_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT, ";
    public static String TEXT = " TEXT,";
    public static String INTEGER = " INTEGER,";


    public static String PersonTableName = "Persons";
    public static String PersonID="PersonId";
    public static String PersonName="PersonName";
    public static String PersonMobile="PresonMobile";

}
