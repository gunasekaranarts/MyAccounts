package POJO;

import java.io.Serializable;

/**
 * Created by USER on 31-01-2018.
 */

public class CustomerTransactionGroup implements Serializable {

    public int CustomerID;
    public String CustomerName;
    public String CustomerMobile;
    public int TotalAmt;
    public int ReceivedAmt;
    public int PendingAmt;
    public String Status;

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCustomerMobile() {
        return CustomerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        CustomerMobile = customerMobile;
    }

    public int getTotalAmt() {
        return TotalAmt;
    }

    public void setTotalAmt(int totalAmt) {
        TotalAmt = totalAmt;
    }

    public int getReceivedAmt() {
        return ReceivedAmt;
    }

    public void setReceivedAmt(int receivedAmt) {
        ReceivedAmt = receivedAmt;
    }

    public int getPendingAmt() {
        return PendingAmt;
    }

    public void setPendingAmt(int pendingAmt) {
        PendingAmt = pendingAmt;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
