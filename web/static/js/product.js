
//商品品物件及 購物車商品物件
import {getProduct} from "./util.js";

export class Product {
    constructor(obj){
        this.id = obj.id; //商品編號
        this.name = obj.name; //商品名稱
        this.inventory = obj.inventory; //商品庫存
        this.type = obj.type==="true"?true:false; //商品販售狀態 sale unsale
        this.capacity = obj.capacity; //容量
        this.price = obj.price; //單價
        this.isFreezing = obj.isFreezing==="true"?true:false;//運送類型是否冷凍運送
        this.url = obj.url; //商品圖片url
        this.intr = obj.intr; //商品介紹
    }
    
    equal(product){
        if(this.id===product.id)
            return true
        else
            return false;
    }
    updateProductInfo(obj){
        this.name = obj.name; //商品名稱
        this.price = obj.price; //單價
        this.inventory = obj.inventory; //商品庫存
        this.type = obj.type==="true"?true:false; //商品販售狀態 sale unsale
        this.url = obj.url; //商品圖片url
        this.intr = obj.intr; //商品介紹
    }

    getProductDiv(){ //銷售主頁的商品顯示
        let productDiv = document.createElement("div");
        productDiv.classList.add("product");
        productDiv.innerHTML = 
        '<img class="productimg" src="">'+
        '<div class="productinfo">'+
        '    <div class="name"></div>'+
        '    <div class="price"></div>'+
        '</div>'+
        //依照商品狀態變更按鈕樣式
        '<div class="productbtn">'+//商品停售 或是 庫存為0
            ( this.type && this.inventory>0 ? '<button class="addbtn">加入購物車</button><img src="../static/icon/addproduct.png">':'<button class="unsale">'+(this.inventory==0?'已售完':'已停售')+'</button>')+
        '</div>';

        let freezingIcon = document.createElement('div');
        freezingIcon.outerHTML = '<div class="ice_icon"><img src="../static/icon/freezing.png"></div>';
        let name = document.createElement('span');
        name.innerText = this.name+this.capacity+'克';
        productDiv.children[0].src = this.url;
        if(this.isFreezing)
            productDiv.children[1].children[0].appendChild(freezingIcon);
        productDiv.children[1].children[0].appendChild(name);
        productDiv.children[1].children[1].innerText = 'NT.'+this.price;
        return  productDiv;
    }
    getProductTR(){ //管理中心的商品管理分頁 商品列
        let newTR = document.createElement("tr");
        newTR.innerHTML = '<td></td>'+
            '<td></td>'+
            '<td></td>'+
            '<td></td>'+
            '<td></td>'+
            '<td></td>'+
            '<td><button class="checkbtn">編輯</button></td>';
        let a  = document.createElement('a');
        a.innerText = this.id;
        newTR.children[0].appendChild(a);
        newTR.children[1].innerText = this.name;
        newTR.children[2].innerText =this.capacity+'克';
        newTR.children[3].innerText =this.price+'元';
        newTR.children[4].innerText =this.inventory;
        newTR.children[5].innerText =this.type?'銷售中':'停售中';

        newTR.lastChild.lastChild.addEventListener("click",()=>{
            //點取編輯進入明細
            this.getProductEdit();
            history.pushState({action:"productedit"}, null, "");
        })
        newTR.firstChild.lastChild.addEventListener("click",()=>{
            //點取商品編號進入明細
            this.getProductEdit();
            history.pushState({action:"productedit"}, null, "");
        })
        return newTR;
    }
    getProductEdit(){ //管理中心的商品管理分頁  商品明細編輯頁
        let main = document.querySelector(".main");
        main.innerHTML = '<div class="subtitle">商品編輯</div>'+
            '<div class="space"></div>'+
            '<div class="basicinfo">'+
            '    <div class="helfinput">'+
            '        <p>商品編號</p>'+
            '        <input type="text" value="" disabled>'+
            '    </div>'+
            '    <div class="helfinput">'+
            '        <p>商品狀態</p>'+
            '        <div class="radios">'+
            '            <div class="radiolabel">'+
            '                <label><input type="radio" name="type" value="true" '+(this.type?'checked':'')+'>銷售中</label>'+
            '            </div>'+
            '            <div class="radiolabel">'+
            '                <label><input type="radio" name="type" value="false" '+(!this.type?'checked':'')+' >停售</label>'+
            '            </div>'+
            '        </div>'+
            '    </div>'+
            '    <div class="helfinput">'+
            '        <p>商品名稱</p>'+
            '        <input type="text" value="">'+
            '    </div>'+
            '    <div class="helfinput">'+
            '        <p>運送類型</p>'+
            '        <div class="radios" >'+
            '            <div class="radiolabel">'+
            '                <label><input type="radio" name="isFreezing" value="true" '+(this.isFreezing?'checked':'')+' disabled>冷凍運送</label>'+
            '            </div>'+
            '            <div class="radiolabel">'+
            '                <label><input type="radio" name="isFreezing" value="false" '+(!this.isFreezing?'checked':'')+' disabled>常溫運送</label>'+
            '            </div>'+
            '        </div>'+
            '    </div>'+
            '    <div class="helfinput">'+
            '        <p>商品單價</p>'+
            '        <input type="text" value="">'+
            '    </div>'+
            '    <div class="helfinput">'+
            '        <p>商品容量</p>'+
            '        <select disabled>'+
            '            <option selected>'+this.capacity+'</option>'+
            '        </select>'+
            '    </div>'+
            '    <div class="helfinput">'+
            '        <p>商品庫存</p>'+
            '        <input type="text" value="">'+
            '    </div>'+
            '    <div class="helfinput">'+
            '    </div>'+
            '    <div class="helfinput">'+
            '    </div>'+
            '</div>'+
            '<div class="fullinput">'+
            '    <p>產品說明</p>'+
            '    <textarea name="" id="" cols="30" rows="10"></textarea>'+
            '</div>'+
            '<button class="mainbtn">儲存變更</button>';
            let inputs = document.querySelectorAll("input[type='text']");
            console.log(inputs)
            inputs[0].value = this.id;
            inputs[1].value = this.name;
            inputs[2].value = this.price;
            inputs[3].value = this.inventory;
            document.querySelector("textarea").innerText = this.intr

            main.lastChild.addEventListener("click",()=>{
                let inputs = document.querySelectorAll("input[type='text']");
                let textarea = document.querySelector("textarea");
                for(let i = 0 ; i<inputs.length;i++){
                    if(inputs[i].value === ""){
                        inputs[i].focus;
                        alert("請輸入商品資料");
                        return false;
                    }else if(i==2){
                        let check = /^[1-9]+[0-9]*$/;
                        if(!check.test(inputs[i].value)){
                            alert("請輸入正確商品單價");
                            return false;
                        }
                    }else if(i==3){
                        let check = /^[1-9]+[0-9]*$/;
                        if(!check.test(inputs[i].value)&&inputs[i].value!="0"){
                            alert("請輸入正確商品數量");
                            return false;
                        }
                    }
                }
                if(textarea.value==""){
                    alert("請輸入商品簡介");
                    return false;
                }
                let temp;
                document.querySelectorAll("input[name='type']").forEach(data=>{
                    if(data.checked){
                        temp = data.value
                    }
                })
            
                //向後端發出變更 取得變更後商品資訊
                let obj={
                    id:this.id,
                    name:inputs[1].value,
                    price:inputs[2].value,
                    inventory:inputs[3].value,
                    type:temp,
                    introduction:textarea.value
                }
                // this.updateProductInfo 依取得商品資訊更新此商品
                fetch('/products/'+this.id,{
                    method:'put',
                    headers:{'Content-Type':'application/json'},
                    body:JSON.stringify(obj)
                }).then(response=> {
                    if (response.status == 200) {
                        getProduct(this.id).then(datas=>{
                            this.updateProductInfo(datas[0]);
                            history.back();
                        })
                    }
                }).catch(err=>console.log(err)) ;

            })
    }
    
    
}
export class BuyProduct extends Product{
    amount;
    sum;
    constructor(obj){
        super(obj);
        this.amount = obj.amount?obj.amount:1;
        this.sum = obj.price*obj.amount;
    }


