/**
 * Created by blaha on 16.11.2016.
 */
function ResultElement(resultId, lengthId, timeId, executionTimeId){
    this.resultElement = document.getElementById(resultId);
    this.lengthElement = document.getElementById(lengthId);
    this.timeElement = document.getElementById(timeId);
    this.executionElement = document.getElementById(executionTimeId);

    this.getResult = function(){
        return this.resultElement.value;
    };

    this.setResult = function(result){
        this.resultElement.value = result;
        return this;
    };
    this.getLength = function(){
        return this.lengthElement.value;
    };

    this.setLength = function(length){
        this.lengthElement.value = length;
        return this;
    };
    this.getTime = function(){
        return this.timeElement.value;
    };

    this.setTime = function(time){
        this.timeElement.value = time;
        return this;
    };
    this.getExecutionTime = function(){
        return this.executionElement.value;
    };

    this.setExecutionTime = function(executionTime){
        this.executionElement.value = executionTime;
        return this;
    };
}