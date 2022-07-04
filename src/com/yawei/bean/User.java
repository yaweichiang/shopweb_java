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

public class User extends JSONObject implements Serializable {
    static int nextUserNo = MySqlConnect.getMySql().getNewUserNo();

    private int id;
    private String name;
    private String nickname;
    private String phone;
    private String mail;
    private String hashPW;
    private String url;



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
        }
    }
    // 新增會員建構 以前端傳送過來的註冊資料建立會員物件
    public User(String name,String phone,String mail,String password){
        this.name = name;
        this.nickname = name;
        this.phone = phone;
        this.mail = mail;
        this.hashPW = HashPassWord.getHash(password);

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

    public static void main(String[] args) {
        System.out.println(search("江"));
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
    @Override
    public String toString() {
        return "{" +
                "\"no\":" + id +
                ",\"name\":\"" + name + '\"' +
                ",\"nickname\":\"" + nickname + '\"' +
                ",\"phone\":\"" + phone + '\"' +
                ",\"email\":\"" + mail + '\"' +
                ",\"url\":\"" + url + '\"' +
                '}';
    }

    public int getId() {
        return id;
    }
}
