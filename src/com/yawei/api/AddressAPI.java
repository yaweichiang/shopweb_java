package com.yawei.api;

import com.yawei.util.MySqlConnect;
import org.json.JSONObject;

import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.regex.Pattern;

@WebServlet("/address/*")
public class AddressAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String subPath = req.getPathInfo();
        JsonArray result = null;
        if(req.getSession().getAttribute("userid")!=null){
            String id = req.getSession().getAttribute("userid").toString();
            result = MySqlConnect.getMySql().getUserAddress(id);
        }else if(req.getSession().getAttribute("managerid")!=null) {
            String idString = "^/[1-9]+[0-9]*$";
            if (Pattern.matches(idString, subPath)) {
                result = MySqlConnect.getMySql().getUserAddress(subPath.substring(1));
            }
        }
        out.print(result);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getSession().getAttribute("userid")!=null) {
            req.setCharacterEncoding("UTF-8");
            String json = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            if (br != null) {
                json = br.readLine();
            }
            JSONObject obj = new JSONObject(json);
            System.out.println(obj);
            MySqlConnect.getMySql().createAddress(obj);
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getSession().getAttribute("userid")!=null) {
            req.setCharacterEncoding("UTF-8");
            String json = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            if (br != null) {
                json = br.readLine();
            }
            JSONObject obj = new JSONObject(json);
            System.out.println(obj);
            MySqlConnect.getMySql().deleteAddress(obj);
        }
    }
}
