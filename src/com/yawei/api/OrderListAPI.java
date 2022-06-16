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
            if(req.getSession().getAttribute("userid")!=null){
                String id = req.getSession().getAttribute("userid").toString();
                System.out.println("user=>"+id+","+subPath[2]);
                System.out.println(id.equals(subPath[2]));
                if(id.equals(subPath[2])) {
                    result = MySqlConnect.getMySql().getOrderListByMemberId(id);
                    System.out.println(result);
                    out.print(result.toString());
                }else{
                    out.print("error");
                }
            }else if(req.getSession().getAttribute("managerid")!=null){
                System.out.println("user=>"+subPath[1]);
                result = MySqlConnect.getMySql().getOrderListByMemberIdforManager(subPath[2]);
                System.out.println(result);
                out.print(result.toString());
            }else{
                out.print("error");
            }
        }else if(subPath[1].equals("date")){
            if(req.getSession().getAttribute("managerid")!=null){
                result = MySqlConnect.getMySql().getOrderListByDate(subPath[2]);
                out.print(result);
            }else{
                out.print("error");
            }
        }else if(subPath[1].equals("days")){
            if(req.getSession().getAttribute("managerid")!=null){
                result = MySqlConnect.getMySql().getOrderListByDays(subPath[2]);
                out.print(result);
            }else{
                out.print("error");
            }
        }else{
            out.print("error");
        }

    }
}
