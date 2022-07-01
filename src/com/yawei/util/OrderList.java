package com.yawei.util;

import com.sun.corba.se.spi.ior.ObjectKey;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderList {
    private int no;
    private int userid;
    private int payID;
    private int toteNo;
    private String recipient;
    private int total;
    private JSONArray products;

    //新增訂單建構
    public OrderList(JSONObject obj, String userid) {
        //取得最新訂單編號
        this.no = MySqlConnect.getMySql().getNewOrderListNo();
        this.userid = Integer.parseInt(userid);
        this.payID = obj.getInt("payID");
        this.toteNo = obj.getInt("toteNo");
        this.recipient = obj.getString("name") + "/" + obj.getString("address") + "/" + obj.getString("phone");
        this.products = new JSONArray(obj.get("products").toString());
        this.total = this.countTotal();
    }

    //依照訂單資料 計算商品金額 運費 手續費用 合計金額
    private int countTotal() {
        int[] toteInfo = MySqlConnect.getMySql().getToteInfo(this.toteNo);
        int payFee = MySqlConnect.getMySql().getPayFee(this.payID);
        int result = 0;
        for (Object item : this.products) {
            JSONObject product = new JSONObject(item.toString());
            int price = MySqlConnect.getMySql().getProductPrice(product.getInt("id"));
            result += price*product.getInt("amount");
        }
        result += (result>=toteInfo[0]) ? payFee : (toteInfo[1]+payFee);
        return result;
    }
    public void save(){
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("insert into order_list(o_no,m_no,pay_id,t_no,o_recipient,o_total) values(?,?,?,?,?,?)");
        try{
            sm = conn.prepareStatement(sql);
            sm.setObject(1,no);
            sm.setObject(2,this.userid);
            sm.setObject(3,this.payID);
            sm.setObject(4,this.toteNo);
            sm.setObject(5,this.recipient);
            sm.setObject(6,this.total);
            sm.executeUpdate();
            Object json = this.products;
            JSONArray products = new JSONArray(json.toString());
            for(Object obj :products){
                JSONObject product = new JSONObject(obj.toString());
                sql = String.format("insert into order_products values(?,?,?,?)");
                sm = conn.prepareStatement(sql);
                sm.setObject(1,no);
                sm.setObject(2,product.getInt("id"));
                sm.setObject(3,product.getInt("amount"));
                sm.setObject(4,product.getInt("price"));
                sm.executeUpdate();
            }
            conn.commit();
            System.out.println("新增完成");
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
                "\"no\":" + no +
                ",\"userid\":" + userid +
                ",\"payID\":" + payID +
                ",\"toteNo\":" + toteNo +
                ",\"recipient\":\"" + recipient + "\"" +
                ",\"total\":" + total +
                ",\"products\":" + products +
                "}";
    }
    public int getNo() {
        return no;
    }

    public int getUserid() {
        return userid;
    }

    public int getPayID() {
        return payID;
    }

    public int getToteNo() {
        return toteNo;
    }

    public String getRecipient() {
        return recipient;
    }

    public int getTotal() {
        return total;
    }

    public JSONArray getProducts() {
        return products;
    }

}


