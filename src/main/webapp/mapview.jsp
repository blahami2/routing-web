<%-- 
    Document   : mapview
    Created on : Apr 12, 2016, 11:15:48 AM
    Author     : Michael Blaha  {@literal <michael.blaha@certicon.cz>}
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="http://api4.mapy.cz/loader.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
        <script>Loader.load();</script>
        <script type="text/javascript">
            var layer;
            var marksLayer;
            var m; 
            function route() {
                var coords = [];

                latFrom = document.getElementById('fromLat').value;
                lonFrom = document.getElementById('fromLon').value;
                latTo = document.getElementById('toLat').value;
                lonTo = document.getElementById('toLon').value;
                layer.removeAll();
                marksLayer.removeAll();
                $.getJSON("http://localhost:8084/routing/rest/route?latFrom=" + latFrom + "&lonFrom=" + lonFrom + "&latTo=" + latTo + "&lonTo=" + lonTo,
                        function (data) {
                            //console.log(data);
                            //var items = [];
                            //$.each(data, function (key, val) {
                            //    console.log(key + ":" + val.latitude);
                            //items.push("<li id='" + key + "'>" + val + "</li>");
                            //});
                            //$("<ul/>", {
                            //    "class": "my-new-list",
                            //    html: items.join("")
                            //}).appendTo("body");
                            $.each(data, function (key, val) {
                                coords.push(
                                        SMap.Coords.fromWGS84(val.longitude, val.latitude)
                                        );
                            });
                            var g = new SMap.Geometry(SMap.GEOMETRY_POLYLINE, null, coords);
                            layer.addGeometry(g);
                            var cz = m.computeCenterZoom(coords, true);
                            m.setCenterZoom(cz[0], cz[1]);
                            var options = {};
                            var marker = new SMap.Marker(coords[0], "Start", options);
                            marksLayer.addMarker(marker);
                            var marker = new SMap.Marker(coords[coords.length - 1], "Destination", options);
                            marksLayer.addMarker(marker);
                        });
                //console.log("doing some stuff");
                //var centerMap = SMap.Coords.fromWGS84(midpoint.getLongitude(), midpoint.getLatitude());
            };
        </script>
        <title>JSP Page</title>
    </head>
    <body>
        <div id="map" style="width:1280px; height:1024px; float: left;"></div>
        <div id="right_panel" style="float: right;">
            <form>
                <fieldset>
                    <legend>From</legend>
                    <label for="longitude">longitude:</label>
                    <input id="fromLon" type="text" name="longitude" value="14.5131146"/>
                    <label for="latitude">latitude:</label>
                    <input id="fromLat" type="text" name="latitude" value="50.1032571"/>
                </fieldset>
                <fieldset>
                    <legend>To</legend>
                    <label for="longitude">longitude:</label>
                    <input id="toLon" type="text" name="longitude" value="14.3638555"/>
                    <label for="latitude">latitude:</label>
                    <input id="toLat" type="text" name="latitude" value="50.0526668"/>
                </fieldset>
            </form>
            <button type="button" onClick="route()">route!</button>
        </div>
        <script type="text/javascript">
            $(document).ready(function () {
                var first = true;
                m = new SMap(JAK.gel("map"));
                var l = m.addDefaultLayer(SMap.DEF_BASE).enable();
                m.addDefaultControls();
                layer = new SMap.Layer.Geometry();
                m.addLayer(layer).enable();
                marksLayer = new SMap.Layer.Marker();
                m.addLayer(marksLayer);
                marksLayer.enable();

                function click(e, elm) {
                    var coords = SMap.Coords.fromEvent(e.data.event, m);
                    //alert("Kliknuto na " + coords.toWGS84(2).reverse().join(" "));
                    //console.log(coords.toWGS84(0));
                    //console.log(coords.toWGS84(1));
                    //console.log(coords.toWGS84(2));
                    var lon = coords.toWGS84(0)[0];
                    //console.log(lon.substring(0, lon.length - 2));
                    var lat = coords.toWGS84(0)[1];
                    //console.log(lat.substring(0, lat.length - 2));

                    var options = {};
                    if (first) {
                        document.getElementById('fromLat').value = lat.substring(0, lat.length - 2);
                        document.getElementById('fromLon').value = lon.substring(0, lon.length - 2);
                        var marker = new SMap.Marker(coords, "Start", options);
                        marksLayer.removeAll();
                        marksLayer.addMarker(marker);
                        first = false;
                    } else {
                        document.getElementById('toLat').value = lat.substring(0, lat.length - 2);
                        document.getElementById('toLon').value = lon.substring(0, lon.length - 2);
                        var marker = new SMap.Marker(coords, "Destination", options);
                        marksLayer.addMarker(marker);
                        first = true;
                    }
                }
                m.getSignals().addListener(window, "map-click", click);
            });
        </script>
    </body>
</html>
