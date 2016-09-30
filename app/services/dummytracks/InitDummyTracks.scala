package services.dummytracks

import java.io.File
import java.util.UUID
import javax.inject.Inject

import com.google.inject.Singleton
import models.TrackPoint
import services.{MultiFormatParser, TrackService}

/** *
  * Will initialize the application with Tracks from gpx-files.
  * All gpx-files or cgps-files in the directionary .../app/services/dummytracks/ will be used.
  * Every file will get his own random UUID and every TrackPoint in each File will be associated
  * to that UUID.
  *
  * @param trackService
  */
@Singleton
class InitDummyTracks @Inject()(trackService: TrackService[TrackPoint]) {

  val listOfGPXFiles = new File("./app/services/dummytracks/").listFiles.filter(_.getName.endsWith(".gpx"))
  val listOfCGPSFiles = new File("./app/services/dummytracks/").listFiles.filter(_.getName.endsWith(".cgps"))

  val listOfFiles = listOfGPXFiles ++ listOfCGPSFiles
  new LoadTracks().loadDummyTracks(listOfFiles, trackService)

}

class LoadTracks {

  def loadDummyTracks(listOfGpxFiles: Array[File], trackService: TrackService[TrackPoint]): Unit = {
    for (file <- listOfGpxFiles) {
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
