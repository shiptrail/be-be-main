package services

import java.util.UUID
import javax.inject.{Inject, Singleton}

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.google.inject.ImplementedBy
import dao.FutureLinkedList
import models.TrackPoint

import scala.concurrent.ExecutionContext

@ImplementedBy(classOf[TrackServiceImpl[TrackPoint]])
trait TrackService[Point] {
  def consume(device: UUID, point: Point): Unit

  def allTracks: Source[(UUID, Point), NotUsed]
}

@Singleton
class TrackServiceImpl[Point] @Inject()(
    implicit mat: Materializer, ec: ExecutionContext)
    extends TrackService[Point] {

  // Guice doesn't support generic parameters well. In tests this dependency can be manually overridden.
  val linkedList: FutureLinkedList[(UUID, Point)] =
    new FutureLinkedList[(UUID, Point)]

  override def consume(device: UUID, point: Point) = {
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
}
