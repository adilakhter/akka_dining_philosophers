package philosophers

import java.awt.Dimension
import javax.swing.UIManager

import akka.actor.{ActorSystem, Props}

import scala.swing.BorderPanel.Position._
import scala.swing._
import scala.swing.event.ButtonClicked

/**
 * nuk on 17.05.15.
 */
object Dinner extends SimpleSwingApplication {
  {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
    } catch {
      case t: Throwable =>
    }
  }
  implicit val system = ActorSystem("dinner")
  val philosopherLabels = (1 to 5).map(_ => new Label {
    icon = sadImage
  })

  override def top = new MainFrame {
    title = "Dining philosophers"
    minimumSize = new Dimension(900, 600)

    val b1 = new Button("Run young philosophers")
    val b2 = new Button("Run greedy philosophers")
    val b3 = new Button("Run waiter")


    contents = new BorderPanel {
      val grid = new GridPanel(3, 5) {
        val before = Seq(2, 2, 3, 1, 1)
        val elements = philosopherLabels.zip(before)
          .flatMap { case (l, i) => (1 to i).map(_ => new Label()) :+ l } :+ new Label()
        contents ++= elements
      }
      val buttons = new FlowPanel(b1, b2, b3)
      layout(grid) = Center
      layout(buttons) = South
      layout(new Label("  ")) = North
      layout(new Label("  ")) = East
      layout(new Label("  ")) = West
    }
    listenTo(b1, b2, b3)

    reactions += {
      case ButtonClicked(`b1`) => run(classOf[YoungPhilosopher])
    }

  }

  def run(c: Class[_]): Unit = {
    val forks = for (i <- 1 to 5) yield system.actorOf(Props[Fork], "Fork" + i)
    val philosophers = for (i <- 1 to 5) yield system.actorOf(Props(c,
      i, forks(i - 1), forks(i % 5)))
    philosophers.foreach(Philosopher.think)
  }
}
