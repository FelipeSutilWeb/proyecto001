/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.inventario;

import DAO.CG;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author Laptop001
 */
@ManagedBean(name = "inventariobean")
@ViewScoped
public class inventario {
    private String RFID;
    private String MATNR;
    private String MODELO;
    private String location;
    private String aedat_sc;
    private String code;
    private String descr;
    private String id;
    private String TIPO;
    private String ATBEZ;
    private String CAR_VALOR;
    private String CAR_TIPO;
    private String TIPO_DATO;
    private String VIN;
    private String MARCA;
    private String NAVE;
    private String VIAJE;
    private String LOTE;
    private String MOTOR;
    private String COLOR;
    private String N_EMBARQUE;
         
    Gson gs = new Gson();
    
    public void updMatnr() throws Exception {
            String vals = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
            String params[] = vals.split(",");
            
            conexion conexiones = new conexion();
            String sql = "update tbl_matnr set ID = '" + params[1] + "' WHERE VIN = '" + params[0] + "' ";
            try{
                conexiones.putSql(sql);
            }catch(Exception e){
                throw e;
            }
            conexiones.disconnect();
    
    }
    
    public void getRfidLst() throws Exception {
        conexion conexiones = new conexion();
        try{
           // ResultSet rs = conexiones.getSql("");
            conexiones.conecta();
            CallableStatement stm = conexiones.conn.prepareCall("{ call SP_GET_RFID_LST() }");
            //stm.setString(1, "000000002033");
            stm.execute();
            
            ResultSet rs = stm.getResultSet();
            ArrayList ls = new ArrayList();
            while (rs.next()){
                inventario inv = new inventario();
                inv.setRFID(rs.getString("CODE_SC"));
                ls.add(inv);
            }
            
            conexiones.disconnect();
            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setRfids(" + arr + ")");
            
        }catch(Exception e){
            throw e;
        }
    }
    
