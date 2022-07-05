package com.yawei.bean;

import com.yawei.util.MySqlConnect;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Anno extends JSONObject {
    static int nextAnnoNo = getNewAnnoNo();

    private int id;
    private String content;
    private String time;
    private String target;

    public static void main(String[] args) {
        System.out.println(new Anno(6));
    }
    //依照公告編號 自資料庫取得公告資料 建立公告物件
    Anno(int no){
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select a_content as content,a_time as time,if(a_time=(select Max(a_time) from announcements),'true','false') as target from announcements where a_no = ?");
        try {
            sm = conn.prepareStatement(sql);
            sm.setObject(1,no);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                this.id = no;
                this.content = rs.getString("content");
                this.time = rs.getString("time");
                this.target = rs.getString("target");
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
    //依照前端傳來的公告內容建立公告物件
    public Anno(String jsonString){
        super(jsonString);
        try {
            this.id = super.getInt("id");
        }catch (JSONException e){//jsonString id = new
            this.id = nextAnnoNo++;
        }
        this.content = super.getString("content");
    }

    private static int getNewAnnoNo() {
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql=String.format("select a_no from announcements order by a_no desc limit 1");
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
    }

    @Override //公告內容相同視為相同公告
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Anno anno = (Anno) o;
        return content.equals(anno.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"content\":\"" + content + '\"' +
                ",\"time\":\"" + time + '\"' +
                ",\"target\":\"" + target + '\"' +
                '}';
    }
    //取得近期10筆公告內容
    public static List<Anno> getAllAnno(){
        List<Anno> result = new ArrayList<>();
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm =null;
        String sql = String.format("select a_no as id from announcements order by a_time desc limit 10 ");
        try{
            sm = conn.prepareStatement(sql);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                result.add(new Anno(id));
            }
        }catch (SQLException e){
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
    //取得目前公告的公告內容
    public static Anno getNewAnno(){
        Anno result = null;
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm =null;
        String sql = String.format("select  a_no as id from (select * from announcements order by a_no desc) as sub order by a_time desc limit 1");
        try{
            sm = conn.prepareStatement(sql);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                System.out.println(id);
                result = new Anno(id);
            }
        }catch (SQLException e){
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

    //新增/更新公告
    public void update(){
            Connection conn = MySqlConnect.getMySql().getConn();
            PreparedStatement sm = null;
            String sql = String.format("replace into announcements(a_no,a_content,a_time) values (?,?,current_timestamp)");
            try {
                sm = conn.prepareStatement(sql);
                sm.setObject(1,this.id);
                sm.setObject(2,this.content);
                sm.executeUpdate();
                conn.commit();
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
    }






