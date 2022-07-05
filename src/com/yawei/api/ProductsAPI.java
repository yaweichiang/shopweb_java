package com.yawei.api;

import com.yawei.bean.Product;
import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.regex.Pattern;


@WebServlet("/products/*")
public class ProductsAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String subPath = req.getPathInfo();
        PrintWriter out = resp.getWriter();
        if(subPath == null){
            //取得全部的商品資訊
            List<Product> result = Product.getAllProducts();
            out.print(result);
        }else{
            //取得指定id的商品資訊
            String idString = "^/[1-9]+[0-9]*$";
            if(Pattern.matches(idString,subPath)){
                out.print(new Product(Integer.parseInt(subPath.substring(1))));

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
        if(subPath == null && req.getSession().getAttribute("managerid")!=null){
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            String json = "";
            if (br != null) {
                json = br.readLine();
            }
            String webPath = req.getServletContext().getRealPath("");
            Product product = new Product(json, webPath);
            product.create();
            out.print(Product.getAllProducts());
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
            Product product = new Product(obj.getInt("id"));
            //新資料
            String name = obj.getString("name");
            int price = Integer.parseInt(obj.getString("price"));
            int inventory = Integer.parseInt(obj.getString("inventory"));
            String type = obj.getString("type");
            String introduction = obj.getString("introduction");
            //更新
            product.update(name,price,inventory,type,introduction);
        }else{
            out.print("error");
        }
    }
}