    public void addCaracters(String vin, String car) throws Exception {
        
        try{
            conexion conexiones = new conexion();
            JSONObject js = new JSONObject(car);
            js.keys().forEachRemaining(keyStr ->
            {
                Object keyvalue;
                try {
                    keyvalue = js.get((String) keyStr);
                    System.out.println("id_caracter: "+ keyStr + " value_caracter: " + keyvalue);
                    String sql = "INSERT INTO TBL_CARACTERISTICAS_MATERIAL (ID, MATNR_ID, CARACTER_ID, VALOR_CARACTER)"
                            + " VALUES(NULL, '" + vin + "', " + keyStr + ", '" + keyvalue + "')";
                    conexiones.putSql(sql);
                    conexiones.disconnect();
                } catch (JSONException ex) {
                    Logger.getLogger(inventario.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(inventario.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            //conexiones.disconnect();
            
        }catch(Exception e){
            throw e;
        } 
    }
    
    public void updateCaractMats() throws Exception{
        String error = "Exito";
        conexion conexiones = new conexion();
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");       
        String params[] = val.split("%"); 
        
        String sql = "UPDATE tbl_caracteristicas cm SET cm.ATBEZ = '" + params[1] + "' WHERE cm.ID = " + params[0] + " ";
        
        try{
            conexiones.putSql(sql);
            conexiones.disconnect();
            
        }catch(Exception e){
            error = e.getMessage();

        }
        
        String arr = gs.toJson(error);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("setErrorUpdateCaractMats("+ arr +")");
    }
    
    public void addMaterial() throws SQLException, Exception {
        String error = "Exito";
        conexion conexiones = new conexion();
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");       
        String params[] = val.split("%"); 
        
        String sql = "INSERT INTO tbl_material (COD_MATNR, DESCR) VALUES('" + params[0].trim() + "', '" + params[1].trim() + "')";
        try{
            conexiones.putSql(sql);
            conexiones.disconnect();
            addCaracters(params[0].trim(), params[2].trim());
            
        }catch(Exception e){
            error = e.getMessage();

        }finally{
            //RequestContext context = RequestContext.getCurrentInstance();
            //context.execute("$('.preloader').attr('style','display:none');");         
        }
        
        String arr = gs.toJson(error);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("setErrorAgregar("+ arr +")");
        //conexiones.disconnect();
        //addCaracters(params[2].trim(), params[3].trim());
        //conexiones.disconnect();
    }
    
    public void agregarMaterial() throws SQLException, Exception {
        conexion conexiones = new conexion();
        //enviar mensaje de error
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        String params[] = val.split("%");
        
        String sql = "INSERT INTO tbl_material (COD_MATNR, DESCR) VALUES ('" + params[0] + "', '" + params[1] + "')";    
        
        try{
            conexiones.putSql(sql);
            conexiones.disconnect();
        }catch (Exception e){
            throw e;
        }finally{
            
        }
    }
    
    public void delMaterial() throws SQLException {
        String error = "Exito";
        conexion conexiones = new conexion();
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        
        String sql = "DELETE FROM tbl_material WHERE COD_MATNR = '" + val + "' ";
        String sql2 = "DELETE FROM tbl_caracteristicas_material WHERE MATNR_ID ='" + val + "'";
        try{
            conexiones.putSql(sql);
            conexiones.putSql(sql2);
        }catch(Exception e){
            error = e.getMessage();
        }
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.execute("setEliminarMateriales("+ error +")");
        conexiones.disconnect();
    }
    
        public void renderTest() throws Exception {
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonStr");
        try{
            RequestContext context = RequestContext.getCurrentInstance();
             context.execute("('textooo append')");
        }catch(Exception e){
                throw e;
        }
    }
    public void buscarMaterialSpe() throws SQLException {
        conexion conexiones = new conexion();
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        
        String sql = "SELECT COD_MATNR FROM tbl_material WHERE COD_MATNR ='"+ val + "'";
        
        try{
            ResultSet rs = conexiones.getSql(sql);
            
            ArrayList ls = new ArrayList();
            
            while (rs.next()){
                inventario inv = new inventario();
                inv.setMATNR(rs.getString("COD_MATNR"));
                ls.add(inv);              
            }
            
            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setBusSpeMatnr("+ arr +")");
        }catch (Exception e){
            throw e;
        }
    }
    
    public void encontrarMaterial() throws SQLException {
        conexion conexiones = new conexion();
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");

        String sql = "SELECT m.COD_MATNR, m.DESCR \n" +
                        "FROM tbl_material m\n" +
                        "where COD_MATNR LIKE '%" + val + "%'";
        
        try{
            ResultSet rs = conexiones.getSql(sql);
            
            ArrayList ls = new ArrayList();
            
            while (rs.next()) {
                inventario inv = new inventario();
                inv.setMATNR(rs.getString("COD_MATNR"));
                inv.setDescr(rs.getString("DESCR"));
                //inv.setVIN(rs.getString("VIN"));
                ls.add(inv);
            }
            
            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setBuscarMatnr("+ arr +")");
            
        }catch (Exception e){
            throw e;
        }
        conexiones.disconnect();
    }
    
    public void buscarRFID() throws SQLException {
        conexion conexiones = new conexion();
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");

        String sql = "call SP_SELECT_MATNR('" + val + "') ;";

        try{
            ResultSet rs = conexiones.getSql(sql);
            
            ArrayList ls = new ArrayList();
            
            while (rs.next()) {
                inventario inv = new inventario();
                inv.setVIN(rs.getString("VIN"));
                inv.setMATNR(rs.getString("MATNR"));
                inv.setMARCA(rs.getString("ID"));
                inv.setDescr(rs.getString("DESCR"));
                ls.add(inv);
            }
            
            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setBuscarRFID(" + arr + ")");
            
        }catch(Exception e) {
            throw e;
        }
        conexiones.disconnect();
    }
    
    public void countMateriales() throws SQLException {
        conexion conexiones = new conexion();
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        
        String sql = "SELECT m.MATNR FROM tbl_matnr m WHERE m.MATNR = '"+ val +"'";
        
        try{
            ResultSet rs = conexiones.getSql(sql);
            
            ArrayList ls = new ArrayList();
            
            while (rs.next()){
                inventario inv = new inventario();
                inv.setMATNR(rs.getString("MATNR"));
                ls.add(inv);
            }
            
            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setCountMateriales(" + arr + ")");
        }catch(Exception e) {
            throw e;
        }
        conexiones.disconnect();
    }
    
    public void processJson() throws Exception {
        
        String bar = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonStr");  
        try{     
            if(bar != null) {
                JSONArray jsonArray = new JSONArray(bar);
                conexion conexiones = new conexion();
                
                for(int i=0;i<jsonArray.length();i++) {
                        JSONObject obj;
                        obj = (JSONObject) jsonArray.get(i);

                        String rf = obj.getString("epc");
                        String loc = obj.getString("locationOfRead");
                        conexiones.putSql("INSERT INTO tbl_inven (ID, CODE_SC, LOCATION_SC) VALUES(null, '" + rf + "', '" + loc + "')");
                }
                
                conexiones.disconnect();
                
            }      
        }catch(Exception e) {
            throw e;
        }
    }
    
    public void renderJson() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/json");
        externalContext.setResponseCharacterEncoding("UTF-8");
        externalContext.getResponseOutputWriter().write("[{\"nombre\":\"Android Apps\"}]");
        facesContext.responseComplete();
    }
    
    //Para Agregar y Ver Caracteristicas
    public void lstTiposCaracter() throws Exception {
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");       
        String params[] = val.split("%"); 
        
        String sql;
        conexion conexiones = new conexion();
        
        sql = "SELECT c.ID, c.ATBEZ, c.TIPO_CARACTER, td.TIPO_DATO FROM tbl_caracteristicas c, tbl_tipo_dato td WHERE c.COD_TIPO_DATO = td.COD_TIPO_DATO AND ID = '" + params[0] + "' AND ATBEZ = '" + params[1] + "'";
        try{
        ResultSet rs = conexiones.getSql(sql);
                    
        ArrayList ls = new ArrayList();
        
            while (rs.next()) {

                inventario inv = new inventario();
                inv.setId(rs.getString("ID"));
                inv.setATBEZ(rs.getString("ATBEZ"));
                inv.setTIPO_DATO(rs.getString("TIPO_DATO"));
                inv.setTIPO(rs.getString("TIPO_CARACTER"));
                ls.add(inv);
            }
            
        conexiones.disconnect();
            
        String arr = gs.toJson(ls);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("setTiposCaracters(" + arr + ")");
        
        }catch(Exception e) {
            throw e;
        }
    }
    
    //Es para Agregar y Ver Material
    public void lstCaractMatnr() throws Exception {
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");       
        String params[] = val.split("%"); 
        
        String sql;
        conexion conexiones = new conexion();
        
        sql = "SELECT c.ID, c.ATBEZ, c.TIPO_CARACTER, td.TIPO_DATO FROM tbl_caracteristicas c, tbl_tipo_dato td WHERE c.COD_TIPO_DATO = td.COD_TIPO_DATO AND c.TIPO_CARACTER = " + params[0] + "";
        try{
        ResultSet rs = conexiones.getSql(sql);
                    
        ArrayList ls = new ArrayList();
        
            while (rs.next()) {

                inventario inv = new inventario();
                inv.setId(rs.getString("ID"));
                inv.setATBEZ(rs.getString("ATBEZ"));
                inv.setTIPO(rs.getString("TIPO_CARACTER"));
                inv.setTIPO_DATO(rs.getString("TIPO_DATO"));
                ls.add(inv);
            }
            
        conexiones.disconnect();
            
        String arr = gs.toJson(ls);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("setCaractersMatnr(" + arr + ")");
        
        }catch(Exception e) {
            throw e;
        }
    }
    
    public void lstTipoDato() throws Exception {
        String sql;
        conexion conexiones = new conexion();
        
        sql = "SELECT COD_TIPO_DATO, DESCR FROM tbl_tipo_dato";
        
        try{
        ResultSet rs = conexiones.getSql(sql);
                    
        ArrayList ls = new ArrayList();
        
            while (rs.next()) {

                inventario inv = new inventario();
                inv.setId(rs.getString("COD_TIPO_DATO"));
                inv.setDescr(rs.getString("DESCR"));
                ls.add(inv);
            }
            
        conexiones.disconnect();
            
        String arr = gs.toJson(ls);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("setListTipoDatos(" + arr + ")");
        
        }catch(Exception e) {
            throw e;
        }
    }
  
    public void lstMateriales() throws Exception {
        
        String sql;
        conexion conexiones = new conexion();
        
        sql = "SELECT * FROM tbl_material";
        try{
            ResultSet rs = conexiones.getSql(sql);

            ArrayList ls = new ArrayList();

                while (rs.next()) {
                    inventario inv = new inventario();
                    inv.setMATNR(rs.getString("COD_MATNR"));
                    inv.setDescr(rs.getString("DESCR"));
                    ls.add(inv);
                }

            conexiones.disconnect();

            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setTableFilter(" + arr + ")");
        
        }catch(Exception e) {
            throw e;
        }
    }
    
    public void lstTipos() throws Exception {
        
        String sql;
        conexion conexiones = new conexion();
        
        sql = "SELECT * FROM TBL_TIPO_CARACTER";
        try{
            ResultSet rs = conexiones.getSql(sql);

            ArrayList ls = new ArrayList();

                while (rs.next()) {
                    inventario inv = new inventario();
                    inv.setId(rs.getString("ID"));
                    inv.setTIPO(rs.getString("TIPO"));
                    ls.add(inv);
                }

            conexiones.disconnect();

            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setTipos(" + arr + ")");
        
        }catch(Exception e) {
            throw e;
        }
    }
    
    public void verInventario() throws SQLException {
        String bar = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("barcode");    
        String loc = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("location_now");
        String sql;
        String arr;
        String arrAll;

        conexion conexiones = new conexion();
        
        try {
            
            if(bar == null) {
                sql = "SELECT i.ID, m.VIN, i.AEDAT_SC, i.CODE_SC, m.MATNR AS MATNR_ID, i.LOCATION_SC, m.DESCR FROM\n" +
                            "tbl_matnr m, tbl_inven i\n" +
                            "WHERE\n" +
                            "m.ID = i.CODE_SC ORDER BY i.ID DESC";
            ResultSet rs = conexiones.getSql(sql);
                    
            ArrayList ls = new ArrayList();
            while (rs.next()) {

                inventario inv = new inventario();
                inv.setId(rs.getString("ID"));
                inv.setCode(rs.getString("CODE_SC"));
                inv.setMATNR(rs.getString("MATNR_ID"));
                inv.setAedat_sc(rs.getString("AEDAT_SC"));
                inv.setLocation(rs.getString("LOCATION_SC"));
                inv.setDescr(rs.getString("DESCR"));
                inv.setVIN(rs.getString("VIN"));
                ls.add(inv);
            }

            arr = gs.toJson(ls);
            
            //sql = "SELECT * FROM tbl_matnr ORDER BY FECHA_IN DESC";
            
            conexiones.conecta();
            CallableStatement stm = conexiones.conn.prepareCall("{ call SP_GET_MATNR_CARACTER() }");
            stm.execute();
            ResultSet rsAll = stm.getResultSet();
            //ResultSet rsAll = conexiones.getSql(sql);
                    
            ArrayList lsAll = new ArrayList();
            while (rsAll.next()) {

                inventario inv = new inventario();
                /*inv.setRFID(rsAll.getString("ID"));
                inv.setMATNR(rsAll.getString("MATNR"));
                inv.setVIN(rsAll.getString("VIN"));
                inv.setMODELO(rsAll.getString("DESCR"));
                inv.setMARCA(rsAll.getString("MARCA"));
                inv.setNAVE(rsAll.getString("NAVE"));
                inv.setVIAJE(rsAll.getString("VIAJE"));
                inv.setN_EMBARQUE(rsAll.getString("N_EMBARQUE"));*/
                inv.setId(rsAll.getString("ID"));
                inv.setATBEZ(rsAll.getString("ATBEZ"));
                inv.setCAR_TIPO(rsAll.getString("TIPO_CARACTER"));
                lsAll.add(inv);
            }

            arrAll = gs.toJson(lsAll);
            
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setInventario(" + arr + "," + arrAll + ")");
            }else{
                sql = "INSERT INTO tbl_inven (ID, CODE_SC, LOCATION_SC) VALUES(null, '" + bar + "', '" + loc + "')";
                conexiones.putSql(sql);
            }

            conexiones.disconnect();
            
        } catch (SQLException ex) {
            Logger.getLogger(inventario.class.getName()).log(Level.SEVERE, null, ex);
            ///RequestContext context = RequestContext.getCurrentInstance();
            //context.execute("alert('"+ex.toString()+"')");
        }
        
        
    }

    public void caracterById() throws Exception {
        
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        String params[] = val.split("%");
        String dato1 = params[0];
        
        conexion conexiones = new conexion();
        String sql = "SELECT cm.MATNR_ID, c.ATBEZ, cm.VALOR_CARACTER, t.TIPO, c.ID AS ID_CAR, td.TIPO_DATO, td.DESCR \n" +
                        "FROM tbl_caracteristicas_material cm, tbl_caracteristicas c, tbl_tipo_caracter t, tbl_tipo_dato td \n" +
                        "WHERE cm.CARACTER_ID = c.ID \n" +
                        "AND c.TIPO_CARACTER = t.ID AND c.COD_TIPO_DATO = td.COD_TIPO_DATO " +
                        "AND cm.MATNR_ID = '" + dato1 + "' ";
        try{
            ResultSet rs = conexiones.getSql(sql);                   
            ArrayList ls = new ArrayList();
                while (rs.next()) {
                    inventario inv = new inventario();
                    inv.setMATNR(rs.getString("MATNR_ID"));
                    inv.setATBEZ(rs.getString("ATBEZ"));
                    inv.setCAR_VALOR(rs.getString("VALOR_CARACTER"));
                    inv.setCAR_TIPO(rs.getString("TIPO"));
                    inv.setId(rs.getString("ID_CAR"));
                    inv.setTIPO_DATO(rs.getString("TIPO_DATO"));
                    
                    ls.add(inv);
                }
            // Close statement
            conexiones.disconnect();

            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setCaracterById(" + arr + "); setBuscarMats(" + arr + ")");
            
        
        }catch(Exception e) {
            throw e;
        }
    }
    
    //Carga de Datos
    //Carga de Packing List
    public void cargarPL() throws Exception {
        String error = "Exito";
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        String params[] = val.split("%");
        
        conexion conexiones = new conexion();
               
        String sql_emb = "INSERT INTO tbl_detalle_embarque (ID, COD_EMB, COD_VIAJE, MATNR_ID, VIN, VIN_CUT, RFID, ESTADO_IN) VALUES(NULL, '" + params[4] + "', '" + params[7] + "', '" + params[2] + "', '" + params[1] + "', '" + params[3] + "', '" + params[5] + "', " + params[6] + ")";
     
        try{
            conexiones.putSql(sql_emb);
            conexiones.disconnect();
            addCaractersVin(params[1], params[0]);
            
        }catch (Exception e) {
            error = e.getMessage();
        }
        
        String arr = gs.toJson(error);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("setErrorCargarPL("+ arr +")");
    }
    
    public void addCaractersVin(String vin, String car) throws Exception {
        
        try{
            conexion conexiones = new conexion();
            JSONObject js = new JSONObject(car);
            js.keys().forEachRemaining(keyStr ->
            {
                Object keyvalue;
                try {
                    keyvalue = js.get((String) keyStr);
                    System.out.println("id_caracter: "+ keyStr + " value_caracter: " + keyvalue);
                    String sql = "INSERT INTO tbl_caracteristicas_vin (ID, VIN, CARACTER_ID, VALOR_CARACTER, COD_EMB, COD_VIAJE)"
                            + " VALUES(NULL, '" + vin + "', " + keyStr + ", '" + keyvalue + "')";
                    conexiones.putSql(sql);
                    conexiones.disconnect();
                } catch (JSONException ex) {
                    Logger.getLogger(inventario.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(inventario.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            //conexiones.disconnect();
            
        }catch(Exception e){
            throw e;
        } 
    }
    
    //Agregar Caracteristicas Vin
    public void cargarDatosVin() throws Exception {
        String error = "Exito";
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        String params[] = val.split("%");
  
        conexion conexiones = new conexion();
               
        String sql = "CALL SP_GET_VIN_CARACTS('" + params[0] + "', '" + params[1] + "' )";        
        
        try{
            ResultSet rs = conexiones.getSql(sql);
            
            ArrayList ls = new ArrayList();
            
            while (rs.next()) {
                inventario inv = new inventario();
                inv.setMARCA(rs.getString("MARCA"));
                inv.setLOTE(rs.getString("LOTE"));
                inv.setVIN(rs.getString("VIN"));
                inv.setMOTOR(rs.getString("MOTOR"));
                inv.setMODELO(rs.getString("MODELO"));
                inv.setMATNR(rs.getString("MATERIAL"));
                inv.setCOLOR(rs.getString("COLOR"));
                inv.setNAVE(rs.getString("COD_EMB"));
                inv.setVIAJE(rs.getString("COD_VIAJE"));
                inv.setId(rs.getString("ESTADO_IN"));
                ls.add(inv);
            }
            
            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setGetVinPL(" + arr + ")");
            
        }catch(Exception e) {
            throw e;
        }
        conexiones.disconnect();
    }
    
    //Agregar Embarque
    public void agregarEmb() throws Exception {
        String error = "Exito";
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        String params[] = val.split("%");
        
        conexion conexiones = new conexion();
        
        String sql = "INSERT INTO tbl_embarque (ID, COD_EMB, COD_VIAJE, STATUS_EMB, DESCR) VALUES(NULL, '" + params[0] + "', '" + params[1] + "', '" + params[2] + "', '" + params[3] + "')";
        
        try{
            conexiones.putSql(sql);
            conexiones.disconnect();
        }catch (Exception e){
            error = e.getMessage();
        }
        
        String arr = gs.toJson(error);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("setAgregarEmb("+ arr +")");
    }
    
    //Buscar Embarque
    public void buscarEmb() throws Exception {
        String error = "Exito";
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        String params[] = val.split("%");
        
        conexion conexiones = new conexion();
        
        String sql = "SELECT de.COD_EMB, de.COD_VIAJE, de.ESTADO_IN, es.DESCR\n" +
                     "FROM tbl_detalle_embarque de, tbl_estatus es\n" +
                     "WHERE de.ESTADO_IN = es.STATUS_ID\n" +
                     "AND de.COD_EMB = '" + params[0] + "'\n" +
                     "AND de.COD_VIAJE = '" + params[1] + "' ";
        
        try{
            ResultSet rs = conexiones.getSql(sql);                   
            ArrayList ls = new ArrayList();
                while (rs.next()) {
                    inventario inv = new inventario();
                    inv.setN_EMBARQUE(rs.getString("COD_EMB"));
                    inv.setVIAJE(rs.getString("COD_VIAJE"));
                    inv.setId(rs.getString("ESTADO_IN"));
                    inv.setDescr(rs.getString("DESCR"));

                    ls.add(inv);
                }
            // Close statement
            conexiones.disconnect();

            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setEmb(" + arr + ");");
            
        }catch(Exception e) {
            error = e.getMessage();
        }
    }
    
    //Buscar Grafico by LIKE
    public void gfEmbLike() throws Exception {
        String error = "Exito";
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        
        conexion conexiones = new conexion();
        
        String sql = "SELECT * FROM tbl_embarque WHERE COD_EMB LIKE '" + val + "%'";
        
        try{
            ResultSet rs = conexiones.getSql(sql);                   
            ArrayList ls = new ArrayList();
                while (rs.next()) {
                    inventario inv = new inventario();
                    inv.setN_EMBARQUE(rs.getString("COD_EMB"));
                    inv.setVIAJE(rs.getString("COD_VIAJE"));
                    inv.setId(rs.getString("ID"));
                    inv.setDescr(rs.getString("DESCR"));

                    ls.add(inv);
                }
            // Close statement
            conexiones.disconnect();

            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setBuscarEmb(" + arr + ");");
            
        
        }catch(Exception e) {
            error = e.getMessage();
        }
    }
    
    //Buscar tabla embarque
    public void buscarTablaEmb() throws Exception {
        String error = "Exito";
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        
        conexion conexiones = new conexion();
        
        String sql = "SELECT e.ID, e.COD_EMB, e.COD_VIAJE, e.DESCR FROM tbl_embarque e";
        
        try{
            ResultSet rs = conexiones.getSql(sql);                   
            ArrayList ls = new ArrayList();
                while (rs.next()) {
                    inventario inv = new inventario();
                    inv.setId(rs.getString("ID"));
                    inv.setN_EMBARQUE(rs.getString("COD_EMB"));
                    inv.setVIAJE(rs.getString("COD_VIAJE"));
                    inv.setDescr(rs.getString("DESCR"));

                    ls.add(inv);
                }
            // Close statement
            conexiones.disconnect();

            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setBuscarTablaEmb(" + arr + ");");
            
        
        }catch(Exception e) {
            error = e.getMessage();
        }
    }
    
    //Busca todas naves
    public void getAllNave() throws Exception {
        
        conexion conexiones = new conexion();
        
        String sql = "SELECT ID, COD_EMB, COD_VIAJE, DESCR FROM tbl_embarque";
        
        try{
            ResultSet rs = conexiones.getSql(sql);
            
            ArrayList ls = new ArrayList();
            
            while (rs.next()) {
                inventario inv = new inventario();
                inv.setId(rs.getString("ID"));
                inv.setNAVE(rs.getString("COD_EMB"));
                inv.setVIAJE(rs.getString("COD_VIAJE"));
                inv.setDescr(rs.getString("DESCR"));
                ls.add(inv);
            }
            
            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setGetAllNave(" + arr + ")");
            
        }catch(Exception e) {
            throw e;
        }
        conexiones.disconnect();
    }
    
    //Buscar Datos Grafico por estatus
    public void graficoNone() throws Exception {
        String error = "Exito";
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        String params[] = val.split("%");
                
        conexion conexiones = new conexion();
        
        String sql = "SELECT de.VIN, de.MATNR_ID, es.DESCR FROM tbl_detalle_embarque de, tbl_estatus es WHERE de.ESTADO_IN = es.STATUS_ID AND de.COD_EMB = '" + params[0] + "' AND de.COD_VIAJE = '" + params[1] + "' AND de.ESTADO_IN = " + params[2] + "";
        
        try{
            ResultSet rs = conexiones.getSql(sql);
            
            ArrayList ls = new ArrayList();
            
            while (rs.next()) {
                inventario inv = new inventario();
                inv.setVIN(rs.getString("VIN"));
                inv.setMATNR(rs.getString("MATNR_ID"));
                inv.setDescr(rs.getString("DESCR"));
                ls.add(inv);
            }
            
            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setGetGrafico(" + arr + ")");
            
        }catch (Exception e){
            error = e.getMessage();
        }
    }
    
    //Mantenedor Caracteristicas
    //Se Busca el ultimo ID de la tabla caracteristicas
    public void maxIdCaract() throws Exception {
        String arr;
        
        conexion conexiones = new conexion();
        
        String sql = "select ID from tbl_caracteristicas order by id desc limit 1";
        try {
            ResultSet rs = conexiones.getSql(sql);
                    
            ArrayList ls = new ArrayList();
            while (rs.next()) {

                inventario inv = new inventario();
                    inv.setId(rs.getString("ID"));
                ls.add(inv);
            }

            arr = gs.toJson(ls);
            
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setmaxIdCaract(" + arr + ");");
        }catch (Exception e)
        {
            throw e;
        }
    }
    
    //Se agrega nueva caracteristica a la bdd.
    public void insertNuevaCaract() throws Exception {
        String error = "Exito";
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        String params[] = val.split("%");
                
        conexion conexiones = new conexion();
        
        String sql = "INSERT INTO tbl_caracteristicas (ID, ATNAM, ATBEZ, TIPO_CARACTER, COD_TIPO_DATO) VALUES(" + params[0] + ", '" + params[1] + "', '" + params[2] + "', " + params[3] + ", " + params[4] + ")";
        
        try{
            conexiones.putSql(sql);
            conexiones.disconnect();
        }catch (Exception e){
            error = e.getMessage();
        }
        
        String arr = gs.toJson(error);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("setErrorAddCaract("+ arr +")");
    }
    
    //Se elimina la caracteristica por ID
    public void deleteCaract() throws Exception {
        String error = "Exito";
        conexion conexiones = new conexion();
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        
        String sql = "DELETE FROM tbl_caracteristicas WHERE ID = '" + val + "' ";
        try{
            conexiones.putSql(sql);
        }catch(Exception e){
            error = e.getMessage();
        }
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.execute("setEliminarMateriales("+ error +")");
        conexiones.disconnect(); 
    }
    
    //Se Busca todos los materiales -> listar.xhtml
    public void getAllMatnr() throws Exception {
        String arr;
        
        conexion conexiones = new conexion();
        
        String sql = "CALL SP_MATCH_MATNR";
        try {
            ResultSet rs = conexiones.getSql(sql);
                    
            ArrayList ls = new ArrayList();
            while (rs.next()) {

                inventario inv = new inventario();
                    inv.setMATNR(rs.getString("COD_MATNR"));
                    inv.setVIN(rs.getString("VIN"));
                    inv.setMARCA(rs.getString("MARCA"));
                    inv.setMODELO(rs.getString("MODELO"));
                    inv.setNAVE(rs.getString("NAVE"));
                    inv.setVIAJE(rs.getString("VIAJE"));
                ls.add(inv);
            }

            arr = gs.toJson(ls);
            
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setGetAllMatnr(" + arr + ");");
        }catch (Exception e)
        {
            throw e;
        }
    }
    
    //Buscar por LIKE para encontrar materiales -> listar.xhtml
    //LIKE por VIN
    public void likeVinMat() throws Exception{
        String error = "Exito";
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        String params[] = val.split("%");
                
        conexion conexiones = new conexion();
        
        String sql = "SELECT cm.ID, cm.MATNR_ID, cm.CARACTER_ID, c.ATBEZ, cm.VALOR_CARACTER FROM tbl_caracteristicas_material cm, tbl_caracteristicas c WHERE cm.CARACTER_ID = c.ID AND cm.CARACTER_ID = " + params[0] + " AND cm.VALOR_CARACTER LIKE '%" + params[1] + "%'";
        
        try{
            
            ResultSet rs = conexiones.getSql(sql);
                    
            ArrayList ls = new ArrayList();
            
            while (rs.next()) {
                inventario inv = new inventario();
                inv.setId(rs.getString("ID"));
                inv.setMATNR(rs.getString("MATNR_ID"));
                inv.setCAR_TIPO(rs.getString("CARACTER_ID"));
                inv.setATBEZ(rs.getString("ATBEZ"));
                inv.setCAR_VALOR(rs.getString("VALOR_CARACTER"));
                ls.add(inv);
            }
            
            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setLikeMat("+ arr +")");
            
        }catch (Exception e){
            error = e.getMessage();
        }
    }
    
    //LIKE por Marca
    public void likeMarcaMat() throws Exception{
        String error = "Exito";
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        String params[] = val.split("%");
                
        conexion conexiones = new conexion();
        
        String sql = "SELECT cm.ID, cm.MATNR_ID, cm.CARACTER_ID, c.ATBEZ, cm.VALOR_CARACTER FROM tbl_caracteristicas_material cm, tbl_caracteristicas c" +
                    "WHERE cm.CARACTER_ID = c.ID AND cm.CARACTER_ID = 9 AND cm.VALOR_CARACTER LIKE '%" + params[1] + "%'";
        
        try{
            
            ResultSet rs = conexiones.getSql(sql);
                    
            ArrayList ls = new ArrayList();
            
            while (rs.next()) {
                inventario inv = new inventario();
                inv.setId(rs.getString("ID"));
                inv.setMATNR(rs.getString("MATNR_ID"));
                inv.setCAR_TIPO(rs.getString("CARACTER_ID"));
                inv.setATBEZ(rs.getString("ATBEZ"));
                inv.setCAR_VALOR(rs.getString("VALOR_CARACTER"));
                ls.add(inv);
            }
            
            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setLikeMat("+ arr +")");
            
        }catch (Exception e){
            error = e.getMessage();
        }
    }
    
    //LIKE por Descripcion Modelo
    public void likeDescrModeloMat() throws Exception{
        String error = "Exito";
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        String params[] = val.split("%");
                
        conexion conexiones = new conexion();
        
        String sql = "SELECT cm.ID, cm.MATNR_ID, cm.CARACTER_ID, c.ATBEZ, cm.VALOR_CARACTER FROM tbl_caracteristicas_material cm, tbl_caracteristicas c" +
                    "WHERE cm.CARACTER_ID = c.ID AND cm.CARACTER_ID = 36 AND cm.VALOR_CARACTER LIKE '%" + params[1] + "%'";
        
        try{
            
            ResultSet rs = conexiones.getSql(sql);
                    
            ArrayList ls = new ArrayList();
            
            while (rs.next()) {
                inventario inv = new inventario();
                inv.setId(rs.getString("ID"));
                inv.setMATNR(rs.getString("MATNR_ID"));
                inv.setCAR_TIPO(rs.getString("CARACTER_ID"));
                inv.setATBEZ(rs.getString("ATBEZ"));
                inv.setCAR_VALOR(rs.getString("VALOR_CARACTER"));
                ls.add(inv);
            }
            
            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setLikeMat("+ arr +")");
            
        }catch (Exception e){
            error = e.getMessage();
        }
    }
    
    //LIKE por Nave
    public void likeNaveMat() throws Exception{
        String error = "Exito";
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        String params[] = val.split("%");
                
        conexion conexiones = new conexion();
        
        String sql = "SELECT cm.ID, cm.MATNR_ID, cm.CARACTER_ID, c.ATBEZ, cm.VALOR_CARACTER FROM tbl_caracteristicas_material cm, tbl_caracteristicas c" +
                    "WHERE cm.CARACTER_ID = c.ID AND cm.CARACTER_ID = 30 AND cm.VALOR_CARACTER LIKE '%" + params[1] + "%'";
        
        try{
            
            ResultSet rs = conexiones.getSql(sql);
                    
            ArrayList ls = new ArrayList();
            
            while (rs.next()) {
                inventario inv = new inventario();
                inv.setId(rs.getString("ID"));
                inv.setMATNR(rs.getString("MATNR_ID"));
                inv.setCAR_TIPO(rs.getString("CARACTER_ID"));
                inv.setATBEZ(rs.getString("ATBEZ"));
                inv.setCAR_VALOR(rs.getString("VALOR_CARACTER"));
                ls.add(inv);
            }
            
            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setLikeMat("+ arr +")");
            
        }catch (Exception e){
            error = e.getMessage();
        }
    }
    
    //LIKE por Viaje
    public void likeViajeMat() throws Exception{
        String error = "Exito";
        String val = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jsonParam");
        String params[] = val.split("%");
                
        conexion conexiones = new conexion();
        
        String sql = "SELECT cm.ID, cm.MATNR_ID, cm.CARACTER_ID, c.ATBEZ, cm.VALOR_CARACTER FROM tbl_caracteristicas_material cm, tbl_caracteristicas c" +
                    "WHERE cm.CARACTER_ID = c.ID AND cm.CARACTER_ID = 39 AND cm.VALOR_CARACTER LIKE '%" + params[1] + "%'";
        
        try{
            
            ResultSet rs = conexiones.getSql(sql);
                    
            ArrayList ls = new ArrayList();
            
            while (rs.next()) {
                inventario inv = new inventario();
                inv.setId(rs.getString("ID"));
                inv.setMATNR(rs.getString("MATNR_ID"));
                inv.setCAR_TIPO(rs.getString("CARACTER_ID"));
                inv.setATBEZ(rs.getString("ATBEZ"));
                inv.setCAR_VALOR(rs.getString("VALOR_CARACTER"));
                ls.add(inv);
            }
            
            String arr = gs.toJson(ls);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("setLikeMat("+ arr +")");
            
        }catch (Exception e){
            error = e.getMessage();
        }
    }
    
    // Set and GETs
    
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAedat_sc() {
        return aedat_sc;
    }

    public void setAedat_sc(String aedat_sc) {
        this.aedat_sc = aedat_sc;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getRFID() {
        return RFID;
    }
    
    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getMATNR() {
        return MATNR;
    }

    public void setMATNR(String MATNR) {
        this.MATNR = MATNR;
    }

    public String getMODELO() {
        return MODELO;
    }

    public void setMODELO(String MODELO) {
        this.MODELO = MODELO;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getTIPO() {
        return TIPO;
    }
    
    public void setTIPO(String TIPO) {
        this.TIPO = TIPO;
    }
    
    public String getATBEZ() {
        return ATBEZ;
    }
    
    public void setATBEZ(String ATBEZ) {
        this.ATBEZ = ATBEZ;
    }
    
    public String getCAR_VALOR() {
        return CAR_VALOR;
    }
    
    public void setCAR_VALOR(String CAR_VALOR) {
        this.CAR_VALOR = CAR_VALOR;
    }
    
    public String getCAR_TIPO() {
        return CAR_TIPO;
    }
    
    public void setCAR_TIPO(String CAR_TIPO) {
        this.CAR_TIPO = CAR_TIPO;
    }
    
    public String getVIN() {
        return VIN;
    }
    
    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    /**
     * @return the MARCA
     */
    public String getMARCA() {
        return MARCA;
    }

    /**
     * @param MARCA the MARCA to set
     */
    public void setMARCA(String MARCA) {
        this.MARCA = MARCA;
    }

    /**
     * @return the NAVE
     */
    public String getNAVE() {
        return NAVE;
    }

    /**
     * @param NAVE the NAVE to set
     */
    public void setNAVE(String NAVE) {
        this.NAVE = NAVE;
    }

    /**
     * @return the VIAJE
     */
    public String getVIAJE() {
        return VIAJE;
    }

    /**
     * @param VIAJE the VIAJE to set
     */
    public void setVIAJE(String VIAJE) {
        this.VIAJE = VIAJE;
    }
    
        public String getN_EMBARQUE() {
        return N_EMBARQUE;
    }

    public void setN_EMBARQUE(String N_EMBARQUE) {
        this.N_EMBARQUE = N_EMBARQUE;
    }
    
    public String getLOTE() {
        return LOTE;
    }

    public void setLOTE(String LOTE) {
        this.LOTE = LOTE;
    }

    public String getMOTOR() {
        return MOTOR;
    }

    public void setMOTOR(String MOTOR) {
        this.MOTOR = MOTOR;
    }

    public String getCOLOR() {
        return COLOR;
    }

    public void setCOLOR(String COLOR) {
        this.COLOR = COLOR;
    }
    
    public String getTIPO_DATO() {
        return TIPO_DATO;
    }

    public void setTIPO_DATO(String TIPO_DATO) {
        this.TIPO_DATO = TIPO_DATO;
    }
}
