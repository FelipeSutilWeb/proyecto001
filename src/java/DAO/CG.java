/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import cl.inventario.conexion;
import java.sql.CallableStatement;

/**
 *
 * @author Laptop001
 */
public class CG {
    
    public void eliminarmatnr(String matnr, String vin) throws Exception {
        conexion conexiones = new conexion();
        try{
           // ResultSet rs = conexiones.getSql("");
            conexiones.conecta();
            CallableStatement stm = conexiones.conn.prepareCall("{ call SP_DELETE_MATNR_LST(?,?) }");
            stm.setString(1, matnr);
            stm.setString(2, vin);
            stm.execute();
        }catch(Exception e){
            throw e;
        }
    }
    
}
