
export default class Announcement{
    constructor(obj){
        if(typeof(obj)==="object"){
            this.id = obj.id;
            this.content = obj.content;
            this.title = this.content.substring(0,10)+'...';  //標題字串 從內文截10個字+...
            this.target = (obj.target==="true"?true:false);
        }else{
            this.id="new"
            this.content = obj;
            this.title = obj.substring(0,10);  //標題字串 從內文截10個字+...
            this.target = true;
        }
  
    }
    getSelectOption(){     
        let option = document.createElement("option");
        if(this.target){
            option.setAttribute("selected","");
            
        }
        option.innerText = this.title;
        return option;
    }
    equal(str){
        return (this.content===str);
    }
}