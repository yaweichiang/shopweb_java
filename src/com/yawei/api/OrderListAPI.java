package com.yawei.api;

import com.yawei.util.MySqlConnect;
import org.json.JSONArray;

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
        JSONArray result = null;
        for(String str : subPath){
            System.out.println("=>"+str);
        }
        if(subPath[1].equals("id")){
            //依照會員id 查詢所有訂單
            if(req.getSession().getAttribute("userid")!=null){
                //會員查詢個人訂單
                String id = req.getSession().getAttribute("userid").toString();
                System.out.println("個人查詢全部訂單user=>"+id+","+subPath[2]);
                System.out.println(id.equals(subPath[2]));
                if(id.equals(subPath[2])) {
                    //確認查詢人與所查詢id為相同 則進行查詢
                    result = MySqlConnect.getMySql().getOrderListByMemberId(id);
                    System.out.println(result);
                    out.print(result.toString());
                }else{
                    out.print("error");
                }
            }else if(req.getSession().getAttribute("managerid")!=null){
                //管理者查詢會員訂單
                System.out.println("管理者查詢會員訂單user=>"+subPath[1]);
                result = MySqlConnect.getMySql().getOrderListByMemberIdforManager(subPath[2]);
                System.out.println(result);
                out.print(result.toString());
            }else{
                out.print("error");
            }
        }else if(subPath[1].equals("date")){
            //依照日期查詢訂單
            if(req.getSession().getAttribute("managerid")!=null){
                System.out.println("管理者依照日期查詢訂單 date=>"+subPath[2]);
                result = MySqlConnect.getMySql().getOrderListByDate(subPath[2]);
                out.print(result.toString());
            }else{
                out.print("error");
            }
        }else if(subPath[1].equals("days")){
            //依照天數查詢訂單
            if(req.getSession().getAttribute("managerid")!=null){
                System.out.println("管理者依照天數查詢訂單 date=>"+subPath[2]);
                result = MySqlConnect.getMySql().getOrderListByDays(subPath[2]);
                System.out.println(result);
                out.print(result.toString());
            }else{
                out.print("error");
            }
        }else{
            out.print("error");
        }

    }
}
