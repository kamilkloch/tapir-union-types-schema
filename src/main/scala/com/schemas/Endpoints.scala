package com.schemas

import cats.effect.IO
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.swagger.bundle.SwaggerInterpreter

sealed trait Fruit

object Fruit {
  case class Apple(color: String) extends Fruit

  case class Potato(weight: Double) extends Fruit

  private implicit val config: Configuration = Configuration.default
  implicit val fruitCodec: io.circe.Codec[Fruit] = deriveConfiguredCodec
}

object Endpoints {
  case class User(name: String) extends AnyVal

  val postFruit: Endpoint[Unit, Fruit, Unit, Unit, Any] = endpoint.post
    .in("fruit")
    .in(jsonBody[Fruit])

  val fruitServerEndpoint = postFruit.serverLogicSuccess[IO](_ => IO.unit)

  val apiEndpoints: List[ServerEndpoint[Any, IO]] = List(fruitServerEndpoint)

  val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
    .fromServerEndpoints[IO](apiEndpoints, "tapir-union-types-schema", "1.0.0")

  val all: List[ServerEndpoint[Any, IO]] = apiEndpoints ++ docEndpoints
}
