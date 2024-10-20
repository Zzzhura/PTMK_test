package org.zhuravlev;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

public class Employee {
    private String personInfo;
    private LocalDate birthDate;
    private String gender;

    Employee(String personInfo, LocalDate birthDate, String gender){
        this.personInfo = personInfo;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public void setPersonInfo(String personInfo) {
        this.personInfo = personInfo;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPersonInfo() {
        return personInfo;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public static int calculateAge(LocalDate birthDate){
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public void saveToDataBase(DataBase dataBase) throws SQLException {
        dataBase.insertInTable(this);
    }

}
