package POJO;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by USER on 31-01-2018.
 */

public class Customers implements Serializable {

    public int CustomerID;
    public String CustomerName;
    public String CustomerMobile;
    public String CustomerPlace;

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

    public String getCustomerPlace() {
        return CustomerPlace;
    }

    public void setCustomerPlace(String customerPlace) {
        CustomerPlace = customerPlace;
    }

    @Override
    public String toString() {
        return CustomerName+" - "+CustomerPlace;
    }

    public static Customers getItemById(ArrayList<Customers> customersArrayList, int customerId)
    {
        for (Customers item:
                customersArrayList) {
            if(item.getCustomerID()==customerId)
                return item;
        }
        return null;
    }
}
