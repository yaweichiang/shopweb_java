import { Address } from './address.js';
import { Member } from './user.js';
import { MYCAR,getMemberinfo,getMemberAddress } from './util.js';

let USER;
   
getMemberinfo().then(data=>{
    USER = new Member(data);
    showIndex();
    if(USER.phone.length===0)
        alert("請於消費前填寫補齊會員電話資料")
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
    let main = document.querySelector(".main")
    main.innerHTML =
        '<div class="space"></div>'+
        '<div class="helloword">'+
            '<span class="nickname"></span> 您好 (不是 <span class="nickname"></span> 嗎？ <a myaction="/logout">請登出</a>)<br>'+

                '在您的帳號控制台裡，您可以檢視<a myaction="my_older_list">近期的訂單</a>，管理您的<a myaction="my_address">收貨地址</a>和<a myaction="my_account">更改密碼或修改帳號資料</a>。'+
        '</div>';
    document.querySelectorAll(".nickname").forEach(item=>{
        item.innerText = USER.nickname;
    })
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
            let tr = list.createTableRowView();
            tr.querySelector(".checkbtn").addEventListener("click",(e)=>{
                if(confirm("確認要刪除此常用收件人？")) {
                    fetch('/address', {
                        method: 'DELETE',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify(list)
                    }).then(response => {
                        if (response.status == 200) {
                            e.target.parentElement.parentElement.remove();
                            // history.back();
                            getMemberAddress().then(data => {
                                let temp = []
                                data.forEach(list => {
                                    temp.push(new Address(list));
                                })
                                USER.addresslists = temp;
                                showAddressList();
                            })
                        }
                    }).catch(err => console.log(err))
                }
            })
            document.querySelector(".orderlist").appendChild(tr);
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
            if(name.length>=15){
                alert("姓名不得超過15個字");
                inputs[0].focus();
            }else if(name===""){
                alert("收件人姓名不得為空");
                inputs[0].focus();
            }
            if(address===""){
                alert("收件地址不得為空");
                inputs[1].focus();
            }
            if(phone===""){
                alert("電話不得為空");
                inputs[3].focus();
            }else if(!phone_rg.test(phone)){
                alert("電話格式不正確");
                inputs[3].focus();
            }

            if(name!=""&&address!=""){
                let obj = {
                    "id":USER.no,
                    "name":name,
                    "address":address,
                    "phone":phone
                }
                fetch('address',{
                    method:'post',
                    headers:{ 'Content-Type': 'application/json' },
                    body:JSON.stringify(obj)
                }).then(response=>{
                    if(response.status == 200) {
                        getMemberAddress().then(data => {
                            let temp = []
                            data.forEach(list => {
                                temp.push(new Address(list));
                            })
                            USER.addresslists = temp;
                            showAddressList();
                        })
                    }
                }).catch(err => console.log(err))
                
            }else{
                alert("請確認資料都有正確輸入")
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
            '<div class="helfinput"><p>名字</p><input type="text" value=""></div>'+
            '<div class="helfinput"><p>電話</p><input type="text" value="" disabled></div>'+
            '<div class="fullinput"><p>顯示名稱</p><input type="text" value=""></div>'+
            '<div class="fullinput">電子郵件<input type="email" value="" disabled></div>'+
        '</div>'+
        '<div class="space"></div>'+
        '<div className="changepw">'+
        '   <div className="oldpw"><p>目前密碼(不需變更請空白)</p><input type="password"></div>'+
        '   <div className="newpw"><p>新密碼 (6~12位英數至少一個英文)</p><input type="password"></div>'+
        '   <div className="newpw2"><p>確認新密碼</p><input type="password"></div>'+
        '</div>'+
        '<div class="space"></div>';
        let inputs = document.querySelectorAll(".basicinfo input");
        inputs[0].value = USER.name;
        inputs[1].value = USER.phone;
        inputs[2].value = USER.nickname;
        inputs[3].value = USER.email;
        let mainbtn = document.createElement("button");
        mainbtn.innerText = "儲存修改";
        mainbtn.classList.add("mainbtn");
        mainbtn.addEventListener("click",(e)=>{
            //檢查資料欄位合法性  //密碼欄位必須是"" 或符合資料
            let inputs = document.querySelectorAll("input");
            console.log(inputs);
            let name = inputs[0];
            let phone = inputs[1];
            let nickname = inputs[2];
            let email = inputs[3];
            let oldPassword =  inputs[4];
            let newPassword =  inputs[5];
            let newPasswordR =  inputs[6];
            let phone_rg = /^09[0-9]{8}$/;
            let pw_rg = /^(?=.*\d)(?=.*[a-zA-Z]).{6,12}$/;
            // if(phone_rg.test(phone.value)){
            //     alert("電話格式有誤")
            //     phone.focus();
            //     return false;
            // }
            if(name.value.length>=15||nickname.value.length>=15){
                alert("姓名、暱稱名字不得超過15字")
                name.focus();
                return false;
            }
            if(oldPassword.value!=""){
                //  變更密碼
                if(newPassword.value.length<=12 && pw_rg.test(newPassword.value) && newPassword.value.length>=6){
                    if(!newPassword.value===newPasswordR.value){
                        alert("第二次密碼與新密碼不同，請確認要變更的新密碼！")
                        newPasswordR.focus();
                        return false;
                    }
                }else{
                    alert("密碼格式錯誤,須為6~12位英數至少一個英文！");
                    newPassword.focus();
                    return false;
                }
            }
            let obj = {
                "id":USER.no,
                "name":name.value,
                "phone":phone.value,
                "nickname":nickname.value,
                "email":email.value,
                "oldPW":oldPassword.value,
                "newPW":newPassword.value
            }
            fetch('user/update',{
                method:'post',
                headers:{"Content-Type":"application/json"},
                body:JSON.stringify(obj)
            }).then(response=>{
                if(response.status==200){
                   return response.text();
                }
            }).then(datas=>{
                console.log(datas);
                getMemberinfo().then(data=>{
                    console.log(data)
                    USER.updateUserInfo(data);
                    showAccountInfo();
                    alert(datas);
                })
                }).catch(err=>console.log(err))

        });
        document.querySelector(".main").appendChild(mainbtn);
        document.getElementById("target").removeAttribute("id");
        document.querySelector("ul li:nth-child(4)").setAttribute("id", "target");
    
}