package philosophers

import akka.actor.{Actor, ActorRef}

/**
 * nuk on 17.05.15.
 */

abstract class Philosopher(left:ActorRef, right: ActorRef) extends Actor {
  import context._
  def hungry:Receive
  def eat():Receive = {
    case EatingTime =>
      left ! Put(self)
      right ! Put(self)
      become(hungry)
  }
}
