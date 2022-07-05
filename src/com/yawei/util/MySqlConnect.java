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


public class MySqlConnect {
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



}