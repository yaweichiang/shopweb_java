package com.yawei.api;

import com.yawei.util.MySqlConnect;
import org.json.JSONObject;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
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
            System.out.print(idString);
            if(Pattern.matches(idString,subPath)){
                JsonArray result =  MySqlConnect.getMySql().getProduct(subPath.substring(1));
                System.out.print(result);
                out.print(result);

            }else{
                out.print("error");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=UTF-8");
        String subPath = req.getPathInfo();
        PrintWriter out = resp.getWriter();
        if(subPath == null){
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            String json = "";
            if (br != null) {
                json = br.readLine();
            }
            JSONObject obj = new JSONObject(json);
            System.out.print(obj);
            JsonArray result = MySqlConnect.getMySql().createProduct(obj);
            System.out.print(result);
            out.print(result);
        }else{
            out.print("error");
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=UTF-8");
        String subPath = req.getPathInfo();
        PrintWriter out = resp.getWriter();
        String idString = "^/[1-9]+[0-9]*$";
        if(Pattern.matches(idString,subPath)){
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            String json = "";
            if (br != null) {
                json = br.readLine();
            }
            JSONObject obj = new JSONObject(json);
            System.out.print(obj);
            JsonArray result = MySqlConnect.getMySql().updateProduct(obj);
            System.out.print(result);
            out.print(result);
        }else{
            out.print("error");
        }
    }
}

