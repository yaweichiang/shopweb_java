package com.yawei.api;

import com.yawei.util.MySqlConnect;
import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;


@WebServlet("/products/*")
public class ProductsAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String subPath = req.getPathInfo();
        PrintWriter out = resp.getWriter();
        if(subPath == null){
            //取的全部的商品資訊
            JsonArray result =  MySqlConnect.getMySql().getAllProducts();
            out.print(result);
        }else{
            //取得指定id的商品資訊
            String idString = "^/[1-9]+[0-9]*$";
            if(Pattern.matches(idString,subPath)){
                JsonArray result =  MySqlConnect.getMySql().getProduct(subPath.substring(1));
                out.print(result);
            }else{
                out.print("error");
            }
        }
    }


}

