import { MYCAR,getFare,getMemberAddress, getPay} from './util.js'; 
import {Address} from './address.js'
import PayType from './pay.js'

//購物車頁面 
MYCAR.showProducts(); // 建立購物車商品明細的dom物件
MYCAR.showproductsTypes();//建立右上角購物車icon數字
getFare().then(data=>{
    MYCAR.FareInfo.normal.id=data[0].name==='常溫'?data[0].id:data[1].id;
    MYCAR.FareInfo.freezing.id=data[0].name==='冷凍'?data[0].id:data[1].id;
    MYCAR.FareInfo.freezing.Fare=data[0].name==='冷凍'?data[0].fare:data[1].fare;
    MYCAR.FareInfo.normal.Fare=data[0].name==='常溫'?data[0].fare:data[1].fare;
    MYCAR.FareInfo.freezing.Threshold=data[0].name==='冷凍'?data[0].threshold:data[1].threshold;
    MYCAR.FareInfo.normal.Threshold=data[0].name==='常溫'?data[0].threshold:data[1].threshold
    document.getElementById("freezing_fare").innerText = MYCAR.FareInfo.freezing.Fare;
    document.getElementById("normal_fare").innerText = MYCAR.FareInfo.normal.Fare;
    document.getElementById("freezing_th").innerText = MYCAR.FareInfo.freezing.Threshold;
    document.getElementById("normal_th").innerText = MYCAR.FareInfo.normal.Threshold;
    MYCAR.showcalculateTotal();
})
getPay().then(datas=>{
    datas.forEach(data=>{
        document.querySelector(".selectpay").appendChild(new PayType(data).showInShopCar())
    })
})

// 這邊要直接跟後端請求address
getMemberAddress().then(datas=>{
    if(typeof(datas.length)!="undefined"){
        datas.forEach(data=>{
            document.querySelector(".selectrecipient").appendChild(new Address(data).createShopCarItem());
            console.log(data);
        })
    }
}).catch(err=>{console.log(err);})



document.addEventListener("change",(e)=>{ // 選取不同的付款方式 重新確認付款金額
    MYCAR.showcalculateTotal();
})
document.querySelector(".subtitle>button").addEventListener("click",(e)=>{
    document.getElementById("flaotboard").classList.remove("hide");

    
    
})
document.querySelector("#flaotboard button").addEventListener("click",(e)=>{
    document.getElementById("flaotboard").classList.add("hide");
})

document.querySelector(".mainbtn").addEventListener("click",()=>{
    if(MYCAR.inCarProducts.length===0){
        alert("請選購商品後再進行結帳！");
    }else{
        // 確認是否登入
        fetch('/check').then(response=>{
          return response.text()
        }).then(datas=>{
            if(datas==="true"){
                //確認登入後 確認應填資料是否齊全
                let radios = document.querySelectorAll("input[type='radio']");//至少選一個
                let inputs = document.querySelectorAll(".addressinfo input[type='text']");
                let inputphone = document.querySelector(".addressinfo input[type='tel']").value;
                let radiocheck = false;
                radios.forEach(data=>{
                    if(data.checked){
                        radiocheck = true;
                    }
                })
                let phone_rg = /^09[0-9]{8}$/;
                if( !radiocheck || inputs[0].value=="" || inputs[1].value=="" ||(!phone_rg.test(inputphone))){
                    alert("資料少填或是輸入電話格式不正確！")
                }else{
                    let obj = {
                        payID:MYCAR.pay.id,
                        toteNo:MYCAR.toteNo,
                        products:MYCAR.inCarProducts,
                        name:inputs[0].value,
                        phone:inputphone,
                        address:inputs[1].value,
                        total:MYCAR.total
                    }
                    // 資料齊全格式正確 產生訂單確認頁面
                    MYCAR.showFinalList(obj);
                }
                
            }else{
                // 未登入 轉到會員中心進行登入
                window.location.href='/usercenter';
            }
        })     
    }
})