import java.awt.Font
import javax.swing.ImageIcon

import akka.actor.ActorRef

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.swing.Font
import scala.util.Random

/**
 * @author nuk
 */
package object philosophers {
  val eatingImage = new ImageIcon(getClass.getClassLoader.getResource("eating.png"))
  val leftImage = new ImageIcon(getClass.getClassLoader.getResource("left.png"))
  val rightImage = new ImageIcon(getClass.getClassLoader.getResource("right.png"))
  val sadImage = new ImageIcon(getClass.getClassLoader.getResource("sad.png"))
  val textFont = new Font("Arial", Font.BOLD, 14)
  def randomDelay = (Random.nextInt(1400) + 1) milliseconds

  def updateUIStatus(id: Int, img: ImageIcon): Unit = {
    val labelID = id match {
      case 5 => 2
      case i if i < 3 => i - 1
      case _ => id
    }
    if (img == eatingImage) Dinner.philosopherCounts(id - 1).count += 1
    Dinner.philosopherLabels(labelID).icon = img
  }

  //messages
  sealed trait DiningMessage

  //could use sender() instead of these arguments, but better for pattern matching
  case class Take(philosopher:ActorRef) extends DiningMessage

  case class Put(philosopher:ActorRef) extends DiningMessage

  case class Taken(fork:ActorRef) extends DiningMessage

  case class Unavailable(fork: ActorRef) extends DiningMessage

  //waiter messages
  case class Need(id: Int) extends DiningMessage

  case class Finished(id: Int) extends DiningMessage

  case object Eaten extends DiningMessage
  case object Thought extends DiningMessage

  case object Served extends DiningMessage

  case object Wait extends DiningMessage
}
