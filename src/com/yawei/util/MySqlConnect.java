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

//    @Override
//    public JsonArray getAllProducts(){
//        JsonArrayBuilder result =createArrayBuilder();
//        PreparedStatement sm = null;
//        String sql = String.format("select p_no as id,p_name as name,p_inventory as inventory," +
//                "p_price as price,p_url as url,p_introduction as intr,p_type as type," +
//                "c_size as capacity,if(t_name='常溫','false','true') as isFreezing " +
//                "from products inner join (product_capacity,tote_type) using(c_no,t_no) order by id");
//        try {
//            sm = this.conn.prepareStatement(sql);
//            ResultSet rs = sm.executeQuery();
//            ResultSetMetaData rsmd = rs.getMetaData();
//            int columnCount = rs.getMetaData().getColumnCount();
//            while(rs.next()) {
//                JsonObjectBuilder obj = createObjectBuilder();
//                for (int i = 1; i <= columnCount; i++) {
//                    if(this.isInt(rs.getObject(i))){
//
//                        obj.add(rsmd.getColumnLabel(i),rs.getInt(i) );
//                    }else{
//                        obj.add(rsmd.getColumnLabel(i),rs.getString(i)==null?"null":rs.getString(i) );
//                    }
//                }
//                result.add(obj);
//                }
//
//        } catch(SQLException e) {
//            e.printStackTrace();
//        }finally {
//            try{
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//            javax.json.JsonArray array = result.build();
//            System.out.println(array);
//            return array;
//        }
//    }//v

//    @Override
//    public JsonArray getProduct(String id) {
//        JsonArrayBuilder result = createArrayBuilder();
//        String sql = String.format("select p_no as id,p_name as name,p_inventory as inventory," +
//                "p_price as price,p_url as url,p_introduction as intr,p_type as type," +
//                "c_size as capacity,if(t_name='常溫','false','true') as isFreezing " +
//                "from products inner join (product_capacity,tote_type) using(c_no,t_no) where p_no = ?");
//        PreparedStatement sm =null;
//        try {
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,id);
//            ResultSet rs = sm.executeQuery();
//
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
//            sm.close();
//        } catch(SQLException e) {
//            e.printStackTrace();
//        }finally {
//            javax.json.JsonArray array = result.build();
//            System.out.println(array);
//            return array;
//        }
//    }//v
    public int getNewUserNo() {
        PreparedStatement sm = null;
        String sql=String.format("select m_no from members order by m_no desc limit 1");
        int id = 1;
        try{
            sm = this.conn.prepareStatement(sql);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                id = rs.getInt(1) + 1;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return id;
    }//v
    @Override
    public int getNewProductNo() {

        PreparedStatement sm = null;
        String sql=String.format("select p_no from products order by p_no desc limit 1");
        int id = 1;
        try{
            sm = this.conn.prepareStatement(sql);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                id = rs.getInt(1) + 1;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return id;
    }//v
//    @Override
//    public JsonArray createProduct(JSONObject data,String path) {
//        PreparedStatement sm = null;
//        String sql = String.format("insert into products values(null,?,?,?,?,?,?,?,?)");
//        System.out.println(sql);
//        try {
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,data.get("name"));
//            sm.setObject(2,data.get("inventory"));
//            sm.setObject(3,data.get("capacity"));
//            sm.setObject(4,data.get("tote_type"));
//            sm.setObject(5,data.get("price"));
//            sm.setObject(6,path);
//            sm.setObject(7,data.get("introduction"));
//            sm.setObject(8,data.get("type"));
//            sm.executeUpdate();
//            this.conn.commit(); // 儲存變更
//        } catch(SQLException e) {
//            e.printStackTrace();
//            try {
//                if(this.conn!=null)
//                    this.conn.rollback();//復原交易
//            }catch (SQLException ex){
//                ex.printStackTrace();
//            }
//        }finally {
//            try {
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//            JsonArray result = this.getAllProducts();
//            System.out.println(result);
//            return result;
//        }
//
//    }//v
//    @Override
//    public JsonArray updateProduct(JSONObject data) {
//        PreparedStatement sm = null;
//        String sql = String.format("update products set p_name=?, p_price=?, p_inventory=?," +
//                        " p_type=?, p_introduction=? where p_no =?");
//        try {
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,data.get("name"));
//            sm.setObject(2,data.get("price"));
//            sm.setObject(3,data.get("inventory"));
//            sm.setObject(4,data.get("type"));
//            sm.setObject(5,data.get("introduction"));
//            sm.setObject(6,data.get("id"));
//            sm.executeUpdate();
//            this.conn.commit();
//        } catch(SQLException e) {
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
//            JsonArray result = this.getProduct(data.get("id").toString());
//            System.out.println(result);
//            return result;
//        }
//
//    }//v
//    @Override
//    public JsonArray getUserInfo(String id) {
//        JsonArrayBuilder result = createArrayBuilder();
//        PreparedStatement sm = null;
//        String sql = String.format("select m_no as no,m_name as name,m_nickname as nickname,m_phone as phone,m_mail as email from members where m_no=?");
//        try {
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,id);
//            ResultSet rs = sm.executeQuery();
//            ResultSetMetaData rsmd = rs.getMetaData();
//            int columnCount = rs.getMetaData().getColumnCount();
//            while(rs.next()) {
//                JsonObjectBuilder obj = createObjectBuilder();
//                obj.add("no",rs.getInt("no"));
//                obj.add("name",rs.getString("name"));
//                obj.add("nickname",rs.getString("nickname"));
//                obj.add("phone",rs.getString("phone")==null?"":rs.getString("phone"));
//                obj.add("email",rs.getString("email"));
//                result.add(obj);
//            }
//
//        } catch(SQLException e) {
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
//
//    }//v
//    @Override
//    public JsonArray searchUsers(String keyword) {
//        JsonArrayBuilder result = createArrayBuilder();
//        PreparedStatement sm = null;
//        String sql = String.format("select m_no as no,m_name as name,m_phone as phone,m_mail as email,url as url from members where m_name like ? or m_phone like ? or m_mail like ?");
//        try {
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,"%"+keyword+"%");
//            sm.setObject(2,"%"+keyword+"%");
//            sm.setObject(3,"%"+keyword+"%");
//            ResultSet rs = sm.executeQuery();
//            ResultSetMetaData rsmd = rs.getMetaData();
//            int columnCount = rs.getMetaData().getColumnCount();
//            while(rs.next()) {
//                JsonObjectBuilder obj = createObjectBuilder();
//                obj.add("no",rs.getInt("no"));
//                obj.add("name",rs.getString("name"));
//                obj.add("phone",rs.getString("phone")==null?"":rs.getString("phone"));
//                obj.add("email",rs.getString("email")==null?"":rs.getString("email"));
//                obj.add("url",rs.getString("url")==null?"":rs.getString("url"));
//                result.add(obj);
//            }
//            System.out.println("result:"+result);
//        } catch(SQLException e) {
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
//
//    }//v


