import javax.swing.ImageIcon

import akka.actor.ActorRef


/**
 * nuk on 17.05.15.
 */
package object philosophers {
  val eatingImage = new ImageIcon(getClass.getClassLoader.getResource("eating.png"))
  val leftImage = new ImageIcon(getClass.getClassLoader.getResource("left.png"))
  val rightImage = new ImageIcon(getClass.getClassLoader.getResource("right.png"))
  val sadImage = new ImageIcon(getClass.getClassLoader.getResource("sad.png"))
  val delay = 1400

  def setImage(id: Int, img: ImageIcon): Unit = {
    val labelID = id match {
      case 5 => 2
      case i if i < 3 => i - 1
      case _ => id
    }
    Dinner.philosopherLabels(labelID).icon = img
  }

  sealed trait DiningMessage

  //could use sender() instead of these arguments, but better for pattern matching
  case class Take(philosopher:ActorRef) extends DiningMessage

  case class Put(philosopher:ActorRef) extends DiningMessage

  case class Taken(fork:ActorRef) extends DiningMessage

  case class Unavailable(fork: ActorRef)

  case class GetFork(fork: ActorRef)
  case object EatingTime extends DiningMessage

  case object Thought extends DiningMessage
  case object GotForks
}
