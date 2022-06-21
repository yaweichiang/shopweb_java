package com.yawei.api;

import com.yawei.util.MySqlConnect;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
            //取得下筆商品編號
            int id = MySqlConnect.getMySql().getNewProductNo();
            //取得前端上傳照片 base64資料
            String[] urlData =  obj.getString("url").split(";");
            //副檔名
            String filetype = urlData[0].split("/")[1];
            //檔案字串 ajax傳遞時會將 "+" 替換成" " 將其復原 並將自傳前面的base64去掉
            String fileStr = urlData[1].replace(" ","+").replace("base64,","");
            //對檔案字串進行解碼成byte[] 存成圖片
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] pic = decoder.decodeBuffer(fileStr);
            //取得web容器的真實路徑 加上資料夾名稱及商品id 作為檔案儲存的path
            String savePath = req.getServletContext().getRealPath("")+"static/products/product"+id+"."+filetype;
            //建立讀取圖片的相對路徑,用來存到資料庫供讀取使用
            String readPath = "../static/products/product"+id+"."+filetype;

            try{
                FileOutputStream productPic = new FileOutputStream(savePath);
                productPic.write(pic);
                productPic.close();
                JsonArray result = MySqlConnect.getMySql().createProduct(obj,readPath);
                System.out.print(result);
                out.print(result);
            }catch (IOException e){
                out.print("error");
            }finally {

            }





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

