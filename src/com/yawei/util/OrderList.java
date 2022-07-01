package com.yawei.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderList extends JSONObject{

    private int no; //訂單編號
    private int id; //會員編號
    private int payID; //付款方式編號
    private int toteNo; //運送方式編號
    private String recipient; //收件人資訊
    private int total; //訂單總額
    private List<OrderProduct> productsLists = new ArrayList<OrderProduct>(); //訂購商品列表

    private  String payNo; //金流編號
    private String type; //訂單狀態
    private String payType; //付款方式名稱
    private String phone; //會員電話
    private String name; // 會員姓名
    private String remark; //訂單備註
    private String sendNo; //貨運單號
    private String sendDate; //出貨日期
    private String orderDate;//訂購日期


    //新增訂單建構

    public OrderList(String jsonString, String id) {
        super(jsonString);
        //取得最新訂單編號
        this.no = MySqlConnect.getMySql().getNewOrderListNo();
        this.id = Integer.parseInt(id);
        this.payID = super.getInt("payID");
        this.toteNo = super.getInt("toteNo");
        this.recipient = super.getString("name") + "/" + super.getString("address") + "/" + super.getString("phone");
        new JSONArray(super.get("products").toString()).forEach(product->this.productsLists.add(new OrderProduct(product.toString())));
        this.total = this.countTotal();
    }

    public static void main(String[] args) {
        System.out.println(new OrderList("1"));
    }
    public OrderList(String orderListNo){
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select * from" +
                "(select o_no as no," +//訂單編號
                "m_no as id," + //會員編號
                "m_phone as phone," +//會員電話
                "m_name as name," + //會員姓名
                "pay_no as payNo," + //金流編號
                "o_remark as remark," +//備註
                "pay_id as payID," + //付款方式編號
                "pay_name as payType," + // 付款方式名稱
                "o_orderdate as orderDate," +//訂購日期
                "o_sendno as sendNo," +//貨運單號
                "o_senddate as sendDate," +//寄送日期
                "o_type as type," +//訂單狀態
                "o_recipient as recipient," +//收件人資訊
                "o_total as total from" +//訂單總額
                "(select * from order_list inner join members using(m_no) inner join pay_type using(pay_id) ) as temp )as temp2" +
                "inner join" +
                "(select o_no as no," +
                "p_no," + //商品編號
                "p_name,c_size ," + //商品名稱 商品容量
                "b_price ," +//購買單價
                "b_num  from" +
                "( (order_products inner join products using(p_no))inner join product_capacity using(c_no) )" +
                ") as temp4 using(no) where no = ? order by orderDate;");

        JSONArray lists = null;
        try{
            sm = conn.prepareStatement(sql);
            sm.setObject(1,orderListNo);
            ResultSet rs = sm.executeQuery();
            HashMap<String, HashMap> result  = new HashMap();
            ResultSetMetaData rsmd =  rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while(rs.next()){
                if(this.no==0){//訂單編號 key 不存在
                    this.no=rs.getInt("no");
                    this.phone=rs.getString("phone");
                    this.id =rs.getInt("id");//
                    this.payID = rs.getInt("payID");
                    this.payType=rs.getString("payType");
                    this.orderDate=rs.getString("orderDate");
                    this.sendNo=rs.getString("sendNo")==null?"":rs.getString("sendNo");
                    this.sendDate=rs.getDate("sendDate")==null?"尚未出貨":rs.getString("sendDate");
                    this.type=rs.getString("type");
                    this.recipient=rs.getString("recipient");
                    this.total=rs.getInt("total");
                    this.name=rs.getString("name");
                    this.payNo =rs.getString("payNo")==null?"":rs.getString("payNo");
                    this.remark=rs.getString("remark")==null?"":rs.getString("remark");

                    this.productsLists.add(new OrderProduct(rs.getString("p_name"),rs.getInt("p_no"),rs.getInt("c_size"),rs.getInt("b_price"),rs.getInt("b_num")));
                }else{// 訂單編號 key已存在 插入新品項資料即可
                    this.productsLists.add(new OrderProduct(rs.getString("p_name"),rs.getInt("p_no"),rs.getInt("c_size"),rs.getInt("b_price"),rs.getInt("b_num")));
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    //依照訂單資料 計算商品金額 運費 手續費用 合計金額
    private int countTotal() {
        int[] toteInfo = MySqlConnect.getMySql().getToteInfo(this.toteNo);
        int payFee = MySqlConnect.getMySql().getPayFee(this.payID);
        int result = 0;

        for (OrderProduct product : this.productsLists) {
            int price = MySqlConnect.getMySql().getProductPrice(product.getId());
            result += price*product.getAmount();
        }
        result += (result>=toteInfo[0]) ? payFee : (toteInfo[1]+payFee);
        return result;
    }
    //新訂單建立 存入資料庫
    public void create(){
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("insert into order_list(o_no,m_no,pay_id,t_no,o_recipient,o_total) values(?,?,?,?,?,?)");
        try{
            sm = conn.prepareStatement(sql);
            sm.setObject(1,no);
            sm.setObject(2,this.id);
            sm.setObject(3,this.payID);
            sm.setObject(4,this.toteNo);
            sm.setObject(5,this.recipient);
            sm.setObject(6,this.total);
            sm.executeUpdate();

            for(OrderProduct product :this.productsLists){
                sql = String.format("insert into order_products values(?,?,?,?)");
                sm = conn.prepareStatement(sql);
                sm.setObject(1,no);
                sm.setObject(2,product.getId());
                sm.setObject(3,product.getAmount());
                sm.setObject(4,product.getPrice());
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
    //舊訂單更新 存入資料庫
    public void update(){}


    public int getNo() {
        return no;
    }

    public int getId() {
        return id;
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

    public List<OrderProduct> getProductsList() {
        return productsLists;
    }

    public List<OrderProduct> getProductsLists() {
        return productsLists;
    }

    public String getPayNo() {
        return payNo;
    }

    public String getType() {
        return type;
    }

    public String getPayType() {
        return payType;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getRemark() {
        return remark;
    }

    public String getSendNo() {
        return sendNo;
    }

    public String getSendDate() {
        return sendDate;
    }

    public String getOrderDate() {
        return orderDate;
    }

    @Override
    public String toString() {
        return "{" +
                "\"no\":" + no +
                ",\"id\":" + id +
                ",\"payID\":" + payID +
                ",\"toteNo\":" + toteNo +
                ",\"recipient\":\"" + recipient + '\"' +
                ",\"total\":" + total +
                ",\"productsLists\":" + productsLists +
                ",\"payNo\":\"" + payNo + '\"' +
                ",\"type\":\"" + type + '\"' +
                ",\"payType\":\"" + payType + '\"' +
                ",\"phone\":\"" + phone + '\"' +
                ",\"name\":\"" + name + '\"' +
                ",\"remark\":\"" + remark + '\"' +
                ",\"sendNo\":\"" + sendNo + '\"' +
                ",\"sendDate\":\"" + sendDate + '\"' +
                ",\"orderDate\":\"" + orderDate + '\"' +
                '}';
    }
}


