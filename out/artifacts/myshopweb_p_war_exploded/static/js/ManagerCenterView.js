import { getFare, getMemberOrderList, getProducts ,getCapacity} from './util.js';
import { getAnnouncement,getManagerOrderList,getMemberinfo } from './util.js';
import Announcement from './announcement.js';
import { ManagerOrderList } from './orderlist.js';
import { Member } from './user.js';
import { Product } from './product.js';


//網頁設定頁面主畫面
export class WebSetting{
    constructor(){
        this.freezingFare;// = FARE.freezingFare;
        this.normalFare;// = FARE.normalFare;
        this.freezingThreshold; //= FARE.freezingThreshold
        this.normalThreshold;//=FARE.normalThreshold;
        this.announcementList;
        this.targetAannouncement;
        getFare().then(data=>{
            this.freezingFare = data[0].name==='冷凍'?data[0].fare:data[1].fare;
            this.normalFare = data[0].name==='常溫'?data[0].fare:data[1].fare;
            this.freezingThreshold = data[0].name==='冷凍'?data[0].threshold:data[1].threshold;
            this.normalThreshold=data[0].name==='常溫'?data[0].threshold:data[1].threshold;;
        })
    }
 
    changeAnno(){  //儲存變更公告
        // // 網站公告的儲存需要獨立
        let inputAnno =  document.getElementById("announcement").innerText;
        // this.announcementList.push(this.targetAannouncement);
        let havesame=false;
        this.announcementList.forEach(anno=>{
            if(anno.equal(inputAnno)){
                this.targetAannouncement = anno;
                havesame = true;
            }
        })
        if(!havesame){
            this.targetAannouncement = new Announcement(inputAnno)
        }
        //將目前公告put到後端，若公告已存在 會更新公告時間 公告若不存在會新增公告 回傳異動的公告編號？
        fetch('/anno',{
            method:'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(this.targetAannouncement)
        }).then(response=>{
            console.log(response);
            this.announcementView()
        }).catch(err=>{
            console.log(err)
        })

    }
    listIndex(str){//查找選取的公告在陣列中的index
        let length = this.announcementList.length;
        console.log(str);
        for(let i = 0 ; i < length ; i++){
            if(this.announcementList[i].title===str){
                return i;
            }
        }
        return (-1);
    }

