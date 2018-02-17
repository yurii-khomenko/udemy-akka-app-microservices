package com.packt.akka.cluster.actor

import akka.actor.{Actor, ActorSystem, Props, RootActorPath}
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster._
import com.packt.akka.commons._
import com.typesafe.config.ConfigFactory

object Backend {
  def initiate(port: Int) = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.load.getConfig("Backend"))

    val system = ActorSystem("ClusterSystem", config)
    val backend = system.actorOf(Props[Backend], name = "backend")
  }
}

class Backend extends Actor {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, MemberUp
  // re-subscribe when restart
  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit =
    cluster.unsubscribe(self)

  def receive = {
    case Add(num1, num2) =>
      println(s"I'm a backend with path: $self and I received add operation.")
    case MemberUp(member) =>
      if (member.hasRole("frontend")) {
        val frontendActor = context.actorSelection(RootActorPath(member.address) / "user" / "frontend")
        frontendActor ! BackendRegistration
      }
  }
}