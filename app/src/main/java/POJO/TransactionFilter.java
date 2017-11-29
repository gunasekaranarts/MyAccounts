package POJO;

import java.io.Serializable;

/**
 * Created by USER on 19-11-2017.
 */

public class TransactionFilter implements Serializable {
    public int TransactionId;
    public String TransactionDate;
    public String FromDate;
    public String ToDate;
    public int PersonId;
    public String Month;
    public String Year;
    public int TransactionTypeId;

    public int getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(int transactionId) {
        TransactionId = transactionId;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }

    public String getFromDate() {
        return FromDate;
    }

    public void setFromDate(String fromDate) {
        FromDate = fromDate;
    }

    public String getToDate() {
        return ToDate;
    }

    public void setToDate(String toDate) {
        ToDate = toDate;
    }

    public int getPersonId() {
        return PersonId;
    }

    public void setPersonId(int personId) {
        PersonId = personId;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public int getTransactionTypeId() {
        return TransactionTypeId;
    }

    public void setTransactionTypeId(int transactionTypeId) {
        TransactionTypeId = transactionTypeId;
    }
}
