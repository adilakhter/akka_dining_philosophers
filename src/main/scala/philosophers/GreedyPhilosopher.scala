package philosophers

import akka.actor.ActorRef

/**
 * nuk on 17.05.15.
 */
/*
Greedy philosophers always try to take both forks,
although they're smart enough to put one down in case they can't do that.
 */
class GreedyPhilosopher(id: Int, left: ActorRef, right: ActorRef) extends Philosopher(id, left, right) {

  import context._

  override def thinking: Receive = {
    case Thought =>
      become(hungry)
      left ! Take(self)
  }

  override def hungry: Receive = {
    //first successful
    case Taken(`left`) =>
      updateUIStatus(id, leftImage)
      right ! Take(self)
    case Taken(`right`) =>
      become(eating)
      updateUIStatus(id, eatingImage)
      system.scheduler.scheduleOnce(randomDelay, self, Eaten) //eating takes some time
    //first unsuccessful
    case Unavailable(`right`) =>
      left ! Put(self)
      updateUIStatus(id, sadImage)
      system.scheduler.scheduleOnce(randomDelay) {
        left ! Take(self)
      }
    case Unavailable(`left`) =>
      system.scheduler.scheduleOnce(randomDelay) {
        left ! Take(self)
      }
  }
}
