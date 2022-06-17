// import {getMemberAddress} from "./util.js";

export class Address{
    constructor(obj){
        this.no = obj.no;
        this.name = obj.name;
        this.address = obj.address;
        this.phone = obj.phone;
    }
    createTableRowView(){// 建立顯示在會員中心的地址列表
        let tableRow = document.createElement("tr");
        tableRow.innerHTML = 
                    '<td>'+this.name+'</td>'+
                    '<td>'+this.address+'<br>'+this.phone+'</td>';
        let btnTD = document.createElement("td");
        let deleteBtn = document.createElement("button");
        deleteBtn.classList.add("checkbtn");
        deleteBtn.innerText="刪除";
        // deleteBtn.addEventListener("click",(e)=>{
        //     // 點選刪除鈕！
        //     fetch('/address',{
        //         method:'DELETE',
        //         headers:{ 'Content-Type': 'application/json' },
        //         body:JSON.stringify(this)
        //     }).then(response=>{
        //         if(response.status == 200) {
        //             e.target.parentElement.parentElement.remove();
        //             // history.back();
        //             getMemberAddress().then(data => {
        //                 let temp = []
        //                 data.forEach(list => {
        //                     temp.push(new Address(list));
        //                 })
        //                 USER.addresslists = temp;
        //                 showAddressList();
        //             })
        //         }
        //     //     if(response.status==200)
        //     //         console.log(e.target.parentElement.parentElement);
        //     //         e.target.parentElement.parentElement.remove();
        //     //         return response.json()
        //     // }).then(data=>{
        //     //     let temp =[]
        //     //         data.forEach(list=>{
        //     //             temp.push(new Address(list));
        //     //         })
        //     //         USER.addresslists = temp;
        //     //         showAddressList();
        //     }).catch(err=>{
        //         console.log(err)
        //     })
        //
        //
        // })
        btnTD.appendChild(deleteBtn);
        tableRow.append(btnTD);
        return tableRow;
    }
    createShopCarItem(){ //建立顯示在購物車常用地址的元件
        let label = document.createElement("label");
        label.innerHTML = 
            '<label>'+
            '<input type="radio" name="recipient">'+this.name+
            '</label>';
        label.addEventListener("click",()=>{
            let info = document.querySelectorAll(".addressinfo input[type='text']");
            console.log(info);
            info[0].value = this.name;
            info[1].value = this.address;
            document.querySelector(".addressinfo input[type='tel']").value = this.phone;
        })

        return label;
    }
}
