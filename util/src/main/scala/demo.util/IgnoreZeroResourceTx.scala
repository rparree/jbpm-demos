package demo.util

import bitronix.tm.BitronixTransactionManager

/**
 * todo
 */
trait IgnoreZeroResourceTx {

  System.setProperty("bitronix.tm.2pc.warnAboutZeroResourceTransactions", "false")


}