    setAmount(num){
        this.amount+=num; 
        this.sum = this.amount*this.price;
    }
    getBuyProductTr(){//建立購物車中頁面商品TR物件
        //建立新的商品tr物件 
        let newTr = document.createElement("tr");
        newTr.classList.add("myproduct");
        newTr.innerHTML=
                '    <td></td>'+
                '    <td></td>'+
                '    <td></td>'+
                '    <td><img src="../static/icon/min_g.png"><input type="text" value="" disabled><img src="../static/icon/add_'+(this.amount<this.inventory?'r':'g')+'.png"></td>'+//購買數量達庫存數量時不顯示
                '    <td><input type="text" value="" disabled></td>'+
                '    <td><button class="checkbtn">刪除</button></td>';
        let iceIcon = document.createElement('div');
        iceIcon.outerHTML = '<div class="ice_icon"><img src="../static/icon/freezing.png"></div>'
        let name = document.createElement('span');
        name.innerText = this.name;
        if(this.isFreezing)
            newTr.children[0].appendChild(iceIcon);
        newTr.children[0].appendChild(name);
        newTr.children[1].innerText = this.capacity+'克';
        newTr.children[2].innerText = this.price+'元';
        newTr.children[3].children[1].value = this.amount;
        newTr.children[4].firstChild.value = (this.amount*this.price);

        return newTr;

    }

}
