/**
 * Created by blaha on 16.11.2016.
 */
function AlgorithmElement(id) {
    this.element = document.getElementById(id);

    this.getAlgorithm = function () {
        return this.element.options[this.element.selectedIndex].value;
    }
}