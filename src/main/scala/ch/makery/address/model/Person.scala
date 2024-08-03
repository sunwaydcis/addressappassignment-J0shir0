package ch.makery.address.model

import ch.makery.address.util.Database
import ch.makery.address.util.DateUtil.{DateFormatter, StringFormatter}
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalikejdbc.{DB, scalikejdbcSQLInterpolationImplicitDef}

import java.time.LocalDate
import scala.util.Try

class Person (firstNameS: String, lastNameS: String) extends Database {
  def this() = this(null, null)

  val firstName = new StringProperty(firstNameS)
  val lastName = new StringProperty(lastNameS)
  val street = new StringProperty(initialValue = "Jalan University")
  val city = new StringProperty(initialValue = "Bandar Sunway")
  val postalCode = ObjectProperty[Int](initialValue = 47500)
  val date = ObjectProperty[LocalDate](LocalDate.of(1999, 2, 2))

  def isExist: Boolean = {
    DB readOnly { implicit session =>
      sql"""
           select * from Person where
           firstName = ${firstName.value} and lastName = ${lastName.value}
         """.map(rs => rs.string(columnLabel = "firstName")).single.apply()
    } match {
      case Some(x) => true
      case None => false
    }
  }

  def save(): Try[Int] = {
    if (!isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
             insert into Person(firstName, lastName, street, postalCode, city, date) values
             (${firstName.value}, ${lastName.value}, ${street.value}, ${postalCode.value},
             ${city.value}, ${date.value.asString})
           """.update.apply()
      })
    } else {
      Try(DB autoCommit { implicit session =>
        sql"""
             update Person
             set
             firstName = ${firstName.value},
             lastName = ${lastName.value},
             street = ${street.value},
             postalCode = ${postalCode.value},
             city = ${city.value},
             date = ${date.value.asString}
             where firstName = ${firstName.value} and lastName = ${lastName.value}
           """.update.apply()
      })
    }
  }

  def delete(): Try[Int] = {
    if (isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
               delete from Person where
                firstName = ${firstName.value} and lastName = ${lastName.value}
             """.update.apply()
      })
    } else{
      throw new Exception("Person not exists in database!")
    }
  }
}

object Person extends Database {

  def apply(
           firstNameS: String,
           lastNameS: String,
           streetS: String,
           postalCodeI: Int,
           cityS: String,
           dateS: String
           ) : Person =
    {
      new Person(firstNameS, lastNameS) {
        street.value = streetS
        postalCode.value = postalCodeI
        city.value = cityS
        date.value = dateS.parseLocalDate
      }
    }

  def initializeTable() = {
    DB autoCommit{
      implicit session =>
        sql"""
              create table Person (
             id int not null GENE  RATED AlWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
             firstName varchar(64),
             lastName varchar(64),
             street varchar(200),
             postalCode int,
             city varchar(100),
             date varchar(64)
             )
             """.execute.apply()
    }
  }

  def getAllPersons: List[Person] = {
    DB readOnly{implicit session =>
      sql"select * from Person".map(rs =>Person(rs.string("firstName"),
        rs.string("lastName"), rs.string("street"), rs.int("postalCode"),
        rs.string("city"),rs.string("date"))).list.apply()
    }
  }
}
