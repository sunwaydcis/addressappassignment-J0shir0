package ch.makery.address.util

import ch.makery.address.model.Person
import scalikejdbc.{AutoSession, ConnectionPool, DB}

trait Database {
  val derbyDriverClassName = "org.apache.derby.jdbc.EmbeddedDriver"

  val dbURL = "jdbc:derby:myDB;create=true;" // I would like to create this database
  // initialize JDBC driver and the connection pool
  Class.forName(derbyDriverClassName)
  ConnectionPool.singleton(dbURL, user = "me", password = "nine")

  // ad-hoc session provider
  implicit val session = AutoSession
}

object Database extends Database {
  def hasDBInitialize: Boolean = {
    DB getTable("Person") match {
      case Some(x) => true
      case None => false
    }
  }

  def setupDB() : Unit= {
    if(!hasDBInitialize) {
      Person.initializeTable()
    }
  }
}