package org.zhuravlev;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class EmployeePopulator {

    private List<String> maleNames;
    private List<String> femaleNames;
    private List<String> surnames;
    private Random random;
    private Connection connection;

    public EmployeePopulator(Connection connection){
        this.maleNames = loadNames("male_names.txt");
        this.femaleNames = loadNames("female_names.txt");
        this.surnames = loadNames("surnames.txt");
        this.random = new Random();
        this.connection = connection;
    }

    private List<String> loadNames(String filename) {
        List<String> names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                names.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return names;
    }

    private String getRandomSurnameStartingWithF() {
        List<String> surnamesStartingWithF = loadNames("surnames.txt").stream()
                .filter(surname -> surname.startsWith("F"))
                .collect(Collectors.toList());

        return surnamesStartingWithF.get(random.nextInt(surnamesStartingWithF.size()));
    }

    public Employee[] generateEmployees(int count) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        List<String[]> batchData = new ArrayList<>();
        List<String[]> batchDataWithFSurnames = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            String gender = "Male";
            String name = getRandomName(maleNames);
            String surname = getRandomSurnameStartingWithF();
            String patronymic = generatePatronymic(name, true);
            String personalInfo = surname + " " + name + " " + patronymic;
            LocalDate birthDate = generateRandomBirthDate();
            Employee employee = new Employee(personalInfo, birthDate, gender);
            employees.add(employee);
        }

        for (int i = 0; i < count - 100; i++) {
            boolean isMale = random.nextBoolean();
            String gender = isMale ? "Male" : "Female";
            String name = isMale ? getRandomName(maleNames) : getRandomName(femaleNames);
            String surname = getRandomSurname();
            String patronymic = generatePatronymic(name, isMale);
            String personalInfo = surname + " " + name + " " + patronymic;
            LocalDate birthDate = generateRandomBirthDate();
            Employee employee = new Employee(personalInfo, birthDate, gender);
            employees.add(employee);
       }

        Employee[] employeesArray = employees.toArray(new Employee[0]);

        batchInsertEmployees(employeesArray);
        return employeesArray;
    }
    private String getRandomName(List<String> names) {
        return names.get(random.nextInt(names.size()));
    }

    private String getRandomSurname() {
        return surnames.get(random.nextInt(surnames.size()));
    }

    private String generatePatronymic(String name, boolean isMale) {
        return isMale ? name + "vich" : name + "vna";
    }

    private LocalDate generateRandomBirthDate() {
        int year = random.nextInt(40) + 1980;
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1;
        return LocalDate.of(year, month, day);
    }

    public void insertEmployeeIntoDatabase(Employee employee) throws SQLException {
        String insert = "INSERT INTO refbook(person_info, birth_date, gender) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insert);

        preparedStatement.setString(1, employee.getPersonInfo());
        preparedStatement.setDate(2, Date.valueOf(employee.getBirthDate()));
        preparedStatement.setString(3, employee.getGender());
        int rowsInserted = preparedStatement.executeUpdate();
    }

    public void batchInsertEmployees(Employee[] employees) throws SQLException {
        String insert = "INSERT INTO refbook(person_info, birth_date, gender) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insert);
        long startTime = System.currentTimeMillis();

        for (Employee employee : employees) {
            preparedStatement.setString(1, employee.getPersonInfo());
            preparedStatement.setDate(2, Date.valueOf(employee.getBirthDate()));
            preparedStatement.setString(3, employee.getGender());
            preparedStatement.addBatch();
        }
        int[] result = preparedStatement.executeBatch();

        long endTime = System.currentTimeMillis();
        System.out.println("Batch Inserted rows: " + Arrays.stream(result).sum());
        System.out.println("Batch Insert Time: " + (endTime - startTime) + " ms");
    }
}