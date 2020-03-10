/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.inventario;

import com.google.gson.Gson;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Laptop001
 */
@ManagedBean(name = "restbean")
@ViewScoped
public class restbean {
    
    Gson gs = new Gson();
    public String arr;
    
    public void renderTs() throws Exception {
        try{
            String em = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("strRf");
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("$('body').prepend('holiiii " + em + "')");
        }catch(Exception e){
            throw e;
        }
    }

    public void armaJson(ArrayList ls) {
        try {
            arr = gs.toJson(ls);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/json");
            externalContext.setResponseCharacterEncoding("UTF-8");
            externalContext.getResponseOutputWriter().write(arr);
            facesContext.responseComplete();
        } catch (IOException ex) {
            Logger.getLogger(restbean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void renderVin() throws Exception {
        String rf = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("rfStr");
        String sql;
        
        conexion conexiones = new conexion();
        
        sql = "SELECT VIN, MATNR FROM TBL_MATNR WHERE ID = '" + rf + "' ";
        try{    
            ResultSet rs = conexiones.getSql(sql);
            ArrayList ls = new ArrayList();
        
            while (rs.next()) {
                inventario inv = new inventario();
                inv.setMATNR(rs.getString("MATNR"));
                inv.setVIN(rs.getString("VIN"));
                ls.add(inv);
            }
            
            conexiones.disconnect();
            armaJson(ls);
            
        }catch(Exception e) {
            throw e;
        }

    } 
    
    public void renderJson() throws Exception {
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonStr");
        String sql;
        String arr = "";
        conexion conexiones = new conexion();
        
        sql = "SELECT cm.MATNR_ID, c.ATBEZ, cm.VALOR_CARACTER, t.TIPO \n" +
                        "FROM tbl_caracteristicas_material cm, tbl_caracteristicas c, tbl_tipo_caracter t, tbl_matnr m\n " +
                        "WHERE cm.CARACTER_ID = c.ID\n " +
                        "AND c.TIPO_CARACTER = t.ID " +
                        "AND cm.MATNR_ID = m.VIN " +
                        "AND cm.MATNR_ID = '" + val + "' ";
        try{    
            ResultSet rs = conexiones.getSql(sql);
            ArrayList ls = new ArrayList();
        
            while (rs.next()) {
                inventario inv = new inventario();
                inv.setMATNR(rs.getString("MATNR_ID"));
                inv.setATBEZ(rs.getString("ATBEZ"));
                inv.setCAR_VALOR(rs.getString("VALOR_CARACTER"));
                inv.setCAR_TIPO(rs.getString("TIPO"));
                ls.add(inv);
            }
            
            conexiones.disconnect();
            armaJson(ls);
        
        }catch(Exception e) {
            throw e;
        }

    }
}
