package com.yawei.api;

import com.yawei.bean.OrderList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/orders/*")
public class OrderListAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String[] subPath = req.getPathInfo().split("/");
        if(subPath[1].equals("id")){
            //依照會員id 查詢所有訂單
            if(req.getSession().getAttribute("userid")!=null){
                //會員查詢個人訂單
                String id = req.getSession().getAttribute("userid").toString();
                if(id.equals(subPath[2])) {
                    //確認查詢人與所查詢id為相同 則回傳查詢訂單資料
                    out.print(OrderList.getOrderListByUserID(id));
                }else{
                    out.print("error");
                }
            }else if(req.getSession().getAttribute("managerid")!=null){
                //管理者查詢會員訂單
                out.print(OrderList.getOrderListByUserID(subPath[2]));

            }else{
                out.print("error");
            }
        }else if(subPath[1].equals("date")){
            //依照日期查詢訂單
            if(req.getSession().getAttribute("managerid")!=null){
                out.print(OrderList.getOrderListByDate(subPath[2]));
            }else{
                out.print("error");
            }
        }else if(subPath[1].equals("days")){
            //依照天數查詢訂單
            if(req.getSession().getAttribute("managerid")!=null){
                out.print(OrderList.getOrderListByDays(subPath[2]));
            }else{
                out.print("error");
            }
        }else{
            out.print("error");
        }

    }
}
