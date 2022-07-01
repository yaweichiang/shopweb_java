package com.yawei.util;

import org.json.JSONObject;
public class OrderProduct extends JSONObject {
    private String name; //商品名稱
    private int id;//商品邊號
    private int price;//購買單價
    private int amount;//購買數量
    private int capacity;//商品容量

    public OrderProduct(String jsonString){
        super(jsonString);
        this.name = super.getString("name");
        this.id = super.getInt("id");
        this.price = super.getInt("price");
        this.amount = super.getInt("amount");
        this.capacity = super.getInt("capacity");
    }

    public OrderProduct(String name,int id,int price,int amount,int capacity){
        this.name = name;
        this.id = id;
        this.price = price;
        this.amount = amount;
        this.capacity = capacity;
    }


    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }
    public int getPrice() {
        return price;
    }
    public int getAmount() {
        return amount;
    }
    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + '\"' +
                ",\"id\":" + id +
                ",\"price\":" + price +
                ",\"amount\":" + amount +
                ",\"capacity\":" + capacity +
                '}';
    }
}
