package com.antlarac;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import burp.api.montoya.logging.Logging;

import java.io.PrintStream;
import java.util.List;

public class Logic {

    private final Logging logging;

    public Logic(Logging logging) {
        this.logging = logging;
    }

//    Logging logging = new Logging() {
//        @Override
//        public PrintStream output() {
//            return null;
//        }
//
//        @Override
//        public PrintStream error() {
//            return null;
//        }
//
//        @Override
//        public void logToOutput(String message) {
//
//        }
//
//        @Override
//        public void logToError(String message) {
//
//        }
//
//        @Override
//        public void logToError(String message, Throwable cause) {
//
//        }
//
//        @Override
//        public void logToError(Throwable cause) {
//
//        }
//
//        @Override
//        public void raiseDebugEvent(String message) {
//
//        }
//
//        @Override
//        public void raiseInfoEvent(String message) {
//
//        }
//
//        @Override
//        public void raiseErrorEvent(String message) {
//
//        }
//
//        @Override
//        public void raiseCriticalEvent(String message) {
//
//        }
//    };
    public List<ProxyHttpRequestResponse> getFullHistory(MontoyaApi api) {
        return api.proxy().history();
    }

    public void populateRequestAndResponseBoxWhenClickingAHistoryTableRow() {
        logging.logToOutput("populating request and response");
    }
}
