package services

import java.util.UUID

import gnieh.diffson.playJson._
import models.TrackPoint
import org.scalatest._
import play.api.libs.json.Json


class JsonPatchServiceTest extends FlatSpec with Matchers {

  "JsonPatchService.toJsonEvent" should "return valid JSONPatch-ADD operation " in {

    val someUUID = UUID.randomUUID()

    val emptyJson = "{}"

    val initPatch = Json.arr(Json.obj(
      "op" -> "add",
      "path" -> ("/" + someUUID),
      "value" -> Json.obj(
        "shipName" -> "",
        "coordinates" -> Json.arr(),
        "events" -> Json.arr()
      )
    ))

    val patchInit: JsonPatch = JsonPatch.parse(initPatch.toString())

    val jsonInit = patchInit(emptyJson)

    val jsonPatchService = new JsonPatchService()
    val jsonInitWithTrackPointAdded = JsonPatch.parse(jsonPatchService.toJsonEvent(someUUID, TrackPoint(1, 1, 1, 1, 1)).toString())(jsonInit)

    jsonInitWithTrackPointAdded should include("[1,1,1]")
  }
}
