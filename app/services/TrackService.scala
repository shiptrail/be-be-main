package services

import java.util.concurrent.atomic.AtomicReference
import javax.inject.{Inject, Singleton}

import akka.NotUsed
import akka.actor.ActorSystem
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
    implicit mat: Materializer, sys: ActorSystem, ec: ExecutionContext)
    extends TrackService[Point] {

  type Chain = Future[(Point, Future[_])]

  val last: AtomicReference[Promise[(Point, Future[_])]] = new AtomicReference(
      Promise[(Point, Future[_])]())
  val head: Chain = last.get().future

  override def consume(point: Point) = {
    val newLast = Promise[(Point, Future[_])]

    val oldLast = last.getAndSet(newLast)

    oldLast.success((point, newLast.future))
  }

  override def allTracks: Source[Point, NotUsed] = {
    Source.unfoldAsync(head) { p: Chain =>
      p.map {
        case (point, next: Chain) =>
          Some((next, point))
      }
    }
  }
}
