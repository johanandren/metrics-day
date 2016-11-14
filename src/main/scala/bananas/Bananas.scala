/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package bananas

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.concurrent.duration._
import scala.io.StdIn

// tick every 500ms passes that on to next, so total processed 2msg/s (grafana says 4)
// sent: 4msg/s (grafana says 8)
class BananaA(next: ActorRef) extends Actor {
  object Tick
  import context.dispatcher
  context.system.scheduler.schedule(500.millis, 500.millis, self, Tick)

  def receive = {
    case Tick =>
      next ! "msg"
  }
}

// gets 2msg/s, (grafana says 4)
// and passes every second on,
// so sent 1msg/s (grafana says 2)
class BananaB(next: ActorRef) extends Actor {
  var count = 0
  var toggle = true
  def receive = {
    case something if toggle =>
      count += 1
      println("b: " + count)
      toggle = false
      next ! count

    case _ if !toggle =>
      count += 1
      println("b: ignoring " + count)
      toggle = true
  }
}

// gets 1msg/s (grafana says 2)
class BananaC extends Actor {
  var counter = 0
  def receive = {
    case m =>
      counter += 1
      println("c:" + counter + " " + m)
  }
}

object Bananas extends App {
  implicit val system = ActorSystem()

  // one of each, grafana says 2 of each
  val bananC = system.actorOf(Props(new BananaC))
  val bananB = system.actorOf(Props(new BananaB(bananC)))
  val bananA = system.actorOf(Props(new BananaA(bananB)))


  println("Enter to terminate")
  StdIn.readLine()
  system.terminate()

}
