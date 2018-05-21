package ch.fhnw.digibp.matterhorn.db;

import java.sql.*;
import java.util.concurrent.TimeUnit;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import javax.inject.Named;

@Named
public class fetchDbData implements JavaDelegate {

    static Connection con;
    static Statement sta;
    static int ticketID;
    //static String processId;
    static String owner;
    static String sentiment;

    public void execute(DelegateExecution execution) throws Exception {
        sqlConnection();
        String processId = execution.getProcessInstanceId();
        while (sentiment == null) { //try again until zapier saved the data into the database
            getRow(processId);
        }
        
        execution.setVariable("sentiment", sentiment);
        execution.setVariable("owner", owner);
    }

    private static void sqlConnection() throws Exception {
        //connection to db
        con = DriverManager.getConnection("jdbc:mysql://35.187.33.54:3306/matterhorn", "root", "knut");

        //Statement
        sta = con.createStatement();
    }

    private void getRow(String processId) throws Exception {
        TimeUnit.SECONDS.sleep(1); // time to finish the previous task (zapier)
        ResultSet res = sta.executeQuery("SELECT * FROM tickets where camunda_instance = \"" + processId + "\"");
        while (res.next()) {
            sentiment = res.getString("sentiment_score");
            owner = res.getString("owner");
        }
    }
}
