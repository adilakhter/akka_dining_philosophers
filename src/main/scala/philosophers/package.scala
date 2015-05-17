import javax.swing.ImageIcon

import akka.actor.ActorRef

import scala.concurrent.duration._
import scala.util.Random

/**
 * nuk on 17.05.15.
 */
package object philosophers {
  val eatingImage = new ImageIcon(getClass.getClassLoader.getResource("eating.png"))
  val leftImage = new ImageIcon(getClass.getClassLoader.getResource("left.png"))
  val rightImage = new ImageIcon(getClass.getClassLoader.getResource("right.png"))
  val sadImage = new ImageIcon(getClass.getClassLoader.getResource("sad.png"))

  def randomDelay = (Random.nextInt(1400) + 1) milliseconds

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

  case object Eaten extends DiningMessage

  case object Thought extends DiningMessage
}
