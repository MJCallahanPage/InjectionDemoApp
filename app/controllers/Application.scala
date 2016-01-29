package controllers

import play.api.mvc._
import com.mongodb.casbah.Imports._
import play.twirl.api.Html
import com.mongodb.DBObject
import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer

object injectionController extends Controller {

  def index = Action {
    Ok(views.html.login())
  }

  def login = Action(parse.urlFormEncoded) {
    implicit request =>

      val username = request.body.get("username").get.head
      val password = request.body.get("password").get.head
      var arrBad   = ArrayBuffer[String]()
      var arrGood  = ArrayBuffer[String]()

      // Connect to Mongo DB
      val client   = MongoClient()
      val db       = client("mp")
      val coll     = db("users")

      // Build Query Object - One bad with concatenation - One good with API
      val badLogin  = MongoDBObject("{ username: '" + username + "', password: '" + password + "' }")
      val goodLogin = MongoDBObject("username" -> username, "password" -> password)

      // Find all documents and move to MongoDBObject
      for (user <- coll.find(badLogin)){
        arrBad += user.toString()
      }

      for (user <- coll.find(goodLogin)){
        arrGood += user.toString()
      }

      // Cleanly Close MongoDB Connection
      client.close()

      // Render the Results
      Ok(views.html.loginResult(goodLogin.toString())(badLogin.toString())(arrGood)(arrBad))
  }
}