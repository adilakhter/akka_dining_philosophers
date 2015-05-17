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
class YoungPhilosopher(id: Int, left: ActorRef, right: ActorRef) extends Philosopher(id, left, right) {

  import context._

  def thinking: Receive = {
    case Thought => //finished thinking
      become(hungry)
      self ! GetFork(left)
  }

  override def hungry: Receive = {
    case GetFork(f) => //trying to get a fork
      become(waitingFor(f))
      f ! Take(self)
    case GotForks => //got both forks
      become(eating)
      setImage(id, eatingImage)
      system.scheduler.scheduleOnce((Random.nextInt(delay) + 1) milliseconds, self, EatingTime) //eating takes time
  }

  def waitingFor(f: ActorRef): Receive = {
    case Taken(`f`) => //
      become(hungry)
      f match {
        case `left` =>
          setImage(id, leftImage)
          self ! GetFork(right)
        case `right` =>
          self ! GotForks
      }
    case Unavailable(`f`) =>
      become(hungry)
      system.scheduler.scheduleOnce((Random.nextInt(delay) + 1) milliseconds, self, GetFork(f))
  }
}
