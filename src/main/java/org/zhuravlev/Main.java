package org.zhuravlev;
import java.sql.SQLException;

public class Main {
    private static final String url = "jdbc:postgresql://localhost:5432/PTMK";
    private static final String dataBaseUser = "postgres";
    private static final String dataBaseUserPassword = "zhura323";
    public static void main(String[] args) throws SQLException {

        ArgsChecker.checkArgsCondition(args);
        ModeExecutor modeExecutor = new ModeExecutor(ArgsChecker.getRuntimeMode(args));
        DataBase dB = new DataBase(url, dataBaseUser, dataBaseUserPassword);
        modeExecutor.executeTheMode(dB, args);
    }
}