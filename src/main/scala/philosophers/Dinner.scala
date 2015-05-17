package philosophers

import java.awt.Dimension
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{Timer, UIManager}

import akka.actor.{ActorSystem, PoisonPill, Props}

import scala.swing.BorderPanel.Position._
import scala.swing._
import scala.swing.event.ButtonClicked

/**
 * nuk on 17.05.15.
 */
/*
GUI running philosopher algorithms
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
  val clock = new ClockLabel(" ")
  var stop = () => {}

  override def top = new MainFrame {
    title = "Dining philosophers"
    minimumSize = new Dimension(900, 600)

    val b1 = new Button("Run young philosophers")
    val b2 = new Button("Run greedy philosophers")
    val b3 = new Button("Run waiter")
    val b4 = new Button("Stop")

    contents = new BorderPanel {
      val grid = new GridPanel(3, 5) {
        val before = Seq(2, 2, 3, 1, 1)
        val elements = philosopherLabels.zip(before)
          .flatMap { case (l, i) => (1 to i).map(_ => new Label()) :+ l } :+ new Label()
        contents ++= elements
      }
      val buttons = new FlowPanel(b1, b2, b3, b4)
      layout(grid) = Center
      layout(buttons) = South
      layout(clock) = North
      layout(new Label("  ")) = East
      layout(new Label("  ")) = West
    }
    listenTo(b1, b2, b3, b4)

    reactions += {
      case ButtonClicked(`b1`) => run(classOf[YoungPhilosopher])
      case ButtonClicked(`b2`) => run(classOf[GreedyPhilosopher])
      case ButtonClicked(`b3`) => runWithWaiter()
      case ButtonClicked(`b4`) => stop()
    }

  }

  //runs the scenario given a class of philosophers
  def run(c: Class[_]): Unit = {
    val forks = for (i <- 1 to 5) yield system.actorOf(Props[Fork], "Fork" + i)
    val philosophers = for (i <- 1 to 5) yield system.actorOf(Props(c,
      i, forks(i - 1), forks(i % 5)))
    philosophers.foreach(Philosopher.think)
    clock.start()
    stop = () => {
      forks.foreach(_ ! PoisonPill)
      philosophers.foreach(_ ! PoisonPill)
      philosopherLabels.foreach(_.icon = sadImage)
      clock.stop()
    }
  }

  //runs waiter scenario
  def runWithWaiter(): Unit = {
    val waiter = system.actorOf(Props[Waiter], "Waiter")
    val forks = for (i <- 1 to 5) yield system.actorOf(Props[Fork], "Fork" + i)
    val philosophers = for (i <- 1 to 5) yield system.actorOf(Props(classOf[LazyPhilosopher],
      i, forks(i - 1), forks(i % 5), waiter))
    philosophers.foreach(Philosopher.think)
    clock.start()
    stop = () => {
      waiter ! PoisonPill
      forks.foreach(_ ! PoisonPill)
      philosophers.foreach(_ ! PoisonPill)
      philosopherLabels.foreach(_.icon = sadImage)
      clock.stop()
    }
  }

  class ClockLabel(s: String) extends Label(s) with ActionListener {
    val t = new Timer(1000, this)
    var elapsed = 0L

    def start() {
      elapsed = 0
      text = "0"
      t.start()
    }

    override def actionPerformed(e: ActionEvent): Unit = {
      elapsed += 1
      text = elapsed.toString
    }

    def stop() = {
      t.stop()
      text = s
    }
  }

}
