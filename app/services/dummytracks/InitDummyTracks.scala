package services.dummytracks

import java.io.File
import java.util.UUID
import javax.inject.Inject

import play.Logger;

import com.google.inject.Singleton
import models.TrackPoint
import com.typesafe.config.ConfigFactory
import services.{MultiFormatParser, TrackService}

/**
  * Will initialize the application with tracks from files inside the ./app/services/dummytracks/ folder.
  * All gpx-, cgps-, fps- and json-files will be used for it. For the json-files it will be assumed
  * that they are in the output-file-format of the Android-App. Every file will get his own random UUID
  * and every TrackPoint in each File will be associated to that UUID, even if there is more than one
  * Track inside the file.
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
      val trackPointIterator = MultiFormatParser.parse(file)
      loadDummyTrack(trackPointIterator)
    }
  }

  def loadDummyTrack(trackPointIterator: Iterator[TrackPoint]): Unit = {
    val device = UUID.randomUUID()
    for (trackPoint <- trackPointIterator) {
      trackService.consume(device, trackPoint)
    }
  }
}
