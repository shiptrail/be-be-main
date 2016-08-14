package services

import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import javax.inject.{Inject, Singleton}

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.google.inject.ImplementedBy
import models.TrackPoint

import scala.concurrent.{ExecutionContext, Future, Promise}

/**
  * Warning: ask for only for TrackService[TrackPoint] with Guice. Guice isn't able to
  * distinguish between multiple Version of this class, because of the type erasure.
  */
@ImplementedBy(classOf[TrackServiceImpl[TrackPoint]])
trait TrackService[Point] {
  def consume(device: UUID, point: Point): Unit

  def allTracks: Source[(UUID, Point), NotUsed]
}

@Singleton
class TrackServiceImpl[Point] @Inject()(
    implicit mat: Materializer, ec: ExecutionContext)
    extends TrackService[Point] {

  case class Chain(future: Future[(UUID, Point, Chain)])

  val last: AtomicReference[Promise[(UUID, Point, Chain)]] =
    new AtomicReference(Promise[(UUID, Point, Chain)]())
  val head: Future[(UUID, Point, Chain)] = last.get().future

  override def consume(device: UUID, point: Point) = {
    val newLast = Promise[(UUID, Point, Chain)]

    val oldLast = last.getAndSet(newLast)

    oldLast.success((device, point, Chain(newLast.future)))
  }

  override def allTracks: Source[(UUID, Point), NotUsed] = {
    Source.unfoldAsync(head) { p =>
      p.map {
        case (device, point, next: Chain) =>
          Some((next.future, (device, point)))
      }
    }
  }
}

//TODO: Diesen Mock für den hollow mode verwenden
// siehe: https://belize.imp.fu-berlin.de/confluence/display/BE/Backend+Architektur#BackendArchitektur-ModularerAufbau
@Singleton
class TrackServiceMock extends TrackService[TrackPoint] {
  override def consume(device: UUID, point: TrackPoint): Unit = {}

  override def allTracks: Source[TrackPoint, NotUsed] = Source.empty
}