    fareView(){ //頁面上半部分運費相關設定顯示dom物件
        let basicDiv = document.createElement("div");
        basicDiv.classList.add("basicinfo");
        basicDiv.innerHTML = '<div class="helfinput"><p>常溫運費</p>'+'<div class="editicon"><img src="../static/icon/edit2.png"></div>'+'<input type="text" value="'+this.normalFare+'" disabled></div>'+
        '    <div class="helfinput"><p>冷凍運費</p>'+'<div class="editicon"><img src="../static/icon/edit2.png"></div>'+'<input type="text" value="'+this.freezingFare+'" disabled></div>'+
        '    <div class="helfinput"><p>常溫免運門檻</p>'+'<div class="editicon"><img src="../static/icon/edit2.png"></div>'+'<input type="text" value="'+this.normalThreshold+'" disabled></div>'+
        '    <div class="helfinput"><p>冷凍免運門檻</p>'+'<div class="editicon"><img src="../static/icon/edit2.png"></div>'+'<input type="text" value="'+this.freezingThreshold+'" disabled></div>';

        //監控 編輯按鈕 點選編輯按鈕才可以異動input格中的數字
        basicDiv.addEventListener("click",(e)=>{
            let target = e.target;
            console.log(target.tagName);
            if(target.tagName==="IMG"){
                target.parentElement.classList.add("hide");
                console.log("a",target.parentElement.nextElementSibling.attributes);
                target.parentElement.nextElementSibling.attributes.removeNamedItem("disabled");
            }
        })

        return basicDiv;

    }
    announcementView(){ //頁面下半部分公告相關設定顯示dom物件
        let annoDiv = document.createElement("div");
        annoDiv.classList.add("fullinput");
        annoDiv.innerHTML = '<p>最新消息公告</p>'+
        '    <div class="editicon"><img src="../static/icon/edit2.png"></div>'+
        '    <div class="floatinput hide">'+    
        '       <textarea id="temp"></textarea>'+
        '       <button class="smallmainbtn">確認</button>'+
        '       <button class="smallmainbtn">取消</button>'+
        '    </div>'+
        '    <div class="helfinput">'+
        '        <select class="announcementselect">'+
        '            <option> ＜近期公告快選＞</option>'+
        '        </select>'+
        '    </div>'+
        '    <div class="inputarea">'+
        '       <div class="" id="announcement"></div>'+
        '    </div>';
        //將公告物件的option物件加到div物件中顯示在畫面上
        getAnnouncement().then(datas=>{
            console.log(datas)
            let list = [] 
            // datas.forEach(data=>{
            for(let i = 0 ; i< datas.length;i++){
                let temp = new Announcement(datas[i]);
                list.push(temp)
                console.log("tt",temp)
                annoDiv.children[3].firstElementChild.append(temp.getSelectOption());
                if(temp.target){
                    // 同步確認哪一個公告是正在公告的選項 將公告的內容顯示在公告內文的欄位中
                    annoDiv.children[4].firstElementChild.innerText = temp.content;
                }
            }
            // })
            this.announcementList = list;
        }).catch(err=>{console.log(err)})

        // 監控選取框，如果選取有變化的時候將選取到的公告內容填入公告內文欄位中，待後續按下儲存時存入資料庫中
        annoDiv.children[3].firstElementChild.addEventListener("change",(e)=>{
            console.log(e.target.value);
            let index = this.listIndex(e.target.value);
            if(index!=-1){
                document.getElementById("announcement").innerText = this.announcementList[index].content;
            }else{
                document.getElementById("announcement").innerText = "";
            }
            
        });
        //監控 新增公告圖示按鈕
        annoDiv.children[1].firstChild.addEventListener("click",(e)=>{
            // 隱藏選取 及主送出鈕
            document.querySelector("select").classList.add("hide");
            document.querySelector(".mainbtn").classList.add("hide");
            document.getElementById("announcement").classList.add("hide");
            // 顯示公告輸入框
            document.querySelector(".floatinput").classList.remove("hide"); 
        })
        //監控 公告編輯框
        annoDiv.children[2].addEventListener("click",(e)=>{
            console.log(e.target.tagName);
            let target = e.target;
            // 如果點擊的是按鈕
            if(target.tagName==="BUTTON"){
                // 點擊確認的話  將輸入的公告內容移到公告內文顯示並關閉編輯框 顯示原本的畫面
                if(target.innerText === "確認"){
                    let orignselect = document.querySelectorAll("select option")[0];
                    //  如果就選項不是第一個預設空值選取 將舊選取清除 將選取改成第一個
                    if(!orignselect.selected){
                                // 清除
                        document.querySelectorAll("select option").forEach( item =>{
                            if(item.selected){
                                item.selected=false;
                            }});
                                // 設定為第一個
                                orignselect.selected = true;   
                    } 
                        //將編輯欄位輸入的內容顯示在公告內容欄位
                    document.getElementById("announcement").innerText = document.getElementById("temp").value;
                }
                    // 顯示 選取及儲存鈕 
                document.getElementById("announcement").classList.remove("hide");
                document.querySelector("select").classList.remove("hide");
                document.querySelector(".mainbtn").classList.remove("hide");
                    // 隱藏編輯框 
                document.querySelector(".floatinput").classList.add("hide"); 
            }
        });
        return annoDiv;
    }
    setFare(){
        let inputsFare = document.querySelector(".main").children[2].children;
        let obj = {
            "常溫":{
                "fare":inputsFare[0].children[2].value,
                "threshold":inputsFare[2].children[2].value
            },
            "冷凍":{
                "fare":inputsFare[1].children[2].value,
                "threshold":inputsFare[3].children[2].value
            }
        };
        fetch('/tote',{
            method:'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(obj)
        }).then(response=>{
           if(response.status==200){
               getFare().then(data=>{
                   this.freezingFare = data[0].name==='冷凍'?data[0].fare:data[1].fare;
                   this.normalFare = data[0].name==='常溫'?data[0].fare:data[1].fare;
                   this.freezingThreshold = data[0].name==='冷凍'?data[0].threshold:data[1].threshold;
                   this.normalThreshold=data[0].name==='常溫'?data[0].threshold:data[1].threshold;
                   let main = document.querySelector(".main");
                   main.replaceChild(this.fareView(),main.children[2]);
               }).catch(err=>console.log(err))
           }
        }).catch(err=>{
            console.log(err)
        })
    }
}
export class ProductsEdit{
    constructor(){
        this.products = []; 
        // ajax 取得商品資訊
        this.flashData();
    }
    flashData(){
        getProducts().then(data=>{
            data.map(item=>{
                this.products.push(new Product(item));
            })
        })
    }
    show(){
        let main = document.querySelector(".main");
        main.innerHTML = 
                '<div class="subtitle">商品管理</div>'+
                '<div class="space"></div>';
        main.appendChild(this.tableView());
        document.getElementById("target").removeAttribute("id");
        document.querySelector("ul li:nth-child(4)").setAttribute("id", "target");
    }
    tableView(){
        let table = document.createElement("table");
        table.innerHTML=
            '    <thead>'+
            '        <tr>'+
            '            <th>商品編號</th>'+
            '            <th>商品名稱</th>'+
            '            <th>商品容量</th>'+
            '            <th>商品單價</th>'+
            '            <th>商品數量</th>'+
            '            <th>銷售狀態</th>'+
            '            <th>動作<div class="addicon" ><img  src="../static/icon/add.png" alt=""></div></th>'+
            '        </tr>'+
            '    </thead>'+
            '    <tbody class="orderlist">'+
            '    </tbody>'+
            '</table>';
            table.firstElementChild.firstElementChild.lastElementChild.lastChild.lastChild.addEventListener("click",()=>{
                //右上角新增加號監聽
                history.pushState({action:"productedit"}, null, "");
                let main = document.querySelector(".main");
                getCapacity().then(datas=>{
                    let temp = "";
                    datas.forEach(data=>{

                        temp+="<option value="+data.c_no+">"+data.c_package+"</option>"
                    })
                    main.innerHTML = this.createNewProduct(temp);

                    main.lastChild.addEventListener("click",()=>{
                        //儲存建立紐監聽
    
                        let temp = []
                        let inputs = document.querySelectorAll("input[type='text']");
                        let radioFreezing = document.querySelectorAll("input[name='isFreezing']");
                        let radioType = document.querySelectorAll("input[name='type']");
                        let textarea = document.querySelector("textarea");
                        let option = document.querySelectorAll("option");
                        let checkRadio =(arr)=>{
                            for(let i = 0; i<arr.length;i++){
                                if(arr[i].checked==true){
                                    temp.push(arr[i].value);
                                    return true;
                                }
                            }
                            return false;
                        }
                        let checkOption =(arr)=>{
                            for(let i = 1; i<arr.length;i++){
                                if(arr[i].selected==true){
                                    temp.push(arr[i].value);
                                    return true;
                                }
                            }
                            return false;
                        }
                        for(let i = 0 ; i<inputs.length;i++){
                            if(inputs[i].value === ""){
                                inputs[i].focus;
                                alert("請輸入商品資料");
                                return false;
                            }else if(i==1||i==2){
                                let check = /^[1-9]+[0-9]*$/;
                                if(!check.test(inputs[i].value)){
                                    alert(i==1?"請輸入正確商品單價":"請輸入正確商品數量");
                                    return false;
                                }
                            }
                        }
                        if(textarea.value==""){
                            alert("請輸入商品簡介");
                            return false;
                        }
                        if(!checkRadio(radioFreezing)||!checkRadio(radioType)||!checkOption(option)){
                            alert("請選取應選資料");
                            return false;
                        }
                      
                        let obj = {
                            name:inputs[0].value, //名稱
                            price:inputs[1].value,//價格
                            inventory:inputs[2].value,//庫存
                            type:temp[1],//銷售狀態
                            capacity:temp[2],//容量
                            tote_type:temp[0]==="freezing"?1:2,//運送狀態
                            introduction:textarea.value,//介紹
                            url:inputs[3].value//圖片url
                        }
                        fetch('/products',{
                            method:'post',
                            headers:{'Content-Type':'application/json'},
                            body:JSON.stringify(obj)
                        }).then(response=>{
                            if(response.status==200){
                                getProducts().then(datas=>{
                                    let temp = [];
                                    datas.forEach(data=>{
                                        temp.push(new Product(data))
                                    })
                                    this.products = temp;
                                    this.show()
                                })
                            }
                            // return response.json()
                        // }).then(datas=>{
                        //     temp = [];
                        //     datas.forEach(data=>{
                        //        temp.push(new Product(data))
                        //     })
                        //     this.products = temp;
                        //     this.show()
                        }).catch(err=>console.log(err))
                        
                    })
                    
                }).catch(err=>{console.log(err)})

                // })
            })

            
            this.products.forEach(product=>{
                table.children[1].appendChild(product.getProductTR());
            })
        return table;

    }
    createNewProduct(temp){
        return '<div class="subtitle">新增商品</div>'+
            '<div class="space"></div>'+
                '<div class="basicinfo">'+
                '    <div class="helfinput">'+
                '        <p>商品名稱</p>'+
                '        <input type="text">'+
                '    </div>'+
                '    <div class="helfinput">'+
                '        <p>商品狀態</p>'+
                '        <div class="radios">'+
                '            <div class="radiolabel">'+
                '                <label><input type="radio" name="type" value="true">銷售中</label>'+
                '            </div>'+
                '            <div class="radiolabel">'+
                '                <label><input type="radio" name="type" value="false" >停售</label>'+
                '            </div>'+
                '        </div>'+
                '    </div>'+
                '    <div class="helfinput">'+
                '        <p>商品單價</p>'+
                '        <input type="text">'+
                '    </div>'+
                '    <div class="helfinput">'+
                '        <p>運送狀態</p>'+
                '        <div class="radios">'+
                '            <div class="radiolabel">'+
                '                <label><input type="radio" name="isFreezing" value="freezing">冷凍運送</label>'+
                '            </div>'+
                '            <div class="radiolabel">'+
                '                <label><input type="radio" name="isFreezing" value="normal">常溫運送</label>'+
                '            </div>'+
                '        </div>'+
                '    </div>'+
                '    <div class="helfinput">'+
                '        <p>商品數量</p>'+
                '        <input type="text">'+
                '    </div>'+
                '    <div class="helfinput">'+
                '        <p>商品容量</p>'+
                '        <select>'+
                '            <option>選擇商品容量</option>'+
                temp+
                '        </select>'+
                '    </div>'+
                '    <div class="helfinput">'+
                '        <p>照片上傳</p>'+
                '        <input type="text" value="../static/products/product1.jpg" disabled>'+
                '    </div>'+
                '    <div class="helfinput">'+
                '    </div>'+
                '</div>'+
                '<div class="fullinput">'+
                '    <p>產品說明</p>'+
                '    <textarea name="" id="" cols="30" rows="10"></textarea>'+
                '</div>'+
                '<button class="mainbtn">儲存建立</button>';
    }
}
export class OrderListSearch{
    constructor(){
        this.str;
        this.searchResult;
    }

