/**
 * Created by blaha on 16.11.2016.
 */
function CommonElement(id){
    this.element = document.getElementById(id);

    this.getValue = function(){
        return this.element.value;
    }

    this.setValue = function(value){
        this.element.value = value;
        return this;
    }
}