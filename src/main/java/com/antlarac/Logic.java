package com.antlarac;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import java.util.List;

public class Logic {

    public List<ProxyHttpRequestResponse> getFullHistory(MontoyaApi api) {
        return api.proxy().history();
    }
}
