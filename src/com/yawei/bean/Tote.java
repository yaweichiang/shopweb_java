package com.yawei.bean;

import com.yawei.util.MySqlConnect;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Tote extends JSONObject {
    private List<SubTote> totes = new ArrayList<>();
    //自資料庫取得 全部運費資料 建立運費物件
    public Tote(){
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select t_no as id,t_name as name ,t_threshold as threshold,t_fare as fare from tote_type");
        try {
            sm = conn.prepareStatement(sql);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                totes.add(new SubTote(rs.getInt("id"),rs.getString("name"),rs.getInt("threshold"),rs.getInt("fare")));
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
    }
    //自前端傳送資料 建立運費物件
    public Tote(String jsonString){
        super(jsonString);
        for(String key : super.keySet()){
            totes.add(new SubTote(super.getJSONObject(key).getInt("id"),key,super.getJSONObject(key).getInt("threshold"),super.getJSONObject(key).getInt("fare")));
        }
    }
    public Tote(int toteNo){
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select t_no as id,t_name as name ,t_threshold as threshold,t_fare as fare from tote_type where t_no = ?");
        try {
            sm = conn.prepareStatement(sql);
            sm.setObject(1,toteNo);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                totes.add(new SubTote(rs.getInt("id"),rs.getString("name"),rs.getInt("threshold"),rs.getInt("fare")));
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
    }
    public void update(){
        for(SubTote tote:totes){
            tote.update();
        }
    }

    @Override
    public String toString() {
        return  totes.toString();
    }

    private class SubTote extends JSONObject{
        private int id;
        private String name;
        private int threshold;
        private int fare;

        private SubTote(int id ,String name,int threshold,int fare){
            this.id = id;
            this.name = name;
            this.threshold = threshold;
            this.fare = fare;
        }
        private void update(){
            Connection conn = MySqlConnect.getMySql().getConn();
            PreparedStatement sm = null;
            String sql = String.format("update tote_type set t_fare=? , t_threshold=? where t_name = ?");
            try {
                sm = conn.prepareStatement(sql);
                sm.setObject(1,this.fare);
                sm.setObject(2,this.threshold);
                sm.setObject(3,this.name);
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
                    ",\"threshold\":" + threshold +
                    ",\"fare\":" + fare +
                    '}';
        }

    }
    public int[] getInfo(){
        if(this.totes.size()==1){
            int[] result = {this.totes.get(0).threshold,this.totes.get(0).fare};
            return result;
        }else{
            int[] result = {0,0};
            return result;
        }
    }
    public List<SubTote> getTotes() {
        return totes;
    }
}
