package com.budjb.httprequests.examples

import com.budjb.httprequests.filter.bundled.LoggingFilter

/**
 * This is a LoggingFilter implementation that simply prints the HTTP conversation to the system output.
 */
class PrintlnLoggingFilter extends LoggingFilter {
    @Override
    void log(String content) {
        println content
    }
}
