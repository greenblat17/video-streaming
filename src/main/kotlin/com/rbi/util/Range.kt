package com.rbi.util

data class Range(val start: Long, val end: Long) {
    fun getRangeStart(): Long {
        return start
    }

    fun getRangeEnd(fileSize: Long): Long {
        return minOf(end, fileSize - 1)
    }

    companion object {
        fun parseHttpRangeString(httpRangeString: String?, defaultChunkSize: Int): Range {
            if (httpRangeString == null) {
                return Range(0, defaultChunkSize.toLong())
            }
            val dashIndex = httpRangeString.indexOf("-")
            val startRange = httpRangeString.substring(6, dashIndex).toLong()
            val endRangeString = httpRangeString.substring(dashIndex + 1)
            if (endRangeString.isEmpty()) {
                return Range(startRange, startRange + defaultChunkSize)
            }
            val endRange = endRangeString.toLong()
            return Range(startRange, endRange)
        }
    }
}