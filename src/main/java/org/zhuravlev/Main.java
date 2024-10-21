package org.zhuravlev;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {

        ArgsChecker.checkArgsCondition(args);
        ModeExecutor modeExecutor = new ModeExecutor(ArgsChecker.getRuntimeMode(args));
        DataBase dB = new DataBase();
        modeExecutor.executeTheMode(dB, args);
    }
}