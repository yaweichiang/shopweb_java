import {getOrderListByNo} from './util.js';

class OrderList{
    constructor(obj){
        this.no = obj.no;  //訂單編號
        this.orderDate = obj.orderDate;//訂購日期
        this.sendDate = obj.sendDate===null?"尚未出貨":obj.sendDate;//出貨日期
        this.type = obj.type==="order"?"訂購":(obj.type==="send"?"已寄送":"訂單取消");//訂單狀態
        this.payType = obj.payType;//付款方式
        this.name = obj.name; //訂購人姓名
        this.phone = obj.phone;//訂購人電話
        this.payNo = obj.payNo===null?"":obj.payNo;//金流編號
        this.sendNo = obj.sendNo===null?"":obj.sendNo;//物流單號
        this.remark = obj.remark===null?"":obj.remark;//訂單備註
        this.productsList = obj.productsList;//JSON.parse(obj.productsList);
        this.recipient = obj.recipient;
        this.total = obj.total;//this.getTotal();//總金額

    }
    updateData(obj){ //
        this.no = obj.no;  //訂單編號
        this.orderDate = obj.orderDate.substring(0,10);//訂購日期
        this.sendDate = obj.sendDate===null?"尚未出貨":obj.sendDate;//出貨日期
        this.type = obj.type==="order"?"訂購":(obj.type==="send"?"已寄送":"訂單取消");//訂單狀態
        this.payType = obj.payType;//付款方式
        this.name = obj.name; //訂購人姓名
        this.phone = obj.phone;//訂購人電話
        this.payNo = obj.payNo===null?"":obj.payNo;//金流編號
        this.sendNo = obj.sendNo===null?"":obj.sendNo;//物流單號
        this.remark = obj.remark===null?"":obj.remark;//訂單備註
        this.productsList = obj.productsList;//JSON.parse(obj.productsList);
        this.total =  obj.total;//總金額
    }
}

