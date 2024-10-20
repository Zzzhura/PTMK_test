package org.zhuravlev;

import java.time.LocalDate;

public class ArgsChecker {
    public static boolean checkArgsCondition(String[] args){
        if (args.length == 0){
            throw new RuntimeException("NO ARGS.");
        }
        return true;
    }

    public static Integer getRuntimeMode(String[] args){
        int mode;
        try{
            mode = Integer.parseInt(args[0]);
            return mode;
        } catch(NumberFormatException e){
            throw new NumberFormatException("First args argument should be a number.");
        }
    }

    public static Employee ArgsCheckerForApp2(String[] args){
        if (args.length < 6){
            throw new RuntimeException("Wrong args for App 2 mode");
        } else{
            String personalInfo = args[1] + " " + args[2] + " " + args[3];
            LocalDate birthDate = LocalDate.parse(args[4]);
            String gender = args[5];
            return new Employee(personalInfo, birthDate, gender);
        }
    }
}
