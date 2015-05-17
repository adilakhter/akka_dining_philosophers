package philosophers

import akka.actor.Actor

/**
 * nuk on 17.05.15.
 */
class Waiter extends Actor {
  private val forks: Array[Boolean] = (1 to 5).map(_ => true).toArray
  private var eating: Int = 0

  override def receive: Receive = {
    case Need(id) if forks(id - 1) && forks(id % 5) && eating < 2 =>
      forks(id - 1) = false
      forks(id % 5) = false
      eating += 1
      sender() ! Served
    case Finished(id) =>
      eating -= 1
      forks(id - 1) = true
      forks(id % 5) = true
    case _ => sender() ! Wait
  }
}
