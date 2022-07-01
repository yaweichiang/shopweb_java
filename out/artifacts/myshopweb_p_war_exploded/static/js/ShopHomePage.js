import {MYCAR, getProducts, getAnnouncement} from './util.js';
import { Product,BuyProduct } from './product.js';

 MYCAR.showproductsTypes();//建立右上角購物車icon數字
 getAnnouncement("new").then(data=>{ //取得最新的公告
     document.querySelector(".context").innerText = data[0].content;
 })


let parent = document.querySelector(".innercontainer");
// 向後端取得商品資訊
getProducts().then(data=>{

    data.map((product)=>{

        let item = new Product(product);
        if(item.type)
            parent.appendChild(createProductsDom(item));

    })
})


//建立商品的div物件 並在加入購物車按鈕加上監聽
function createProductsDom(product){
    let productDiv = product.getProductDiv();
    if(product.type||product.inventory === 0){//若商品為停售商品 或 庫存0 則不監聽按鈕事件
        productDiv.children[2].addEventListener("click",(e)=>{
            let tagName = e.target.tagName;
            if(tagName == "BUTTON"|| tagName == "IMG"){
                MYCAR.addProduct(product);
            }
            })
        }
    return productDiv;
}
