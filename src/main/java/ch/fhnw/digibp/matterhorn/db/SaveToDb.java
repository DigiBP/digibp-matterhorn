package ch.fhnw.digibp.matterhorn.db;

import java.sql.*;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import javax.inject.Named;

@Named
public class SaveToDb implements JavaDelegate {

    static Connection con;
    static Statement sta;
    static int ticketID;
    static String processId;
    
    public void execute(DelegateExecution execution) throws Exception{
        sqlConnection();
        processId = execution.getProcessInstanceId();
        claimTicket();
        //sqlInsert("6", "From Camunda");
        //sqlQuery();
        execution.setVariable("delegateExecuteData", "delegate execute result: " + execution.getVariable("sampleData"));
    }
    
    private static void sqlConnection() throws Exception {
        //connection to db
        con = DriverManager.getConnection("jdbc:mysql://35.187.33.54:3306/matterhorn", "root", "knut");

        //Statement
        sta = con.createStatement();
    }

    private static void sqlQuery() throws Exception {
        //SQL query
        ResultSet res = sta.executeQuery("SELECT * FROM tickets");

        //process result set
        while (res.next()) {
            System.out.println(res.getShort(1) + ", " + res.getString("description"));
        }
    }

    private static void sqlInsert(String id, String desc) throws Exception {
        PreparedStatement pst = con.prepareStatement("INSERT INTO `matterhorn`.`tickets` (`id`, `description`) VALUES (?,?);");
        pst.setString(1, id);
        pst.setString(2, desc);
        pst.execute();
        pst.close();
    }
    
    private static void claimTicket() throws Exception {
        ResultSet res = sta.executeQuery("SELECT id FROM matterhorn.tickets where camunda_instance is null limit 1");
        String ticketId = null;
        while (res.next()) {
            ticketId = Integer.toString(res.getShort(1));
        }
        
        PreparedStatement pst = con.prepareStatement("UPDATE `matterhorn`.`tickets` SET `camunda_instance`=? WHERE `id`=?;");
        pst.setString(1, processId);
        pst.setString(2, ticketId);
        pst.execute();
        pst.close();
    }
}
