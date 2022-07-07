package com.yawei.bean;

import com.yawei.util.HashPassWord;
import com.yawei.util.MySqlConnect;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends JSONObject implements Serializable {
    static int nextUserNo = getNewUserNo();

    private int id;
    private String name;
    private String nickname;
    private String phone;
    private String mail;
    private String hashPW;
    private String url;
    private List<RecipientAddress> recipientAddresses;

    public User(){}
    // 舊有會員建構 依照會員編號自資料庫取得會員資料建立會員物件
    public User(String idOrPhone){
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = null;
        if(idOrPhone.matches("^09[0-9]{8}$")) {//透過電話
            sql = String.format("select m_no as id ,m_name as name ,m_nickname as nickname,m_phone as phone,m_mail as mail,m_hashPW as hashPW,url from members where m_phone = ?");
        }else{//透過id
            sql = String.format("select m_no as id ,m_name as name ,m_nickname as nickname,m_phone as phone,m_mail as mail,m_hashPW as hashPW,url from members where m_no = ?");
        }
        try {
            sm = conn.prepareStatement(sql);
            sm.setObject(1,idOrPhone);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                this.id = rs.getInt("id");
                this.name = rs.getString("name");
                this.nickname = rs.getString("nickname");
                this.phone = rs.getString("phone");
                this.mail = rs.getString("mail");
                this.hashPW = rs.getString("hashPW");
                this.url = rs.getString("url");
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }finally {
            try {
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        this.recipientAddresses = this.getAllRecipientAddress();
    }
    // 新增會員建構 以前端傳送過來的註冊資料建立會員物件
//
    public User(String name,String phone,String mail,String password){
        this.name = name;
        this.nickname = name;
        this.phone = phone;
        this.mail = mail;
        this.hashPW = HashPassWord.getHash(password);

    }

    // 會員常用地址類別
    private class RecipientAddress extends JSONObject {
        private int recipientNo;
        private String recipientName;
        private String recipientAddress;
        private String recipientPhone;
        //新增地址建構 接收前端傳回的資料 建立地址物件
        public RecipientAddress(String jsonString){
            super(jsonString);
            this.recipientName = super.getString("name");
            this.recipientAddress = super.getString("address");
            this.recipientPhone = super.getString("phone");
        }
        //舊有地址建構 依照常用地址編號 從資料庫取得資料建立地址物件
        private RecipientAddress(int no){
            Connection conn = MySqlConnect.getMySql().getConn();
            PreparedStatement sm = null;
            String sql = String.format("select r_name as name,r_address as address,r_phone as phone from address where r_no = ?");
            try{
                sm =conn.prepareStatement(sql);
                sm.setObject(1,no);
                ResultSet rs = sm.executeQuery();
                while(rs.next()){
                    this.recipientNo = no;
                    this.recipientName = rs.getString("name");
                    this.recipientAddress = rs.getString("address");
                    this.recipientPhone = rs.getString("phone");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                try {
                    sm.close();
                }catch (SQLException ex){
                    ex.printStackTrace();
                }
            }

        }
        private void create(){
            if(this.recipientNo==0) {
                Connection conn = MySqlConnect.getMySql().getConn();
                PreparedStatement sm = null;
                String sql = String.format("insert into address (m_no,r_name,r_phone,r_address) values(?,?,?,?)");
                try {
                    sm = conn.prepareStatement(sql);
                    sm.setObject(1, User.this.id);
                    sm.setObject(2, this.recipientName);
                    sm.setObject(3, this.recipientPhone);
                    sm.setObject(4, this.recipientAddress);
                    sm.executeUpdate();
                    conn.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                    try {
                        if (conn != null)
                            conn.rollback();//復原交易
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } finally {
                    try {
                        sm.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        private void delete(){
            Connection conn = MySqlConnect.getMySql().getConn();
            PreparedStatement sm = null;
            String sql = String.format("delete from address where r_no = ?");
            try{
                sm = conn.prepareStatement(sql);
                sm.setObject(1,this.recipientNo);
                sm.executeUpdate();
                conn.commit();
            }catch (SQLException e){
                e.printStackTrace();
                try {
                    if(conn!=null)
                        conn.rollback();//復原交易
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
        }
        @Override
        public String toString() {
            return "{" +
                    "\"no\":" + recipientNo +
                    ",\"name\":\"" + recipientName + '\"' +
                    ",\"address\":\"" + recipientAddress + '\"' +
                    ",\"phone\":\"" + recipientPhone + '\"' +
                    '}';
        }
        @Override
        public boolean equals(Object o) { // 姓名 電話 地址 相同 視為相同
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RecipientAddress that = (RecipientAddress) o;
            return Objects.equals(recipientName, that.recipientName) && Objects.equals(recipientAddress, that.recipientAddress) && Objects.equals(recipientPhone, that.recipientPhone);
        }
        @Override
        public int hashCode() {
            return Objects.hash(recipientName, recipientAddress, recipientPhone);
        }
    }
    //取得新會員編號
    private static int getNewUserNo() {
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql=String.format("select m_no from members order by m_no desc limit 1");
        int id = 1;
        try{
            sm = conn.prepareStatement(sql);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                id = rs.getInt(1) + 1;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return id;
    }//v
    //取得會員所有的地址物件
    private List<RecipientAddress> getAllRecipientAddress(){
        List<RecipientAddress> result = new ArrayList<>();
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select r_no as no from address  where m_no = ?");
        try{
            sm = conn.prepareStatement(sql);
            sm.setObject(1,this.id);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                result.add(new RecipientAddress(rs.getInt("no")));
            }
        }catch (SQLException e){
            e.printStackTrace();
            try {
                sm.close();
            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }finally {
            return result;
        }
    }


    //會員註冊 將會員資料存入資料庫
    public void create(){
        if(this.id==0){
            this.id = nextUserNo++;
            Connection conn = MySqlConnect.getMySql().getConn();
            PreparedStatement sm =null;
            try {
                String sql = String.format("insert into members(m_no,m_name,m_nickname,m_phone,m_mail,m_hashPW) values(?,?,?,?,?,?)");
                sm = conn.prepareStatement(sql);
                sm.setObject(1, this.id);
                sm.setObject(2, this.name);
                sm.setObject(3, this.nickname);
                sm.setObject(4, this.phone);
                sm.setObject(5, this.mail);
                sm.setObject(6, this.hashPW);
                sm.executeUpdate();
                conn.commit();
            }catch(SQLException e){
                e.printStackTrace();
                try {
                    conn.rollback();
                }catch (SQLException ex){
                    ex.printStackTrace();
                }
            }finally {
                try {
                    sm.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }
    //會員資料變更
    public void update(String name,String nickname,String mail){
        this.name = name;
        this.nickname = nickname;
        this.mail = mail;
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("update members set m_name = ?,m_nickname = ?,m_mail = ? where m_no = ? ");
        try{
            sm = conn.prepareStatement(sql);
            sm.setObject(1,this.name);
            sm.setObject(2,this.nickname);
            sm.setObject(3,this.mail);
            sm.setObject(4,this.id);
            sm.executeUpdate();
            conn.commit();
        }catch (SQLException e){
            e.printStackTrace();
            try {
                if(conn!=null)
                    conn.rollback();//復原交易
            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }finally {
            try {
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    //會員密碼變更
    public String changePassword(String oldPassword,String newPassword){
        if(HashPassWord.getHash(oldPassword).equals(this.hashPW)){
            //舊密碼正確 變更密碼
            newPassword = HashPassWord.getHash(newPassword);
            Connection conn = MySqlConnect.getMySql().getConn();
            PreparedStatement sm = null;
            String sql = String.format("update members set m_hashPW = ? where m_no = ? ");
            try{
                sm = conn.prepareStatement(sql);
                sm.setObject(1,newPassword);
                sm.setObject(2,this.id);
                sm.executeUpdate();
                conn.commit();
                return "會員資料及密碼變更成功";
            }catch (SQLException e){
                e.printStackTrace();
                try {
                    if(conn!=null)
                        conn.rollback();//復原交易
                }catch (SQLException ex){
                    ex.printStackTrace();
                }finally {
                    return "會員資料變更成功，密碼變更失敗！";
                }
            }finally {
                try {
                    sm.close();
                }catch (SQLException e ){
                    e.printStackTrace();
                }
            }
        }else{
            return "會員資料更新成功;舊密碼錯誤，密碼變更失敗！";
        }
    }
    //刪除會員指定編號地址
    public void removeRecipientAddress(int addressNo){
        for(RecipientAddress address:this.recipientAddresses){
            if(address.recipientNo==addressNo)
                address.delete();
        }
    }
    //新增收件人地址
    public void addRecipientAddress(String jsonString){
        RecipientAddress newAddress = new RecipientAddress(jsonString);
        if(!this.recipientAddresses.contains(newAddress)){
            System.out.println("新增收件人");
            newAddress.create();
        }else{
            System.out.println("收件人重複");
        }
    }

    //會員登入 回傳User 物件
    public static User login(String userPhone,String password){
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select m_hashPW as hashPW, m_no as id from members where m_phone = ?");
        User user = null;
        try{
            sm = conn.prepareStatement(sql);
            sm.setObject(1,userPhone);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                String hashPW = rs.getString("hashPW");
                int id = rs.getInt("id");
                if(HashPassWord.getHash(password).equals(hashPW)){
                    user = new User(String.valueOf(id));
                }
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
        return user;
    }
    //查詢會員 回傳List<User>
    public static List<User> search(String keyword){
        List<User> result = new ArrayList<>();
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select m_no as id ,m_name,m_phone,m_mail from members where m_name like ? or m_phone like ? or m_mail like ?");
        System.out.println(sql);
        System.out.println(keyword);

        try{
            sm = conn.prepareStatement(sql);
            sm.setObject(1,"%"+keyword+"%");
            sm.setObject(2,"%"+keyword+"%");
            sm.setObject(3,"%"+keyword+"%");

            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                result.add(new User(String.valueOf(rs.getInt("id"))));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return result;
        }
    }
    //檢查電話是否已註冊 true 已存在會員 false電話未使用
    public static boolean checkPhone(String phone){
        boolean result = false;
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select m_phone from members where m_phone = ?");
        //,id);
        try{
            sm = conn.prepareStatement(sql);
            sm.setObject(1,phone);
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

    }

    @Override
    public String toString() {
        return "{" +
                "\"no\":" + id +
                ",\"name\":\"" + name + '\"' +
                ",\"nickname\":\"" + nickname + '\"' +
                ",\"phone\":\"" + phone + '\"' +
                ",\"email\":\"" + mail + '\"' +
                ",\"url\":\"" + url + '\"' +
                ",\"addressList\":" + recipientAddresses +
                '}';
    }

    //User 物件屬性getter
    public List<RecipientAddress> getRecipientAddresses() {
        return recipientAddresses;
    }
    public int getId() {
        return id;
    }
    public String getPhone() {
        return phone;
    }
}