    searchBar(arr){
        let div = document.createElement("div");
        div.innerHTML = 
        '<div class="searchbar">'+
        '    <div>搜尋：</div>'+
        '    <input type="date">'+
        '    <a><img src="../static/icon/search.png"></a>'+
        '</div>'+
        '<div class="quicksearch">'+
        '    <p>快速搜尋： </p>'+
        '    <a>近7日訂單</a>'+
        '    <a>近30日訂單</a>'+
        '    <a>近半年訂單</a>'+
        '</div>'+
        '<div class="space"></div>'+
        '   <table>'+
        '       <thead>'+
        '           <tr>'+
        '               <th>訂單</th>'+
        '               <th>姓名</th>'+
        '               <th>電話</th>'+
        '               <th>訂購日期</th>'+
        '               <th>狀態</th>'+
        '               <th>總計</th> '+
        '               <th>出貨日期</th>'+
        '               <th>付款方式</th>'+
        '               <th>動作</th>'+
        '           </tr>'+
        '       </thead>'+
        '       <tbody class="orderlist">'+
        '       </tbody>'+
        '   </table>';
        console.log(div.children[3]);
        if(arr!=null){
            arr.forEach(item=>{
                div.children[3].lastElementChild.appendChild(new ManagerOrderList(item).createTableRowView());
            })
        }
                // 搜尋圖示點選監聽
        div.firstChild.lastChild.addEventListener("click",(e)=>{
            let date = div.firstChild.children[1].value;
            console.log("data",date);
            
            // this.searchResult = date==""?this.ajaxSearchOrderList("180"):this.ajaxSearchOrderList(date);
            div.replaceChild(this.tableView("date",date),div.lastChild);
        })
        div.children[1].addEventListener("click",(e)=>{
            console.log(e.target.text)
            let target = e.target
            console.log(target.tagName === "A")
            if(target.tagName === "A"){
                switch(target.text){
                    case "近7日訂單":
                        div.replaceChild(this.tableView("days",7),div.lastChild);
                        break;
                    case "近30日訂單":
                        div.replaceChild(this.tableView("days",30),div.lastChild);
                        break;
                    case "近半年訂單":
                        div.replaceChild(this.tableView("days",180),div.lastChild);
                        break;
                }
            }
        })
        return div;
    }
    tableView(type,date){
        history.pushState({action:"doordersearch",searchKey:this.searchResult}, null, "");
        let table = document.createElement("table");
        table.innerHTML=
            '       <thead>'+
            '           <tr>'+
            '               <th>訂單</th>'+
            '               <th>姓名</th>'+
            '               <th>電話</th>'+
            '               <th>訂購日期</th>'+
            '               <th>狀態</th>'+
            '               <th>總計</th> '+
            '               <th>出貨日期</th>'+
            '               <th>付款方式</th>'+
            '               <th>動作</th>'+
            '           </tr>'+
            '       </thead>'+
            '       <tbody class="orderlist">'+
            '       </tbody>';
        getManagerOrderList(type,date).then(datas=>{
            datas.forEach(data=>{
                table.children[1].appendChild(new ManagerOrderList(data).createTableRowView()); 
            })
        })
        return table;
    }

}
export class MemberSearch{
    constructor(){
        this.member;
    }

