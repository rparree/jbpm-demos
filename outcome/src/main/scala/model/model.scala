package model

import scala.beans.BeanProperty

case class Policy(@BeanProperty var risk: Integer) {
  @BeanProperty var price = 0
}

case class Rejection(@BeanProperty var reason: String)



