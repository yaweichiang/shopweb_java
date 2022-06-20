
import { ManagerOrderList } from './orderlist.js';
import {  SETTING , getManagerOrderList } from './util.js';
import { MemberSearch, OrderListSearch ,ProductsEdit } from './ManagerCenterView.js';

let PRODUCTSEDIT =  new ProductsEdit();

history.pushState({action:"todayorder"}, null, "");
showIndex(); //依照當日訂單生成畫面

//監聽左側控制欄位進行換頁
document.querySelector(".control").addEventListener("click",(e)=>{
    let target = e.target;
    //監控左側控制列表點選進行頁面抽換
    if(target.attributes['myaction'].value==="todayorder"){
        showIndex();
        history.pushState({action:"todayorder"}, null, "");
    }
    if(target.attributes['myaction'].value==="ordersearch"){
        searchOrderLists();
        history.pushState({action:"ordersearch"}, null, "");
    }
    if(target.attributes['myaction'].value==="membersearch"){
        searchMember();
        history.pushState({action:"membersearch"}, null, "");
    }
    if(target.attributes['myaction'].value==="productedit"){
        productEdit();
        history.pushState({action:"productedit"}, null, "");
    }
    if(target.attributes['myaction'].value==="othersetting"){
        othersetting();
        history.pushState({action:"othersetting"}, null, "");
    }

})

//監控上下頁的切換 
window.addEventListener("popstate",(e)=>{
    //管理中心主頁 當日訂單列表
    if(e.state.action==="todayorder"){
        showIndex(); 
        console.log("控制台");
    }
    //訂單查詢頁面
    if(e.state.action==="ordersearch"){
        searchOrderLists();
        console.log("查詢");
    }
    //會員查詢頁面
    if(e.state.action==="membersearch"){
        searchMember();
        console.log("會員查詢");
    }
    //商品編輯頁面
    if(e.state.action==="productedit"){
        productEdit();
        console.log("編輯商品");
    }
    //網站設定頁面
    if(e.state.action==="othersetting"){
        othersetting();
        console.log("網站設定");
    }
    if(e.state.action==="test"){
       
        console.log("test");
    }
    if(e.state.action==="doordersearch"){
        searchOrderLists(e.state.searchKey);
        console.log("doordersearch",e.state.searchKey);
    }
    if(e.state.action==="domembersearch"){
        console.log("ss domembersearch",e.state.searchKey);

        searchMember(e.state.searchKey,null);
    }
    if(e.state.action==="domemberorderlistsearch"){
        console.log("ss domemberorderlistsearch",e.state.member,e.state.list);

        searchMember(e.state.member,e.state.list);
    }

    
})
// 監控右邊生成頁面按鈕動作

function showIndex(){
    let main = document.querySelector(".main");
    main.innerHTML = 
        '<div class="subtitle">今日訂單</div>'+
        '<div class="space"></div>'+
        '<table>'+
        '    <thead>'+
        '        <tr>'+
        '            <th>訂單</th>'+
        '            <th>姓名</th>'+
        '            <th>電話</th>'+
        '            <th>訂購日期</th>'+
        '            <th>狀態</th>'+
        '            <th>總計</th> '+
        '            <th>出貨日期</th>'+
        '            <th>付款方式</th>'+
        '            <th>動作</th>'+
        '        </tr>'+
        '    </thead>'+
        '    <tbody class="orderlist">'+
        '    </tbody>'+
        '</table>';
        let today = new Date();
        let date = today.getFullYear()+"-"+(today.getMonth()<9?'0':'')+(today.getMonth()+1)+"-"+(today.getDate()<=9?'0':'')+today.getDate();
        getManagerOrderList("date",date).then(datas=>{
            datas.forEach(data=>{
                main.children[2].lastChild.appendChild(new ManagerOrderList(data).createTableRowView());

            })
        })
       
  
        document.getElementById("target").removeAttribute("id");
        document.querySelector("ul li:nth-child(1)").setAttribute("id", "target");
}
function searchOrderLists(arr){
    let search = new OrderListSearch();
    let main = document.querySelector(".main");
    main.innerHTML ='<div class="subtitle">訂單查詢</div>';
    main.appendChild(arr!=null?search.searchBar(arr):search.searchBar());
    document.getElementById("target").removeAttribute("id");
    document.querySelector("ul li:nth-child(2)").setAttribute("id", "target"); 
}
function searchMember(memberArr,orderlistArr){
    let search = new MemberSearch();
    let main = document.querySelector(".main");
    main.innerHTML = '<div class="subtitle">會員查詢</div>';
    main.appendChild(search.searchBar());
    if(memberArr!=null && orderlistArr!=null ){
        console.log("會員訂單 m",memberArr,"o",orderlistArr)
       search.memberSearchTableView(memberArr);
       search.orderListTableView(orderlistArr);
    }else if(memberArr!=null){
        console.log( "會員 m",memberArr,"o",orderlistArr)
        search.memberSearchTableView(memberArr);
    }
    document.getElementById("target").removeAttribute("id");
    document.querySelector("ul li:nth-child(3)").setAttribute("id", "target");
}
function productEdit(){
    PRODUCTSEDIT.show()
}
function othersetting(){
    let main = document.querySelector(".main");
    main.innerHTML = '<div class="subtitle">網站設定</div>'+'<div class="space"></div>';
    main.appendChild(SETTING.fareView());
    main.appendChild(SETTING.announcementView());
    let mainBtn = document.createElement("button");
    mainBtn.classList.add("mainbtn")
    mainBtn.innerText="儲存變更";
    mainBtn.addEventListener("click",(e)=>{

        SETTING.setFare();
        SETTING.changeAnno();


    })
    main.appendChild(mainBtn);
 
    
    document.querySelector(".announcementselect").addEventListener("selecte",(e)=>{
        console.log(e);
    })
    document.getElementById("target").removeAttribute("id");
    document.querySelector("ul li:nth-child(5)").setAttribute("id", "target");

}
