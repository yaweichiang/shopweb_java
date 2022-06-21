import { getCarProducts,setProducts, }from './util.js';
import { BuyProduct } from './product.js';


export default class ShopCar{
    constructor(){
        this.inCarProducts = getCarProducts().map(product=> new BuyProduct(product));//this.createProducts();// 購物車的商品資訊
        this.fare ;//運費金額
        this.toteNo; // 運送類型編號  ＊
        this.pay = {id:null,name:null,fee:0}; //付款資訊
        this.productTotal;
        this.total;//總計金額
        this.FareInfo = {
            "freezing":{
                "id":null,
                "Fare":null,
                "Threshold":null
            },
            "normal":{
                "id":null,
                "Fare":null,
                "Threshold":null
            }
        };

    }

    createProducts(){//創建購物車 建立購物車商品物件
        //從localStorage 取得過物車商品資訊
        let products = getCarProducts();
        let temp = [];
        // 根據localStorage 購物車商品資訊建立購物車商品物件

        products.forEach(product=>{
             temp.push(new BuyProduct(product));
        })
        return temp;
    }

    // 在商品頁面選購商品加入購物車 變動購物車商品資訊
    addProduct(product){ //新增商品先比對 購物車中是否有相同商品 有的話該商品購買數量＋1 
        let length =  this.inCarProducts.length;
        if(length ===0){
            this.inCarProducts.push(new BuyProduct(product)
                );
            this.showproductsTypes(); //改變右上角購物車商品種類圖示
        }else{
            for(let i = 0 ; i < length;i++){
                if(this.inCarProducts[i].equal(product)){
                    this.inCarProducts[i].setAmount(1);
                    break;
                }else if(i === length-1){
                    this.inCarProducts.push(new BuyProduct(product)
                    ); //沒有相同商品的話建立該購買商品物件並新增到購物車
                    this.showproductsTypes(); //改變右上角購物車商品種類圖示
                    break;
                }
            }     
        }
        setProducts(this.inCarProducts); // 將變動後的商品物件存入localStorage
    }
    
    //購物車頁面金額計算顯示
    showcalculateTotal(){ //重新計算總額並顯示購物車頁面總計金額
        this.calculateTotal(); //更新購物車選購金額
        document.getElementById("fare").innerText = this.fare; //改變畫面上顯示的運費
        document.getElementById("total").innerText = this.total;
        document.getElementById("ptotal").innerText = this.productTotal;
        document.getElementById("fee").innerText = this.pay.fee;//改變畫面上顯示的代收費 
    }
    // 購物車頁面購物明細表格建立
    showProducts(){ //將購物車購買商品tr物件建構到頁面上
        let listTarget = document.querySelector(".myproducts"); //購物車明細父節點
        this.inCarProducts.forEach(product=>{
            let productTr = this.setBtnEvent(product);
            listTarget.appendChild(productTr);
        })
    }
    // 將選購商品種類顯示到右上角購物車
    showproductsTypes(){  
        document.querySelector(".products_count").textContent = this.inCarProducts.length;
    }
   
    //計算購物車金額 (商品 運費 手續費)    
    calculateTotal(){//計算運費及總額
        let productTotal = 0;
        let haveFreezing = false; //預設常溫運送
        this.toteNo = this.FareInfo.normal.id;
        this.inCarProducts.forEach(product=>{
            if((!haveFreezing)&&product.isFreezing){ //商品中有冷凍商品就切換為冷凍運送 
                haveFreezing = true;
                this.toteNo = this.FareInfo.freezing.id;
            }
            productTotal += product.sum
            
        })
        this.productTotal = productTotal;
                        //商品總價是空的情況運費也會是0
        this.fare = this.productTotal===0? 0 : haveFreezing?  //依是否有冷凍商品及是否達免運門檻回傳運費  
        // 商品總額超過門檻免運 否則回傳運費
        (productTotal>=this.FareInfo.freezing.Threshold ? 0 : this.FareInfo.freezing.Fare) : //冷凍  
        (productTotal>=this.FareInfo.normal.Threshold? 0 : this.FareInfo.normal.Fare) ;//常溫
        this.total = this.productTotal + this.fare + this.pay.fee;     
        
    }
    // 在購物車頁面刪除商品品項 需要需要更新頁面金額計算顯示 及儲存購物車異動後資訊 並改變右上角購物車商品種類數量
    changeProducts(){//變動商品 刪除 
        this.updateProducts(); //將當前購物車商品資訊存入localStorage
        this.showproductsTypes(); //更新右上角顯示購物車商品種類
    }
    // 在購物車頁面異動商品數量 需要更新頁面金額計算顯示 及儲存購物車異動後資訊
    updateProducts(){//變動商品 數量增減增加刪除
        setProducts(this.inCarProducts);//將當前購物車商品資訊存入localStorage
        this.showcalculateTotal();

    }

