package dao

import java.util.concurrent.atomic.AtomicReference

import scala.concurrent.{Future, Promise}

class FutureLinkedList[E] {

  case class Link(future: Future[(E, Link)])
  private val last: AtomicReference[Promise[(E, Link)]] = new AtomicReference(
      Promise[(E, Link)]())

  val head: Link = Link(last.get().future)

  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  def append(p: E): Unit = {
    val newLast = Promise[(E, Link)]

    val oldLast = last.getAndSet(newLast)

    oldLast.success((p, Link(newLast.future)))
  }
}
