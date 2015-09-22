package demo.cxf

import javax.xml.bind.annotation.{XmlAccessType, XmlAccessorType, XmlType}

/**
 * todo
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
case class DemoRequest(name : String, email: String){
  def this() = this("","")
}
