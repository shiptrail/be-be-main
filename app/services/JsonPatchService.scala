package services

import java.util.UUID

import models.TrackPoint
import play.api.libs.json.Json


class JsonPatchService {
  def toJsonEvent(device: UUID, trackPoint: TrackPoint) = {
    Json.arr(Json.obj(
      "op" -> "add",
      "path" -> ("/" + device.toString + "/coordinates" + "/-"),
      "value" -> Json.arr(trackPoint.lat, trackPoint.lng, trackPoint.timestamp)
    ))
  }
}
