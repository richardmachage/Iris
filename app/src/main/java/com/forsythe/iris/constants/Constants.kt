package com.forsythe.iris.constants

import java.util.regex.Pattern

val sendRegexPattern = Pattern.compile(
    """(\w+) Confirmed\. Ksh(\d+\.\d{2}) sent to ([A-Z\s]+) (\d+) on (\d+/\d+/\d{2}) at (\d+:\d{2} (?:AM|PM))\. 
    |New M-PESA balance is Ksh([\d,]+\.\d{2})\. Transaction cost, Ksh(\d+\.\d{2})\. 
    |Amount you can transact within the day is ([\d,]+\.\d{2})\.""".trimMargin()
)

val receiveRgexPattern = Pattern.compile(
    """(\w+) Confirmed\.You have received Ksh([\d,]+\.\d{2}) from ([A-Z\s]+) (\d+) on (\d+/\d+/\d{2}) at (\d+:\d{2} (?:AM|PM))  
    |New M-PESA balance is Ksh([\d,]+\.\d{2})\. Use a unique M-PESA PIN to keep your money safe - don't use your date of birth as your PIN\.""".trimMargin()
)