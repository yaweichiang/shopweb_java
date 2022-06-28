package com.yawei.api;

import com.yawei.util.MySqlConnect;
import com.yawei.util.OrderList;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@WebServlet("/order/*")
public class OrderAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String subPath = req.getPathInfo();
        String no = subPath.substring(1);
        JSONArray result = null;
        if(subPath!=null){
            if(req.getSession().getAttribute("managerid")!=null){
                result = MySqlConnect.getMySql().getOrderListByNoForManager(no);
                out.print(result.toString());
            }else if(req.getSession().getAttribute("userid")!=null){
                String id = req.getSession().getAttribute("userid").toString();
                result = MySqlConnect.getMySql().getOrderListByNo(no,id);
                out.print(result.toString());
            }else{
                out.print("error");
            }
        }else{
            out.print("error");
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String subPath = req.getPathInfo().substring(1);
        String json = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        if(br!=null){
            json = br.readLine();
        }
        JSONObject obj = new JSONObject(json);
        if(subPath.equals("cancel")){
            if(req.getSession().getAttribute("userid")!=null) {
                String id = req.getSession().getAttribute("userid").toString();
                System.out.println("cancel" + obj.get("order_no") + ";id=" +id );
                MySqlConnect.getMySql().cancelOrder(obj.get("order_no").toString(),id);
            }else{
                out.print("error");
            }
        }else if(subPath.equals("update")){
            if(req.getSession().getAttribute("managerid")!=null) {
                System.out.println("update:" + obj.get("order_no"));
                System.out.println("obj:" + obj);
                MySqlConnect.getMySql().updateOrder(obj);
            }else{
                out.print("error");
            }
        }else{
            out.print("error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String json = "";
        if(req.getSession().getAttribute("userid")!=null) {
            String id = req.getSession().getAttribute("userid").toString();
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            if (br != null) {
                json = br.readLine();
            }
            JSONObject obj = new JSONObject(json);
            int total = obj.getInt("total");
            //依照前端傳遞過來的訂單資料(json) 建立訂單物件 存入資料庫
            OrderList orderlist = new OrderList(obj,id);
            orderlist.save();
            out.print("ok");

        }else{
            out.print("fail");
        }
    }
}
