package model

import java.util.Date

import model.RejectionLevel.RejectionLevel


import scala.beans.BeanProperty


case class Driver(@BeanProperty var driverName: String,
                  @BeanProperty var age: Integer,
                  @BeanProperty var ssn: String,
                  @BeanProperty var dlNumber: String,
                  @BeanProperty var numberOfAccidents: Integer,
                  @BeanProperty var numberOfTickets: Integer,
                  @BeanProperty var creditScore: Integer
                 ) {

}

case class Policy(@BeanProperty var requestDate: Date,
                  @BeanProperty var policyType: String,
                  @BeanProperty var vehicleYear: Integer,
                  @BeanProperty var price: Integer,
                  @BeanProperty var priceDiscount: Integer,
                  @BeanProperty var driver: Driver,

                  @BeanProperty var risk: Integer) {


}

case class Rejection(@BeanProperty var reason: String,
                     @BeanProperty var level: RejectionLevel) {

}

object RejectionLevel extends Enumeration {
  type RejectionLevel = Value
  val NONE, ERROR, FATAL = Value
}