    // 對購物車明細tr按鈕加上監聽事件
    setBtnEvent(product){ //將購物車明細TR加上監聽
        let productTr = product.getBuyProductTr();
        let deleteBtn= productTr.lastChild.firstChild;
        deleteBtn.addEventListener("click",(e)=>{
            // 當點擊事件發生時 移除商品的tr
            e.target.parentElement.parentElement.remove(); //移除畫面上tr物件 
            let removeIndex = this.inCarProducts.indexOf(product); //取得要刪除的商品物件在購物車inCar陣列中的引索位置
            this.inCarProducts.splice(removeIndex, 1); // 刪除引索位置起的1個物件
            this.changeProducts(); // 更新localStorage購買商品資料  
        })

        let minbtn = productTr.children[3].children[0];
        let addbtn = productTr.children[3].children[2];
        minbtn.addEventListener("click",(e)=>{
            // 點擊時 做amount屬性減少 並刪除當前就有物件重新建立新的商品物件插入原位 
            if(product.amount>1){
                product.setAmount(-1);
                //建立新tr物件替換掉舊的
                let listTarget = document.querySelector(".myproducts"); //購物車明細父節點
                let thisone = e.target.parentElement.parentElement;
                listTarget.replaceChild(this.setBtnEvent(product),thisone);
                this.updateProducts(); // 更新localStorage購買商品資料
                }

            })
        if(product.amount<product.inventory){
            addbtn.addEventListener("click",(e)=>{
                if(product.amount<99){
                    product.setAmount(1);
                    //建立新tr物件替換掉舊的
                    let listTarget = document.querySelector(".myproducts"); //購物車明細父節點
                    let thisone = e.target.parentElement.parentElement;
                    listTarget.replaceChild(this.setBtnEvent(product),thisone);
                    this.updateProducts(); // 更新localStorage購買商品資料
                    }
                })
            }
        return productTr;
    }
    showFinalList(data){
        let temp="";
        this.inCarProducts.forEach(data=>{
            temp+='<tr><td>'+data.name+'</td><td>'+data.capacity+'</td><td>'+data.price+'</td><td>'+data.amount+'</td><td>'+data.sum+'</td></tr>';
        })

        let main = document.querySelector(".innercontainer");
        main.innerHTML = 
        '<div class="space line"></div>'+
        '<div class="subtitle"><img src="../static/icon/checkout.png">訂單確認</div>'+
        '<div class="space"></div>'+
        '<table id="cartable">'+
        '    <thead>'+
        '        <tr>'+
        '            <th>商品名稱</th>'+
        '            <th>商品容量</th>'+
        '            <th>商品單價</th>'+
        '            <th>購買數量</th>'+
        '            <th>金額小計</th>'+
        '        </tr>'+
        '    </thead>'+
        '    <tbody class="myproducts" >'+
        temp+
        '    </tbody>'+
        '</table>'+
        '<div class="checkout">'+
        '    <span class="subtotal">+ 商品小計：<span id="ptotal">'+this.productTotal+'</span>元</span>'+
        '    <span class="subtotal">+ 運費：<span id="fare">'+this.fare+'</span>元</span>'+
        '    <span class="subtotal">+ 手續費：<span id="fee">'+this.pay.fee+'</span>元</span>'+
        '    <span calss="total">總計金額：<span id="total">'+this.total+'</span>元</span>'+
         '</div>'+
        
        '<div class="subtitle">其他資訊</div>'+
        '<div class="orderinfo">'+
        '<div>付款方式：'+this.pay.name+'</div>'+
        '<div>寄送方式：'+(this.toteNo===1?'冷凍運送':'常溫運送')+'</div>'+
        '<div calss = "space"></div>'+
        '<div>收件人姓名：'+data.name+'</div>'+
        '<div>收件人電話：'+data.phone+'</div>'+
        '<div>收件人地址：'+data.address+'</div>'+
        '</div>'+
        '<form action="" method="post" display="hiden">'+
        '</form>'+
        '<div class="space">'+
        '<button class="mainbtn">確認結帳</button>'+
        '</div>';
        
        main.lastChild.firstChild.addEventListener("click",(e)=>{
            fetch("/order",{
                method:'post',
                headers:{"Content-Type":"Application/json"},
                body:JSON.stringify(data)
            }).then(response=>{
                if(response.status==200){
                  return response.text();
                }
            }).then(datas=>{
                if(datas==="ok"){
                    alert("完成訂購");
                    localStorage.removeItem('car_products');
                    window.location.href='/usercenter';
                }else{
                    console.log("訂購失敗")
                }
            })
        })
    }

}