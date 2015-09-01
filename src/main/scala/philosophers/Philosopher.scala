package philosophers

import akka.actor.{Actor, ActorRef, ActorSystem}

/**
 * No matter the experience, every philosopher is hungry and eats when possible.
 * @author nuk
 */
object Philosopher {
  def think(p: ActorRef)(implicit system: ActorSystem) = {
    import system.dispatcher
    system.scheduler.scheduleOnce(randomDelay, p, Thought)
  }
}

abstract class Philosopher(id: Int, left: ActorRef, right: ActorRef) extends Actor {
  import context._
  def hungry:Receive

  def thinking: Receive

  override def receive: Receive = thinking

  def eating: Receive = {
    case Eaten =>
      left ! Put(self)
      right ! Put(self)
      updateUIStatus(id, sadImage)
      become(thinking)
      Philosopher.think(self)
  }
}
