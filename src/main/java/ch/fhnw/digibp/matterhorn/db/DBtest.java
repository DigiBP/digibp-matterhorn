package ch.fhnw.digibp.matterhorn.db;

import java.sql.*;

public class DBtest {

    static Connection con;
    static Statement sta;

    public static void main(String[] args) throws Exception {
        sqlConnection();
        //claimTicket();
        //sqlInsert("4", "It's ID 4");
        sqlQuery();

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
            System.out.println(res.getShort(1) + ", " + res.getString("description") + ", " +res.getString("sentiment_score"));
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
        pst.setString(1, "this is a simple test");
        pst.setString(2, ticketId);
        pst.execute();
        pst.close();
    }

}
