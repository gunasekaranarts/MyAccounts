package POJO;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by USER on 08-11-2017.
 */

public class Persons implements Serializable {
    public int PersonId;
    public String PersonName;
    public String Mobile;

    public int getPersonId() {
        return PersonId;
    }

    public void setPersonId(int personId) {
        PersonId = personId;
    }

    public String getPersonName() {
        return PersonName;
    }

    public void setPersonName(String personName) {
        PersonName = personName;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    @Override
    public String toString() {
        return PersonName;
    }

    public static Persons getItemById(ArrayList<Persons> personsArrayList, int personId)
    {
        for (Persons item:
                personsArrayList) {
            if(item.getPersonId()==personId)
                return item;
        }
        return null;
    }
}
