package com.yawei.bean;

import com.yawei.util.MySqlConnect;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Pay extends JSONObject {
    private int id;
    private String name;
    private int fee;
    public Pay(){}
    public Pay(int id,String name,int fee){
        this.id = id;
        this.name = name;
        this.fee = fee;
    }
    public Pay(int id){
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select pay_id as id ,pay_name as name ,pay_fee as fee from pay_type where pay_id = ? ");
        try {
            sm = conn.prepareStatement(sql);
            sm.setObject(1,id);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                this.id = rs.getInt("id");
                this.name = rs.getString("name");
                this.fee = rs.getInt("fee");
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

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"name\":\"" + name + '\"' +
                ",\"fee\":" + fee +
                '}';
    }

    public int getFee() {
        return fee;
    }
    public static List<Pay> all(){
        List<Pay> result = new ArrayList<>();
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select pay_id as id ,pay_name as name ,pay_fee as fee from pay_type");
        try {
            sm = conn.prepareStatement(sql);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                result.add(new Pay(rs.getInt("id"),rs.getString("name"),rs.getInt("fee")));
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }finally {
            try {
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        return result;
        }
    }
}
