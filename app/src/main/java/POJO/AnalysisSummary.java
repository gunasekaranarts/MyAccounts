package POJO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 26-04-2018.
 */

public class AnalysisSummary implements Serializable {
    public int Income;
    public int Expense;
    public int Saving;
    public int Credit;
    public int keyword1;
    public int keyword2;
    public int keyword3;
    public ArrayList<Integer> keywords;

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

    public int getKeyword1() {
        return keyword1;
    }

    public void setKeyword1(int keyword1) {
        this.keyword1 = keyword1;
    }

    public int getKeyword2() {
        return keyword2;
    }

    public void setKeyword2(int keyword2) {
        this.keyword2 = keyword2;
    }

    public int getKeyword3() {
        return keyword3;
    }

    public void setKeyword3(int keyword3) {
        this.keyword3 = keyword3;
    }

    public ArrayList<Integer> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<Integer> keywords) {
        this.keywords = keywords;
    }
}
