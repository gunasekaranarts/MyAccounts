package POJO;

import java.io.Serializable;

/**
 * Created by USER on 26-04-2018.
 */

public class AnalysisSummary implements Serializable {
    public int Income;
    public int Expense;
    public int Saving;
    public int Credit;
    public int Home;
    public int RoomRent;
    public int Food;

    public int getIncome() {
        return Income;
    }

    public void setIncome(int income) {
        Income = income;
    }

    public int getExpense() {
        return Expense;
    }

    public void setExpense(int expense) {
        Expense = expense;
    }

    public int getSaving() {
        return Saving;
    }

    public void setSaving(int saving) {
        Saving = saving;
    }

    public int getCredit() {
        return Credit;
    }

    public void setCredit(int credit) {
        Credit = credit;
    }

    public int getHome() {
        return Home;
    }

    public void setHome(int home) {
        Home = home;
    }

    public int getRoomRent() {
        return RoomRent;
    }

    public void setRoomRent(int roomRent) {
        RoomRent = roomRent;
    }

    public int getFood() {
        return Food;
    }

    public void setFood(int food) {
        Food = food;
    }
}
