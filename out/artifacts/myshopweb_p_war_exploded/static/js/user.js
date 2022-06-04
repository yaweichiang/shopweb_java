import { MemberOrderList } from '../js/orderlist.js';
import { Address } from '../js/address.js';
import { getMemberAddress,getMemberOrderList } from '../js/util.js';


class User{
    constructor(obj){
        this.name = obj.name;
    }
}

export class Member extends User{
    constructor(memberDate){
        super(memberDate)
        this.no = memberDate.no;
        this.phone = memberDate.phone;
        this.nickname = memberDate.nickname==null?"":memberDate.nickname;
        this.email = memberDate.email==null?"":memberDate.email;
        this.orderlists = this.createOrderList();
        this.addresslists = this.createAddress();
    }

    createOrderList(){
      
        let temp =[];
        getMemberOrderList(this.phone).then(datas=>{
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