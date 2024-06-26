package com.example

import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class TestServiceAction(creationContext: ActionCreationContext) extends AbstractTestServiceAction {

  override def testNested(requestWithJwt: RequestWithJwt): Action.Effect[GetValue] = {
    effects.reply(GetValue(requestWithJwt.toString))
  }
}

