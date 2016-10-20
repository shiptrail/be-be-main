package services

import java.util.UUID
import javax.inject.{Inject, Singleton}

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.google.inject.ImplementedBy
import dao.FutureLinkedList
import io.github.karols.units.SI.Short.ms
import io.github.karols.units._
import models.TrackPoint

import scala.collection.mutable
import scala.concurrent.ExecutionContext

@ImplementedBy(classOf[TrackServiceImpl[TrackPoint]])
trait TrackService[Point] {
  def consume(device: UUID, point: Point): Unit

  def allTracks: Source[(UUID, Point), NotUsed]

  def allDevices: Seq[UUID]

  def getTimestampOfDevice(device: UUID): IntU[ms]
}

@Singleton
class TrackServiceImpl[Point] @Inject()(
    implicit mat: Materializer, ec: ExecutionContext)
    extends TrackService[Point] {

  // Guice doesn't support generic parameters well. In tests this dependency can be manually overridden.
  val linkedList: FutureLinkedList[(UUID, Point)] =
    new FutureLinkedList[(UUID, Point)]

  val dates: mutable.Map[UUID,IntU[ms]] = mutable.Map()

  val knownUuids: mutable.HashSet[UUID] = mutable.HashSet()

  override def consume(device: UUID, point: Point) = {
    knownUuids += (device)
    if (point.isInstanceOf[TrackPoint]) {
      dates += (device -> point.asInstanceOf[TrackPoint].timestamp)
    }
    linkedList.append((device, point))
  }

  override def allTracks: Source[(UUID, Point), NotUsed] = {
    Source.unfoldAsync(linkedList.head) { p =>
      p.future.map {
        case ((device, point), next) =>
          Some((next, (device, point)))
      }
    }
  }

  override def allDevices: Seq[UUID] = {
    knownUuids.toSeq
  }

  override def getTimestampOfDevice(device: UUID): IntU[ms] = {
    if (dates.contains(device)) {
      dates(device)
    } else {
      //Default value
      0.of[ms]
    }
  }
}
