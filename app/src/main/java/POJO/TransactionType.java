package POJO;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by USER on 08-11-2017.
 */

public class TransactionType implements Serializable {
    public int transactionTypeId;
    public String transactionType;
    public String CashFlow;
    public int getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(int transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getCashFlow() {
        return CashFlow;
    }

    public void setCashFlow(String cashFlow) {
        CashFlow = cashFlow;
    }

    @Override
    public String toString() {
        return transactionType;
    }

    public static TransactionType getItemById(ArrayList<TransactionType> transactionTypeArrayList,int TransactionTypeId)
    {
        for (TransactionType item:
        transactionTypeArrayList) {
            if(item.getTransactionTypeId()==TransactionTypeId)
            return item;
        }
        return null;
    }
}
