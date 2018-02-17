package com.packt.akka.loadBalancing

import akka.actor.{Actor, ActorSystem, Props}
import com.packt.akka.commons._
import com.typesafe.config.ConfigFactory

object Backend {
  def initiate(port: Int){
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
      withFallback(ConfigFactory.load("loadbalancer"))

    val system = ActorSystem("ClusterSystem", config)

    val backend = system.actorOf(Props[Backend], name = "backend")
  }
}

class Backend extends Actor {
  def receive = {
    case op@Add(num1, num2) =>
      val sum = num1 + num2
      println(s"I'm a backend with path: $self and I received add operation. $op => $sum")
  }
}