package services

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
  def consume(point: Point): Unit

  def allTracks: Source[Point, NotUsed]
}

@Singleton
class TrackServiceImpl[Point] @Inject()(
    implicit mat: Materializer, ec: ExecutionContext)
    extends TrackService[Point] {

  case class Chain(future: Future[(Point, Chain)])

  val last: AtomicReference[Promise[(Point, Chain)]] = new AtomicReference(
      Promise[(Point, Chain)]())
  val head: Future[(Point, Chain)] = last.get().future

  override def consume(point: Point) = {
    val newLast = Promise[(Point, Chain)]

    val oldLast = last.getAndSet(newLast)

    oldLast.success(point -> Chain(newLast.future))
  }

  override def allTracks: Source[Point, NotUsed] = {
    Source.unfoldAsync(head) { p =>
      p.map {
        case (point, next: Chain) =>
          Some((next.future, point))
      }
    }
  }
}

//TODO: Diesen Mock für den hollow mode verwenden
// siehe: https://belize.imp.fu-berlin.de/confluence/display/BE/Backend+Architektur#BackendArchitektur-ModularerAufbau
@Singleton
class TrackServiceMock extends TrackService[TrackPoint] {
  override def consume(point: TrackPoint): Unit = {}

  override def allTracks: Source[TrackPoint, NotUsed] = Source.empty
}
