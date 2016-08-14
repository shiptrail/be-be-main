package services

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.stream.testkit.scaladsl.TestSink
import dao.FutureLinkedList
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

class TrackServiceTest
    extends FlatSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar {

  implicit val sys = ActorSystem()
  implicit val mat = ActorMaterializer()

  val someId = UUID.randomUUID()

  "Tracks service" should "pass-through all Points (first write than read)" in {
    val linkedListMock = mock[FutureLinkedList[(UUID, Int)]]
    val classUnderTest = new TrackServiceImpl[Int] {
      override val linkedList = linkedListMock
    }

    val givenPoints = List(1, 2, 3)

    givenPoints.foreach(classUnderTest.consume(someId, _))
    verify(linkedListMock, times(givenPoints.size)).append(any())
  }

  "Tracks service allTracks" should "never terminate" in {
    val classUnderTest = new TrackServiceImpl[Int]

    val probe = classUnderTest.allTracks.runWith(TestSink.probe[(UUID, Int)])

    probe.ensureSubscription()
    probe.request(2)
    probe.expectNoMsg()
  }

  it should "unroll all futures" in {
    val linkedListMock = new FutureLinkedList[(UUID, Int)] {
      override val head: Future[((UUID, Int), Chain)] = Future.successful(
          (someId, 1),
          Chain(Future.successful((someId, 2), Chain(Promise.apply().future))))
    }
    val classUnderTest = new TrackServiceImpl[Int] {
      override val linkedList = linkedListMock
    }

    val result = classUnderTest.allTracks.take(2).runWith(Sink.seq)

    result.futureValue should be(Seq(someId -> 1, someId -> 2))
  }
}
