package POJO;

import java.io.Serializable;

/**
 * Created by USER on 08-11-2017.
 */

public class Transaction implements Serializable {
    public int TransactionID;
    public int TransactionTypeID;
    public String TransactionName;
    public String TransactionDesc;
    public int TransactionAmount;
    public String TransactionDate;
    public int TransactionPersonID;

    public int getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(int transactionID) {
        TransactionID = transactionID;
    }

    public int getTransactionTypeID() {
        return TransactionTypeID;
    }

    public void setTransactionTypeID(int transactionTypeID) {
        TransactionTypeID = transactionTypeID;
    }

    public String getTransactionName() {
        return TransactionName;
    }

    public void setTransactionName(String transactionName) {
        TransactionName = transactionName;
    }

    public String getTransactionDesc() {
        return TransactionDesc;
    }

    public void setTransactionDesc(String transactionDesc) {
        TransactionDesc = transactionDesc;
    }

    public int getTransactionAmount() {
        return TransactionAmount;
    }

    public void setTransactionAmount(int transactionAmount) {
        TransactionAmount = transactionAmount;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }

    public int getTransactionPersonID() {
        return TransactionPersonID;
    }

    public void setTransactionPersonID(int transactionPersonID) {
        TransactionPersonID = transactionPersonID;
    }

}
