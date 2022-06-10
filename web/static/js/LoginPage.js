import { MYCAR } from './util.js';
import jwtDecode from 'https://esm.run/jwt-decode';
MYCAR.showproductsTypes();
// 登入頁面
//登入資料輸入狀況檢核
// document.getElementById("login").addEventListener("click",(e)=>{
//     let usertel = document.querySelector(".infoboard input[type='tel']");
//     let password = document.querySelector(".infoboard input[type='password']");
//     let msgbox =document.getElementById('checkmsg');
//
//     if(usertel.value.length == 0 || password.value.length == 0){
//         usertel.focus();
//         usertel.select();
//         msgbox.innerText="請確實輸入帳號密碼";
//     }else{
//         let phone_rg = /^09[0-9]{8}$/;
//         if(phone_rg.test(usertel.value)){
//             msgbox.innerText="";
//             document.loginform.submit();
//         }else{
//             msgbox.innerText="手機號碼格式有誤";
//         }
//     }
// });

function handleCredentialResponse(response) {

    const responsePayload = jwtDecode(response.credential);
    let data = {
        id:responsePayload.sub,
        name:responsePayload.name,
        nickname:responsePayload.given_name,
        email:responsePayload.email,
        url:responsePayload.picture
    }
    console.log(typeof(responsePayload.sub));
    console.log(data);

    fetch('/user/',{
        method:'post',
        headers:{'Content-Type':'application/json'},
        body:JSON.stringify(data)
    }).then(response=>{
        if(response.status==200){
            window.location.href='/usercenter';
        }
    }).catch(err=>console.log(err))
}
window.onload = function () {
    google.accounts.id.initialize({
        client_id:"539314786146-5vihlvbdoppi677kdppo9gdpo3moas5e.apps.googleusercontent.com",
        callback: handleCredentialResponse
    });
    google.accounts.id.renderButton(
        document.getElementById("buttonDiv"),
        { theme: "outline", size: "large" }  // customization attributes
    );
    google.accounts.id.prompt(); // also display the One Tap dialog
}

