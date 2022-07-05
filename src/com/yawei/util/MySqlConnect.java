package com.yawei.util;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.json.*;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;


public class MySqlConnect implements DatabaseConnect{
    private static Properties properties = new Properties();
    static{
        try {
            InputStream in = MySqlConnect.class.getClassLoader().getResourceAsStream("dbconfig.properties");
            properties.load(in);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static String databaseUrl = properties.getProperty("databaseUrl");
    private static String user = properties.getProperty("user");
    private static String password = properties.getProperty("password");
    private static String database = properties.getProperty("database");
    private Connection conn;
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
    private Connection getConnection() {
        Connection conn = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(String.format("jdbc:mysql:%s%s?serverTimezone=GMT&user=%s&password=%s",databaseUrl,database,user,password));
            conn.setAutoCommit(false); //開啟交易模式
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally{
            return conn;
        }
    }

    public Connection getConn() {
        return conn;
    }

    private boolean isInt(Object object){

        String num = object.toString();
        if(num.length()>1&&num.substring(0,1).equals("0"))
            return false;
        try {
            int i = Integer.parseInt(object.toString());
        }catch (Exception e){
            return false;
        }
        return  true;
    } //v
    @Override
    public String getManagerHashPW( String managerId) {
        String result = null;
        PreparedStatement sm = null;
        String sql = String.format("select ma_hashPW from managers where ma_phone = ?");
        try{
            sm = this.conn.prepareStatement(sql);
            sm.setObject(1,managerId);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                result = rs.getString(1);

            }
        }catch (SQLException e ){
            e.printStackTrace();
        }
        finally {
            try {
                sm.close();
            }catch (SQLException e ){
                e.printStackTrace();
            }
        }
        return result;
    } //v
    @Override
    public void updateManagerHashPW(String pa , String managerId) {
        PreparedStatement sm = null;
        String sql = String.format("update managers set ma_hashPW = ? where ma_phone = ? ");
        try{
            sm = this.conn.prepareStatement(sql);
            sm.setObject(1,pa);
            sm.setObject(2,managerId);
            sm.executeUpdate();
            this.conn.commit();
        }catch (SQLException e){
            e.printStackTrace();
            try {
                if(this.conn!=null)
                    this.conn.rollback();//復原交易
            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }finally {
            try {
                sm.close();
            }catch (SQLException e ){
                e.printStackTrace();
            }
        }
    }//v
    @Override
    public int checkIdByMail(String mail) {
        PreparedStatement sm = null;
        String sql = String.format("select m_no from members where m_mail = ?");
        //,mail);
        try{
             sm = this.conn.prepareStatement(sql);
             sm.setObject(1,mail);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                return rs.getInt(1);
            }
        }catch ( SQLException e){
            e.printStackTrace();
            return 0;
        }
        return 0;
    }//v
    @Override
    public int checkIdByPhone(String phone) {
        PreparedStatement sm =null;
        String sql = String.format("select m_no from members where m_phone = ?");

        try{
            sm = this.conn.prepareStatement(sql);
            sm.setObject(1,phone);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                return rs.getInt(1);
            }
        }catch ( SQLException e){
            e.printStackTrace();
            return 0;
        }
        return 0;
    }//v

