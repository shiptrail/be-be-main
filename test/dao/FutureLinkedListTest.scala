package dao

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

class FutureLinkedListTest extends FlatSpec with Matchers with ScalaFutures{

  "append" should "append points" in {
    val classUnderTest = new FutureLinkedList[Int]
    import classUnderTest.Chain

    val givenPoints = Set(1, 2)

    givenPoints.foreach(classUnderTest.append)

    val (x1, Chain(tail)) = classUnderTest.head.futureValue
    val (x2, Chain(_)) = tail.futureValue


    Set(x1, x2) should be(givenPoints)
  }

  "head" should "be initially empty" in {
    val classUnderTest = new FutureLinkedList[Int]

    classUnderTest.head should not be 'completed
  }

  it should "reproduce the same output over and over again" in {
    val classUnderTest = new FutureLinkedList[Int]

    val givenPoints = List(1, 2, 3)

    givenPoints.foreach(classUnderTest.append)

    classUnderTest.head.futureValue should matchPattern { case (1, _) => () }

    classUnderTest.head.futureValue should matchPattern { case (1, _) => () }
  }

}
