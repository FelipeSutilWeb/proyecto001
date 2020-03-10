/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.inventario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Laptop001
 */
public class conexion {
    public Connection conn;
    public Statement stmt;
    ResultSet rs;
    public void conecta() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/sg_rfidproyect001?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","root");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sg_rfidproyect003","root","rfidDatabase2019");
            
            
            stmt = conn.createStatement();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(conexion.class.getName()).log(Level.SEVERE, null, ex);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("alert('Erro driver: "+ex.toString()+"')");
        }
    }
    
    public void disconnect() throws SQLException {
            stmt.close();
            conn.close(); 
    }
    
    public ResultSet getSql(String str) throws SQLException {
        conecta();
        rs = stmt.executeQuery(str);

        return rs;
    }
    
    public void putSql(String str) throws SQLException {
        conecta();
        stmt.executeUpdate(str);
    }
}
