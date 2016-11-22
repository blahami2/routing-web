/**
 * Created by blaha on 16.11.2016.
 */
function Map(element, onMarkerDropListener, onClickListener) {
    // attributes
    this.thisMapObject = this;
    this.map = new SMap(JAK.gel(element));
    this.onMarkerDropListener = onMarkerDropListener;
    this.onClickListener = onClickListener;
    this.first = true;
    this.layer = new SMap.Layer.Geometry();
    this.marksLayer = new SMap.Layer.Marker();

    // constructor
    this.map.addDefaultLayer(SMap.DEF_BASE).enable();
    this.map.addDefaultControls();
    this.map.addLayer(this.layer).enable();
    this.map.addLayer(this.marksLayer);
    this.marksLayer.enable();

    var m465745347 = this.map;
    m465745347.getSignals().addListener(window, "map-click", function (e, elm) {
        var coordinates = SMap.Coords.fromEvent(e.data.event, m465745347);
        var lonStr = coordinates.toWGS84(0)[0];
        var latStr = coordinates.toWGS84(0)[1];
        var latitude = latStr.substring(0, latStr.length - 2);
        var longitude = lonStr.substring(0, lonStr.length - 2);
        if (this.first) {
            onClickListener.sourceClick(latitude, longitude);
            var marker = this.thisMapObject.createMarker(coordinates, "S");
            this.marksLayer.removeAll();
            this.marksLayer.addMarker(marker);
            this.first = false;
        } else {
            onClickListener.targetClick(latitude, longitude);
            var marker = this.thisMapObject.createMarker(coordinates, "T");
            console.log(marker.getTitle());
            this.marksLayer.addMarker(marker);
            this.first = true;
        }
    });
}


function startDragging(event) {

};

function stopDragging(event) {
    var node = event.target.getContainer();
    node[SMap.LAYER_MARKER].style.cursor = "";
    var coords = event.target.getCoords();
    var longitude = coords.toWGS84()[0];
    var latitude = coords.toWGS84()[1];
    if (event.target.getId() === "S") { // source
        this.onMarkerDropListener.dropSource(latitude, longitude);
    } else { // target
        this.onMarkerDropListener.dropTarget(latitude, longitude);
    }
    route();
};

function createMarker(map, coordinate, text) {
    console.log(Object.getOwnPropertyNames(coordinate));
    var markerDOM = JAK.mel("div");
    var markerImg = JAK.mel("img", {src: SMap.CONFIG.img + "/marker/drop-red.png"});
    markerDOM.appendChild(markerImg);
    var markerText = JAK.mel("div", {}, {
        position: "absolute",
        left: "0px",
        top: "2px",
        textAlign: "center",
        width: "22px",
        color: "white",
        fontWeight: "bold"
    });
    markerText.innerHTML = text;
    markerDOM.appendChild(markerText);
    var marker = new SMap.Marker(coordinate, text);//, {url: markerDOM});
    //      marker.decorate(SMap.Marker.Feature.Draggable);
    //      var signals = this.map.getSignals();
    //      signals.addListener(window, "marker-drag-stop", this.stopDragging);
    //       signals.addListener(window, "marker-drag-start", this.startDragging);
    return marker;
};

function displayRoute(map, coordinates) {
    var g = new SMap.Geometry(SMap.GEOMETRY_POLYLINE, null, coordinates);
    this.layer.addGeometry(g);
    this.marksLayer.removeAll();
    var cz = this.m.computeCenterZoom(coordinates, true);
    this.map.setCenterZoom(cz[0], cz[1]);
    console.log(coordinates[0]);
    console.log(coordinates[coordinates.length - 1]);
    var marker = this.createMarker(this.m, coordinates[0], "S");
    this.marksLayer.addMarker(marker);
    var marker = this.createMarker(this.m, coordinates[coordinates.length - 1], "T");
    //      this.marksLayer.addMarker(marker);
};
