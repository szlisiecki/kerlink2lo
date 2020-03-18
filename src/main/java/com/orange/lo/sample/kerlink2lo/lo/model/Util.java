package com.orange.lo.sample.kerlink2lo.lo.model;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Base64;

public class Util {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String encodeToBase64(String raw) {
        String encoded = Base64.getEncoder().encodeToString(raw.getBytes());
        return encoded;
    }

    public static String decodeFromBase64(String encoded) {
        String decoded = Base64.getEncoder().encodeToString(encoded.getBytes());
        return decoded;
    }

    public static String encodeToHex(String raw) {
        String encoded = Hex.encodeHexString(raw.getBytes());
        return encoded;
    }

    public static String decodeFromHex(String encoded) {
        String decoded = Base64.getEncoder().encodeToString(encoded.getBytes());
        return decoded;
    }
}
