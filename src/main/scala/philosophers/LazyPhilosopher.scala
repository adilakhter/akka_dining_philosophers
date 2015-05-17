package philosophers

import akka.actor.ActorRef

/**
 * nuk on 17.05.15.
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
      setImage(id, eatingImage)
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
      setImage(id, sadImage)
      become(thinking)
      Philosopher.think(self)
  }
}
