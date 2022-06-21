package com.yawei.util;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.json.*;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.sql.*;
import java.util.ArrayList;
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

        String num = object.toString();
        if(num.substring(0,1).equals("0"))
            return false;
        try {
            int i = Integer.parseInt(object.toString());
        }catch (Exception e){
            return false;
        }
        return  true;
    } //v
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
    public int getNewProductNo() {

        Statement sm = null;
        String sql=String.format("select p_no from products order by p_no desc limit 1");
        int id = 1;
        try{
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
            while(rs.next()){
                id = rs.getInt(1) + 1;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return id;
    }
    @Override
    public JsonArray createProduct(JSONObject data,String path) {
        Statement sm = null;
        String sql = String.format("insert into products values(null,'%s',%s,%s,%s,%s,'%s','%s','%s')",
                data.get("name"),data.get("inventory"),data.get("capacity"),
                data.get("tote_type"),data.get("price"),path,
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
    public JsonArray searchUsers(String keyword) {
        JsonArrayBuilder result = createArrayBuilder();
        Statement sm = null;
        String sql = String.format("select m_no as no,m_name as name,m_phone as phone,m_mail as email,url as url from members where m_name like '%s' or m_phone like '%s' or m_mail like '%s'","%"+keyword+"%","%"+keyword+"%","%"+keyword+"%");
        System.out.println("sql"+sql);
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
            System.out.println("result:"+result);
        } catch(SQLException e) {
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            JsonArray array = result.build();
            System.out.println("array"+array);
            return array;
        }

    }
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
    }//v
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
    }//v
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
    }//v
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

    }//v
    @Override
    public int getNewOrderListNo() {

        Statement sm = null;
        String sql=String.format("select o_no from order_list order by o_no desc limit 1");
        int id = 1;
        try{
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
            while(rs.next()){
                id = rs.getInt(1) + 1;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return id;
    }
    @Override
    public void createOrderList(JSONObject object,String userid) {
        int no = MySqlConnect.getMySql().getNewOrderListNo();
        Statement sm = null;
        String sql = String.format("insert into order_list(o_no,m_no,pay_id,t_no,o_recipient,o_total) values(%d,%s,%d,'%d','%s',%d)",
                no,userid,object.getInt("payID"),object.getInt("toteNo"),(object.getString("name")+"/"+object.getString("address")+"/"+object.getString("phone")),object.getInt("total"));
        try{
            sm = this.conn.createStatement();
            sm.executeUpdate(sql);
            Object json = object.get("products");
            JSONArray products = new JSONArray(json.toString());
            for(Object obj :products){
                JSONObject product = new JSONObject(obj.toString());
                sql = String.format("insert into order_products values(%d,%d,%d,%d)",no,product.getInt("id"),product.getInt("amount"),product.getInt("price"));
                sm.executeUpdate(sql);
            }

            System.out.println("新增完成");
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
    public JSONArray getOrderListByMemberId(String id) {
        Statement sm = null;
        String sql = String.format("select * from" +
                "(select o_no as no," +
                "m_no as id," +
                "m_phone as phone," +
                "pay_name as payType," +
                "o_orderdate as orderDate," +
                "o_sendno as sendNo," +
                "o_senddate as sendDate," +
                "o_type as type," +
                "o_recipient as recipient," +
                "o_total as total from" +
                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
                "inner join" +
                "(select o_no as no," +
                "p_no," +
                "p_name,c_size ," +
                "b_price ," +
                "b_num  from" +
                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
                ") as temp4 using(no) where id = %s order by no;",id);
        JSONArray lists =null;
        try{
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
            HashMap<String,HashMap> result  = new HashMap();
            ResultSetMetaData rsmd =  rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while(rs.next()){
                String no = rs.getString("no");
                if(!result.containsKey(no)){//訂單編號 key 不存在
                    HashMap<String,Object> temp = new HashMap();
                    temp.put("no",rs.getInt("no"));
                    temp.put("phone",rs.getString("phone"));
                    temp.put("id",rs.getInt("id"));
                    temp.put("payType",rs.getString("payType"));
                    temp.put("orderDate",rs.getDate("orderDate"));
                    temp.put("sendNo",rs.getString("sendNo")==null?"":rs.getString("sendNo"));
                    temp.put("sendDate",rs.getString("sendDate")==null?"":rs.getString("sendDate"));
                    temp.put("type",rs.getString("type"));
                    temp.put("recipient",rs.getString("recipient"));
                    temp.put("total",rs.getInt("total"));
                    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
                    HashMap<String,Object> product = new HashMap();
                    product.put("id",rs.getInt("p_no"));
                    product.put("name",rs.getString("p_name"));
                    product.put("capacity",rs.getInt("c_size"));
                    product.put("price",rs.getInt("b_price"));
                    product.put("amount",rs.getInt("b_num"));
                    list.add(product);
                    temp.put("productsList",list);
                    result.put(no,temp);
                }else{// 訂單編號 key已存在 插入新品項資料即可
                    ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) result.get(no).get("productsList");
                    HashMap<String,String> product = new HashMap();
                    product.put("id",rs.getString("p_no"));
                    product.put("name",rs.getString("p_name"));
                    product.put("capacity",rs.getString("c_size"));
                    product.put("price",rs.getString("b_price"));
                    product.put("amount",rs.getString("b_num"));
                    list.add(product);
                }
            }
            lists = new JSONArray();
            for(String key : result.keySet()){
                lists.put(result.get(key));
            }
            System.out.print("lists="+lists);
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return lists;
        }
    }//v
    @Override
    public JSONArray getOrderListByMemberIdforManager(String id) {
        Statement sm = null;
        String sql = String.format("select * from" +
                "(select o_no as no," +
                "m_no as id," +
                "m_phone as phone," +
                "m_name as name," +
                "pay_no as payNo," +
                "o_remark as remark," +
                "pay_name as payType," +
                "o_orderdate as orderDate," +
                "o_sendno as sendNo," +
                "o_senddate as sendDate," +
                "o_type as type," +
                "o_recipient as recipient," +
                "o_total as total from" +
                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
                "inner join" +
                "(select o_no as no," +
                "p_no," +
                "p_name,c_size ," +
                "b_price ," +
                "b_num  from" +
                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
                ") as temp4 using(no) where id = %s order by orderDate;",id);
        JSONArray lists = null;
        try{
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
            HashMap<String,HashMap> result  = new HashMap();
            ResultSetMetaData rsmd =  rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while(rs.next()){
                String no = rs.getString("no");
                if(!result.containsKey(no)){//訂單編號 key 不存在
                    HashMap<String,Object> temp = new HashMap();
                    temp.put("no",rs.getInt("no"));
                    temp.put("phone",rs.getString("phone"));
                    temp.put("id",rs.getInt("id"));
                    temp.put("payType",rs.getString("payType"));
                    temp.put("orderDate",rs.getDate("orderDate"));
                    temp.put("sendNo",rs.getString("sendNo")==null?"":rs.getString("sendNo"));
                    temp.put("sendDate",rs.getString("sendDate")==null?"":rs.getString("sendDate"));
                    temp.put("type",rs.getString("type"));
                    temp.put("recipient",rs.getString("recipient"));
                    temp.put("total",rs.getInt("total"));
                    temp.put("name",rs.getString("name"));
                    temp.put("payNo",rs.getString("payNo")==null?"":rs.getString("payNo"));
                    temp.put("remark",rs.getString("remark")==null?"":rs.getString("remark"));
                    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
                    HashMap<String,Object> product = new HashMap();
                    product.put("id",rs.getInt("p_no"));
                    product.put("name",rs.getString("p_name"));
                    product.put("capacity",rs.getInt("c_size"));
                    product.put("price",rs.getInt("b_price"));
                    product.put("amount",rs.getInt("b_num"));
                    list.add(product);
                    temp.put("productsList",list);
                    result.put(no,temp);
                }else{// 訂單編號 key已存在 插入新品項資料即可
                    ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) result.get(no).get("productsList");
                    HashMap<String,String> product = new HashMap();
                    product.put("id",rs.getString("p_no"));
                    product.put("name",rs.getString("p_name"));
                    product.put("capacity",rs.getString("c_size"));
                    product.put("price",rs.getString("b_price"));
                    product.put("amount",rs.getString("b_num"));
                    list.add(product);
                }
            }
            lists = new JSONArray();
            for(String key : result.keySet()){
                lists.put(result.get(key));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return lists;
        }
    }//v
    @Override
    public JSONArray getOrderListByDate(String date) {
        Statement sm = null;
        String sql = String.format("select * from" +
                "(select o_no as no," +
                "m_no as id," +
                "m_phone as phone," +
                "m_name as name," +
                "pay_no as payNo," +
                "o_remark as remark," +
                "pay_name as payType," +
                "o_orderdate as orderDate," +
                "o_sendno as sendNo," +
                "o_senddate as sendDate," +
                "o_type as type," +
                "o_recipient as recipient," +
                "o_total as total from" +
                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
                "inner join" +
                "(select o_no as no," +
                "p_no," +
                "p_name,c_size ," +
                "b_price ," +
                "b_num  from" +
                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
                ") as temp4 using(no) where orderDate like '%s %s' order by orderDate",date,'%');
        JSONArray lists = null;
        try{
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
            HashMap<String,HashMap> result  = new HashMap();
            ResultSetMetaData rsmd =  rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while(rs.next()){
                String no = rs.getString("no");
                if(!result.containsKey(no)){//訂單編號 key 不存在
                    HashMap<String,Object> temp = new HashMap();
                    temp.put("no",rs.getInt("no"));
                    temp.put("phone",rs.getString("phone"));
                    temp.put("id",rs.getInt("id"));
                    temp.put("payType",rs.getString("payType"));
                    temp.put("orderDate",rs.getDate("orderDate"));
                    temp.put("sendNo",rs.getString("sendNo")==null?"":rs.getString("sendNo"));
                    temp.put("sendDate",rs.getString("sendDate")==null?"":rs.getString("sendDate"));
                    temp.put("type",rs.getString("type"));
                    temp.put("recipient",rs.getString("recipient"));
                    temp.put("total",rs.getInt("total"));
                    temp.put("name",rs.getString("name"));
                    temp.put("payNo",rs.getString("payNo")==null?"":rs.getString("payNo"));
                    temp.put("remark",rs.getString("remark")==null?"":rs.getString("remark"));
                    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
                    HashMap<String,Object> product = new HashMap();
                    product.put("id",rs.getInt("p_no"));
                    product.put("name",rs.getString("p_name"));
                    product.put("capacity",rs.getInt("c_size"));
                    product.put("price",rs.getInt("b_price"));
                    product.put("amount",rs.getInt("b_num"));
                    list.add(product);
                    temp.put("productsList",list);
                    result.put(no,temp);
                }else{// 訂單編號 key已存在 插入新品項資料即可
                    ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) result.get(no).get("productsList");
                    HashMap<String,String> product = new HashMap();
                    product.put("id",rs.getString("p_no"));
                    product.put("name",rs.getString("p_name"));
                    product.put("capacity",rs.getString("c_size"));
                    product.put("price",rs.getString("b_price"));
                    product.put("amount",rs.getString("b_num"));
                    list.add(product);
                }
            }
            lists = new JSONArray();
            for(String key : result.keySet()){
                lists.put(result.get(key));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return lists;
        }
    } //v
    @Override
    public JSONArray getOrderListByDays(String days) {
        Statement sm = null;
        String sql = String.format("select * from" +
                "(select o_no as no," +
                "m_no as id," +
                "m_phone as phone," +
                "m_name as name," +
                "pay_no as payNo," +
                "o_remark as remark," +
                "pay_name as payType," +
                "o_orderdate as orderDate," +
                "o_sendno as sendNo," +
                "o_senddate as sendDate," +
                "o_type as type," +
                "o_recipient as recipient," +
                "o_total as total from" +
                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
                "inner join" +
                "(select o_no as no," +
                "p_no," +
                "p_name,c_size ," +
                "b_price ," +
                "b_num  from" +
                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
                ") as temp4 using(no) where orderDate >= current_date-%s order by orderDate;",days);
        JSONArray lists = null;
        try{
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
            HashMap<String,HashMap> result  = new HashMap();
            ResultSetMetaData rsmd =  rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while(rs.next()){
                String no = rs.getString("no");
                if(!result.containsKey(no)){//訂單編號 key 不存在
                    HashMap<String,Object> temp = new HashMap();
                    temp.put("no",rs.getInt("no"));
                    temp.put("phone",rs.getString("phone"));
                    temp.put("id",rs.getInt("id"));
                    temp.put("payType",rs.getString("payType"));
                    temp.put("orderDate",rs.getDate("orderDate"));
                    temp.put("sendNo",rs.getString("sendNo")==null?"":rs.getString("sendNo"));
                    temp.put("sendDate",rs.getString("sendDate")==null?"":rs.getString("sendDate"));
                    temp.put("type",rs.getString("type"));
                    temp.put("recipient",rs.getString("recipient"));
                    temp.put("total",rs.getInt("total"));
                    temp.put("name",rs.getString("name"));
                    temp.put("payNo",rs.getString("payNo")==null?"":rs.getString("payNo"));
                    temp.put("remark",rs.getString("remark")==null?"":rs.getString("remark"));
                    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
                    HashMap<String,Object> product = new HashMap();
                    product.put("id",rs.getInt("p_no"));
                    product.put("name",rs.getString("p_name"));
                    product.put("capacity",rs.getInt("c_size"));
                    product.put("price",rs.getInt("b_price"));
                    product.put("amount",rs.getInt("b_num"));
                    list.add(product);
                    temp.put("productsList",list);
                    result.put(no,temp);
                }else{// 訂單編號 key已存在 插入新品項資料即可
                    ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) result.get(no).get("productsList");
                    HashMap<String,String> product = new HashMap();
                    product.put("id",rs.getString("p_no"));
                    product.put("name",rs.getString("p_name"));
                    product.put("capacity",rs.getString("c_size"));
                    product.put("price",rs.getString("b_price"));
                    product.put("amount",rs.getString("b_num"));
                    list.add(product);
                }
            }
            lists = new JSONArray();
            for(String key : result.keySet()){
                lists.put(result.get(key));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return lists;
        }
    } //v
    @Override
    public JSONArray getOrderListByNo(String o_no,String m_id) {
        Statement sm = null;
        String sql = String.format("select * from" +
                "(select o_no as no," +
                "m_no as id," +
                "m_phone as phone," +
                "pay_name as payType," +
                "o_orderdate as orderDate," +
                "o_sendno as sendNo," +
                "o_senddate as sendDate," +
                "o_type as type," +
                "o_recipient as recipient," +
                "o_total as total from" +
                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
                "inner join" +
                "(select o_no as no," +
                "p_no," +
                "p_name,c_size ," +
                "b_price ," +
                "b_num  from" +
                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
                ") as temp4 using(no) where no = %s and id = %s order by orderDate;",o_no,m_id);
        JSONArray lists =null;
        try{
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
            HashMap<String,HashMap> result  = new HashMap();
            ResultSetMetaData rsmd =  rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while(rs.next()){
                String no = rs.getString("no");
                if(!result.containsKey(no)){//訂單編號 key 不存在
                    HashMap<String,Object> temp = new HashMap();
                    temp.put("no",rs.getInt("no"));
                    temp.put("phone",rs.getString("phone"));
                    temp.put("id",rs.getInt("id"));
                    temp.put("payType",rs.getString("payType"));
                    temp.put("orderDate",rs.getDate("orderDate"));
                    temp.put("sendNo",rs.getString("sendNo")==null?"":rs.getString("sendNo"));
                    temp.put("sendDate",rs.getString("sendDate")==null?"":rs.getString("sendDate"));
                    temp.put("type",rs.getString("type"));
                    temp.put("recipient",rs.getString("recipient"));
                    temp.put("total",rs.getInt("total"));
                    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
                    HashMap<String,Object> product = new HashMap();
                    product.put("id",rs.getInt("p_no"));
                    product.put("name",rs.getString("p_name"));
                    product.put("capacity",rs.getInt("c_size"));
                    product.put("price",rs.getInt("b_price"));
                    product.put("amount",rs.getInt("b_num"));
                    list.add(product);
                    temp.put("productsList",list);
                    result.put(no,temp);
                }else{// 訂單編號 key已存在 插入新品項資料即可
                    ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) result.get(no).get("productsList");
                    HashMap<String,String> product = new HashMap();
                    product.put("id",rs.getString("p_no"));
                    product.put("name",rs.getString("p_name"));
                    product.put("capacity",rs.getString("c_size"));
                    product.put("price",rs.getString("b_price"));
                    product.put("amount",rs.getString("b_num"));
                    list.add(product);
                }
            }
            lists = new JSONArray();
            for(String key : result.keySet()){
                lists.put(result.get(key));
            }
            System.out.print("lists="+lists);
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return lists;
        }
    } //v
    @Override
    public JSONArray getOrderListByNoForManager(String o_no) {
        Statement sm = null;
        String sql = String.format("select * from" +
                "(select o_no as no," +
                "m_no as id," +
                "m_phone as phone," +
                "m_name as name," +
                "pay_no as payNo," +
                "o_remark as remark," +
                "pay_name as payType," +
                "o_orderdate as orderDate," +
                "o_sendno as sendNo," +
                "o_senddate as sendDate," +
                "o_type as type," +
                "o_recipient as recipient," +
                "o_total as total from" +
                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
                "inner join" +
                "(select o_no as no," +
                "p_no," +
                "p_name,c_size ," +
                "b_price ," +
                "b_num  from" +
                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
                ") as temp4 using(no) where no = %s order by orderDate;",o_no);
        JSONArray lists = null;
        try{
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
            HashMap<String,HashMap> result  = new HashMap();
            ResultSetMetaData rsmd =  rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while(rs.next()){
                String no = rs.getString("no");
                if(!result.containsKey(no)){//訂單編號 key 不存在
                    HashMap<String,Object> temp = new HashMap();
                    temp.put("no",rs.getInt("no"));
                    temp.put("phone",rs.getString("phone"));
                    temp.put("id",rs.getInt("id"));
                    temp.put("payType",rs.getString("payType"));
                    temp.put("orderDate",rs.getDate("orderDate"));
                    temp.put("sendNo",rs.getString("sendNo")==null?"":rs.getString("sendNo"));
                    temp.put("sendDate",rs.getString("sendDate")==null?"":rs.getString("sendDate"));
                    temp.put("type",rs.getString("type"));
                    temp.put("recipient",rs.getString("recipient"));
                    temp.put("total",rs.getInt("total"));
                    temp.put("name",rs.getString("name"));
                    temp.put("payNo",rs.getString("payNo")==null?"":rs.getString("payNo"));
                    temp.put("remark",rs.getString("remark")==null?"":rs.getString("remark"));
                    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
                    HashMap<String,Object> product = new HashMap();
                    product.put("id",rs.getInt("p_no"));
                    product.put("name",rs.getString("p_name"));
                    product.put("capacity",rs.getInt("c_size"));
                    product.put("price",rs.getInt("b_price"));
                    product.put("amount",rs.getInt("b_num"));
                    list.add(product);
                    temp.put("productsList",list);
                    result.put(no,temp);
                }else{// 訂單編號 key已存在 插入新品項資料即可
                    ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) result.get(no).get("productsList");
                    HashMap<String,String> product = new HashMap();
                    product.put("id",rs.getString("p_no"));
                    product.put("name",rs.getString("p_name"));
                    product.put("capacity",rs.getString("c_size"));
                    product.put("price",rs.getString("b_price"));
                    product.put("amount",rs.getString("b_num"));
                    list.add(product);
                }
            }
            lists = new JSONArray();
            for(String key : result.keySet()){
                lists.put(result.get(key));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return lists;
        }
    } //v
    @Override
    public void cancelOrder(String no,String id ) {
        Statement sm = null;
        String sql = String.format("update order_list set o_type = 'cancel' where o_no = %s and m_no = %s",no,id);
        try {
            sm = this.conn.createStatement();
            sm.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void updateOrder(JSONObject object) {
        Statement sm = null;
        if(object.get("send_no").equals("")) {
            String sql = String.format("update order_list set o_remark = '%s' where o_no=%s", object.get("remark"), object.get("order_no"));
        }else if(object.get("send_date").equals("尚未出貨")){
            String sql = String.format("update order_list set o_senddate=current_date ,o_type = 'send' ,o_sendno = '{data['send_no']}',o_remark = '{data['remark']}' where o_no={data['order_no']}", object.get("remark"), object.get("order_no"));
        }else{
            String sql = String.format("update order_list set o_remark = '%s' where o_no=%s", object.get("remark"), object.get("order_no"));
        }
    }
    @Override
    public boolean checkPhone(String id){
        boolean result = false;
        Statement sm = null;
        String sql = String.format("select m_phone from members where m_no = %s",id);
        try{
            sm = this.conn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
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
    }
}