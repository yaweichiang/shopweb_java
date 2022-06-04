import { MYCAR } from './util.js'; 
MYCAR.showproductsTypes();
document.getElementById("phone").addEventListener("blur",()=>{
    checkphone();
})
document.getElementById("fstpw").addEventListener("keyup",()=>{
    checkpassword();
})
document.getElementById("secpw").addEventListener("click",()=>{
    checkpassword();
})
document.getElementById("secpw").addEventListener("keyup",()=>{
    checkpassword();
})
document.getElementById("singup").addEventListener("click",()=>{
    singup();
    console.log("click")
})
// 註冊頁面
function checkphone(){ //使用者填完號碼後 確認電話號碼是否有重複註冊
    
    let checkmsg =document.getElementById('checkmsg');
    let checkbox = document.getElementById("telchekresult");
    let phonenumber = document.getElementById("phone").value;
    let phone_rg = /^09[0-9]{8}$/;

    if(phone_rg.test(phonenumber)){
        //輸入電話符合格式
        //ajax後台查詢電話是否存在後進行後續
        fetch('/check/'+phonenumber).then(response=>{
            return response.text()
        }).then(data=>{
            if(data === "NotExist"){
                checkbox.innerHTML = '<img src="../static/icon/success.png" ></img>';
                checkmsg.innerText = "";
                return true;
            }else{
                checkbox.innerHTML = '<img src="../static/icon/fail.png" ></img>';
                checkmsg.innerText = "此電話已註冊";
                return false;
            }
        }).catch(err=>console.log(err))
       //不符合格式  若為空不顯示 非空顯示錯誤提示格式錯誤
       if(phonenumber.length===0){
           //若為空 清除顯示
           checkmsg.innerText = "";
           checkbox.innerHTML = "";
       }else{
            checkbox.innerHTML = '<img src="../static/icon/fail.png" ></img>';
            checkmsg.innerText = "電話號碼格式錯誤";
       }
    }   
}

function checkpassword(){ //使用者填寫第一次密碼時進行格式檢查
    let checkbox = document.getElementById("passwordcheckresult");
    let first = document.getElementById("fstpw").value;
    let pw_rg = /^(?=.*\d)(?=.*[a-zA-Z]).{6,12}$/;
    if(first.length<=12 && pw_rg.test(first) && first.length>=6){
        checkbox.innerHTML = '<img src="../static/icon/success.png" ></img>';
        checkmsg.innerText = "";
    }else if(first.length>0){
        checkbox.innerHTML = '<img src="../static/icon/fail.png" ></img>';
        checkmsg.innerText = "密碼格式錯誤，須為6~12位英數至少一個英文";
      
    }else{
        checkbox.innerHTML = "";       
    }
}

function check2password(){ //檢查再次輸入密碼是否輸入一致
    let second = document.getElementById("secpw").value;
    let first = document.getElementById("fstpw").value;
    if(first === second){

        return true;
    }else if(second.length!=0){     
        return false;
    }
}

function singup(){ //註冊資料輸入狀況檢核
    let checkmsg =document.getElementById("checkmsg");
    let username = document.getElementById("user");
    let phonenumber = document.getElementById("phone");
    let first = document.getElementById("fstpw");
    let second = document.getElementById("secpw");
    let success = document.querySelectorAll('img[src="../static/icon/success.png"]');
    if(username.value.length===0){
        username.focus();
        checkmsg.innerText = "請輸入使用者名稱"; 
        return false;
    }
    if(phonenumber.value.length===0){
        phonenumber.focus();
        checkmsg.innerText = "請輸入手機號碼"; 
        return false;
    }
    if(first.value.length===0){
        first.focus();
        checkmsg.innerText = "請輸入密碼"; 
        return false;
    }
    if(second.value.length===0){
        second.focus();
        return false;
    }else if(!check2password()){
        checkmsg.innerText = "密碼輸入不相同";
        return false;
    }
    if(success.length===2){
        document.singupform.submit();
        
    }
}