    @Override
    public JsonArray getCapacity() {
        JsonArrayBuilder result = createArrayBuilder();
        PreparedStatement sm = null;
        String sql = String.format("select * from product_capacity");
        try {
            sm = this.conn.prepareStatement(sql);
            ResultSet rs = sm.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rs.getMetaData().getColumnCount();
            while(rs.next()) {
                JsonObjectBuilder obj = createObjectBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    if(this.isInt(rs.getObject(i))){
                        obj.add(rsmd.getColumnLabel(i),rs.getInt(i));
                    }else{
                        obj.add(rsmd.getColumnLabel(i),rs.getString(i)==null?"null":rs.getString(i) );
                    }
                }
                result.add(obj);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            JsonArray array = result.build();
            System.out.println(array);
            return array;
        }
    }//v
    @Override
    public JsonArray getTote() {
        JsonArrayBuilder result = createArrayBuilder();
        PreparedStatement sm = null;
        String sql = String.format("select t_no as id,t_name as name,t_threshold as threshold,t_fare as fare from tote_type;");
        try {
            sm = this.conn.prepareStatement(sql);
            ResultSet rs = sm.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rs.getMetaData().getColumnCount();
            while(rs.next()) {
                JsonObjectBuilder obj = createObjectBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    if(this.isInt(rs.getObject(i))){
                        obj.add(rsmd.getColumnLabel(i),rs.getInt(i) );
                    }else{
                        obj.add(rsmd.getColumnLabel(i),rs.getString(i)==null?"null":rs.getString(i) );
                    }
                }
                result.add(obj);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            JsonArray array = result.build();
            System.out.println(array);
            return array;
        }    }//v
    @Override
    public void updateTote(JSONObject object) {
        PreparedStatement sm = null;
        try{
            String sql="";
            for(String key:object.keySet()) {

                sql = String.format("update tote_type set t_fare=? , t_threshold=? where t_name = ?");
                sm = this.conn.prepareStatement(sql);
                sm.setObject(1,object.getJSONObject(key).get("fare"));
                sm.setObject(2,object.getJSONObject(key).get("threshold"));
                sm.setObject(3,key);
                sm.executeUpdate();
            }
            this.conn.commit();
        }catch (SQLException e){
            e.printStackTrace();
            try {
                if(this.conn!=null)
                    this.conn.rollback();//復原交易
            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }//v
    @Override
    public JsonArray getPay() {
        JsonArrayBuilder result = createArrayBuilder();
        PreparedStatement sm = null;
        String sql = String.format("select pay_id as id,pay_name as name,pay_fee as fee from pay_type");
        try {
            sm = this.conn.prepareStatement(sql);
            ResultSet rs = sm.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rs.getMetaData().getColumnCount();
            while(rs.next()) {
                JsonObjectBuilder obj = createObjectBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    if(this.isInt(rs.getObject(i))){
                        obj.add(rsmd.getColumnLabel(i),rs.getInt(i) );
                    }else{
                        obj.add(rsmd.getColumnLabel(i),rs.getString(i)==null?"null":rs.getString(i) );
                    }
                }
                result.add(obj);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            JsonArray array = result.build();
            System.out.println(array);
            return array;
        }
    }//v
//    @Override
//    public JsonArray getNewAnno() {
//        JsonArrayBuilder result = createArrayBuilder();
//        PreparedStatement sm = null;
//        String sql = String.format("select  a_no as id ,a_content as content from (select * from announcements order by a_no desc) as sub order by a_time desc limit 1");
//        try {
//            sm = this.conn.prepareStatement(sql);
//            ResultSet rs = sm.executeQuery();
//            ResultSetMetaData rsmd = rs.getMetaData();
//            int columnCount = rs.getMetaData().getColumnCount();
//            while(rs.next()) {
//                JsonObjectBuilder obj = createObjectBuilder();
//                for (int i = 1; i <= columnCount; i++) {
//                    if(this.isInt(rs.getObject(i))){
//                        obj.add(rsmd.getColumnLabel(i),rs.getInt(i) );
//                    }else{
//                        obj.add(rsmd.getColumnLabel(i),rs.getString(i)==null?"null":rs.getString(i) );
//                    }
//                }
//                result.add(obj);
//            }
//        }catch (SQLException e){
//            e.printStackTrace();
//        }finally {
//            try{
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//            JsonArray array = result.build();
//            System.out.println(array);
//            return array;
//        }
//    }//v
//    @Override
//    public JsonArray getAllAnno() {
//        JsonArrayBuilder result = createArrayBuilder();
//        PreparedStatement sm = null;
//        String sql = String.format("select a_no as id , a_content as content , if(a_time=(select Max(a_time) from announcements),'true','false') as target from announcements  order by a_time desc limit 6");
//        try {
//            sm = this.conn.prepareStatement(sql);
//            ResultSet rs = sm.executeQuery();
//            ResultSetMetaData rsmd = rs.getMetaData();
//            int columnCount = rs.getMetaData().getColumnCount();
//            while(rs.next()) {
//                JsonObjectBuilder obj = createObjectBuilder();
//                for (int i = 1; i <= columnCount; i++) {
//                    if(this.isInt(rs.getObject(i))){
//                        obj.add(rsmd.getColumnLabel(i),rs.getInt(i) );
//                    }else{
//                        obj.add(rsmd.getColumnLabel(i),rs.getString(i)==null?"null":rs.getString(i) );
//                    }
//                }
//                result.add(obj);
//            }
//        }catch (SQLException e){
//            e.printStackTrace();
//        }finally {
//            try{
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//            JsonArray array = result.build();
//            System.out.println(array);
//            return array;
//        }
//    }//v
//    @Override
//    public void updateAnno(JSONObject object) {
//        PreparedStatement sm = null;
//        String sql = null;
//        try{
//            if(this.isInt(object.get("id"))){
//                sql = String.format("replace into announcements(a_no,a_content,a_time) values (?,?,current_timestamp)");
//                sm = this.conn.prepareStatement(sql);
//                sm.setObject(1, object.get("id"));
//                sm.setObject(2,object.get("content"));
//            }else{
//                sql = String.format("replace into announcements(a_content,a_time) values (?,current_timestamp)");
//                sm = this.conn.prepareStatement(sql);
//                sm.setObject(1,object.get("content"));
//            }
//            sm.executeUpdate();
//            this.conn.commit();
//        }catch (SQLException e){
//            e.printStackTrace();
//            try {
//                if(this.conn!=null)
//                    this.conn.rollback();//復原交易
//            }catch (SQLException ex){
//                ex.printStackTrace();
//            }
//        }finally {
//            try{
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//        }
//    }//v

    @Override
    public boolean checkPhone(String id){
        boolean result = false;
        PreparedStatement sm = null;
        String sql = String.format("select m_phone from members where m_no = ?");
        //,id);
        try{
            sm = this.conn.prepareStatement(sql);
            sm.setObject(1,id);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                result = rs.getString(1)==null?false:true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();

            }catch (SQLException e){
                e.printStackTrace();
            }

        }
        return result;
    } //v
    @Override
    public boolean checkPhoneExist(String phone){
        boolean result = false;
        PreparedStatement sm = null;
        String sql = String.format("select m_name from members where m_phone = ?");
        //,phone);
        try{
            sm = this.conn.prepareStatement(sql);
            sm.setObject(1,phone);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                result = rs.getString(1)==null?false:true;
                System.out.println("電話查詢結果："+result); //不存在 false  存在true
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();

            }catch (SQLException e){
                e.printStackTrace();
            }

        }
        return result;
    }
    public int getProductPrice(int productNo){
        int result = 0;
        PreparedStatement sm = null;
        String sql = String.format("select p_price from products where p_no = ?");
        try{
            sm = this.conn.prepareStatement(sql);
            sm.setObject(1,productNo);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                result = rs.getInt(1);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();

            }catch (SQLException e){
                e.printStackTrace();
            }

        }
        return result;
    }
    public int[] getToteInfo(int toteNo){
        int[] result = new int[2];
        PreparedStatement sm = null;
        String sql = String.format("select t_threshold,t_fare from tote_type where t_no = ?");
        try{
            sm = this.conn.prepareStatement(sql);
            sm.setObject(1,toteNo);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                result[0] = rs.getInt(1);
                result[1] = rs.getInt(2);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();

            }catch (SQLException e){
                e.printStackTrace();
            }

        }
        return result;
    }
    public int getPayFee(int payId){
        int result = 0;
        PreparedStatement sm = null;
        String sql = String.format("select pay_fee from pay_type where pay_id = ?");
        try{
            sm = this.conn.prepareStatement(sql);
            sm.setObject(1,payId);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                result = rs.getInt(1);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();

            }catch (SQLException e){
                e.printStackTrace();
            }

        }
        return result;
    }
}