//
//    @Override
//    public void createUser(HashMap<String,String> user) {
//        PreparedStatement sm = null;
//        String sql = String.format("insert into members(m_name,m_nickname,m_phone,m_mail,m_hashPW) values(?,?,?,?,?)");
//        try{
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,user.get("name"));
//            sm.setObject(2,user.get("nickname"));
//            sm.setObject(3,user.get("phone"));
//            sm.setObject(4,user.get("mail"));
//            sm.setObject(5,user.get("hashPW"));
//            int rs = sm.executeUpdate();
//            this.conn.commit();
//        }catch(SQLException e){
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
//            return;
//        }
//    }//v
//
//    @Override
//    public void updateUser(JSONObject data) {
//        PreparedStatement sm =null;
//        String sql = String.format("update members set m_name = ?,m_nickname = ?,m_mail = ? where m_no = ? ");
//
//        System.out.println(sql);
//        try{
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,data.get("name"));
//            sm.setObject(2,data.get("nickname"));
//            sm.setObject(3,data.get("email"));
//            sm.setObject(4,data.get("id"));
//            int rs = sm.executeUpdate();
//            this.conn.commit();
//        }catch(SQLException e){
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
//            return;
//        }
//    }//v
//    @Override
//    public void updateUserHashPW(String pa,int userId) {
//        PreparedStatement sm = null;
//        String sql = String.format("update members set m_hashPW = ? where m_no = ? ");
//        try{
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,pa);
//            sm.setObject(2,userId);
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
//            try {
//                sm.close();
//            }catch (SQLException e ){
//                e.printStackTrace();
//            }
//        }
//    }//v
//    public static void main(String[] args) {
//        System.out.println(MySqlConnect.getMySql().getAllProducts());
//        System.out.println(MySqlConnect.getMySql().getProduct("1"));
//        System.out.println(MySqlConnect.getMySql().getNewProductNo());
//        JSONObject obj1 = new JSONObject("{\"name\":\"測試\",\"inventory\":20,\"capacity\":1,\"tote_type\":2,\"price\":666,\"introduction\":\"介紹！！！\",\"type\":\"true\"}");
//        MySqlConnect.getMySql().createProduct(obj1,"../static/products/product1.jpg");
//        JSONObject obj2 = new JSONObject("{\"id\":13,\"name\":\"測試222\",\"inventory\":40,\"price\":656,\"introduction\":\"介紹！！！\",\"type\":\"true\"}");
//        MySqlConnect.getMySql().updateProduct(obj2);
//        System.out.println(MySqlConnect.getMySql().getUserInfo("12"));
//        System.out.println(MySqlConnect.getMySql().searchUsers("09"));
//        HashMap obj3 = new HashMap();
//        obj3.put("name","ff");
//        obj3.put("nickname","ff");
//        obj3.put("phone","0937513566");
//        obj3.put("mail","ff");
//        obj3.put("hashPW","ff");
//        MySqlConnect.getMySql().createUser(obj3);
//        JSONObject obj4 = new JSONObject("{\"id\":15,\"name\":\"測試2aaa2\",\"nickname\":\"daaaa\",\"email\":dhj2@askldhjkaj}");
//        MySqlConnect.getMySql().updateUser(obj4);
//        MySqlConnect.getMySql().updateUserHashPW("djskhdkj",15);
//    }
//    @Override
//    public String getMemberHashPW(String memberPhone) {
//        String result = null;
//        PreparedStatement sm = null;
//        String sql = String.format("select m_hashPW from members where m_phone = ?");
//        try{
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,memberPhone);
//            ResultSet rs = sm.executeQuery();
//            while(rs.next()){
//                result = rs.getString(1);
//            }
//        }catch (SQLException e ){
//            e.printStackTrace();
//        }
//        finally {
//            try {
//                sm.close();
//            }catch (SQLException e ){
//                e.printStackTrace();
//            }
//        }
//        return result;
//
//    }//v
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
    @Override
    public JsonArray getNewAnno() {
        JsonArrayBuilder result = createArrayBuilder();
        PreparedStatement sm = null;
        String sql = String.format("select  a_no as id ,a_content as content from (select * from announcements order by a_no desc) as sub order by a_time desc limit 1");
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
    @Override
    public JsonArray getAllAnno() {
        JsonArrayBuilder result = createArrayBuilder();
        PreparedStatement sm = null;
        String sql = String.format("select a_no as id , a_content as content , if(a_time=(select Max(a_time) from announcements),'true','false') as target from announcements  order by a_time desc limit 6");
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
    @Override
    public void updateAnno(JSONObject object) {
        PreparedStatement sm = null;
        String sql = null;
        try{
            if(this.isInt(object.get("id"))){
                sql = String.format("replace into announcements(a_no,a_content,a_time) values (?,?,current_timestamp)");
                sm = this.conn.prepareStatement(sql);
                sm.setObject(1, object.get("id"));
                sm.setObject(2,object.get("content"));
            }else{
                sql = String.format("replace into announcements(a_content,a_time) values (?,current_timestamp)");
                sm = this.conn.prepareStatement(sql);
                sm.setObject(1,object.get("content"));
            }
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
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }//v
    @Override
    public JsonArray getUserAddress(String id) {
        JsonArrayBuilder result = createArrayBuilder();
        PreparedStatement sm = null;
        String sql = String.format("select r_no as no ,r_name as name ,r_phone as phone ,r_address as address from address  where m_no = ?");
        try{
            sm = this.conn.prepareStatement(sql);
            sm.setObject(1,id);
            ResultSet rs = sm.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while(rs.next()){
                JsonObjectBuilder obj = createObjectBuilder();
                obj.add("no",rs.getInt("no"));
                obj.add("name",rs.getString("name"));
                obj.add("phone",rs.getString("phone")==null?"":rs.getString("phone"));
                obj.add("address",rs.getString("address"));
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
    public void createAddress(JSONObject object) {
        PreparedStatement sm = null;
        String sql = String.format("insert into address (m_no,r_name,r_phone,r_address) values(?,?,?,?)");

        System.out.print(sql);
        try{
            sm = this.conn.prepareStatement(sql);
            sm.setObject(1,object.get("id"));
            sm.setObject(2,object.get("name"));
            sm.setObject(3,object.get("phone"));
            sm.setObject(4,object.get("address"));
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
            try{
                sm.close();
            }catch (SQLException e ){
                e.printStackTrace();
            }
        }
    }//v
    @Override
    public void deleteAddress(JSONObject object) {
        PreparedStatement sm = null;
        String sql = String.format("delete from address where r_no = ?");
        try{
            sm = this.conn.prepareStatement(sql);
            sm.setObject(1,object.get("no"));
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
            try{
                sm.close();
            }catch (SQLException e ){
                e.printStackTrace();
            }
        }

    }//v
    @Override
    public int getNewOrderListNo() {
        PreparedStatement sm = null;
        String sql=String.format("select o_no from order_list order by o_no desc limit 1");
        int id = 1;
        try{
            sm = this.conn.prepareStatement(sql);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                id = rs.getInt(1) + 1;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return id;
    } //v
//    public void createOrderList(OrderList object) {
//        int no = object.getNo();
//        PreparedStatement sm = null;
//        String sql = String.format("insert into order_list(o_no,m_no,pay_id,t_no,o_recipient,o_total) values(?,?,?,?,?,?)");
//        try{
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,no);
//            sm.setObject(2,object.getUserid());
//            sm.setObject(3,object.getPayID());
//            sm.setObject(4,object.getToteNo());
//            sm.setObject(5,object.getRecipient());
//            sm.setObject(6,object.getTotal());
//            sm.executeUpdate();
//            Object json = object.getProducts();
//            JSONArray products = new JSONArray(json.toString());
//            for(Object obj :products){
//                JSONObject product = new JSONObject(obj.toString());
//                sql = String.format("insert into order_products values(?,?,?,?)");
//                sm = this.conn.prepareStatement(sql);
//                sm.setObject(1,no);
//                sm.setObject(2,product.getInt("id"));
//                sm.setObject(3,product.getInt("amount"));
//                sm.setObject(4,product.getInt("price"));
//                sm.executeUpdate();
//            }
//            this.conn.commit();
//            System.out.println("新增完成");
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
//    } //v
//    @Override
//    public JSONArray getOrderListByMemberId(String id) {
//        PreparedStatement sm = null;
//        String sql = String.format("select * from" +
//                "(select o_no as no," +
//                "m_no as id," +
//                "m_phone as phone," +
//                "pay_name as payType," +
//                "o_orderdate as orderDate," +
//                "o_sendno as sendNo," +
//                "o_senddate as sendDate," +
//                "o_type as type," +
//                "o_recipient as recipient," +
//                "o_total as total from" +
//                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
//                "inner join" +
//                "(select o_no as no," +
//                "p_no," +
//                "p_name,c_size ," +
//                "b_price ," +
//                "b_num  from" +
//                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
//                ") as temp4 using(no) where id = ? order by no");
//        JSONArray lists =null;
//        try{
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,id);
//            ResultSet rs = sm.executeQuery();
//            HashMap<String,HashMap> result  = new HashMap();
//            ResultSetMetaData rsmd =  rs.getMetaData();
//            int columnCount = rsmd.getColumnCount();
//            while(rs.next()){
//                String no = rs.getString("no");
//                if(!result.containsKey(no)){//訂單編號 key 不存在
//                    HashMap<String,Object> temp = new HashMap();
//                    temp.put("no",rs.getInt("no"));
//                    temp.put("phone",rs.getString("phone"));
//                    temp.put("id",rs.getInt("id"));
//                    temp.put("payType",rs.getString("payType"));
//                    temp.put("orderDate",rs.getDate("orderDate"));
//                    temp.put("sendNo",rs.getString("sendNo")==null?"":rs.getString("sendNo"));
//                    temp.put("sendDate",rs.getDate("sendDate")==null?"尚未出貨":rs.getString("sendDate"));
//                    temp.put("type",rs.getString("type"));
//                    temp.put("recipient",rs.getString("recipient"));
//                    temp.put("total",rs.getInt("total"));
//                    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
//                    HashMap<String,Object> product = new HashMap();
//                    product.put("id",rs.getInt("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getInt("c_size"));
//                    product.put("price",rs.getInt("b_price"));
//                    product.put("amount",rs.getInt("b_num"));
//                    list.add(product);
//                    temp.put("productsList",list);
//                    result.put(no,temp);
//                }else{// 訂單編號 key已存在 插入新品項資料即可
//                    ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) result.get(no).get("productsList");
//                    HashMap<String,String> product = new HashMap();
//                    product.put("id",rs.getString("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getString("c_size"));
//                    product.put("price",rs.getString("b_price"));
//                    product.put("amount",rs.getString("b_num"));
//                    list.add(product);
//                }
//            }
//            lists = new JSONArray();
//            for(String key : result.keySet()){
//                lists.put(result.get(key));
//            }
//            System.out.print("lists="+lists);
//        }catch(SQLException e){
//            e.printStackTrace();
//        }finally {
//            try{
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//            return lists;
//        }
//    }//v
//    @Override
//    public JSONArray getOrderListByMemberIdforManager(String id) {
//        PreparedStatement sm = null;
//        String sql = String.format("select * from" +
//                "(select o_no as no," +
//                "m_no as id," +
//                "m_phone as phone," +
//                "m_name as name," +
//                "pay_no as payNo," +
//                "o_remark as remark," +
//                "pay_name as payType," +
//                "o_orderdate as orderDate," +
//                "o_sendno as sendNo," +
//                "o_senddate as sendDate," +
//                "o_type as type," +
//                "o_recipient as recipient," +
//                "o_total as total from" +
//                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
//                "inner join" +
//                "(select o_no as no," +
//                "p_no," +
//                "p_name,c_size ," +
//                "b_price ," +
//                "b_num  from" +
//                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
//                ") as temp4 using(no) where id = ? order by orderDate");
//
//        JSONArray lists = null;
//        try {
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,id);
//            ResultSet rs = sm.executeQuery();
//            HashMap<String, HashMap> result = new HashMap();
//            ResultSetMetaData rsmd = rs.getMetaData();
//            int columnCount = rsmd.getColumnCount();
//            while (rs.next()) {
//                String no = rs.getString("no");
//                if (!result.containsKey(no)) {//訂單編號 key 不存在
//                    HashMap<String, Object> temp = new HashMap();
//                    temp.put("no", rs.getInt("no"));
//                    temp.put("phone", rs.getString("phone"));
//                    temp.put("id", rs.getInt("id"));
//                    temp.put("payType", rs.getString("payType"));
//                    temp.put("orderDate", rs.getDate("orderDate"));
//                    temp.put("sendNo", rs.getString("sendNo") == null ? "" : rs.getString("sendNo"));
//                    temp.put("sendDate", rs.getString("sendDate") == null ? "" : rs.getString("sendDate"));
//                    temp.put("type", rs.getString("type"));
//                    temp.put("recipient", rs.getString("recipient"));
//                    temp.put("total", rs.getInt("total"));
//                    temp.put("name", rs.getString("name"));
//                    temp.put("payNo", rs.getString("payNo") == null ? "" : rs.getString("payNo"));
//                    temp.put("remark", rs.getString("remark") == null ? "" : rs.getString("remark"));
//                    ArrayList<HashMap<String, Object>> list = new ArrayList<>();
//                    HashMap<String, Object> product = new HashMap();
//                    product.put("id", rs.getInt("p_no"));
//                    product.put("name", rs.getString("p_name"));
//                    product.put("capacity", rs.getInt("c_size"));
//                    product.put("price", rs.getInt("b_price"));
//                    product.put("amount", rs.getInt("b_num"));
//                    list.add(product);
//                    temp.put("productsList", list);
//                    result.put(no, temp);
//                } else {// 訂單編號 key已存在 插入新品項資料即可
//                    ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) result.get(no).get("productsList");
//                    HashMap<String, String> product = new HashMap();
//                    product.put("id", rs.getString("p_no"));
//                    product.put("name", rs.getString("p_name"));
//                    product.put("capacity", rs.getString("c_size"));
//                    product.put("price", rs.getString("b_price"));
//                    product.put("amount", rs.getString("b_num"));
//                    list.add(product);
//                }
//            }
//            lists = new JSONArray();
//            for(String key :  result.keySet()){
//                lists.put(result.get(key));
//            }
//
//        }catch(SQLException e){
//            e.printStackTrace();
//        }finally {
//            try{
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//            return lists;
//        }
//    }//v
//    @Override
//    public JSONArray getOrderListByDate(String date) {
//        PreparedStatement sm = null;
//        String sql = String.format("select * from" +
//                "(select o_no as no," +
//                "m_no as id," +
//                "m_phone as phone," +
//                "m_name as name," +
//                "pay_no as payNo," +
//                "o_remark as remark," +
//                "pay_name as payType," +
//                "o_orderdate as orderDate," +
//                "o_sendno as sendNo," +
//                "o_senddate as sendDate," +
//                "o_type as type," +
//                "o_recipient as recipient," +
//                "o_total as total from" +
//                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
//                "inner join" +
//                "(select o_no as no," +
//                "p_no," +
//                "p_name,c_size ," +
//                "b_price ," +
//                "b_num  from" +
//                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
//                ") as temp4 using(no) where orderDate like ? order by orderDate");
//        //,date,'%');
//        JSONArray lists = null;
//        try{
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,date+" %");
//            ResultSet rs = sm.executeQuery();
//            HashMap<String,HashMap> result  = new HashMap();
//            ResultSetMetaData rsmd =  rs.getMetaData();
//            int columnCount = rsmd.getColumnCount();
//            while(rs.next()){
//                String no = rs.getString("no");
//                if(!result.containsKey(no)){//訂單編號 key 不存在
//                    HashMap<String,Object> temp = new HashMap();
//                    temp.put("no",rs.getInt("no"));
//                    temp.put("phone",rs.getString("phone"));
//                    temp.put("id",rs.getInt("id"));
//                    temp.put("payType",rs.getString("payType"));
//                    temp.put("orderDate",rs.getDate("orderDate"));
//                    temp.put("sendNo",rs.getString("sendNo")==null?"":rs.getString("sendNo"));
//                    temp.put("sendDate",rs.getDate("sendDate")==null?"尚未出貨":rs.getString("sendDate"));
//                    temp.put("type",rs.getString("type"));
//                    temp.put("recipient",rs.getString("recipient"));
//                    temp.put("total",rs.getInt("total"));
//                    temp.put("name",rs.getString("name"));
//                    temp.put("payNo",rs.getString("payNo")==null?"":rs.getString("payNo"));
//                    temp.put("remark",rs.getString("remark")==null?"":rs.getString("remark"));
//                    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
//                    HashMap<String,Object> product = new HashMap();
//                    product.put("id",rs.getInt("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getInt("c_size"));
//                    product.put("price",rs.getInt("b_price"));
//                    product.put("amount",rs.getInt("b_num"));
//                    list.add(product);
//                    temp.put("productsList",list);
//                    result.put(no,temp);
//                }else{// 訂單編號 key已存在 插入新品項資料即可
//                    ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) result.get(no).get("productsList");
//                    HashMap<String,String> product = new HashMap();
//                    product.put("id",rs.getString("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getString("c_size"));
//                    product.put("price",rs.getString("b_price"));
//                    product.put("amount",rs.getString("b_num"));
//                    list.add(product);
//                }
//            }
//            lists = new JSONArray();
//            for(String key : result.keySet()){
//                lists.put(result.get(key));
//            }
//        }catch(SQLException e){
//            e.printStackTrace();
//        }finally {
//            try{
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//            return lists;
//        }
//    } //v
//    @Override
//    public JSONArray getOrderListByDays(String days) {
//        PreparedStatement sm = null;
//        String sql = String.format("select * from" +
//                "(select o_no as no," +
//                "m_no as id," +
//                "m_phone as phone," +
//                "m_name as name," +
//                "pay_no as payNo," +
//                "o_remark as remark," +
//                "pay_name as payType," +
//                "o_orderdate as orderDate," +
//                "o_sendno as sendNo," +
//                "o_senddate as sendDate," +
//                "o_type as type," +
//                "o_recipient as recipient," +
//                "o_total as total from" +
//                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
//                "inner join" +
//                "(select o_no as no," +
//                "p_no," +
//                "p_name,c_size ," +
//                "b_price ," +
//                "b_num  from" +
//                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
//                ") as temp4 using(no) where orderDate >= current_date-? order by orderDate;");
//
//        JSONArray lists = null;
//        try{
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,days);
//            ResultSet rs = sm.executeQuery();
//            HashMap<String,HashMap> result  = new HashMap();
//            ResultSetMetaData rsmd =  rs.getMetaData();
//            int columnCount = rsmd.getColumnCount();
//            while(rs.next()){
//                String no = rs.getString("no");
//                if(!result.containsKey(no)){//訂單編號 key 不存在
//                    HashMap<String,Object> temp = new HashMap();
//                    temp.put("no",rs.getInt("no"));
//                    temp.put("phone",rs.getString("phone"));
//                    temp.put("id",rs.getInt("id"));
//                    temp.put("payType",rs.getString("payType"));
//                    temp.put("orderDate",rs.getDate("orderDate"));
//                    temp.put("sendNo",rs.getString("sendNo")==null?"":rs.getString("sendNo"));
//                    temp.put("sendDate",rs.getDate("sendDate")==null?"尚未出貨":rs.getString("sendDate"));
//                    temp.put("type",rs.getString("type"));
//                    temp.put("recipient",rs.getString("recipient"));
//                    temp.put("total",rs.getInt("total"));
//                    temp.put("name",rs.getString("name"));
//                    temp.put("payNo",rs.getString("payNo")==null?"":rs.getString("payNo"));
//                    temp.put("remark",rs.getString("remark")==null?"":rs.getString("remark"));
//                    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
//                    HashMap<String,Object> product = new HashMap();
//                    product.put("id",rs.getInt("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getInt("c_size"));
//                    product.put("price",rs.getInt("b_price"));
//                    product.put("amount",rs.getInt("b_num"));
//                    list.add(product);
//                    temp.put("productsList",list);
//                    result.put(no,temp);
//                }else{// 訂單編號 key已存在 插入新品項資料即可
//                    ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) result.get(no).get("productsList");
//                    HashMap<String,String> product = new HashMap();
//                    product.put("id",rs.getString("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getString("c_size"));
//                    product.put("price",rs.getString("b_price"));
//                    product.put("amount",rs.getString("b_num"));
//                    list.add(product);
//                }
//            }
//            lists = new JSONArray();
//            for(String key : result.keySet()){
//                lists.put(result.get(key));
//            }
//        }catch(SQLException e){
//            e.printStackTrace();
//        }finally {
//            try{
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//            return lists;
//        }
//    } //v
//    @Override
//    public JSONArray getOrderListByNo(String o_no,String m_id) {
//        PreparedStatement sm = null;
//        String sql = String.format("select * from" +
//                "(select o_no as no," +
//                "m_no as id," +
//                "m_phone as phone," +
//                "pay_name as payType," +
//                "o_orderdate as orderDate," +
//                "o_sendno as sendNo," +
//                "o_senddate as sendDate," +
//                "o_type as type," +
//                "o_recipient as recipient," +
//                "o_total as total from" +
//                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
//                "inner join" +
//                "(select o_no as no," +
//                "p_no," +
//                "p_name,c_size ," +
//                "b_price ," +
//                "b_num  from" +
//                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
//                ") as temp4 using(no) where no = ? and id = ? order by orderDate;");
//        //,o_no,m_id);
//        JSONArray lists =null;
//
//        try{
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,o_no);
//            sm.setObject(2,m_id);
//            ResultSet rs = sm.executeQuery();
//            HashMap<String,HashMap> result  = new HashMap();
//            ResultSetMetaData rsmd =  rs.getMetaData();
//            int columnCount = rsmd.getColumnCount();
//            while(rs.next()){
//                String no = rs.getString("no");
//                if(!result.containsKey(no)){//訂單編號 key 不存在
//                    HashMap<String,Object> temp = new HashMap();
//                    temp.put("no",rs.getInt("no"));
//                    temp.put("phone",rs.getString("phone"));
//                    temp.put("id",rs.getInt("id"));
//                    temp.put("payType",rs.getString("payType"));
//                    temp.put("orderDate",rs.getDate("orderDate"));
//                    temp.put("sendNo",rs.getString("sendNo")==null?"":rs.getString("sendNo"));
//                    temp.put("sendDate",rs.getDate("sendDate")==null?"尚未出貨":rs.getString("sendDate"));
//                    temp.put("type",rs.getString("type"));
//                    temp.put("recipient",rs.getString("recipient"));
//                    temp.put("total",rs.getInt("total"));
//                    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
//                    HashMap<String,Object> product = new HashMap();
//                    product.put("id",rs.getInt("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getInt("c_size"));
//                    product.put("price",rs.getInt("b_price"));
//                    product.put("amount",rs.getInt("b_num"));
//                    list.add(product);
//                    temp.put("productsList",list);
//                    result.put(no,temp);
//                }else{// 訂單編號 key已存在 插入新品項資料即可
//                    ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) result.get(no).get("productsList");
//                    HashMap<String,String> product = new HashMap();
//                    product.put("id",rs.getString("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getString("c_size"));
//                    product.put("price",rs.getString("b_price"));
//                    product.put("amount",rs.getString("b_num"));
//                    list.add(product);
//                }
//            }
//            lists = new JSONArray();
//            for(String key : result.keySet()){
//                lists.put(result.get(key));
//            }
//            System.out.print("lists="+lists);
//        }catch(SQLException e){
//            e.printStackTrace();
//        }finally {
//            try{
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//            return lists;
//        }
//    } //v
//    @Override
//    public JSONArray getOrderListByNoForManager(String o_no) {
//        PreparedStatement sm = null;
//        String sql = String.format("select * from" +
//                "(select o_no as no," +
//                "m_no as id," +
//                "m_phone as phone," +
//                "m_name as name," +
//                "pay_no as payNo," +
//                "o_remark as remark," +
//                "pay_name as payType," +
//                "o_orderdate as orderDate," +
//                "o_sendno as sendNo," +
//                "o_senddate as sendDate," +
//                "o_type as type," +
//                "o_recipient as recipient," +
//                "o_total as total from" +
//                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
//                "inner join" +
//                "(select o_no as no," +
//                "p_no," +
//                "p_name,c_size ," +
//                "b_price ," +
//                "b_num  from" +
//                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
//                ") as temp4 using(no) where no = ? order by orderDate;");
//
//        JSONArray lists = null;
//        try{
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,o_no);
//            ResultSet rs = sm.executeQuery();
//            HashMap<String,HashMap> result  = new HashMap();
//            ResultSetMetaData rsmd =  rs.getMetaData();
//            int columnCount = rsmd.getColumnCount();
//            while(rs.next()){
//                String no = rs.getString("no");
//                if(!result.containsKey(no)){//訂單編號 key 不存在
//                    HashMap<String,Object> temp = new HashMap();
//                    temp.put("no",rs.getInt("no"));//
//                    temp.put("phone",rs.getString("phone"));
//                    temp.put("id",rs.getInt("id"));//
//                    temp.put("payType",rs.getString("payType"));
//                    temp.put("orderDate",rs.getDate("orderDate"));
//                    temp.put("sendNo",rs.getString("sendNo")==null?"":rs.getString("sendNo"));
//                    temp.put("sendDate",rs.getDate("sendDate")==null?"尚未出貨":rs.getString("sendDate"));
//                    temp.put("type",rs.getString("type"));
//                    temp.put("recipient",rs.getString("recipient"));
//                    temp.put("total",rs.getInt("total"));
//                    temp.put("name",rs.getString("name"));
//                    temp.put("payNo",rs.getString("payNo")==null?"":rs.getString("payNo"));
//                    temp.put("remark",rs.getString("remark")==null?"":rs.getString("remark"));
//                    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
//                    HashMap<String,Object> product = new HashMap();
//                    product.put("id",rs.getInt("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getInt("c_size"));
//                    product.put("price",rs.getInt("b_price"));
//                    product.put("amount",rs.getInt("b_num"));
//                    list.add(product);
//                    temp.put("productsList",list);
//                    result.put(no,temp);
//                }else{// 訂單編號 key已存在 插入新品項資料即可
//                    ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) result.get(no).get("productsList");
//                    HashMap<String,String> product = new HashMap();
//                    product.put("id",rs.getString("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getString("c_size"));
//                    product.put("price",rs.getString("b_price"));
//                    product.put("amount",rs.getString("b_num"));
//                    list.add(product);
//                }
//            }
//            lists = new JSONArray();
//            for(String key : result.keySet()){
//                lists.put(result.get(key));
//            }
//        }catch(SQLException e){
//            e.printStackTrace();
//        }finally {
//            try{
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//            return lists;
//        }
//    } //v
//    @Override
//    public void cancelOrder(String no,String id ) {
//        PreparedStatement sm = null;
//        String sql = String.format("update order_list set o_type = 'cancel' where o_no = ? and m_no = ?");
//
//        try {
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,no);
//            sm.setObject(2,id);
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
//            try {
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//        }
//    } //v
//    @Override
//    public void updateOrder(JSONObject object) {
//        PreparedStatement sm = null;
//        String sql = null;
//        try{
//            if(object.get("send_no").equals("")) {
//                sql = String.format("update order_list set o_remark = ? where o_no=?");
//                sm = this.conn.prepareStatement(sql);
//                sm.setObject(1,object.get("remark"));
//                sm.setObject(2,object.get("order_no"));
//            }else if(object.get("send_date").equals("尚未出貨")){
//                sql = String.format("update order_list set o_senddate=current_date ,o_type = 'send' ,o_sendno = ?,o_remark = ? where o_no=?");
//                sm = this.conn.prepareStatement(sql);
//                sm.setObject(1,object.get("send_no"));
//                sm.setObject(2,object.get("remark"));
//                sm.setObject(3,object.get("order_no"));
//            }else{
//                sql = String.format("update order_list set o_sendno = ?,o_remark = ? where o_no=?");
//                sm = this.conn.prepareStatement(sql);
//                sm.setObject(1,object.get("send_no"));
//                sm.setObject(2,object.get("remark"));
//                sm.setObject(3,object.get("order_no"));
//            }
//            System.out.println("訂單變更sql:"+sql);
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
//            try {
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//        }
//    } //v
//    @Override
//    public JSONArray getOrderListByNo(String o_no,String m_id) {
//        PreparedStatement sm = null;
//        String sql = String.format("select * from" +
//                "(select o_no as no," +
//                "m_no as id," +
//                "m_phone as phone," +
//                "pay_name as payType," +
//                "o_orderdate as orderDate," +
//                "o_sendno as sendNo," +
//                "o_senddate as sendDate," +
//                "o_type as type," +
//                "o_recipient as recipient," +
//                "o_total as total from" +
//                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
//                "inner join" +
//                "(select o_no as no," +
//                "p_no," +
//                "p_name,c_size ," +
//                "b_price ," +
//                "b_num  from" +
//                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
//                ") as temp4 using(no) where no = ? and id = ? order by orderDate;");
//        //,o_no,m_id);
//        JSONArray lists =null;
//
//        try{
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,o_no);
//            sm.setObject(2,m_id);
//            ResultSet rs = sm.executeQuery();
//            HashMap<String,HashMap> result  = new HashMap();
//            ResultSetMetaData rsmd =  rs.getMetaData();
//            int columnCount = rsmd.getColumnCount();
//            while(rs.next()){
//                String no = rs.getString("no");
//                if(!result.containsKey(no)){//訂單編號 key 不存在
//                    HashMap<String,Object> temp = new HashMap();
//                    temp.put("no",rs.getInt("no"));
//                    temp.put("phone",rs.getString("phone"));
//                    temp.put("id",rs.getInt("id"));
//                    temp.put("payType",rs.getString("payType"));
//                    temp.put("orderDate",rs.getDate("orderDate"));
//                    temp.put("sendNo",rs.getString("sendNo")==null?"":rs.getString("sendNo"));
//                    temp.put("sendDate",rs.getDate("sendDate")==null?"尚未出貨":rs.getString("sendDate"));
//                    temp.put("type",rs.getString("type"));
//                    temp.put("recipient",rs.getString("recipient"));
//                    temp.put("total",rs.getInt("total"));
//                    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
//                    HashMap<String,Object> product = new HashMap();
//                    product.put("id",rs.getInt("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getInt("c_size"));
//                    product.put("price",rs.getInt("b_price"));
//                    product.put("amount",rs.getInt("b_num"));
//                    list.add(product);
//                    temp.put("productsList",list);
//                    result.put(no,temp);
//                }else{// 訂單編號 key已存在 插入新品項資料即可
//                    ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) result.get(no).get("productsList");
//                    HashMap<String,String> product = new HashMap();
//                    product.put("id",rs.getString("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getString("c_size"));
//                    product.put("price",rs.getString("b_price"));
//                    product.put("amount",rs.getString("b_num"));
//                    list.add(product);
//                }
//            }
//            lists = new JSONArray();
//            for(String key : result.keySet()){
//                lists.put(result.get(key));
//            }
//            System.out.print("lists="+lists);
//        }catch(SQLException e){
//            e.printStackTrace();
//        }finally {
//            try{
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//            return lists;
//        }
//    } //v
//    @Override
//    public JSONArray getOrderListByNoForManager(String o_no) {
//        PreparedStatement sm = null;
//        String sql = String.format("select * from" +
//                "(select o_no as no," +
//                "m_no as id," +
//                "m_phone as phone," +
//                "m_name as name," +
//                "pay_no as payNo," +
//                "o_remark as remark," +
//                "pay_name as payType," +
//                "o_orderdate as orderDate," +
//                "o_sendno as sendNo," +
//                "o_senddate as sendDate," +
//                "o_type as type," +
//                "o_recipient as recipient," +
//                "o_total as total from" +
//                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
//                "inner join" +
//                "(select o_no as no," +
//                "p_no," +
//                "p_name,c_size ," +
//                "b_price ," +
//                "b_num  from" +
//                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
//                ") as temp4 using(no) where no = ? order by orderDate;");
//
//        JSONArray lists = null;
//        try{
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,o_no);
//            ResultSet rs = sm.executeQuery();
//            HashMap<String,HashMap> result  = new HashMap();
//            ResultSetMetaData rsmd =  rs.getMetaData();
//            int columnCount = rsmd.getColumnCount();
//            while(rs.next()){
//                String no = rs.getString("no");
//                if(!result.containsKey(no)){//訂單編號 key 不存在
//                    HashMap<String,Object> temp = new HashMap();
//                    temp.put("no",rs.getInt("no"));//
//                    temp.put("phone",rs.getString("phone"));
//                    temp.put("id",rs.getInt("id"));//
//                    temp.put("payType",rs.getString("payType"));
//                    temp.put("orderDate",rs.getDate("orderDate"));
//                    temp.put("sendNo",rs.getString("sendNo")==null?"":rs.getString("sendNo"));
//                    temp.put("sendDate",rs.getDate("sendDate")==null?"尚未出貨":rs.getString("sendDate"));
//                    temp.put("type",rs.getString("type"));
//                    temp.put("recipient",rs.getString("recipient"));
//                    temp.put("total",rs.getInt("total"));
//                    temp.put("name",rs.getString("name"));
//                    temp.put("payNo",rs.getString("payNo")==null?"":rs.getString("payNo"));
//                    temp.put("remark",rs.getString("remark")==null?"":rs.getString("remark"));
//                    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
//                    HashMap<String,Object> product = new HashMap();
//                    product.put("id",rs.getInt("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getInt("c_size"));
//                    product.put("price",rs.getInt("b_price"));
//                    product.put("amount",rs.getInt("b_num"));
//                    list.add(product);
//                    temp.put("productsList",list);
//                    result.put(no,temp);
//                }else{// 訂單編號 key已存在 插入新品項資料即可
//                    ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) result.get(no).get("productsList");
//                    HashMap<String,String> product = new HashMap();
//                    product.put("id",rs.getString("p_no"));
//                    product.put("name",rs.getString("p_name"));
//                    product.put("capacity",rs.getString("c_size"));
//                    product.put("price",rs.getString("b_price"));
//                    product.put("amount",rs.getString("b_num"));
//                    list.add(product);
//                }
//            }
//            lists = new JSONArray();
//            for(String key : result.keySet()){
//                lists.put(result.get(key));
//            }
//        }catch(SQLException e){
//            e.printStackTrace();
//        }finally {
//            try{
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//            return lists;
//        }
//    } //v
//    @Override
//    public void cancelOrder(String no,String id ) {
//        PreparedStatement sm = null;
//        String sql = String.format("update order_list set o_type = 'cancel' where o_no = ? and m_no = ?");
//
//        try {
//            sm = this.conn.prepareStatement(sql);
//            sm.setObject(1,no);
//            sm.setObject(2,id);
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
//            try {
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//        }
//    } //v
//    @Override
//    public void updateOrder(JSONObject object) {
//        PreparedStatement sm = null;
//        String sql = null;
//        try{
//            if(object.get("send_no").equals("")) {
//                sql = String.format("update order_list set o_remark = ? where o_no=?");
//                sm = this.conn.prepareStatement(sql);
//                sm.setObject(1,object.get("remark"));
//                sm.setObject(2,object.get("order_no"));
//            }else if(object.get("send_date").equals("尚未出貨")){
//                sql = String.format("update order_list set o_senddate=current_date ,o_type = 'send' ,o_sendno = ?,o_remark = ? where o_no=?");
//                sm = this.conn.prepareStatement(sql);
//                sm.setObject(1,object.get("send_no"));
//                sm.setObject(2,object.get("remark"));
//                sm.setObject(3,object.get("order_no"));
//            }else{
//                sql = String.format("update order_list set o_sendno = ?,o_remark = ? where o_no=?");
//                sm = this.conn.prepareStatement(sql);
//                sm.setObject(1,object.get("send_no"));
//                sm.setObject(2,object.get("remark"));
//                sm.setObject(3,object.get("order_no"));
//            }
//            System.out.println("訂單變更sql:"+sql);
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
//            try {
//                sm.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//        }
//    } //v
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