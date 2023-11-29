package com.example

import akka.actor.ActorSystem
import com.google.protobuf.empty.Empty
import kalix.scalasdk.testkit.KalixTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.Millis
import org.scalatest.time.Seconds
import org.scalatest.time.Span
import org.scalatest.wordspec.AnyWordSpec
import spray.json.{JsValue, enrichAny}

import java.util.Base64

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class CounterServiceIntegrationSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit private val patience: PatienceConfig =
    PatienceConfig(Span(5, Seconds), Span(500, Millis))

  private val testKit = KalixTestKit(Main.createKalix()).start()

  // using this client allows for passing headers
  private val counterClient = testKit.getGrpcClient(classOf[CounterService])
    .asInstanceOf[CounterServiceClient]

  "CounterService" must {

    "accept requests with a valid bearer token passed as metadata" in {
      val token = bearerTokenWith(Map("iss" -> "my-issuer", "ten" -> "ten1", "roles" -> "role1"))
      val changeCounterCmd = ChangeCounterCmd(counterId = "test", value = 10)
      val response = counterClient.increase()
        .addHeader("Authorization", "Bearer " + token) // <3>
        .invoke(changeCounterCmd)

      response.futureValue shouldBe Empty()

      val responseGet = counterClient.getCurrentCounter()
        .addHeader("Authorization", "Bearer " + token) // <3>
        .invoke(GetCounter(counterId = "test"))

      responseGet.futureValue.value shouldBe 10
    }

  }

  private def bearerTokenWith(claims: Map[String, String]): String = {
    // setting algorithm to none
    val alg = Base64.getEncoder.encodeToString("""{"alg":"none"}""".getBytes); // <4>

    import spray.json.DefaultJsonProtocol._
    val claimsJson: JsValue = claims.toJson

    // no validation is done for integration tests, thus no valid signature required
    s"$alg.${Base64.getEncoder.encodeToString(claimsJson.toString().getBytes)}" // <5>
  }

  override def afterAll(): Unit = {
    testKit.stop()
    super.afterAll()
  }
}
