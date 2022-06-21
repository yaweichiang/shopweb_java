import { MemberOrderList } from './orderlist.js';
import { Address } from './address.js';
import { getMemberAddress,getMemberOrderList } from './util.js';


export class User{
    constructor(obj){
        this.name = obj.name;
        this.no = obj.no;
        this.phone = obj.phone==null?"": obj.phone;
        this.email = obj.email==null?"":obj.email;
        this.url=obj.url==null?"":obj.url;
    }
    createSearchResultTR(){
        let tr = document.createElement("tr");
        tr.innerHTML =
            '<td><a>'+this.no+'</a></td>'+
            '<td><div style="display: flex;justify-content: center;flex-direction: row;">'+'<img src="'+this.url+'" width="30px" height="30px" style="border-radius: 15px">'+this.name+'<div></td>'+
            '<td>'+this.phone+'</td>'+
            '<td>'+this.email+'</td>';
        return tr;
    }
}

export class Member extends User{
    constructor(memberDate){
        super(memberDate)
        this.nickname = memberDate.nickname==null?"":memberDate.nickname;
        this.orderlists = this.createOrderList();
        this.addresslists = this.createAddress();
    }

    createOrderList(){
      
        let temp =[];
        getMemberOrderList(this.no).then(datas=>{
            datas.forEach(data=>{
                temp.push(new MemberOrderList(data));
            })
        }).catch(err=>{console.log(err)})
  
        return temp;
    }
    updateUserInfo(obj){
        this.name = obj.name;
        this.phone = obj.phone;
        this.nickname = obj.nickname==null?"":obj.nickname;
        this.email = obj.email==null?"":obj.email;

    }
    createAddress(){
        let temp =[];
        getMemberAddress(this.phone).then(datas=>{
                datas.forEach(data=>{
                    temp.push(new Address(data));
             
                })
        }).catch(err=>{console.log(err);})   
        return temp;
    }

}