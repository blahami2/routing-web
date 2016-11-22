/**
 * Created by blaha on 16.11.2016.
 */
function Reporter(url) {
    this.url = url;

    this.report = function (latFrom, lonFrom, latTo, lonTo, metric, algorithm, message, onReportListener) {
        var requestString = url + "report?latFrom=" + latFrom + "&lonFrom=" + lonFrom + "&latTo=" + latTo + "&lonTo=" + lonTo + "&metric=" + metric + "&algorithm=" + algorithm + "&message=" + encodeURIComponent(message);
        console.log(requestString);
        $.getJSON(requestString)
            .done(
                function (data) {
                    onReportListener.done();
                }
            )
            .fail(
                function () {
                    onReportListener.fail();
                }
            );
    };
};
