package ch.fhnw.digibp.matterhorn.db;

import java.sql.*;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import javax.inject.Named;

@Named
public class SaveToDb implements JavaDelegate {

    static Connection con;
    static Statement sta;
    
    public void execute(DelegateExecution execution) throws Exception{
        sqlConnection();
        sqlInsert("5", "From Camunda");
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
}
