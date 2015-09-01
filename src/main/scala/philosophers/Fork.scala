package philosophers

import akka.actor.{Actor, ActorRef}

/**
 * @author nuk
 */
class Fork extends Actor {

  import context._

  def available: Receive = {
    case Take(p) =>
      become(taken(p))
      p ! Taken(self)
  }

  def taken(philosopher: ActorRef): Receive = {
    case Put(`philosopher`) =>
      become(available)
    case Take(p) => p ! Unavailable(self)
  }

  override def receive: Receive = available
}