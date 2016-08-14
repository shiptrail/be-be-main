package services

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.immutable.Seq
import scala.concurrent.Future

class TrackServiceTest extends FlatSpec with Matchers with ScalaFutures {

  implicit val sys = ActorSystem()
  implicit val mat = ActorMaterializer()

  "Tracks service" should "pass-through all Points" in {
    val classUnderTest = new TrackServiceImpl[Int]

    val givenPoints = List(1)

    classUnderTest.consume(1)

    Thread.sleep(100)
    val points: Future[Seq[Int]] = classUnderTest.allTracks.take(1).runWith(Sink.seq)

    points.futureValue should be(givenPoints)
  }

  it should "pass-through all Points (first read than write)" in {
    val classUnderTest = new TrackServiceImpl[Int]

    val givenPoints = List(1)

    val points: Future[Seq[Int]] = classUnderTest.allTracks.take(1).runWith(Sink.seq)

    Thread.sleep(100)

    classUnderTest.consume(1)

    points.futureValue should be(givenPoints)
  }

  "Tracks service allTracks" should "never terminate" in {
    val classUnderTest = new TrackServiceImpl[Int]

    val givenPoints = List(1)

    val testSink = TestSink.probe[Int]

    val probe = classUnderTest.allTracks.take(1).runWith(testSink)

    Thread.sleep(100)

//    Source(givenPoints).runWith(classUnderTest.consume)

    probe.ensureSubscription()
    //probe.expectNext(1)
    probe.expectNoMsg()
  }

  it  should "never terminate 2" in {
    val classUnderTest = new TrackServiceImpl[Int]

    val givenPoints = List(1)

    val testSink = TestSink.probe[Int]

    classUnderTest.consume(1)
    Thread.sleep(100)

    val probe = classUnderTest.allTracks.runWith(testSink)

    probe.ensureSubscription()
    //probe.expectNext(1)
    probe.expectNoMsg()
  }
}
