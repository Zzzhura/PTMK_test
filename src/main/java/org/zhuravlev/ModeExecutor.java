package org.zhuravlev;

import java.sql.SQLException;

public class ModeExecutor {
    private Integer mode;

    ModeExecutor(Integer mode){
        try{
            this.mode = mode;
            System.out.println("Created ModeExecutor with value " + mode + "\n");
        } catch(RuntimeException e){
            throw new RuntimeException("Error can't init Mode executor with value " + mode + '\n');
        }
    }

    public boolean executeTheMode(DataBase dataBase, String[] args) throws SQLException {
        switch(this.mode){
            case 1:
                dataBase.createTable();
                break;
            case 2:
                Employee employee = ArgsChecker.ArgsCheckerForApp2(args);
                employee.saveToDataBase(dataBase);
                break;
            case 3:
                dataBase.printAllDataToConsole();
                break;
            case 4:
                EmployeePopulator employeePopulator = new EmployeePopulator(dataBase.connection);
                employeePopulator.generateEmployees(1000000);
                break;
            case 5:
                dataBase.queryMaleEmployeesWithSurnameStartingWithF();
                break;
            case 6:
                dataBase.queryMaleEmployeesWithSurnameStartingWithFOptimized();
                break;
            default:
                System.out.println("Invalid mode: " + this.mode);
                return false;
        }
        return true;
    }
}
