/**
 * Created by blaha on 16.11.2016.
 */
function MetricElement(id) {
    this.element = $("input[name=" + id + "]:checked");

    this.getMetric = function () {
        return this.element.val();
    }
}