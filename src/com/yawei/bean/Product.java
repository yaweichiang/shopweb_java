package com.yawei.bean;

import com.yawei.util.MySqlConnect;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Product extends JSONObject implements Serializable {
    static int nextProductNo = getNewProductNo();

    private int id; // 商品編號
    private String name; // 商品名稱
    private int inventory; // 商品庫存
    private int price; // 商品單價
    private String url; // 照片路徑
    private String introduction; // 商品介紹
    private String type; // 商品銷售狀態
    private int capacity_no; // 容量編號
    private String capacity; // 容量
    private int tote_no; // 運送編號
    private String isFreezing; // 是否冷凍

    //取得新商品編號
    private static int getNewProductNo(){
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql=String.format("select p_no from products order by p_no desc limit 1");
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
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return id;
    }

    // 舊有訂單建構 依照商品編號自資料庫取得商品資料建立商品物件
    public Product(int id){
        Connection conn = MySqlConnect.getMySql().getConn();
        String sql = String.format("select p_no as id,p_name as name,p_inventory as inventory," +
                "p_price as price,p_url as url,p_introduction as introduction,p_type as type," +
                "c_no as capacity_no,c_size as capacity,t_no as tote_no,if(t_name='常溫','false','true') as isFreezing " +
                "from products inner join (product_capacity,tote_type) using(c_no,t_no) where p_no = ?");
        PreparedStatement sm =null;
        try {
            sm = conn.prepareStatement(sql);
            sm.setObject(1,id);
            ResultSet rs = sm.executeQuery();
            while(rs.next()) {
                this.id = rs.getInt("id");
                this.name = rs.getString("name"); // 商品名稱
                this.inventory = rs.getInt("inventory"); // 商品庫存
                this.price = rs.getInt("price"); // 商品單價
                this.url = rs.getString("url"); // 照片路徑
                this.introduction = rs.getString("introduction"); // 商品介紹
                this.type = rs.getString("type"); // 商品銷售狀態
                this.capacity_no = rs.getInt("capacity_no"); // 容量編號
                this.capacity = rs.getString("capacity"); // 容量
                this.tote_no = rs.getInt("tote_no"); // 運送編號
                this.isFreezing = rs.getString("isFreezing"); // 是否冷凍
            }
            sm.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    // 新增商品建構 以前端傳送過來的商品資料建立商品物件
    public Product(String jsonString,String webPath) throws IOException {
        super(jsonString); //
        this.name = super.getString("name");
        this.price = super.getInt("price");
        this.inventory = super.getInt("inventory");
        this.type = super.getString("type");
        this.capacity = super.getString("capacity");
        this.tote_no = super.getInt("tote_type");
        this.introduction = super.getString("introduction");
        savePic(super.getString("url"),webPath); //存圖片 設定this.url
    }

    // 新商品建立 存入資料庫
    public void create(){
        if(this.id==0){
            this.id = nextProductNo++;
            Connection conn = MySqlConnect.getMySql().getConn();
            PreparedStatement sm = null;
            String sql = String.format("insert into products values(null,?,?,?,?,?,?,?,?)");
            System.out.println(sql);
            try {
                sm = conn.prepareStatement(sql);
                sm.setObject(1,this.name);
                sm.setObject(2,this.inventory);
                sm.setObject(3,this.capacity);
                sm.setObject(4,this.tote_no);
                sm.setObject(5,this.price);
                sm.setObject(6,this.url);
                sm.setObject(7,this.introduction);
                sm.setObject(8,this.type);
                sm.executeUpdate();
                conn.commit(); // 儲存變更
            } catch(SQLException e) {
                e.printStackTrace();
                try {
                    if(conn!=null) {
                        conn.rollback();//復原交易
                        this.id = 0;
                        nextProductNo--;
                    }
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
    // 商品更新 更新商品名稱 單價 庫存 類型 介紹
    public void update(String name,int price,int inventory,String type,String introduction){
        this.name = name;
        this.price = price;
        this.inventory = inventory;
        this.type = type;
        this.introduction = introduction;

        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("update products set p_name=?, p_price=?, p_inventory=?," +
                " p_type=?, p_introduction=? where p_no =?");
        try {
            sm = conn.prepareStatement(sql);
            sm.setObject(1,this.name);
            sm.setObject(2,this.price);
            sm.setObject(3,this.inventory);
            sm.setObject(4,this.type);
            sm.setObject(5,this.introduction);
            sm.setObject(6,this.id);
            sm.executeUpdate();
            conn.commit();
        } catch(SQLException e) {
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
    //儲存商品照片
    private void savePic(String picData,String webPath) throws IOException {
        //取得照片 base64資料
        String[] urlData =  picData.split(";");
        //副檔名
        String filetype = urlData[0].split("/")[1];
        //檔案字串 ajax傳遞時會將 "+" 替換成" " 將其復原 並將自傳前面的base64去掉
        String fileStr = urlData[1].replace(" ","+").replace("base64,","");
        //對檔案字串進行解碼成byte[] 存成圖片
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] pic = decoder.decodeBuffer(fileStr);
        //取得web容器的真實路徑 加上資料夾名稱及商品id 作為檔案儲存的path
        String savePath = webPath+"static/products/product"+id+"."+filetype;
        FileOutputStream productPic=null;
        try{
            productPic = new FileOutputStream(savePath);
            productPic.write(pic);
            //成功儲存圖片設定商品圖片url ,用來存到資料庫供讀取使用
            this.url = "../static/products/product"+id+"."+filetype;
        }catch (IOException e){
            e.printStackTrace();
//            out.print("error");
        }finally {
            productPic.close();
        }
    }
    // 取得所有商品
    public static List<Product> getAllProducts(){
        List<Product> result = new ArrayList<>();
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select p_no as id from products order by id");
        try{
            sm = conn.prepareStatement(sql);
            ResultSet rs = sm.executeQuery();
            while(rs.next()) {
                result.add(new Product(rs.getInt("id")));
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"name\":\"" + name + '\"' +
                ",\"inventory\":" + inventory +
                ",\"price\":" + price +
                ",\"url\":\"" + url + '\"' +
                ",\"intr\":\"" + introduction + '\"' +
                ",\"type\":\"" + type + '\"' +
                ",\"capacity_no\":" + capacity_no +
                ",\"capacity\":" + capacity +
                ",\"tote_no\":" + tote_no +
                ",\"isFreezing\":\"" + isFreezing + '\"' +
                '}';
    }
}