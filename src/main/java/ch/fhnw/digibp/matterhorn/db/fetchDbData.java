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
    static double sentiment;
    static String id;
    static String urgency;

    public void execute(DelegateExecution execution) throws Exception {
        sqlConnection();
        String processInstance = execution.getProcessInstanceId();
        while (id == null) { //try again until zapier saved the data into the database
            getRow(processInstance);
        }
                
        if (sentiment < 0.1) {
            urgency = "Very";
        } else if (sentiment < 0.5){
            urgency = "Medium";
        } else {
            urgency = "Low";        
    }
        execution.setVariable("urgency", urgency);
        execution.setVariable("sentiment", sentiment);
        execution.setVariable("owner", owner);
        execution.setVariable("id", id);
        
        id = null; //reset the variable (the running program keeps it assigned)
    }

    private static void sqlConnection() throws Exception {
        //connection to db
        con = DriverManager.getConnection("jdbc:mysql://35.187.33.54:3306/matterhorn", "root", "knut");

        //Statement
        sta = con.createStatement();
    }

    private void getRow(String processInst) throws Exception {
        TimeUnit.SECONDS.sleep(1); // time to finish the previous task (zapier)
        ResultSet res = sta.executeQuery("SELECT * FROM tickets where camunda_instance = \"" + processInst + "\"");
        while (res.next()) {
            sentiment = Double.parseDouble(res.getString("sentiment_score"));
            owner = res.getString("owner");
            id = res.getString("id");
        }
    }
}
