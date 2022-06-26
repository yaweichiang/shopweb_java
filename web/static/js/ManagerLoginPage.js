// 登入頁面
//登入資料輸入狀況檢核
document.getElementById("login").addEventListener("click",(e)=>{
    let usertel = document.querySelector(".infoboard input[type='text']");
    let password = document.querySelector(".infoboard input[type='password']");
    let msgbox =document.getElementById('checkmsg');
    if(usertel.value.length == 0 || password.value.length == 0){
        usertel.focus();
        usertel.select();
        msgbox.innerText="請確實輸入帳號密碼";
    }else{
        // let phone_rg = /^09[0-9]{8}$/;
        // if(phone_rg.test(usertel.value)){
        msgbox.innerText="";
        document.loginform.submit();
        // }else{
        //     msgbox.innerText="手機號碼格式有誤";
        // }
    }
});


