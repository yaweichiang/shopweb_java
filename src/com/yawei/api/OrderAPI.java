package com.yawei.api;

import com.yawei.bean.OrderList;
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
        if(subPath!=null){
            OrderList orderlist = new OrderList(no);
            if(req.getSession().getAttribute("managerid")!=null){
                out.print(orderlist);
            }else if(req.getSession().getAttribute("userid")!=null){
                String id = req.getSession().getAttribute("userid").toString();
                if(orderlist.getId() == Integer.parseInt(id))
                    out.print(orderlist);
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
                OrderList orderlist = new OrderList(obj.get("order_no").toString());
                if(String.valueOf(orderlist.getId()).equals(id))
                    orderlist.cancel();
            }else{
                out.print("error");
            }
        }else if(subPath.equals("update")){
            if(req.getSession().getAttribute("managerid")!=null) {
                OrderList orderlist = new OrderList(obj.get("order_no").toString());
                if(orderlist.getSendDate().equals("尚未出貨")&&obj.get("send_no").toString()!="") {
                    System.out.println("寄出");
                    System.out.println(orderlist.getSendDate().equals("尚未出貨"));
                    System.out.println(orderlist.getSendDate());
                    System.out.println(obj.get("send_no").toString()=="");
                    System.out.println(obj.get("send_no"));
                    orderlist.send(obj.get("send_no").toString(), obj.get("remark").toString());
                }else {
                    System.out.println("更新");
                    System.out.println(orderlist.getSendDate());
                    System.out.println(obj.get("send_no"));
                    orderlist.update(obj.get("send_no").toString(), obj.get("remark").toString());
                }
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
            //依照前端傳遞過來的訂單資料(json) 建立訂單物件 存入資料庫
            OrderList orderlist = new OrderList(json,id);
            orderlist.create();
            out.print("ok");

        }else{
            out.print("fail");
        }
    }
}
