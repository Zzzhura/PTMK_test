package org.zhuravlev;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataBase implements AutoCloseable{
    private static final String url = "jdbc:postgresql://localhost:5432/PTMK";
    private static final String dataBaseUser = "postgres";
    private static final String dataBaseUserPassword = "zhura323";
    public Connection connection;

    public DataBase() throws SQLException {
        this.connection =  DriverManager.getConnection(url, dataBaseUser, dataBaseUserPassword);
        System.out.println("Created DB object" + "\n");
    }

    public void createTable() throws SQLException {
        try {
            String query = "CREATE TABLE public.refbook (" +
                    "person_info VARCHAR(150) NOT NULL, " +
                    "birth_date VARCHAR(15) NOT NULL, " +
                    "gender VARCHAR(6) NOT NULL" +
                    ")";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            if ("42P07".equals(e.getSQLState())) {
                System.out.println("Table already exists. Skipping creation.");
            } else {
                throw new SQLException("Error while executing statement\n", e);
            }
        }
    }

    public void insertInTable(Employee employee) throws SQLException {
        try{
            String query = "INSERT INTO public.refbook(person_info, birth_date, gender)" +
                    "values(?, ? ,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, employee.getPersonInfo());
            preparedStatement.setString(2, employee.getBirthDate().toString());
            preparedStatement.setString(3, employee.getGender());
            preparedStatement.executeUpdate();
        } catch(SQLException e){
            throw new SQLException("INSERTION EXCEPTION");
        }
    }

    public void printAllDataToConsole() throws SQLException{
        try{
            String selectQuery = "SELECT DISTINCT ON (person_info, birth_date) person_info, birth_date, gender " +
                    "FROM public.refbook " +
                    "ORDER BY " +
                    "person_info ASC," +
                    "birth_date ASC" +
                    ";";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("All data from the refbook table\n");

            while (resultSet.next()){
                String personalInfo = resultSet.getString("person_info");
                String birthDate = resultSet.getString("birth_date");
                String gender = resultSet.getString("gender");
                int age = Employee.calculateAge(LocalDate.parse(birthDate.substring(0, 10)));
                System.out.println("Person info: " + personalInfo + ", birth date: " + birthDate + ", gender: " + gender +  ", full age: " + age + "\n");
            }


        } catch(SQLException e){
            throw new SQLException("Error occured while print call.");
        }
    }

    public List<Employee> queryMaleEmployeesWithSurnameStartingWithF() throws SQLException {
        String optmization = "CREATE INDEX idx_person_info_gender ON refbook(person_info, birth_date, gender);";
        Statement optimizationStatement = connection.createStatement();
        optimizationStatement.executeUpdate(optmization);
        String sql = "SELECT * FROM refbook WHERE person_info LIKE 'F%' AND gender = 'Male';";
        List<Employee> maleEmployees = new ArrayList<>();
        Statement statement = connection.createStatement();

        System.out.println("Employees with Male Gender and Surname starting with 'F':");
        int id = 1;
        long startTime = System.currentTimeMillis();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            String personInfo = resultSet.getString("person_info");
            LocalDate birthDate = resultSet.getDate("birth_date").toLocalDate();
            String gender = resultSet.getString("gender");
            System.out.println(id + ") Personal Info: " + personInfo + ", Birth Date: " + birthDate + ", Gender: " + gender + "\n");
            id++;
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Execution Time(without optimization): " + duration + " ms");
        return maleEmployees;
    }

    public List<Employee> queryMaleEmployeesWithSurnameStartingWithFOptimized() throws SQLException {
        String select = "SELECT * FROM refbook WHERE person_info LIKE 'F%' and gender = 'Male';";
        List<Employee> maleEmployees = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(select);
        int id = 1;
        long startTime = System.currentTimeMillis();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String personInfo = resultSet.getString("person_info");
            LocalDate birthDate = resultSet.getDate("birth_date").toLocalDate();
            String gender = resultSet.getString("gender");
            maleEmployees.add(new Employee(personInfo, birthDate, gender));
            System.out.println(id + ") Personal Info: " + personInfo + ", Birth Date: " + birthDate + ", Gender: " + gender + "\n");
            id++;
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time(with optmization): " + (endTime - startTime) + " ms");
        return maleEmployees;
    }

    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()){
            connection.close();
        }
    }
}
