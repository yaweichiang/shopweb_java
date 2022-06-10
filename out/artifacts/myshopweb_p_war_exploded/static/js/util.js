import  ShopCar  from './shopcar.js';
import {WebSetting} from './ManagerCenterView.js';

// 讀取資料庫運費資料
export function getFare(){
    return new Promise((resolve,reject)=>{
        let src_url = '/tote';
        let xhr = new XMLHttpRequest();
        xhr.open('get',src_url,true);
        xhr.send();
        xhr.onload = ()=>{
            if( xhr.status == 200 ){
                // resolve(JSON.parse(xhr.responseText));
                if(xhr.responseText===""){
                    resolve([])
                }else{
                    resolve(JSON.parse(xhr.responseText));
                }
            }
        };
    });
    
}
// 讀取商品容量資訊
export function getCapacity(){
    return new Promise((resolve,reject)=>{
        let src_url = '/capacity';
        let xhr = new XMLHttpRequest();
        xhr.open('get',src_url,true);
        xhr.send();
        xhr.onload = ()=>{
            if( xhr.status == 200 ){
                // resolve(JSON.parse(xhr.responseText));
                if(xhr.responseText===""){
                    resolve([])
                }else{
                    resolve(JSON.parse(xhr.responseText));
                }
            }
        };
    });
    
}
//  讀取付款方式及手續費用
export function getPay(){
    return new Promise((resolve,reject)=>{
        let src_url = '/pay';
        let xhr = new XMLHttpRequest();
        xhr.open('get',src_url,true);
        xhr.send();
        xhr.onload = ()=>{
            if( xhr.status == 200 ){
                // resolve(JSON.parse(xhr.responseText));
                if(xhr.responseText===""){
                    resolve([])
                }else{
                    resolve(JSON.parse(xhr.responseText));
                }
            }
        };
    });
    
}
//自localStorage 取出購物車商品資料
export function getCarProducts() {
    //將會員瀏覽器紀錄的購物車商品內容提取出來(temp的型態為String)
    let temp = localStorage.getItem("car_products");
    //若有購物車商品紀錄   
    if(temp){
        // 向資料庫請求目前商品資訊
        fetch('/products').then(response => {
            return response.json();
        }).then(data=>{
            //將請求到的資訊存到storage
            localStorage.setItem("products",JSON.stringify(data));
        })
        .catch(err => {
            console.log("錯誤：",err);
        })
        
        //將String 轉換為物件型態
        let carProducts = JSON.parse(temp);
        let data ;
        data = localStorage.getItem("products");
        let products = JSON.parse(data);
        for(let i = 0 ; i<carProducts.length;i++){
            products.forEach(item=>{
                // 以新載入的資料更新商品資訊
                if(item.id===carProducts[i].id){
                    carProducts[i].name = item.name;
                    carProducts[i].inventory = item.inventory;
                    carProducts[i].type = item.type;
                    carProducts[i].capacity = item.capacity;
                    carProducts[i].price = item.price;
                    carProducts[i].isFreezing = item.isFreezing;
                    if(!item.type){//商品停售
                        carProducts.splice(i,1); //剔除此商品
                        i--;
                    }else if(carProducts[i].amount >= item.inventory){//選取商品數量大於庫存
                        carProducts[i].amount = item.inventory;
                    }
                }
            })
        }
        return carProducts;
    }else{
        return [];
    };
};

//將購買商品資料存入localStorage   購買商品異動時操作
export function setProducts(products){
    localStorage.setItem("car_products",JSON.stringify(products));
}

// ajax 取得所有商品資訊
export function getProducts(){
    return new Promise((resolve,reject)=>{
        let src_url = '/products';
        let xhr = new XMLHttpRequest();
        xhr.open('get',src_url,true);
        xhr.send();
        xhr.onload = ()=>{
            if( xhr.status == 200 ){
                if(xhr.responseText===""){
                    resolve([])
                }else{
                    resolve(JSON.parse(xhr.responseText));
                }
            }
        };
    });
}

// ajax 取得指定商品資訊
export function getProduct(id){
    return new Promise((resolve,reject)=>{
        let src_url = '/products/'+id;
        let xhr = new XMLHttpRequest();
        xhr.open('get',src_url,true);
        xhr.send();
        xhr.onload = ()=>{
            if( xhr.status == 200 ){
                if(xhr.responseText===""){
                    resolve([])
                }else{
                    resolve(JSON.parse(xhr.responseText));
                }
            }
        };
    });
}


export function getMemberinfo(str){//取得會員資料 有傳入參數時取得指定會員資料 未傳入參數時取得當前會員資料 
    return new Promise((resolve,reject)=>{
        let src_url = '/user'+(str?'/'+str:'');
        let xhr = new XMLHttpRequest();
        xhr.open('get',src_url,true);
        xhr.send();
        xhr.onload = ()=>{
            if( xhr.status == 200 ){
                if(xhr.responseText===""){
                    resolve([])
                }else{
                    resolve(JSON.parse(xhr.responseText));
                }
            }
        };
    });    
}

export function getAnnouncement(str){//取得網站公告資料 有傳入參數new時取得最新公告 未傳入參數時取得最近6筆
    return new Promise((resolve,reject)=>{
        let src_url='/anno'+(str?'/'+str:'');
        let xhr = new XMLHttpRequest();
        xhr.open('get',src_url,true);
        xhr.send();
        xhr.onload=()=>{
            if(xhr.status == 200){
                if(xhr.responseText===""){
                    resolve([])
                }else{
                    resolve(JSON.parse(xhr.responseText));
                }
            }
        }
    });
}
export function getMemberAddress(str){//取得會員常用地址資料
    return new Promise((resolve,reject)=>{
        let src_url = '/address'+(str?'/'+str:'');
        let xhr = new XMLHttpRequest()
        xhr.open('get',src_url,true)
        xhr.send();
        xhr.onload=()=>{
            if(xhr.status ==200){
                if(xhr.responseText===""){
                    resolve([])
                }else{
                    resolve(JSON.parse(xhr.responseText));
                }
            }
        }
    })
}
export function getMemberOrderList(str){//取得會員訂單記錄資料 根據會員電話  
    return new Promise((resolve,reject)=>{
        let src_url = '/order'+(str?'/'+str:'');
        let xhr = new XMLHttpRequest();
        xhr.open('get',src_url,true);
        xhr.send();
        xhr.onload=()=>{
            if(xhr.status==200){
                if(xhr.responseText===""){
                    resolve([])
                }else{
                    resolve(JSON.parse(xhr.responseText));
                }
            }
        }
    })
}
export function getManagerOrderList(type,data){//根據要搜尋的訂單建立時間  取得訂單記錄資料  到後端查詢訂單資料
    return new Promise((resolve,reject)=>{
        let src_url = '/orders/'+type+'/'+data;
        let xhr = new XMLHttpRequest();
        xhr.open('GET',src_url,true);
        xhr.send();
        xhr.onload=()=>{
            if(xhr.status==200){
                if(xhr.responseText===""){
                    resolve([])
                }else{
                    resolve(JSON.parse(xhr.responseText));
                }
            }
        }
    })
}


export const FEE=100; //貨到付款手續費  付款頁面的付款方式應該也要動態產生選項
/////



export const PRODUCTS = [];
export const MYCAR= new ShopCar(); //依照localStorage建立購物車物件 提供給其他頁面存取
export const  SETTING = new WebSetting(); //連線系統取得網站設定資訊 建立網站設定資訊物件供各頁面存取
