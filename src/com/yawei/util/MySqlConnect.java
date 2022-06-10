package com.yawei.util;

import org.json.JSONObject;

import javax.json.*;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.*;

import java.sql.*;
import java.util.HashMap;

import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;


public class MySqlConnect implements DatabaseConnect{

    private static String databaseUrl = "//localhost:3306/";
    private static String user = "root";
    private static String password = "0937513541";
    private static String database = "yawei_shopweb";
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
    public JsonArray getAllProducts(){
        JsonArrayBuilder result =createArrayBuilder();
        Statement sm = null;
        String sql = String.format("select p_no as id,p_name as name,p_inventory as inventory," +
                "p_price as price,p_url as url,p_introduction as intr,p_type as type," +
                "c_size as capacity,if(t_name='常溫','false','true') as isFreezing " +
                "from products inner join (product_capacity,tote_type) using(c_no,t_no) order by id");
        try {
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
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

        } catch(SQLException e) {
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            javax.json.JsonArray array = result.build();
            return array;
        }
    }//v
    @Override
    public JsonArray getProduct(String id) {
        JsonArrayBuilder result = createArrayBuilder();
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
            sm.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }finally {
            javax.json.JsonArray array = result.build();
            return array;
        }
    }//v
    @Override
    public JsonArray createProduct(JSONObject data) {
        Statement sm = null;
        String sql = String.format("insert into products values(null,'%s',%s,%s,%s,%s,'%s','%s','%s')",
                data.get("name"),data.get("inventory"),data.get("capacity"),
                data.get("tote_type"),data.get("price"),data.get("url"),
                data.get("introduction"),data.get("type"));
        System.out.println(sql);
        try {
            sm = this.conn.createStatement();
            sm.executeUpdate(sql);

        } catch(SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            JsonArray result = this.getAllProducts();
            return result;
        }

    }//v
    @Override
    public JsonArray updateProduct(JSONObject data) {
        Statement sm = null;
        String sql = String.format("update products set p_name='%s', p_price=%s, p_inventory=%s," +
                        " p_type='%s', p_introduction='%s' where p_no =%s",
                data.get("name"),data.get("price"),data.get("inventory"),data.get("type"),
                data.get("introduction"),data.get("id"));
        try {
            sm = this.conn.createStatement();
            sm.executeUpdate(sql);
        } catch(SQLException e) {
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            JsonArray result = this.getProduct(data.get("id").toString());
            return result;
        }

    }//v
    @Override
    public JsonArray getUserInfo(String id) {
        JsonArrayBuilder result = createArrayBuilder();
        Statement sm = null;
        String sql = String.format("select m_no as no,m_name as name,m_nickname as nickname,m_phone as phone,m_mail as email from members where m_no=%s",id);
        System.out.println(sql);
        try {
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
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

        } catch(SQLException e) {
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
    public void createUser(JSONObject data) {
        Statement sm = null;
        String sql = String.format("insert into members(m_name,m_nickname,m_mail,url) values('%s','%s','%s','%s')",data.get("name"),data.get("nickname"),data.get("email"),data.get("url"));
        try{
            sm = this.conn.createStatement();
            int rs = sm.executeUpdate(sql);
            System.out.println(rs);
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return;
        }
    }//v

    @Override
    public void updateUser(JSONObject data) {
        Statement sm =null;
        String sql = String.format("update members set m_name = '%s',m_nickname = '%s',m_phone = '%s' where m_no = %s ",
                data.get("name"),data.get("nickname"),data.get("phone"),data.get("id"));
        System.out.println(sql);
        try{
            sm = this.conn.createStatement();
            int rs = sm.executeUpdate(sql);
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return;
        }
    }//v

    @Override
    public JsonArray updateUserPW(JsonObject object) {
        return null;
    }//x

    @Override
    public boolean checkUserLogin(JsonObject object) {
        return false;
    }//x

    @Override
    public Boolean checkManagerLogin(JsonObject object) {
        return null;
    }


    @Override
    public int checkMail(String mail) {
        String sql = String.format("select m_no from members where m_mail = '%s'",mail);
        try{
            Statement sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
            while(rs.next()){
                return rs.getInt(1);
            }
        }catch ( SQLException e){
//            e.printStackTrace();
            return 0;
        }
        return 0;
    }//v


    @Override
    public JsonArray getCapacity() {
        JsonArrayBuilder result = createArrayBuilder();
        Statement sm = null;
        String sql = String.format("select * from product_capacity");
        try {
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
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
        Statement sm = null;
        String sql = String.format("select t_no as id,t_name as name,t_threshold as threshold,t_fare as fare from tote_type;");
        try {
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
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
        Statement sm = null;
        try{
            sm = this.conn.createStatement();
            String sql="";
            for(String key:object.keySet()) {
                sql = String.format("update tote_type set t_fare=%s , t_threshold=%s where t_name = '%s'"
                        ,object.getJSONObject(key).get("fare"),object.getJSONObject(key).get("threshold"),key );
                System.out.println("sql="+sql);
                sm.executeUpdate(sql);
            }
        }catch (SQLException e){
            e.printStackTrace();
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
        Statement sm = null;
        String sql = String.format("select pay_id as id,pay_name as name,pay_fee as fee from pay_type");
        try {
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
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

    @Override
    public JsonArray getNewAnno() {
        JsonArrayBuilder result = createArrayBuilder();
        Statement sm = null;
        String sql = String.format("select  a_no as id ,a_content as content from (select * from announcements order by a_no desc) as sub order by a_time desc limit 1");
        try {
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
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

    @Override
    public JsonArray getAllAnno() {
        JsonArrayBuilder result = createArrayBuilder();
        Statement sm = null;
        String sql = String.format("select a_no as id , a_content as content , if(a_time=(select Max(a_time) from announcements),'true','false') as target from announcements  order by a_time desc limit 6");
        try {
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
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
            return array;
        }
    }//v

    @Override
    public void updateAnno(JSONObject object) {
        Statement sm = null;
        try{
            sm = this.conn.createStatement();
            String sql = String.format("replace into announcements(a_no,a_content,a_time) values (%s,'%s',current_timestamp)"
                    ,this.isInt(object.get("id"))?object.get("id"):"null",object.get("content") );
            System.out.println("sql="+sql);
            sm.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public JsonArray getUserAddress(String id) {
        JsonArrayBuilder result = createArrayBuilder();
        Statement sm = null;
        String sql = String.format("select r_no as no ,r_name as name ,r_phone as phone ,r_address as address from address  where m_no = %s",id);
        try{
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while(rs.next()){
                JsonObjectBuilder obj = createObjectBuilder();
                for(int i = 1 ; i <= columnCount; i++) {
                    if(isInt(rs.getObject(i))) {
                        obj.add(rsmd.getColumnLabel(i),rs.getInt(i));
                    }else{
                        obj.add(rsmd.getColumnLabel(i),rs.getString(i)==null?"null":rs.getString(i));
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
            return array;
        }
    }

    @Override
    public void createAddress(JSONObject object) {
        Statement sm = null;
        String sql = String.format("insert into address (m_no,r_name,r_phone,r_address) values(%s,'%s','%s','%s')",
                object.get("id"),object.get("name"),object.get("phone"),object.get("address"));
        System.out.print(sql);
        try{
            sm = this.conn.createStatement();
            sm.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e ){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deleteAddress(JSONObject object) {
        Statement sm = null;
        String sql = String.format("delete from address where r_no = %s",
                object.get("no"));
        System.out.println(sql);
        try{
            sm = this.conn.createStatement();
            sm.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e ){
                e.printStackTrace();
            }
        }

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
