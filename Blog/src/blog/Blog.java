/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blog;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Scanner;

/**
 *
 * @author outsider
 */
public class Blog {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        createNewDB();
        createNewTable();
        menu();

        OUTER:
        while (true) {
            Scanner kbr = new Scanner(System.in);
            int choice = kbr.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Enter your entry below:\n");
                    Scanner keyboard = new Scanner(System.in);
                    String text = keyboard.nextLine();
                    insert(text);
                    System.out.println("Entry inserted!");
                    break;
                case 2:
                    selectAll();
                    break;
                default:
                    break OUTER;
            }
            
            menu();
        }
        
    }
    private static void menu(){
        System.out.println("Welcome to your own private blog journal!");
        System.out.println("Please proceed with one of the following choices");
        System.out.println("To insert a new entry, press 1");
        System.out.println("To see all the entries, press 2");
        System.out.println("To exit, press 3");        
    }
    private static Connection connect(){
        String url = "jdbc:sqlite:blog.db";
        Connection conn = null;
        
        try{
            conn = DriverManager.getConnection(url);
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return conn;
    }
    
    private static void selectAll(){
        String sql = "SELECT * FROM entries ORDER BY entryDATE ASC";
        
        try(Connection conn = Blog.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                System.out.println("Entry number: " + rs.getInt("entryID") + "\t\tSubmitted on:\t" + rs.getTimestamp("entryDATE") + "\n" + rs.getString("entryNOTE") + "\n");
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    }
    
    private static void createNewTable(){
        String url = "jdbc:sqlite:blog.db";
        String sql = "CREATE TABLE IF NOT EXISTS entries(\n"
                + " entryID integer AUTO_INCREMENT PRIMARY KEY,\n"
                + " entryNOTE text NOT NULL,\n"
                + " entryDATE TIMESTAMP\n"
                + ");";
        
        try(Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    }
    
    private static void createNewDB(){
        String url = "jdbc:sqlite:blog.db";
        
        try(Connection conn = DriverManager.getConnection(url)){
            if(conn != null){
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new Database has been created for you!");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private static void insert(String entry){
        String sql = "INSERT INTO entries(entryNOTE, entryDATE) VALUES(?,?)";
        Calendar calendar = Calendar.getInstance();
        Timestamp tmstmp = new java.sql.Timestamp(calendar.getTime().getTime());
        
        try(Connection conn = Blog.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, entry);
            pstmt.setTimestamp(2, tmstmp);
            pstmt.executeUpdate();
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    }
    
}
