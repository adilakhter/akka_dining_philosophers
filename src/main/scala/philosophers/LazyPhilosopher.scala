package philosophers

import akka.actor.ActorRef

/**
 * Lazy philosophers wait for the dish to be served by the waiter.
 * @author nuk
 */
class LazyPhilosopher(id: Int, left: ActorRef, right: ActorRef, waiter: ActorRef) extends Philosopher(id, left, right) {

  import context._

  override def hungry: Receive = {
    case Wait =>
      become(thinking)
      Philosopher.think(self)
    case Served =>
      left ! Take(self)
      right ! Take(self)
      //no need to care about the responses from forks
      become(eating)
      updateUIStatus(id, eatingImage)
      system.scheduler.scheduleOnce(randomDelay, self, Eaten) //eating takes some time
  }

  override def thinking: Receive = {
    case Thought =>
      become(hungry)
      waiter ! Need(id)
  }

  override def eating: Receive = {
    case Eaten =>
      left ! Put(self)
      right ! Put(self)
      waiter ! Finished(id)
      updateUIStatus(id, sadImage)
      become(thinking)
      Philosopher.think(self)
  }
}
