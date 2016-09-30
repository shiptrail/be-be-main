package services.dummytracks

import java.io.File
import java.util.UUID
import javax.inject.Inject

import com.google.inject.Singleton
import models.TrackPoint
import services.{MultiFormatParser, TrackService}

/**
  * Will initialize the application with tracks from files inside the ./app/services/dummytracks/ folder.
  * All gpx-, cgps-, fps- and json-files will be used for it. For the json-files it will be assumed
  * that they are in the output-file-format of the Android-App. Every file will get his own random UUID
  * and every TrackPoint in each File will be associated to that UUID, even if there is more than one
  * Track inside the file.
  * @param trackService
  */
@Singleton
class InitDummyTracks @Inject()(trackService: TrackService[TrackPoint]) {
  val listOfGPXFiles = new File("./app/services/dummytracks/").listFiles.filter(_.getName.endsWith(".gpx"))
  val listOfCGPSFiles = new File("./app/services/dummytracks/").listFiles.filter(_.getName.endsWith(".cgps"))
  val listOfFPSFiles = new File("./app/services/dummytracks/").listFiles.filter(_.getName.endsWith(".fps"))
  val listOfAndroidJSONFiles = new File("./app/services/dummytracks/").listFiles.filter(_.getName.endsWith(".json"))
  val listOfFiles = listOfGPXFiles ++ listOfCGPSFiles ++ listOfFPSFiles ++ listOfAndroidJSONFiles
  new LoadTracks().loadDummyTracks(listOfFiles, trackService)
}

class LoadTracks {

  def loadDummyTracks(listOfFiles: Array[File], trackService: TrackService[TrackPoint]): Unit = {
    for (file <- listOfFiles) {
      val trackPointIterator = MultiFormatParser.parse(file)
      loadDummyTrack(trackPointIterator, trackService)
    }
  }

  def loadDummyTrack(trackPointIterator: Iterator[TrackPoint], trackService: TrackService[TrackPoint]): Unit = {
    val device = UUID.randomUUID()
    for (trackPoint <- trackPointIterator) {
      trackService.consume(device, trackPoint)
    }
  }
}
