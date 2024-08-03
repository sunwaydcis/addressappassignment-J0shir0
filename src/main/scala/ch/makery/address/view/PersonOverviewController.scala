package ch.makery.address.view

import ch.makery.address.MainApp
import ch.makery.address.model.Person
import scalafx.scene.control.{Alert, Label, TableColumn, TableView}
import scalafxml.core.macros.sfxml
import ch.makery.address.util.DateUtil.DateFormatter
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType
import scala.util.{Try, Success, Failure}

@sfxml
class PersonOverviewController
(
  private val personTable: TableView[Person],
  private val firstNameCol: TableColumn[Person, String],
  private val lastNameCol: TableColumn[Person, String],
  private val firstNameLbl: Label,
  private val lastNameLbl: Label,
  private val streetLbl: Label,
  private val cityLbl: Label,
  private val postalCodeLbl: Label,
  private val birthdayLbl: Label

){
  personTable.items = MainApp.personData
  firstNameCol.cellValueFactory = {_.value.firstName}
  lastNameCol.cellValueFactory = {_.value.lastName}

  showPersonDetails(None)

  personTable.selectionModel().selectedItem.onChange(
    (_, _, newValue) => {showPersonDetails(Some(newValue))}
  )

  def showPersonDetails(person: Option[Person]): Unit={
    person match {
      case Some(person)=>
        //Fill the label with info from the person object
        firstNameLbl.text <== person.firstName
        lastNameLbl.text <== person.lastName
        streetLbl.text <== person.street
        cityLbl.text <== person.city
        postalCodeLbl.text = person.postalCode.value.toString
        //Birthday label requires a special handler -> Implicit class converter
        birthdayLbl.text = person.date.value.asString

      case None=>
        //Person is null, remove all the text
        firstNameLbl.text = ""
        lastNameLbl.text = ""
        streetLbl.text = ""
        postalCodeLbl.text = ""
        cityLbl.text = ""
        birthdayLbl.text = ""
    }
  }
  def handleDeletePerson(action: ActionEvent): Any={
    val selectedIndex = personTable.selectionModel().getSelectedIndex
    val selectedPerson = personTable.selectionModel().getSelectedItem
    if (selectedIndex >= 0) {
      selectedPerson.delete() match {
        case Success(x) =>
          MainApp.personData remove(selectedIndex)
        case Failure (e) =>
          val alert = new Alert(Alert.AlertType.Warning) {
            initOwner(MainApp.stage)
            title = "Failure to save"
            headerText = "Database Error"
            contentText = "Database problem failed to save changes"
          }.showAndWait()
      }
    } else {
      val alert = new Alert(AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Person Selected"
        contentText = "Please select a person in the table"
      }.showAndWait()
    }
  }
  def handleNewPerson(action: ActionEvent) = {
    val person = new Person("","")
    val okClicked = MainApp.showPersonEditDialogue(person)
    if (okClicked) {
      person.save() match {
        case Success(value) =>
          MainApp.personData += person
        case Failure (e) =>
          val alert = new Alert(Alert.AlertType.Warning) {
            initOwner(MainApp.stage)
            title = "Failure to save"
            headerText = "Database Error"
            contentText = "Database problem failed to save changes"
          }.showAndWait()
      }
    }
  }

  def handleEditPerson(actionEvent: ActionEvent) = {
    val selectedPerson = personTable.selectionModel().getSelectedItem
    if (selectedPerson != null) {
      val okClicked = MainApp.showPersonEditDialogue(selectedPerson)

      if (okClicked) {
        selectedPerson.save() match {
          case Success(x) =>
            showPersonDetails(Some(selectedPerson))
          case Failure(e) =>
            val alert = new Alert(Alert.AlertType.Warning) {
              title = "Failure to save"
              headerText = "Database Error"
              contentText = "Database problem failed to save changes"
            }.showAndWait()
        }
      }
    } else {
      val alert = new Alert(Alert.AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No person selected"
        contentText = "Please select a person in the table"
      }.showAndWait()
    }
  }
}