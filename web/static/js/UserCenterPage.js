import { Address } from './address.js';
import { Member } from './user.js';
import { MYCAR,getMemberinfo } from './util.js';

let USER;
   
getMemberinfo().then(data=>{
    USER = new Member(data[0]);
    showIndex();
})



// js 一執行應該要建立 會員物件 會員物件名下包含了訂單 地址 物件
MYCAR.showproductsTypes();//建立右上角購物車icon數字
let actionList = []; //記錄使用者點擊的紀錄



//監聽畫面上的點擊事件 依照自定義的標籤 myaction 執行對應的頁面抽換動作 將應顯示的物件顯示出來
document.querySelector(".control").addEventListener("click",(e)=>{
    let target = e.target;
    //點選登出按鈕
    if(target.attributes['myaction'].value==="logout"){
    }
    //點選會員控制主頁
    if(target.attributes['myaction'].value==="my_control_center"){
        showIndex(); 
        // history.pushState({action:"my_control_center"}, null, "");
    }
    //點選會員訂單
    if(target.attributes['myaction'].value==="my_older_list"){
        showMyOrderLists();
        history.pushState({action:"my_older_list"}, null, "");
    }
    //點選常用地址
    if(target.attributes['myaction'].value==="my_address"){
        showAddressList();
        history.pushState({action:"my_address"}, null, "");
    }
    //點選帳戶資料
    if(target.attributes['myaction'].value==="my_account"){
        showAccountInfo();
        history.pushState({action:"my_account"}, null, "");
    }
 
});

//監聽操作瀏覽紀錄頁 針對瀏覽動作做對應的畫面操作  
window.addEventListener("popstate",(e)=>{
    //會員中心主頁
    if(e.state.action==="my_control_center"){
        showIndex(); 
    }
    //會員訂單頁面
    if(e.state.action==="my_older_list"){
        showMyOrderLists();
    }
    //常用地址頁面
    if(e.state.action==="my_address"){
        showAddressList();
    }
    //帳戶資料頁面
    if(e.state.action==="my_account"){
        showAccountInfo();
    }
    //新增地址頁面
    if(e.state.action==="edit_address"){
        showAddressEdit();
    }
})


