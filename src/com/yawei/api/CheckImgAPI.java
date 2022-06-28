package com.yawei.api;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

@WebServlet("/checkImg")
public class CheckImgAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Cache-Control","no-store");
        resp.setContentType("text/html;charset=UTF-8");
        int width = 60;
        int height = 30;
        Random random = new Random();
        //取得畫圖空間
        BufferedImage pic = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        //取得畫筆
        Graphics g = pic.getGraphics();
        //設定顏色 畫矩形
        g.setColor(new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256)));
        g.fillRect(0,0,width,height);

        //設定字體
        g.setFont(new Font("Times New Roman",Font.PLAIN,18));
        g.setColor(new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256)));
        String number ="";
        for(int i = 0 ; i<4 ;i++){
            String num = String.valueOf(random.nextInt(10));
            number += num;
            g.drawString(num,13*i+6,16);
        }
        System.out.println(number);
        req.getSession().setAttribute("checkCode",number);

        g.dispose();

        OutputStream out = resp.getOutputStream();
        ImageIO.write(pic,"JPEG",out);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}
