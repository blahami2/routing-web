/**
 * Created by blaha on 16.11.2016.
 */
function toHHMMSS(time) {
    var seconds = Math.floor(time),
        hours = Math.floor(seconds / 3600);
    seconds -= hours * 3600;
    var minutes = Math.floor(seconds / 60);
    seconds -= minutes * 60;

    if (hours < 10) {
        hours = "0" + hours;
    }
    if (minutes < 10) {
        minutes = "0" + minutes;
    }
    if (seconds < 10) {
        seconds = "0" + seconds;
    }
    return hours + ':' + minutes + ':' + seconds;
};

function Router(url) {
    this.url = url;

    this.route = function (latFrom, lonFrom, latTo, lonTo, metric, algorithm, onResultListener) {
        var requestString = this.url + "route?latFrom=" + latFrom + "&lonFrom=" + lonFrom + "&latTo=" + latTo + "&lonTo=" + lonTo + "&metric=" + metric + "&algorithm=" + algorithm;
        console.log(requestString);
        $.getJSON(requestString)
            .done(
                function (data) {
                    console.log(data);
                    var coordinates = [];
                    $.each(data.coords, function (key, val) {
                        coordinates.push(
                            SMap.Coords.fromWGS84(val.longitude, val.latitude)
                        );
                    });
                    console.log(coordinates);
                    onResultListener.done(data.length / 1000, toHHMMSS(data.time), data.executionTime, coordinates);
                }
            )
            .fail(
                function () {
                    onResultListener.fail();
                }
            );
    };
};


