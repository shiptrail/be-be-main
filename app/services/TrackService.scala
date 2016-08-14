package services

import javax.inject.{Inject, Singleton}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Keep, Sink, Source}
import com.google.inject.ImplementedBy
import models.TrackPoint
import org.reactivestreams.Publisher

@ImplementedBy(classOf[TrackServiceImpl[TrackPoint]])
trait TrackService[Point] {
  def consume(point: Point): Unit

  def allTracks: Source[Point, NotUsed]
}

@Singleton
class TrackServiceImpl[Point] @Inject()(
    implicit mat: Materializer, sys: ActorSystem)
    extends TrackService[Point] {

  val (storeOut, storeIn) = {

    val sink = Source.actorRef[Point](10000, OverflowStrategy.dropHead)
    val source: Sink[Point, Publisher[Point]] =
      Sink.asPublisher[Point](fanout = false)

    sink.toMat(source)(Keep.both).run()
  }

  override def consume(point: Point) = {
    storeOut ! point
  }

  override def allTracks: Source[Point, NotUsed] = {
    Source.fromPublisher(storeIn)
  }

  // allTracks.runWith(Sink.ignore)
}
