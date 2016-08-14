package dao

import java.util.concurrent.atomic.AtomicReference

import scala.concurrent.{Future, Promise}

class FutureLinkedList[E] {

  // case class Chain(future: Future[(E, Chain)])
  private val last: AtomicReference[Promise[(E, Chain)]] = new AtomicReference(
      Promise[(E, Chain)]())

  val head: Future[(E, Chain)] = last.get().future

  def append(p: E): Unit = {
    val newLast = Promise[(E, Chain)]

    val oldLast = last.getAndSet(newLast)

    oldLast.success((p, Chain(newLast.future)))
  }

  case class Chain(future: Future[(E, Chain)])
}
