package com.yawei.api;

import com.yawei.bean.User;
import com.yawei.util.MySqlConnect;
import org.json.JSONArray;

import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/search/*")
public class SearchAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        JsonArray result;
        if(req.getSession().getAttribute("managerid")!=null){
            if(!req.getPathInfo().equals("/")) {
                String subPath = req.getPathInfo();
                out.print(User.search(subPath.substring(1)));
//                result = MySqlConnect.getMySql().searchUsers(subPath.substring(1));
//                out.print(result);
            }else{
                out.print("null");
            }
        }else{
            out.print("error");
        }



    }
}
