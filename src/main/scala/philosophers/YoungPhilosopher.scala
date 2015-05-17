package philosophers

import akka.actor.ActorRef

import scala.concurrent.duration._
import scala.util.Random

/**
 * nuk on 17.05.15.
 */
/*
Young philosophers try to first get the fork on their left, then the one on their right.
 */
class YoungPhilosopher(left: ActorRef, right: ActorRef) extends Philosopher(left, right) {

  import context._

  override def receive: Receive = {
    case Think =>
      become(hungry)
      self ! GetFork(left)
  }

  override def hungry: Receive = {
    case GetFork(f) =>
      become(waitingFor(f))
      f ! Take(self)
    case GotForks =>
      become(eating)
      system.scheduler.scheduleOnce((Random.nextInt(5) + 1) seconds, self, EatingTime)
  }

  def waitingFor(f: ActorRef): Receive = {
    case Taken(`f`) => become(hungry)
      f match {
        case `left` => self ! GetFork(right)
        case `right` => self ! GotForks
      }
  }
}
