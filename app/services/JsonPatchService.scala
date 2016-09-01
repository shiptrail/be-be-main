package services

import java.util.UUID

import models.TrackPoint
import play.api.libs.json.Json


class JsonPatchService {

  val knownUUIDs = scala.collection.mutable.Set("")

  def toJsonEvent(device: UUID, trackPoint: TrackPoint) = {
    println(knownUUIDs)
    if (knownUUIDs(device.toString)) {
      Json.arr(Json.obj(
        "op" -> "add",
        "path" -> ("/" + device.toString + "/coordinates" + "/-"),
        "value" -> Json.arr(trackPoint.lat, trackPoint.lng, trackPoint.timestamp)
      ))
    } else {
      knownUUIDs += device.toString
      Json.arr(Json.obj(
        "op" -> "add",
        "path" -> ("/" + device),
        "value" -> Json.obj(
          "shipName" -> "",
          "coordinates" -> Json.arr(),
          "events" -> Json.arr()
        )
      ),Json.obj(
        "op" -> "add",
        "path" -> ("/" + device.toString + "/coordinates" + "/-"),
        "value" -> Json.arr(trackPoint.lat, trackPoint.lng, trackPoint.timestamp)
      ))
    }


  }

}
