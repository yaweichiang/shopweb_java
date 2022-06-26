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
                    '<td></td>'+
                    '<td><span></span><br><span></span></td>';
        tableRow.firstChild.innerText = this.name;
        tableRow.lastChild.firstChild.innerText = this.address;
        tableRow.lastChild.lastChild.innerText = this.phone;
        let btnTD = document.createElement("td");
        let deleteBtn = document.createElement("button");
        deleteBtn.classList.add("checkbtn");
        deleteBtn.innerText="刪除";
        btnTD.appendChild(deleteBtn);
        tableRow.append(btnTD);
        return tableRow;
    }
    createShopCarItem(){ //建立顯示在購物車常用地址的元件
        let label = document.createElement("label");
        label.innerHTML = 
            '<label>'+
            '<input type="radio" name="recipient">'+//this.name+
            '</label>';
        label.innerText = this.name;
        label.addEventListener("click",()=>{
            let info = document.querySelectorAll(".addressinfo input[type='text']");
            info[0].value = this.name;
            info[1].value = this.address;
            document.querySelector(".addressinfo input[type='tel']").value = this.phone;
        })

        return label;
    }
}
