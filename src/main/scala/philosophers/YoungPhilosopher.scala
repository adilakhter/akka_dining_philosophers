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
class YoungPhilosopher(name: String, left: ActorRef, right: ActorRef) extends Philosopher(name, left, right) {

  import context._

  def thinking: Receive = {
    case Thought =>
      become(hungry)
      println(name + " became hungry")
      self ! GetFork(left)
  }

  override def hungry: Receive = {
    case GetFork(f) =>
      become(waitingFor(f))
      f ! Take(self)
    case GotForks =>
      become(eating)
      system.scheduler.scheduleOnce((Random.nextInt(3) + 1) seconds, self, EatingTime)
  }

  def waitingFor(f: ActorRef): Receive = {
    case Taken(`f`) => become(hungry)
      println(name + " got " + f)
      f match {
        case `left` => self ! GetFork(right)
        case `right` => self ! GotForks
      }
  }

}
