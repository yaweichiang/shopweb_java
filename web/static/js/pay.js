import { MYCAR } from "./util.js";
export default class PayType{
    constructor(obj){
        this.id = obj.id;
        this.name =obj.name;
        this.fee =  obj.fee;
    }

    showInShopCar(){
        let label = document.createElement("label");
        label.innerHTML = '<label><input type="radio" name="paytype" no="'+this.id+'" value="'+this.fee+'">'+this.name+(this.fee!=0?' +手續費 $'+this.fee:'')+'</label>'
        label.addEventListener('click',(e)=>{
            MYCAR.pay = this;            
        })
        return label;
    }
}