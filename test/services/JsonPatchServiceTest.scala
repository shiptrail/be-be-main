package services

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator
import org.scalatest.FunSuite
import gnieh.diffson.playJson._
import models.TrackPoint
import play.api.libs.json.Json


class JsonPatchServiceTest extends FunSuite {

  test("debuggingTestForJSONPatch") {

    val uuidGenerator = new UUIDGenerator()
    val uuid = uuidGenerator.generateId()

    val emptyJson = """{}"""

    val initPatch = Json.arr(Json.obj(
      "op" -> "add",
      "path" -> ("/" + uuid),
      "value" -> Json.obj(
        "shipName" -> "",
        "coordinates" -> Json.arr(),
        "events" -> Json.arr()
      )
    ))

    val patchInit: JsonPatch = JsonPatch.parse(initPatch.toString())

    val jsonInit = patchInit(emptyJson)

    println(jsonInit)

    val jsonPatchService = new JsonPatchService()
    val jsonInitWithTrackPointAdded = JsonPatch.parse(jsonPatchService.toJsonEvent(uuid, TrackPoint(1, 1, 1, 1, 1)).toString())(jsonInit)

    println(jsonInitWithTrackPointAdded)
  }
}
