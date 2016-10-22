package services.dummytracks

import java.io.{File, FileWriter, PrintWriter}
import java.util.UUID
import javax.inject.Inject

import play.Logger
import com.google.inject.Singleton
import models.TrackPoint
import com.typesafe.config.ConfigFactory
import services.{MultiFormatParser, TrackService}

import scala.collection.mutable
import com.lambdaworks.jacks.JacksMapper

/**
  * Will initialize the application with tracks from files inside the ./app/services/dummytracks/ folder.
  * All gpx-, cgps-, fps- and json-files will be used for it. Every file will get his own random UUID
  * and every TrackPoint in each File will be associated to that UUID, even if there is more than one
  * Track inside the file. Each Track will be writen into a file in the Frontend-File-Format.
  */
@Singleton
class InitDummyTracks @Inject()(trackService: TrackService[TrackPoint]) {
  private val filePath =
    ConfigFactory.load().getString("file.path.of.inittracks")

  if (!filePath.isEmpty && new File(filePath).isDirectory) {
    val listOfFiles = new File(filePath).listFiles.filter(f =>
          f.getName.endsWith(".gpx") || f.getName.endsWith(".cgps") ||
          f.getName.endsWith(".fps"))
    Logger.debug(s"List of files: $listOfFiles")
    new LoadInitTracks(trackService).loadDummyTracks(listOfFiles)
  }
}

class LoadInitTracks @Inject()(trackService: TrackService[TrackPoint]) {

  def loadDummyTracks(listOfFiles: Array[File]): Unit = {
    for (file <- listOfFiles) {
      Logger.debug(s"Currently parsing: $file")
      val (trackPointIterator1, trackPointIterator2) =
        MultiFormatParser.parse(file).duplicate
      val device = UUID.randomUUID()
      loadDummyTrack(trackPointIterator1, device)
      new SaveDummyTracksIntoFile().saveToJsonFile(trackPointIterator2, device)
    }
  }

  def loadDummyTrack(
      trackPointIterator: Iterator[TrackPoint], device: UUID): Unit = {
    for (trackPoint <- trackPointIterator) {
      trackService.consumeDummyTrack(device, trackPoint)
    }
  }
}

class SaveDummyTracksIntoFile {
  def saveToJsonFile(
      trackPointIterator: Iterator[TrackPoint], device: UUID): Unit = {
    val filePath =
      ConfigFactory.load().getString("file.path.of.destination.for.dummyfiles")
    val id = device.toString()
    val file = filePath + id + ".json"
    Logger.debug(s"Currently writing: $file")
    val coordinates: mutable.ListBuffer[(Double, Double, Long)] =
      mutable.ListBuffer()
    for (trackPoint <- trackPointIterator) {
      val coordinatesEntry =
        (trackPoint.lat, trackPoint.lng, trackPoint.timestamp.value)
      coordinates += coordinatesEntry
    }
    val json = JacksMapper
      .writeValueAsString[mutable.ListBuffer[(Double, Double, Long)]](
        coordinates)

    val out = new PrintWriter(new FileWriter(file));
    out.write("{ \"tracks\" : [{ \"id\": \"" + id + "\",\"coordinates\": " +
        json + ", \"events\": []}]}")
    out.close()
  }
}
