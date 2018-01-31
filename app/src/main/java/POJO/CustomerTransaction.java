package POJO;

import java.io.Serializable;

/**
 * Created by USER on 31-01-2018.
 */

public class CustomerTransaction implements Serializable {
    public int CustomerTransactionId;
    public int CustomerId;
    public String TransactionDesc;
    public int TransactionType;
    public int TransactionAmt;
    public String TransactionDate;

    public int getCustomerTransactionId() {
        return CustomerTransactionId;
    }

    public void setCustomerTransactionId(int customerTransactionId) {
        CustomerTransactionId = customerTransactionId;
    }

    public int getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(int customerId) {
        CustomerId = customerId;
    }

    public String getTransactionDesc() {
        return TransactionDesc;
    }

    public void setTransactionDesc(String transactionDesc) {
        TransactionDesc = transactionDesc;
    }

    public int getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(int transactionType) {
        TransactionType = transactionType;
    }

    public int getTransactionAmt() {
        return TransactionAmt;
    }

    public void setTransactionAmt(int transactionAmt) {
        TransactionAmt = transactionAmt;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }
}
