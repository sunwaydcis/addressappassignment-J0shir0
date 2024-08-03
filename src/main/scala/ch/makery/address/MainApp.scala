package ch.makery.address

import ch.makery.address.model.Person
import ch.makery.address.util.Database
import ch.makery.address.view.PersonEditDialogController
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import javafx.scene
import scalafx.scene.image.Image
import scalafx.stage.{Modality, Stage}

object MainApp extends JFXApp {
  // Initialize Database
  Database.setupDB()

  // transform our root layout to URI
  val rootResource = getClass.getResource("view/rootLayout.fxml")
  // Loader
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  // Load root layout from fxml file
  loader.load()
  // Retrieve the root component which is the BorderPane
  val roots = loader.getRoot[javafx.scene.layout.BorderPane]

  // Initialize the stage
  stage = new PrimaryStage {
    title = "Address App"
    icons += new Image(getClass.getResourceAsStream("/images/library_icon.png"))
    scene = new Scene {
      root = roots
      stylesheets = Seq(getClass.getResource("view/DarkTheme.css").toString)
    }
  }

/*  val personData = new ObservableBuffer[Person]()
  personData += new Person("Ernest", "Loo")
  personData += new Person("Felix", "Low")
  personData += new Person("Ryan", "Keshav") */

  val personData = new ObservableBuffer[Person]()
  personData ++= Person.getAllPersons

  // actions to show the Person Overview
  def showPersonOverview(): Unit = {
    val resources = getClass.getResource("view/personOverview.fxml")
    val loader = new FXMLLoader(resources, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[javafx.scene.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  // Call to display PersonOverview
  showPersonOverview()

  def showPersonEditDialogue(person: Person):Boolean = {
    val resource = getClass.getResourceAsStream("view/PersonEditDialog.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val roots2 = loader.getRoot[javafx.scene.Parent]
    val control = loader.getController [PersonEditDialogController#Controller]

    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      scene = new Scene {
        root = roots2
        stylesheets = Seq(getClass.getResource("view/DarkTheme.css").toString)
      }
    }
    control.dialogStage = dialog
    control.person = person
    dialog.showAndWait()
    control.okClicked
  }

  var a:Option[Int] = None
  println(a.isDefined)
  a = Some(3)
  println(a.get)

  //Implicit parameters
  def multiply(value:Int)(implicit by:Int)={
    value * by
  }
  implicit val multiplier = 2
  println(multiply(value = 10))
  println(multiply(value = 10)(by = 7))

  //Anonymous Function
  def add (a:Int, b:Int) = {a + b}
  val add1 = (a:Int, b:Int) => {a + b}
  println(add1(10, 54))
}