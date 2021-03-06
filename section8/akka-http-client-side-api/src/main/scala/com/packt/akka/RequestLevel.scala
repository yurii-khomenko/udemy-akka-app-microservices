package com.packt.akka

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration._

object RequestLevel extends App {
  import JsonProtocol._

  implicit val sys = ActorSystem("Request-Level")
  implicit val mat = ActorMaterializer()
  implicit val dis = sys.dispatcher

  val response = Http().singleRequest(HttpRequest(uri = "https://api.ipify.org?format=json"))

  response map { res =>
    res.status match {
      case OK =>
        Unmarshal(res.entity).to[IpInfo].map { info =>
          println(s"The information for my ip is: $info")
          shutdown()
        }
      case _ =>
        Unmarshal(res.entity).to[String].map { body =>
          println(s"The response status is ${res.status} and response body is $body")
          shutdown()
        }
    }
  }

  def shutdown() =
    Http().shutdownAllConnectionPools().onComplete { _ =>
      sys.terminate()
      Await.ready(sys.whenTerminated, 10 seconds)
    }
}