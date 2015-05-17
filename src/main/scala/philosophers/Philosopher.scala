package philosophers

import akka.actor.{Actor, ActorRef, ActorSystem}

import scala.concurrent.duration._
import scala.util.Random
/**
 * nuk on 17.05.15.
 */
/*
No matter the experience, every philosopher is hungry and eats when possible.
 */
object Philosopher {
  def think(p: ActorRef)(implicit system: ActorSystem) = {
    import system.dispatcher
    system.scheduler.scheduleOnce((Random.nextInt(3) + 1) seconds, p, Thought)
  }
}

abstract class Philosopher(name: String, left: ActorRef, right: ActorRef) extends Actor {
  import context._
  def hungry:Receive

  def thinking: Receive

  override def receive: Receive = thinking
  def eating(): Receive = {
    case EatingTime =>
      println(name + " eating")
      left ! Put(self)
      right ! Put(self)
      become(thinking)
      Philosopher.think(self)
  }
}
