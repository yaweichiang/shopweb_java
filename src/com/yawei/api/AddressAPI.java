package com.yawei.api;

import com.yawei.bean.User;
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
        System.out.println("address subpath"+subPath);
        if(req.getSession().getAttribute("userid")!=null){
            String id = req.getSession().getAttribute("userid").toString();
            User user = new User(id);
            out.print(user.getRecipientAddresses());
        }else if(req.getSession().getAttribute("managerid")!=null && subPath!=null) {
            String idString = "^/[1-9]+[0-9]*$";
            if (Pattern.matches(idString, subPath)) {
                User user = new User(subPath.substring(1));
                out.print(user.getRecipientAddresses());
            }
        }
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
            String id = req.getSession().getAttribute("userid").toString();
            User user = new User(id);
            user.addRecipientAddress(json);
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
            User user = new User(req.getSession().getAttribute("userid").toString());
            user.removeRecipientAddress(obj.getInt("no"));
        }
    }
}
