/**
 * Created by blaha on 16.11.2016.
 */
function ComboBoxElement(id) {
    this.element = document.getElementById(id);

    this.getValue = function () {
        return this.element.options[this.element.selectedIndex].value;
    }

    this.setValue = function(value){
        for(var i = 0; i < this.element.options.length; i++){
            if(this.element.options[i].value == value){
                this.element.selectedIndex = i;
                break;
            }
        }
    }
}