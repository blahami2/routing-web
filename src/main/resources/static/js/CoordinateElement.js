/**
 * Created by blaha on 16.11.2016.
 */
function CoordinateElement(latId, lonId) {
    this.latElement = document.getElementById(latId);
    this.lonElement = document.getElementById(lonId);

    this.getLatitude = function () {
        return this.latElement.value;
    };

    this.setLatitude = function (latitude) {
        this.latElement.value = latitude;
        return this;
    };

    this.getLongitude = function () {
        return this.lonElement.value;
    };

    this.setLongitude = function (longitude) {
        this.lonElement.value = longitude;
        return this;
    };
}
