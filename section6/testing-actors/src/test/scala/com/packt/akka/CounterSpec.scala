package com.packt.akka

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.packt.akka.actor.Counter
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}

import scala.concurrent.duration._

class CounterSpec extends TestKit(ActorSystem("test-system"))
                  with FlatSpecLike
                  with ImplicitSender
                  with BeforeAndAfterAll
                  with MustMatchers {

  override def afterAll = TestKit.shutdownActorSystem(system)

  "Counter Actor" should "handle GetCount message with using TestProbe" in {

    val sender = TestProbe()
    val counter = system.actorOf(Props[Counter])

    sender.send(counter, Counter.Increment)
    sender.send(counter, Counter.GetCount)

    val state = sender.expectMsgType[Int]

    state mustBe 1
  }

  it should "handle Increment message" in {
    val counter = system.actorOf(Counter.props)
    counter ! Counter.Increment
    expectNoMessage(1 second)
  }

  it should "handle Decrement message" in {
    val counter = system.actorOf(Counter.props)
    counter ! Counter.Decrement
    expectNoMessage(1 seconds)
  }

  it should "handle GetCount message" in {
    val counter = system.actorOf(Counter.props)
    counter ! Counter.GetCount
    expectMsg(0)
  }

  it should "handle sequence of messages" in {

    val counter = system.actorOf(Counter.props)

    counter ! Counter.Increment
    counter ! Counter.Increment
    counter ! Counter.Decrement
    counter ! Counter.Increment

    counter ! Counter.GetCount

    expectMsg(2)
  }
}