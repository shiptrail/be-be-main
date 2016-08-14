package services

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TrackServiceTest extends FlatSpec with Matchers with ScalaFutures {

  implicit val sys = ActorSystem()
  implicit val mat = ActorMaterializer()

  "Tracks service" should "pass-through all Points (first write than read)" in {
    val classUnderTest = new TrackServiceImpl[Int]

    val givenPoints = List(1, 2, 3)

    givenPoints.foreach(classUnderTest.consume)

    Thread.sleep(100)
    val points: Future[Seq[Int]] =
      classUnderTest.allTracks.take(3).runWith(Sink.seq)

    points.futureValue should be(givenPoints)
  }

  it should "pass-through all Points (first read than write)" in {
    val classUnderTest = new TrackServiceImpl[Int]

    val givenPoints = List(1, 2, 3)

    val points: Future[Seq[Int]] =
      classUnderTest.allTracks.take(3).runWith(Sink.seq)

    Thread.sleep(100)

    givenPoints.foreach(classUnderTest.consume)

    points.futureValue should be(givenPoints)
  }

  "Tracks service allTracks" should "never terminate" in {
    val classUnderTest = new TrackServiceImpl[Int]

    val probe = classUnderTest.allTracks.runWith(TestSink.probe[Int])

    probe.ensureSubscription()
    probe.request(2)
    probe.expectNoMsg()
  }

  it should "never terminate 2" in {
    val classUnderTest = new TrackServiceImpl[Int]

    classUnderTest.consume(1)

    val probe = classUnderTest.allTracks.runWith(TestSink.probe[Int])

    probe.ensureSubscription()
    probe.request(2)
    probe.expectNext(1)
    probe.expectNoMsg()
  }

  it should "reproduce the input for all frontend clients in the same order" in {
    val classUnderTest = new TrackServiceImpl[Int]

    val givenPoints = List(1, 2, 3)

    givenPoints.foreach(classUnderTest.consume)

    val points: Future[Seq[Int]] =
      classUnderTest.allTracks
        .take(3)
        .runWith(Sink.seq)

    points.futureValue should be(givenPoints)

    val points2: Future[Seq[Int]] =
      classUnderTest.allTracks
        .take(3)
        .runWith(Sink.seq)

    points2.futureValue should be(givenPoints)
  }
}
