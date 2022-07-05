package com.yawei.bean;

import com.yawei.util.MySqlConnect;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderList extends JSONObject implements Serializable {

    static int nextOrderNo = getNewOrderListNo();

    private int no; //訂單編號
    private int id; //會員編號
    private int payID; //付款方式編號
    private int toteNo; //運送方式編號
    private String recipient; //收件人資訊
    private int total; //訂單總額
    private List<OrderProduct> productsList = new ArrayList<>();
    private String payNo; //金流編號
    private String type; //訂單狀態
    private String payType; //付款方式名稱
    private String phone; //會員電話
    private String name; // 會員姓名
    private String remark; //訂單備註
    private String sendNo; //貨運單號
    private String sendDate; //出貨日期
    private String orderDate;//訂購日期


    //新增訂單建構 以前端傳送過來的訂單資料建立訂單物件
    public OrderList(String jsonString, String id) {
        super(jsonString);
        this.id = Integer.parseInt(id);
        this.payID = super.getInt("payID");
        this.toteNo = super.getInt("toteNo");
        this.recipient = super.getString("name") + "/" + super.getString("address") + "/" + super.getString("phone");
        new JSONArray(super.get("products").toString()).forEach(product->this.productsList.add(new OrderProduct(product.toString())));
        this.total = this.countTotal();
    }

    //舊有訂單建構 以訂單編號自資料庫取得訂單資料建立訂單物件
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

        try{
            sm = conn.prepareStatement(sql);
            sm.setObject(1,orderListNo);
            ResultSet rs = sm.executeQuery();
            HashMap<String, HashMap> result  = new HashMap();
            ResultSetMetaData rsmd =  rs.getMetaData();
            while(rs.next()){
                if(this.no==0){//訂單編號 key 不存在
                    this.no=rs.getInt("no");
                    this.phone=rs.getString("phone");
                    this.id =rs.getInt("id");//
                    this.payID = rs.getInt("payID");
                    this.payType=rs.getString("payType");
                    this.orderDate=rs.getString("orderDate");
                    this.sendNo=rs.getString("sendNo")==null?"":rs.getString("sendNo");
                    this.sendDate=rs.getString("sendDate")==null?"尚未出貨":rs.getString("sendDate");
                    this.type=rs.getString("type");
                    this.recipient=rs.getString("recipient");
                    this.total=rs.getInt("total");
                    this.name=rs.getString("name");
                    this.payNo =rs.getString("payNo")==null?"":rs.getString("payNo");
                    this.remark=rs.getString("remark")==null?"":rs.getString("remark");

                    this.productsList.add(new OrderProduct(rs.getString("p_name"),rs.getInt("p_no"),rs.getInt("c_size"),rs.getInt("b_price"),rs.getInt("b_num")));
                }else{// 訂單編號 key已存在 插入新品項資料即可
                    this.productsList.add(new OrderProduct(rs.getString("p_name"),rs.getInt("p_no"),rs.getInt("c_size"),rs.getInt("b_price"),rs.getInt("b_num")));
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    //取得新訂單編號
    private static int getNewOrderListNo(){
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql=String.format("select o_no from order_list order by o_no desc limit 1");
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


    //依照訂單商品資料 計算商品金額 運費 手續費用 合計金額  （建立新訂單時使用）
    private int countTotal() {
//        int[] toteInfo = MySqlConnect.getMySql().getToteInfo(this.toteNo);
//        List<Tote> toteInfo = null;
        int[] toteInfo = new Tote(this.toteNo).getInfo();
        int payFee =new Pay(this.payID).getFee();
        int result = 0;

        for (OrderProduct product : this.productsList) {
            int price = new Product(product.getId()).getPrice();
            result += price*product.getAmount();
        }
        result += (result>=toteInfo[0]) ? payFee : (toteInfo[1]+payFee);
        return result;
    }

    //新訂單建立 存入資料庫
    public void create(){
        if(this.no==0) {
            //取得最新訂單編號
            this.no = nextOrderNo++;
            Connection conn = MySqlConnect.getMySql().getConn();
            PreparedStatement sm = null;
            String sql = String.format("insert into order_list(o_no,m_no,pay_id,t_no,o_recipient,o_total) values(?,?,?,?,?,?)");
            try {
                sm = conn.prepareStatement(sql);
                sm.setObject(1, no);
                sm.setObject(2, this.id);
                sm.setObject(3, this.payID);
                sm.setObject(4, this.toteNo);
                sm.setObject(5, this.recipient);
                sm.setObject(6, this.total);
                sm.executeUpdate();

                for (OrderProduct product : this.productsList) {
                    sql = String.format("insert into order_products values(?,?,?,?)");
                    sm = conn.prepareStatement(sql);
                    sm.setObject(1, no);
                    sm.setObject(2, product.getId());
                    sm.setObject(3, product.getAmount());
                    sm.setObject(4, product.getPrice());
                    sm.executeUpdate();
                }
                conn.commit();
                System.out.println("新增訂單");
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    if (conn != null) {
                        conn.rollback();//復原交易
                        this.no = 0;
                        nextOrderNo--;
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } finally {
                try {
                    sm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //取消訂單
    public void cancel(){
        if(this.type.equals("order")) { //訂單狀態為order才能取消 cancel send 都不能取消
            Connection conn = MySqlConnect.getMySql().getConn();
            PreparedStatement sm = null;
            String sql = String.format("update order_list set o_type = 'cancel' where o_no = ?");
            try {
                sm = conn.prepareStatement(sql);
                sm.setObject(1, this.no);
                sm.executeUpdate();
                conn.commit();
                System.out.println("取消訂單");
            } catch (SQLException e) {
                e.printStackTrace();
                try{
                    conn.rollback();
                }catch (SQLException ex){
                    ex.printStackTrace();
                }
            } finally {
                try {
                    sm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //訂單出貨 更新出貨日期 出貨單號 備註
    public void send(String sendNo,String remark){
        if(this.type.equals("order")){ // 訂單狀態為order 才能出貨 send cancel 都不能操作出貨
            System.out.println("send");
            this.sendNo = sendNo;
            this.remark = remark;//新增貨運單號 及備註
            Connection conn = MySqlConnect.getMySql().getConn();
            PreparedStatement sm = null;
            //更新資料庫中的 出貨時間 貨運單號及備註
            String sql = String.format("update order_list set o_senddate=current_timestamp ,o_type = 'send' ,o_sendno = ?,o_remark = ? where o_no=?");
            try {
                sm = conn.prepareStatement(sql);
                sm.setObject(1,this.sendNo);
                sm.setObject(2,this.remark);
                sm.setObject(3,this.no);
                sm.executeUpdate();
                conn.commit();
            }catch(SQLException e){
                e.printStackTrace();
                try {
                    conn.rollback();
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

    //訂單更新 更新出貨單號 備註
    public void update(String sendNo,String remark){
        this.remark = remark;//新增貨運單號 及備註
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        //更新資料庫中的 出貨時間 貨運單號及備註
        try {
            if(sendNo.equals(this.sendNo) ){ //貨運單號未變動 更新備註
                String sql = String.format("update order_list set o_remark = ? where o_no=?");
                sm = conn.prepareStatement(sql);
                sm.setObject(1,this.remark);
                sm.setObject(2,this.no);
            }else{ //更新備註及貨運單號
                this.sendNo = sendNo;
                String sql = String.format("update order_list set o_sendno = ?,o_remark = ? where o_no=?");
                sm = conn.prepareStatement(sql);
                sm.setObject(1,this.sendNo);
                sm.setObject(2,this.remark);
                sm.setObject(3,this.no);
            }
            sm.executeUpdate();
            conn.commit();
        }catch(SQLException e){
            e.printStackTrace();
            try {
                conn.rollback();
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

    //依照 會員編號取得訂單資料
    public static List<OrderList> getOrderListByUserID(String id){
        List<OrderList> result = new ArrayList<>();
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select o_no as no from members inner join order_list using(m_no) where m_no=? order by no");
        try{
            sm = conn.prepareStatement(sql);
            sm.setObject(1,id);
            ResultSet rs = sm.executeQuery();
            while (rs.next()) {
                result.add(new OrderList(rs.getString("no")));
                }


        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return result;
        }
    }

    //依照 日期取得訂單資料
    public static List<OrderList> getOrderListByDate(String date){
        List<OrderList> result = new ArrayList<>();
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select o_no as no from order_list where o_orderdate like ? order by o_orderdate");

        try {
            sm = conn.prepareStatement(sql);
            sm.setObject(1,date+" %");
            ResultSet rs = sm.executeQuery();
            while (rs.next()) {
                result.add(new OrderList(rs.getString("no")));
            }


        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return result;
        }
    }

    //依照 天數區間取得訂單資料
    public static List<OrderList> getOrderListByDays(String days){
        List<OrderList> result = new ArrayList<>();
        Connection conn = MySqlConnect.getMySql().getConn();
        PreparedStatement sm = null;
        String sql = String.format("select o_no as no from order_list where o_orderdate >= current_date-? order by o_orderdate;");

        try {
            sm = conn.prepareStatement(sql);
            sm.setObject(1,days);
            ResultSet rs = sm.executeQuery();
            while (rs.next()) {
                result.add(new OrderList(rs.getString("no")));
            }


        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                sm.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return result;
        }
    }

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
        return productsList;
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
                ",\"productsList\":" + productsList +
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

    private class OrderProduct extends JSONObject{
        private String name; //商品名稱
        private int id;//商品邊號
        private int price;//購買單價
        private int amount;//購買數量
        private int capacity;//商品容量

        OrderProduct(String jsonString){
            super(jsonString);
            this.name = super.getString("name");
            this.id = super.getInt("id");
            this.price = super.getInt("price");
            this.amount = super.getInt("amount");
            this.capacity = super.getInt("capacity");
        }
        OrderProduct(String name,int id,int price,int amount,int capacity){
            this.name = name;
            this.id = id;
            this.price = price;
            this.amount = amount;
            this.capacity = capacity;
        }
        public String getName() {
            return name;
        }
        public int getId() {
            return id;
        }
        public int getPrice() {
            return price;
        }
        public int getAmount() {
            return amount;
        }
        public int getCapacity() {
            return capacity;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"name\":\"" + name + '\"' +
                    ",\"id\":" + id +
                    ",\"price\":" + price +
                    ",\"amount\":" + amount +
                    ",\"capacity\":" + capacity +
                    '}';
        }
    }
}


