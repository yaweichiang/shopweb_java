package com.yawei.api;


import com.yawei.util.MySqlConnect;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/check/*")
public class UtilAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String subPath = req.getPathInfo();
        if(subPath == null){
            //檢查會員登入狀況 登入 且已有登記電話 回傳true  //登入未登記電話回傳nophone //未登入 回傳false

            if(req.getSession().getAttribute("userid")!=null){
                String id = req.getSession().getAttribute("userid").toString();
                if(MySqlConnect.getMySql().checkPhone(id)){
                    out.print("true");
                }else{
                    out.print("nophone");
                }
            }else{
                out.print("false");
            }
        }else{
            out.print("error");
        }
    }

}
