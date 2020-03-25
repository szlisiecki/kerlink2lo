package com.orange.lo.sample.kerlink2lo.lo;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PayloadCodecTest {

    public String getRandomString() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)(random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        return generatedString;
    }

    @Test
    public void testEncodeToBase64() {
        String input = "test input";
        assertEquals("dGVzdCBpbnB1dA==", PayloadCodec.encodeToBase64(input));
    }

    @Test
    public void testDecodeFromBase64() {
        String input = "dGVzdCBpbnB1dA==";
        assertEquals("test input", PayloadCodec.decodeFromBase64(input));
    }

    @Test
    public void testEncodeDecodeBase64() {
        String input = getRandomString();
        assertEquals(input, PayloadCodec.decodeFromBase64(PayloadCodec.encodeToBase64(input)));
    }

    @Test public void testEncodeToHex() {
        String input = "test input";
        assertEquals("7465737420696e707574", PayloadCodec.encodeToHex(input));
    }

    @Test public void testDecodeFromHex() throws DecoderException {
        String input = "7465737420696e707574";
        assertEquals("test input", PayloadCodec.decodeFromHex(input));
    }

    @Test
    public void testEncodeDecodeHex() throws DecoderException {
        String input = getRandomString();
        assertEquals(input, PayloadCodec.decodeFromHex(PayloadCodec.encodeToHex(input)));
    }
}