    searchBar(arr){
        let div = document.createElement("div");
        div.innerHTML = 
        '<div class="searchbar">'+
        '    <div>搜尋會員：</div>'+
        '    <input type="text">'+
        '    <a><img src="../static/icon/search.png"></a>'+
        '</div>'+
        '    <div class="space"></div>'+
        '    <div class="minsubtitle">會員資訊</div>'+
        '    <div class="memberinfo">'+
        
        '        <div>'+
        '            <div class="name">姓名：'+(arr!=null?arr.name:"")+'</div>'+
        '            <div class="phone">電話：'+(arr!=null?arr.phone:"")+'</div>'+
        '            <div class="mail">Mail：'+(arr!=null?arr.email:"")+'</div>  '+
        '        </div>'+
        '    </div>'+
        '    <div class="space"></div>'+
        '<div class="minsubtitle">訂單資訊</div>'+
        '<table>'+
        '    <thead>'+
        '        <tr>'+
        '            <th>訂單</th>'+
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
        if(arr!=null){
            arr.orderlists.forEach(list=>{
                div.children[6].lastChild.appendChild(new ManagerOrderList(list).createTableRowViewForMemberSearch());
            })
        }
        div.firstChild.lastChild.addEventListener("click",(e)=>{
            console.log("點擊搜尋 先進行會員搜尋 如果搜尋結果為一人,就同時進行訂單查詢 若搜尋為多人 顯示多位會員供點選 點選後才進行訂單查詢");
            console.log(div.firstChild.children[1].value);
            getMemberinfo(div.firstChild.children[1].value).then(info=>{
                console.log("info=>",info.length)
                if(info.length === 0){
                    this.member = new Member([]);
                    let temp = document.createElement("div");
                    temp.innerHTML=
                    '<div class="name">姓名：</div>'+
                    '<div class="phone">電話：</div>'+
                    '<div class="mail">Mail：</div>  ';
                    
                    div.children[3].replaceChild(temp,div.children[3].firstElementChild);
                    console.log("list",this.member.orderlists[0])
                    console.log("list",this.member.orderlists)
                }else{
                    this.member = new Member(info[0]);
                    // console.log(typeof(this.member.orderlists) === undefined)
                    // console.log(this.member)
                    let temp = document.createElement("div");
                    temp.innerHTML=
                    '<div class="name">姓名：'+this.member.name+'('+this.member.nickname+')</div>'+
                    '<div class="phone">電話：'+this.member.phone+'</div>'+
                    '<div class="mail">Mail：'+this.member.email+'</div>  ';
                    
                    div.children[3].replaceChild(temp,div.children[3].firstElementChild);
                    console.log("list",this.member.orderlists[0])
                    console.log("list",this.member.orderlists)
                }
                // console.log("ddd",document.querySelector(".main").children[1].children[6])
                getMemberOrderList(this.member.no).then(datas=>{
                    let temp = [];
                    datas.forEach(data=>{
                        console.log("管理者載入會員訂單",data)
                        temp.push(new ManagerOrderList(data))
                    })
                    this.member.orderlists = temp;
                    document.querySelector(".main").children[1].replaceChild(this.tableView(), document.querySelector(".main").children[1].children[6])

                })
                console.log(this.member.orderlists[0])
                document.querySelector(".main").children[1].replaceChild(this.tableView(), document.querySelector(".main").children[1].children[6])
                // this.tableView();
                console.log("33",this.member.orderlists === null)
            })
        })
        return div;
    }
    

    tableView(){
        history.pushState({action:"domembersearch",searchKey:this.member}, null, "");//
        let table = document.createElement("table");
        table.innerHTML=
            '       <thead>'+
            '        <tr>'+
            '            <th>訂單</th>'+
            '            <th>訂購日期</th>'+
            '            <th>狀態</th>'+
            '            <th>總計</th> '+
            '            <th>出貨日期</th>'+
            '            <th>付款方式</th>'+
            '            <th>動作</th>'+
            '        </tr>'+
            '       </thead>'+
            '       <tbody class="orderlist">'+
            '       </tbody>';
        console.log(this.member.orderlists)
        this.member.orderlists.forEach(list=>{
            console.log("fe",list);
            table.children[1].appendChild(list.createTableRowViewForMemberSearch());
        });
        console.log("table",table);
        return table;

    
    }
    ajaxSearchMember(str){
        // 這邊應該要ajax向後端查詢訂單資料
        let numcheck = /^09[0-9]{8}$/;
        if(numcheck.test(str)){
            console.log("查詢會員:",str);
            if(str ==180){
                return getMemberinfo("");
            }
        }else{
            console.log("輸入電話格式異常",str);
                        //暫時使用假資料填充
            return getMemberinfo();
        }
    }
}