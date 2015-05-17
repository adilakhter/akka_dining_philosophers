import akka.actor.ActorRef

/**
 * nuk on 17.05.15.
 */
package object philosophers {
  sealed trait DiningMessage
  //could use sender() instead of these arguments, but better for pattern matching
  case class Take(philosopher:ActorRef) extends DiningMessage
  case class Put(philosopher:ActorRef) extends DiningMessage
  case class Taken(fork:ActorRef) extends DiningMessage
  case object EatingTime extends DiningMessage
}