export class MemberOrderList extends OrderList{
    cancelOrder(){
        // 取消訂單
    }
    createTableRowView(){
        // 建立會員後台訂單欄dom物件
        let tableRow = document.createElement("tr");
        tableRow.innerHTML = 
            '<td></td>'+
            '<td></td>'+
            '<td></td>';
        tableRow.children[0].innerText = this.orderDate;
        tableRow.children[1].innerText = this.type;
        tableRow.children[2].innerText = ('NT$'+this.total);
        let noTD =  document.createElement("td");
        let noLink = document.createElement("a");
        noLink.innerText = this.no;
        noLink.addEventListener("click",()=>{
            //顯示訂單明細物件 並記錄瀏覽動作
            document.querySelector(".main").replaceWith(this.createDitalView());//innerHTML = this.createDitalView();
            history.pushState({action:"my_older_list"}, null, "");
        })
        noTD.appendChild(noLink);
        tableRow.insertBefore(noTD,tableRow.firstChild);
        let btnTD = document.createElement("td");
        let cancelBtn = document.createElement("button");
        cancelBtn.classList.add("checkbtn");
        cancelBtn.innerText="取消訂購";
        cancelBtn.addEventListener("click",(e)=>{
            if(this.type === "訂購") {
                if (confirm("確認取消訂單！")) {
                    let obj = {'order_no': this.no}
                    fetch('order/cancel', {
                        method: 'put',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify(obj)
                    }).then(response => {
                        let oldrow = e.target.parentElement.parentElement;
                        getOrderListByNo(this.no).then(datas => {
                            this.type = datas[0].type === "order" ? "訂購" : (obj.type === "send" ? "已寄送" : "訂單取消")
                            oldrow.parentElement.replaceChild((this.createTableRowView()), oldrow)

                        })
                    }).catch(err => {
                        console.log(err)
                    })

                }
            }else{
                let msg = this.type==="已寄送"?"商品已寄出不得取消":"此訂單已取消訂購";
                alert(msg);
            }
            
        })
        btnTD.appendChild(cancelBtn);
        tableRow.appendChild(btnTD);

        return tableRow;

    }
    createDitalView(){

        let temp = document.createElement('div');
        this.productsList.forEach(product=>{
            let p = document.createElement('p');
            p.innerText = product.name+"(單價:"+product.price+"元)"+"數量:"+product.amount;
            temp.appendChild(p)
        })
        let mainDiv = document.createElement('div');
        mainDiv.classList.add("main");
        mainDiv.innerHTML =
        '<div class="subtitle">訂單明細</div>'+
        '<div class="space"></div>'+
        '   <table class="edittable">'+
        '           <tr>'+
        '               <th>訂單</th>'+
        '               <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '               <th>訂購日期</th>'+
        '               <td></td>'+
        '               <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>狀態</th>'+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>總計</th> '+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>出貨日期</th>'+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>收貨人資訊</th>'+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>付款方式</th>'+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>購買商品</th>'+
        '                <td></td>'+
        '            </tr>'+
        '   </table>';
        console.log(mainDiv);
        console.log(mainDiv.children[2].children[0].children);
        let a = document.createElement('a');
        a.innerText = this.no;
        mainDiv.children[2].children[0].children[0].children[1].appendChild(a);
        mainDiv.children[2].children[0].children[1].children[1].innerText = this.orderDate;
        mainDiv.children[2].children[0].children[2].children[1].innerText = this.type;
        mainDiv.children[2].children[0].children[3].children[1].innerText = this.total;
        mainDiv.children[2].children[0].children[4].children[1].innerText = this.sendDate;
        mainDiv.children[2].children[0].children[5].children[1].innerText = this.recipient;
        mainDiv.children[2].children[0].children[6].children[1].innerText = this.payType;
        mainDiv.children[2].children[0].children[7].children[1].appendChild(temp);
        return mainDiv;
    }

}
export class ManagerOrderList extends MemberOrderList{
    constructor(obj){
        super(obj);
    }
    createTableRowView(){
        let newTR = document.createElement("tr");
        newTR.innerHTML = 
        '                <td></td>'+
        '                <td></td>'+
        '                <td></td>'+
        '                <td></td>'+
        '                <td></td>'+
        '                <td></td>'+
        '                <td></td>'+
        '                <td></td>'+
        '                <td><a><button class="checkbtn">查看</button></a></td>';
        let a = document.createElement('a');
        a.innerText = this.no;
        newTR.children[0].appendChild(a);
        newTR.children[1].innerText = this.name;
        newTR.children[2].innerText = this.phone;
        newTR.children[3].innerText = this.orderDate;
        newTR.children[4].innerText = this.type;
        newTR.children[5].innerText = this.total;
        newTR.children[6].innerText = this.sendDate;
        newTR.children[7].innerText = this.payType;
        newTR.lastChild.firstChild.firstChild.addEventListener("click",()=>{
            history.pushState({action:"orderdital"}, null, "");
            //點取查看進入明細
            this.createDitalView();
        });
        newTR.firstElementChild.firstChild.addEventListener("click",()=>{
            history.pushState({action:"orderdital"}, null, "");
            //點取訂單編號進入明細
            this.createDitalView();
        });
        return newTR;
    }

    createTableRowViewForMemberSearch(){
        let newTR = document.createElement("tr");
        newTR.innerHTML = 
        '                <td><a></a></td>'+
        '                <td></td>'+
        '                <td></td>'+
        '                <td></td>'+
        '                <td></td>'+
        '                <td></td>'+
        '                <td><a><button class="checkbtn">查看</button></a></td>';
        let a = document.createElement('a');
        a.innerText = this.no;
        newTR.children[0].appendChild(a);
        newTR.children[1].innerText = this.orderDate;
        newTR.children[2].innerText = this.type;
        newTR.children[3].innerText = this.total;
        newTR.children[4].innerText = this.sendDate;
        newTR.children[5].innerText = this.payType;
        newTR.lastChild.firstChild.firstChild.addEventListener("click",()=>{
            history.pushState({action:"orderdital"}, null, "");
            //點取查看進入明細
            this.createDitalView();
        });
        newTR.firstElementChild.firstChild.addEventListener("click",()=>{
            history.pushState({action:"orderdital"}, null, "");
            //點取訂單編號進入明細
            this.createDitalView();
        });
        return newTR;
    }


