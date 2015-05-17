package philosophers

import akka.actor.{Actor, ActorRef}

/**
 * nuk on 17.05.15.
 */
/*

 */
class Fork extends Actor {

  import context._

  def available: Receive = {
    case Take(philosopher: ActorRef) =>
      become(taken(philosopher))
  }

  def taken(philosopher: ActorRef): Receive = {
    case Put(`philosopher`) =>
      become(available)
  }

  override def receive: Receive = available
}