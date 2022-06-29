package com.yawei.util;

import org.json.JSONObject;
import sun.misc.BASE64Decoder;

import javax.json.JsonArray;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Product {
    private int id;
    private String name;
    private int inventory;
    private int price;
    private String url;
    private String intr;
    private String type;
    private int capacity;
    private String isFreezing;


    //資料庫商品資料建構
    public Product(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.inventory = rs.getInt("inventory");
        this.price = rs.getInt("price");
        this.url = rs.getString("url");
        this.intr = rs.getString("intr")==null?"null":rs.getString("intr");
        this.type = rs.getString("type");//這邊應該可以轉成boolean
        this.capacity = rs.getInt("capacity");
        this.isFreezing = rs.getString("isFreezing");
    }
    //新建商品資料建構
    public Product(JSONObject object,String savePath) throws IOException {
        this.id = MySqlConnect.getMySql().getNewProductNo();
        savePic(object.getString("url"),savePath);
    }
    //儲存商品照片
    private void savePic(String url,String savePath) throws IOException {
        //取得前端上傳照片 base64資料
        String[] urlData = url.split(";");
        //副檔名
        String filetype = urlData[0].split("/")[1];
        //圖片base64
        String fileStr = urlData[1].replace(" ","+").replace("base64,","");
        //對檔案字串進行解碼成byte[] 存成圖片
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] pic = decoder.decodeBuffer(fileStr);
        FileOutputStream productPic=null;
        try{
            productPic = new FileOutputStream(savePath);
            productPic.write(pic);
            this.url = "../static/products/product"+this.id+"."+filetype;
        }catch (IOException e){
            this.url = "";
        }finally {
            productPic.close();
        }
    }
    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"name\":\"" + name + "\"" +
                ",\"inventory\":" + inventory +
                ",\"price\":" + price +
                ",\"url\":\"" + url + "\"" +
                ",\"intr\":\"" + intr + "\"" +
                ",\"type\":\"" + type + "\"" +
                ",\"capacity\":" + capacity +
                ",\"isFreezing\":\"" + isFreezing + "\"" +
                '}';
    }
}
