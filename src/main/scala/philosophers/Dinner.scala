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

  val philosopherCounts = (1 to 5).map(_ => new CountLabel {
    horizontalAlignment = Alignment.Left
  })
  philosopherCounts(2).horizontalAlignment = Alignment.Right
  philosopherCounts(4).horizontalAlignment = Alignment.Right
  val clock = new ClockLabel(" ")
  var stop = () => {}
  var running = false
  override def top = new MainFrame {
    title = "Dining philosophers"
    minimumSize = new Dimension(900, 500)

    val b1 = new Button("Run young philosophers")
    val b2 = new Button("Run greedy philosophers")
    val b3 = new Button("Run waiter")
    val b4 = new Button("Stop")

    contents = new BorderPanel {
      background = new Color(85, 140, 137)
      val grid = new GridPanel(3, 5) {
        opaque = false
        val before = Seq(2, 2, 3, 1, 1)
        val countsInd = Seq(3, 6, 10, 14, 8)
        val elements = philosopherLabels.zip(before)
          .flatMap { case (l, i) => (1 to i).map(_ => new Label()) :+ l } :+ new Label()
        val zipped = philosopherCounts.zip(countsInd)
        val toAdd = zipped.foldLeft(elements) {
          case (el, (l, i)) => el.updated(i, l)
        }
        contents ++= toAdd
      }
      val buttons = new FlowPanel(b1, b2, b3, b4) {
        background = new Color(43, 43, 43)
      }
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
    if (running) stop()
    philosopherCounts.foreach(_.count = 0)
    clock.stop()
    val forks = for (i <- 1 to 5) yield system.actorOf(Props[Fork], "Fork" + i)
    val philosophers = for (i <- 1 to 5) yield system.actorOf(Props(c,
      i, forks(i - 1), forks(i % 5)))
    philosophers.foreach(Philosopher.think)
    clock.start()
    running = true
    stop = () => {
      forks.foreach(_ ! PoisonPill)
      philosophers.foreach(_ ! PoisonPill)
      philosopherLabels.foreach(_.icon = sadImage)
      running = false
    }
  }

  //runs waiter scenario
  def runWithWaiter(): Unit = {
    if (running) stop()
    philosopherCounts.foreach(_.count = 0)
    clock.stop()
    val waiter = system.actorOf(Props[Waiter], "Waiter")
    val forks = for (i <- 1 to 5) yield system.actorOf(Props[Fork], "Fork" + i)
    val philosophers = for (i <- 1 to 5) yield system.actorOf(Props(classOf[LazyPhilosopher],
      i, forks(i - 1), forks(i % 5), waiter))
    philosophers.foreach(Philosopher.think)
    clock.start()
    running = true
    stop = () => {
      waiter ! PoisonPill
      forks.foreach(_ ! PoisonPill)
      philosophers.foreach(_ ! PoisonPill)
      philosopherLabels.foreach(_.icon = sadImage)
      running = false
    }
  }

  class ClockLabel(s: String) extends Label(s) with ActionListener {
    val t = new Timer(1000, this)
    var elapsed = 0L
    border = Swing.EmptyBorder(3, 0, 3, 0)
    font = textFont
    def start() {
      elapsed = 0
      text = "Time: 0 seconds"
      t.start()
    }

    override def actionPerformed(e: ActionEvent): Unit = {
      elapsed += 1
      text = "Time: " + elapsed + " seconds"
    }

    def stop() = {
      t.stop()
      text = s
    }
  }

  class CountLabel extends Label {
    border = Swing.EmptyBorder(0, 30, 0, 30)
    font = textFont
    private var _count = 0

    def count = _count

    def count_=(i: Int): Unit = {
      _count = i
      text = "Count: " + _count
    }

  }

}
