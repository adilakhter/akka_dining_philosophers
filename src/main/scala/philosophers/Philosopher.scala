package philosophers

import akka.actor.{Actor, ActorRef}

/**
 * nuk on 17.05.15.
 */
/*
No matter the experience, every philosopher is hungry and eats when possible.
 */
abstract class Philosopher(left:ActorRef, right: ActorRef) extends Actor {
  import context._
  def hungry:Receive

  def eating(): Receive = {
    case EatingTime =>
      left ! Put(self)
      right ! Put(self)
      become(hungry)
  }
}