    createDitalView(){
        let temp=document.createElement('div');
        this.productsList.forEach(product=>{
            let p = document.createElement('p');
            p.innerText = product.name+"(單價:"+product.price+"元)"+"數量:"+product.amount;
            temp.appendChild(p);
        })
        let main = document.querySelector(".main");
        main.innerHTML =
        '<div class="subtitle">訂單明細</div>'+
        '   <div class="space"></div>'+
        '   <table class="edittable">'+
        '           <tr>'+
        '               <th>訂單</th>'+
        '               <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '               <th>姓名</th>'+
        '               <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>電話</th>'+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '               <th>訂購日期</th>'+
        '               <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>狀態</th>'+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>收貨人資訊</th>'+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>總計</th> '+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>出貨日期</th>'+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>付款方式</th>'+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>購買商品</th>'+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>金流編號</th>'+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>貨運單號</th>'+
        '                <td></td>'+
        '            </tr>'+
        '            <tr>'+
        '                <th>備註</th>'+
        '                <td></td>'+
        '           </tr>'+
        '   </table>'+
        '   <div class="space"></div>'+
        '   <div class="basicinfo">'+
        '        <div class="fullinput"><p>出貨單號：</p><input type="text" value="'+this.sendNo+'"></div>'+
        '        <div class="fullinput"><p>備註：</p><input type="text"></div>'+
        '   </div>'+
        '   <button class="mainbtn">訂單異動儲存</button>';
        console.log(main.children[2].children[0]);
        let a = document.createElement('a');
        a.innerText = this.no;
        main.children[2].children[0].children[0].children[1].appendChild(a);
        main.children[2].children[0].children[1].children[1].innerText = this.name;
        main.children[2].children[0].children[2].children[1].innerText = this.phone;
        main.children[2].children[0].children[3].children[1].innerText = this.orderDate;
        main.children[2].children[0].children[4].children[1].innerText = this.type;
        main.children[2].children[0].children[5].children[1].innerText = this.recipient;
        main.children[2].children[0].children[6].children[1].innerText = ('NT$'+this.total);
        main.children[2].children[0].children[7].children[1].innerText = this.sendDate;
        main.children[2].children[0].children[8].children[1].innerText = this.payType;
        main.children[2].children[0].children[9].children[1].appendChild(temp);
        main.children[2].children[0].children[10].children[1].innerText = this.payNo;
        main.children[2].children[0].children[11].children[1].innerText = this.sendNo;
        main.children[2].children[0].children[12].children[1].innerText = this.remark;

        main.lastChild.addEventListener("click",()=>{
            console.log("存變動")
            this.setInfo();
        })
        if(this.sendNo){ //編號存在 設定圖示 input 欄位設為 disable
            let basicInfo =  main.children[4];
            basicInfo.children[0].lastChild.setAttribute("disabled","");
            let editIcon = document.createElement("div");
            editIcon.classList.add("editicon");
            editIcon.innerHTML = '<img src="../static/icon/edit2.png">';
            editIcon.addEventListener("click",(e)=>{
                e.target.parentElement.nextElementSibling.lastChild.attributes.removeNamedItem("disabled")
                e.target.parentElement.classList.add('hide')
            })
            basicInfo.insertBefore(editIcon,basicInfo.firstChild);
        }else{
            //編號不存在
            let basicInfo =  main.children[4];
            let editIcon = document.createElement("div");
            basicInfo.insertBefore(editIcon,basicInfo.firstChild);
        }
    }
    setInfo(){
        let today = new Date();
        let date = today.getFullYear()+"/"+(today.getMonth()+1)+"/"+today.getDate();
        // 這邊應該要將變動資訊存入後端資料庫 然後重建畫面
        let obj ={
            order_no:this.no,
            send_no:document.querySelector(".basicinfo").children[1].lastChild.value,
            send_date:this.sendDate,
            remark:this.remark+date+document.querySelector(".basicinfo").children[2].lastChild.value+";"
        }
        if(this.type==="訂單取消"&&obj.send_no!=""){
            alert("會員已取消此筆訂單，出貨單號不與更新");
            obj.send_no = "";
        }
        fetch('/order/update',{
            method:'PUT',
            headers:{'Content-Type':'application/json'},
            body:JSON.stringify(obj)
        }).then(response=>{
            getOrderListByNo(obj.order_no).then(datas=>{
                this.updateData(datas[0])
                this.createDitalView();
            })
        }).catch(err=>console.log(err))
    }
}
