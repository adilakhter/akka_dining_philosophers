package philosophers

import akka.actor.{ActorSystem, Props}

/**
 * nuk on 17.05.15.
 */
object Dinner extends App {
  implicit val system = ActorSystem("dinner")
  val c = classOf[YoungPhilosopher]

  def run(c: Class[_]): Unit = {
    val forks = for (i <- 1 to 5) yield system.actorOf(Props[Fork], "Fork" + i)
    val philosophers = for (i <- 1 to 5) yield system.actorOf(Props(c,
      i.toString, forks(i - 1), forks(i % 5)))
    philosophers.foreach(Philosopher.think)
  }

  run(c)
}
