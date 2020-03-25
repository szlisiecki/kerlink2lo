package com.orange.lo.sample.kerlink2lo.lo;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

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
    public void shouldEncodeToBase64() {
        // given
        String input = "test input";
        String expected = "dGVzdCBpbnB1dA==";
        // when
        String coded = PayloadCodec.encodeToBase64(input);
        // then
        assertEquals(expected, coded);
    }

    @Test
    public void shouldDecodeFromBase64() {
        // given
        String input = "dGVzdCBpbnB1dA==";
        String expected = "test input";
        // when
        String decoded = PayloadCodec.decodeFromBase64(input);
        // then
        assertEquals(expected, decoded);
    }

    @Test
    public void shouldEncodeDecodeBase64() {
        // given
        String input = getRandomString();
        // when
        String coded = PayloadCodec.encodeToBase64(input);
        String decoded = PayloadCodec.decodeFromBase64(coded);
        // then
        assertEquals(input, decoded);
    }

    @Test
    public void shouldEncodeToHex() {
        // given
        String input = "test input";
        String expected = "7465737420696e707574";
        // when
        String coded = PayloadCodec.encodeToHex(input);
        // then
        assertEquals(expected, coded);
    }

    @Test public void shouldDecodeFromHex() throws DecoderException {
        // given
        String input = "7465737420696e707574";
        String expected = "test input";
        // when
        String decoded = PayloadCodec.decodeFromHex(input);
        assertEquals(expected, decoded);
    }

    @Test
    public void shouldEncodeDecodeHex() throws DecoderException {
        // given
        String input = getRandomString();
        // when
        String coded = PayloadCodec.encodeToHex(input);
        String decoded = PayloadCodec.decodeFromHex(coded);
        // then
        assertEquals(input, decoded);
    }
}