function showIndex(){
    history.pushState({action:"my_control_center"}, null, "");
    document.querySelector(".main").innerHTML = 
        '<div class="space"></div>'+
        '<div class="helloword">'+
            '<span class="nickname">'+USER.nickname+'</span> 您好 (不是 <span class="nickname">'+USER.nickname+'</span> 嗎？ <a myaction="logout">請登出</a>)<br>'+

                '在您的帳號控制台裡，您可以檢視<a myaction="my_older_list">近期的訂單</a>，管理您的<a myaction="my_address">收貨地址</a>和<a myaction="my_account">更改密碼或修改帳號資料</a>。'+
        '</div>';
    document.getElementById("target").removeAttribute("id");
    document.querySelector("ul li:nth-child(1)").setAttribute("id", "target");
    document.querySelector(".helloword").addEventListener("click",(e)=>{
        let target = e.target;
        if(target.attributes['myaction'].value==="logout"){
        }
        //點選會員訂單
        if(target.attributes['myaction'].value==="my_older_list"){
            history.pushState({action:"my_older_list"}, null, "");
            showMyOrderLists();
        }
        //點選常用地址
        if(target.attributes['myaction'].value==="my_address"){
            history.pushState({action:"my_address"}, null, "");
            showAddressList();
        }
        //點選帳戶資料
        if(target.attributes['myaction'].value==="my_account"){
            history.pushState({action:"my_account"}, null, "");
            showAccountInfo();
        }
     
    })

}
function showMyOrderLists(){
    document.querySelector(".main").innerHTML = 
        '<div class="subtitle">我的訂單</div>'+
        '<div class="space"></div>'+
        '   <table>'+
        '       <thead>'+
        '           <tr>'+
        '               <th>訂單</th>'+
        '               <th>日期</th>'+
        '               <th>狀態</th>'+
        '               <th>總計</th>'+
        '               <th>動作</th>'+
        '           </tr>'+
        '       </thead>'+
        '       <tbody class="orderlist">'+
        '       </tbody>'+
        '   </table>';
        document.getElementById("target").removeAttribute("id");
        document.querySelector("ul li:nth-child(2)").setAttribute("id", "target");
        USER.orderlists.forEach(list=>{
            document.querySelector(".orderlist").appendChild(list.createTableRowView());
        });
}
function showAddressList(){
document.querySelector(".main").innerHTML = 
    '<div class="subtitle">常用地址</div>'+
    '<div class="space"></div>'+
    '<table>'+
        '<thead>'+
            '<tr>'+
                '<th>收件人</th>'+
                '<th>地址/電話</th>'+
                '<th >動作</th>'+
            '</tr>'+
        '</thead>'+
        '<tbody class="orderlist">'+
        '</tbody>'+
    '</table>';
    let addbtn = document.createElement("div");
    addbtn.classList.add("addicon");
    addbtn.innerHTML = '<a myaction="edit_address"><img myaction="edit_address" src="../static/icon/add.png" alt=""></a>';
    addbtn.addEventListener("click",(e)=>{
        showAddressEdit();
        history.pushState({action:"edit_address"}, null, "");
    })
    document.querySelector("thead").firstChild.lastChild.appendChild(addbtn);
    document.getElementById("target").removeAttribute("id");
    document.querySelector("ul li:nth-child(3)").setAttribute("id", "target");
    USER.addresslists.forEach(list=>{
            document.querySelector(".orderlist").appendChild(list.createTableRowView());
    });
}
function showAddressEdit(){
    document.querySelector(".main").innerHTML = 
        '<div class="subtitle">新增收件地址資料</div>'+
        '<div class="space"></div>'+
        '   <table class="edittable">'+
        '        <tr>'+
        '            <th>收件人</th>'+
        '            <td><input type="text" placeholder="請輸入收件人姓名"></td>'+
        '        </tr>'+
        '        <tr>'+
        '            <th>地址</th>'+
        '            <td><input type="text" placeholder="請輸入收件地址"></td>'+
        '        </tr>'+
        '        <tr>'+
        '            <th>收件人電話</th>'+
        '            <td><input type="tel" placeholder="請輸入收件人聯絡電話"></td>'+
        '        </tr>'+
        '   </table>';
        let mainbtn = document.createElement("button");
        mainbtn.innerText = "儲存修改";
        mainbtn.classList.add("mainbtn");
        mainbtn.addEventListener("click",(e)=>{
            let phone_rg = /^09[0-9]{8}$/;
            let inputs = document.querySelectorAll("input");
            let name = inputs[0].value;
            let address = inputs[1].value;
            let phone = inputs[2].value;
            console.log(name,phone,address)
            console.log((name!=""&&address!=""&&phone_rg.test(phone)))
            if(name!=""&&address!=""&&phone_rg.test(phone)){
                let obj = {
                    "name":name,
                    "address":address,
                    "phone":phone
                }
                fetch('address',{
                    method:'post',
                    headers:{ 'Content-Type': 'application/json' },
                    body:JSON.stringify(obj)
                }).then(response=>{
                    return response.json()
                }).then(data=>{
                    let temp =[]
                    data.forEach(list=>{
                        temp.push(new Address(list));
                    })
                    USER.addresslists = temp;
                    showAddressList();
                })
                
            }else{
            }            
        });
        document.querySelector(".main").appendChild(mainbtn);
        document.getElementById("target").removeAttribute("id");
        document.querySelector("ul li:nth-child(3)").setAttribute("id", "target");
}
function showAccountInfo(){
    document.querySelector(".main").innerHTML = 
        ' <div class="subtitle">帳戶資料</div>'+
        '<div class="space"></div>'+
        '<div class="basicinfo">'+
            '<div class="helfinput"><p>名字</p><input type="text" value="'+USER.name+'"></div>'+
            '<div class="helfinput"><p>電話[帳號]</p><input type="text" value="'+USER.phone+'" disabled></div>'+
            '<div class="fullinput"><p>顯示名稱</p><input type="text" value="'+USER.nickname+'"></div>'+
            '<div class="fullinput">電子郵件<input type="email" value="'+USER.email+'"></div>'+
        '</div>'+
        '<div class="changepw">'+
            '<div class="oldpw"><p>目前密碼(不需變更請空白)</p><input type="password"></div>'+
            '<div class="newpw"><p>新密碼</p><input type="password"></div>'+
            '<div class="newpw2"><p>確認新密碼</p><input type="password"></div>'+
        '</div>';
        let mainbtn = document.createElement("button");
        mainbtn.innerText = "儲存修改";
        mainbtn.classList.add("mainbtn");
        mainbtn.addEventListener("click",(e)=>{
            //檢查資料欄位合法性  密碼欄位必須是"" 或符合資料 
            let inputs = document.querySelectorAll("input");
            let name = inputs[0].value;
            let nickname = inputs[2].value;
            let email = inputs[3].value;
            let oldpassword = inputs[4].value;
            let newpassword = inputs[5].value;
            let password_re = inputs[6].value;
            console.log(name,nickname,email==="",oldpassword,newpassword,USER.no,USER.phone)
            let obj = {
                "id":USER.no,
                "name":name,
                "phone":USER.phone,
                "nickname":nickname,
                "email":email,
                "oldpassword":oldpassword,
                "newpassword":newpassword
            }
            fetch('user/',{
                method:'put',
                headers:{"Content-Type":"application/json"},
                body:JSON.stringify(obj)
            }).then(response=>{
                if(response.status==200){
                    return response.json()
                }
            }).then(datas=>{
                if(datas[0]=="舊密碼錯誤，密碼變更失敗！"){
                    alert(datas);
                }
                USER.updateUserInfo(datas[1][0]);
                showAccountInfo();
                
            })
        });
        document.querySelector(".main").appendChild(mainbtn);
        document.getElementById("target").removeAttribute("id");
        document.querySelector("ul li:nth-child(4)").setAttribute("id", "target");
    
}