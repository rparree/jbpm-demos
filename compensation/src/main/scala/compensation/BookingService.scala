package compensation

import scala.beans.BeanProperty

/**
 * todo
 */
class BookingService {

  def book(f: BookingRequest) = println("Booking")
  def cancelBooking(f: BookingRequest) = println("Cancelling")

}

case class BookingRequest(@BeanProperty email:String)

