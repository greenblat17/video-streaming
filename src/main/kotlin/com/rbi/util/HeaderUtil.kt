package com.rbi.util

class HeaderUtil {
    companion object {
        fun calculateContentLengthHeader(range: Range, fileSize: Long): String {
            return (range.getRangeEnd(fileSize) - range.getRangeStart() + 1).toString()
        }

        fun constructContentRangeHeader(range: Range, fileSize: Long): String {
            return "bytes ${range.getRangeStart()}-${range.getRangeEnd(fileSize)}/$fileSize"
        }
    }
}