package ch.makery.address.view

import ch.makery.address.model.Person
import ch.makery.address.util.DateUtil._
import scalafx.event.ActionEvent
import scalafx.scene.control.{Alert, TextField}
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class PersonEditDialogController
(
  private val firstNameField: TextField,
  private val lastNameField: TextField,
  private val streetField: TextField,
  private val postalCodeField: TextField,
  private val cityField: TextField,
  private val birthdayField: TextField
) {
  var dialogStage: Stage = null
  private var _person: Person = null
  var okClicked = false

  def person = _person // Getter to return a person object

  def person_=(x: Person) { // Setter to set a new person object
    _person = x
    firstNameField.text = _person.firstName.value
    lastNameField.text = _person.lastName.value
    streetField.text = _person.street.value
    postalCodeField.text = _person.postalCode.value.toString
    cityField.text = _person.city.value
    birthdayField.text = _person.date.value.asString
    birthdayField.setPromptText("dd.mm.yyyy")
  }

  def nullChecking(x: String) = x == null || x.length == 0 //Checks if user input is valid or not

  def isInputValid(): Boolean = {
    var errorMessage = ""

    if (nullChecking(firstNameField.text.value))
      errorMessage += "No valid first name!\n"
    if (nullChecking(lastNameField.text.value))
      errorMessage += "No valid last name!\n"
    if (nullChecking(streetField.text.value))
      errorMessage += "No valid street!\n"
    if (nullChecking(postalCodeField.text.value))
      errorMessage += "No valid postal code!\n"
    else {
      try {
        Integer.parseInt(postalCodeField.getText())
      } catch {
        case e: NumberFormatException => errorMessage += "No valid postal code (Must be an integer)\n"
      }
    }
    if (nullChecking(cityField.text.value))
      errorMessage += "No valid city!\n"
    if (nullChecking(birthdayField.text.value))
      errorMessage += "No valid birthday!\n"
    else {
      if (!birthdayField.text.value.isValid) {
        errorMessage += "No valid birthday! Use the format dd.mm.yyyy\n"
      }
    }
    if (errorMessage.length() == 0) {
      return true
    } else {
      val alert = new Alert(Alert.AlertType.Error) {
        initOwner(dialogStage)
        title = "Invalid Fields"
        headerText = "Please correct individual fields"
        contentText = errorMessage
      }.showAndWait()
      return false
    }
  }


  def handleOk(action: ActionEvent): Unit = {
    if (isInputValid()) {
      _person.firstName <== firstNameField.text
      _person.lastName <== lastNameField.text
      _person.street <== streetField.text
      _person.city <== cityField.text
      _person.postalCode.value = postalCodeField.getText.toInt
      _person.date.value = birthdayField.text.value.parseLocalDate

      okClicked = true
      dialogStage.close()
    }
  }

  def handleCancel(action: ActionEvent): Unit = {
    dialogStage.close()
  }
}