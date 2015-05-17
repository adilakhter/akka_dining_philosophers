package philosophers

import akka.actor.ActorRef

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
      left ! Take(self)
  }

  override def hungry: Receive = {
    case Taken(`left`) =>
      setImage(id, leftImage)
      right ! Take(self)
    case Taken(`right`) =>
      become(eating)
      setImage(id, eatingImage)
      system.scheduler.scheduleOnce(randomDelay, self, Eaten) //eating takes time
    case Unavailable(fork) =>
      //stay hungry for a while and retry
      system.scheduler.scheduleOnce(randomDelay, fork, Take(self))
  }
}
