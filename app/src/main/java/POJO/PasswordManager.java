package POJO;

import java.io.Serializable;



/**
 * Created by USER on 10-10-2018.
 */

public class PasswordManager implements Serializable {
    public int AccountId;
    public String AccountName;
    public String UserName;
    public String Password;

    public int getAccountId() {
        return AccountId;
    }

    public void setAccountId(int accountId) {
        AccountId = accountId;
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
