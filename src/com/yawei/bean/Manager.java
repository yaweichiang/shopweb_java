package com.yawei.bean;

import com.yawei.util.HashPassWord;
import com.yawei.util.MySqlConnect;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Manager extends JSONObject {
    private int id;
    private String phone;
    private String hashPW;

    public Manager(){}
    Manager(String phone){
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select ma_no as id ,ma_phone as phone,ma_hashPW as hashPW from managers where ma_phone = ?");
        try {
            sm = conn.prepareStatement(sql);
            sm.setObject(1,phone);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                this.id = rs.getInt("id");
                this.phone = rs.getString("phone");
                this.hashPW = rs.getString("hashPW");
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

    }

    public static Manager login(String phone,String password){
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select ma_hashPW as hashPW, ma_no as id from managers where ma_phone = ?");
        Manager manager = null;
        try{
            sm = conn.prepareStatement(sql);
            sm.setObject(1,phone);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                String hashPW = rs.getString("hashPW");
                int id = rs.getInt("id");
                if(HashPassWord.getHash(password).equals(hashPW)){
                    manager = new Manager(String.valueOf(id));
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
        return manager;
    }

}
