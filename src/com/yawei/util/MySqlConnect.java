package com.yawei.util;

import javax.json.*;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

import java.sql.*;


public class MySqlConnect implements DatabaseConnect{

    private static String databaseUrl = "//localhost:3306/";
    private static String user = "root";
    private static String password = "0937513541";
    private static String database = "yawei_shopweb";
    private Connection conn ;
    private static MySqlConnect mysql;

    public MySqlConnect(){
        this.conn = this.getConnection();
    }

    public static MySqlConnect getMySql(){
        if(mysql==null){
            mysql = new MySqlConnect();
        }
        return mysql;
    }

    @Override
    public Connection getConnection() {
        Connection conn = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(String.format("jdbc:mysql:%s%s?serverTimezone=GMT&user=%s&password=%s",databaseUrl,database,user,password));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally{
            return conn;
        }
    }

    private boolean isInt(Object object){
        try {
            int i = Integer.parseInt(object.toString());
        }catch (Exception e){
            return false;
        }
        return  true;
    }

    @Override
    public JsonArray getAllProducts() {
        javax.json.JsonArrayBuilder result =javax.json.Json.createArrayBuilder();
        String sql = "select p_no as id,p_name as name,p_inventory as inventory," +
                "p_price as price,p_url as url,p_introduction as intr,p_type as type," +
                "c_size as capacity,if(t_name='常溫','false','true') as isFreezing " +
                "from products inner join (product_capacity,tote_type) using(c_no,t_no) order by id";
        try {
            Statement sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rs.getMetaData().getColumnCount();
            while(rs.next()) {
                javax.json.JsonObjectBuilder obj = javax.json.Json.createObjectBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    if(this.isInt(rs.getObject(i))){
                        obj.add(rsmd.getColumnLabel(i),rs.getInt(i) );
                    }else{
                        obj.add(rsmd.getColumnLabel(i),rs.getString(i) );
                    }
                }
                result.add(obj);
                }
        } catch(SQLException e) {
            e.printStackTrace();
        }finally {
            javax.json.JsonArray array = result.build();
            return array;
        }
    }
    @Override
    public JsonArray getProduct(String id) {
        javax.json.JsonArrayBuilder result =javax.json.Json.createArrayBuilder();
        String sql = String.format("select p_no as id,p_name as name,p_inventory as inventory," +
                "p_price as price,p_url as url,p_introduction as intr,p_type as type," +
                "c_size as capacity,if(t_name='常溫','false','true') as isFreezing " +
                "from products inner join (product_capacity,tote_type) using(c_no,t_no) where p_no = %s",id);
        try {
            Statement sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rs.getMetaData().getColumnCount();
            while(rs.next()) {
                javax.json.JsonObjectBuilder obj = javax.json.Json.createObjectBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    if(this.isInt(rs.getObject(i))){
                        obj.add(rsmd.getColumnLabel(i),rs.getInt(i) );
                    }else{
                        obj.add(rsmd.getColumnLabel(i),rs.getString(i) );
                    }
                }
                result.add(obj);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }finally {
            javax.json.JsonArray array = result.build();
            return array;
        }
    }

    @Override
    public JsonArray createProduct(JsonObject object) {
        return null;
    }

    @Override
    public JsonArray updateProduct(JsonObject object) {
        return null;
    }

    @Override
    public JsonArray getUserInfo(String userPhone) {
        return null;
    }

    @Override
    public void createUser(JsonObject object) {

    }

    @Override
    public JsonArray updateUser(JsonObject object) {
        return null;
    }

    @Override
    public JsonArray updateUserPW(JsonObject object) {
        return null;
    }

    @Override
    public boolean checkUserLogin(JsonObject object) {
        return false;
    }

    @Override
    public Boolean checkManagerLogin(JsonObject object) {
        return null;
    }

    @Override
    public boolean checkPhone(String phone) {
        return false;
    }

    @Override
    public JsonArray getCapacity() {
        return null;
    }

    @Override
    public JsonArray getTote() {
        return null;
    }

    @Override
    public JsonArray updateTote(JsonObject object) {
        return null;
    }

    @Override
    public JsonArray getPay() {
        return null;
    }

    @Override
    public JsonArray getNewAnno() {
        return null;
    }

    @Override
    public JsonArray getAllAnno() {
        return null;
    }

    @Override
    public void updateAnno() {

    }

    @Override
    public JsonArray getUserAddress(String phone) {
        return null;
    }

    @Override
    public JsonArray createAddress(JsonObject object) {
        return null;
    }

    @Override
    public JsonArray deleteAddress(JsonObject object) {
        return null;
    }

    @Override
    public JsonArray getNewOrderListNo() {
        return null;
    }

    @Override
    public void createOrderList(JsonObject object) {

    }

    @Override
    public JsonArray getOrderListByPhone(String phone) {
        return null;
    }

    @Override
    public JsonArray getOrderListByPhoneforManager(String phone) {
        return null;
    }

    @Override
    public JsonArray getOrderLsitByDate(String date) {
        return null;
    }

    @Override
    public JsonArray getOrderListByDays(String days) {
        return null;
    }

    @Override
    public JsonArray getOrderListByNo(String no) {
        return null;
    }

    @Override
    public JsonArray getOrderListByNoforManager(String no) {
        return null;
    }

    @Override
    public JsonArray cancelOrder(JsonObject object) {
        return null;
    }

    @Override
    public JsonArray updateOrder(JsonObject object) {
        return null;
    }
}
