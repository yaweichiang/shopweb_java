package com.yawei.bean;


import com.yawei.util.MySqlConnect;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Capacity extends JSONObject {

    private int id;
    private int size;
    private String productPackage;

    public Capacity(int id, int size,String productPackage){
        this.id = id;
        this.size = size;
        this.productPackage = productPackage;

    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"size\":" + size +
                ",\"productPackage\":\"" + productPackage + '\"' +
                '}';
    }

    public static List<Capacity> all(){
        List<Capacity> result = new ArrayList<>();
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select c_no as id,c_size as size,c_package as productPackage from product_capacity");
        try{
            sm = conn.prepareStatement(sql);
            ResultSet rs = sm.executeQuery();
            while(rs.next()){
                result.add(new Capacity(rs.getInt("id"),rs.getInt("size"),rs.getString("productPackage")));
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
        return result;
    }
}
