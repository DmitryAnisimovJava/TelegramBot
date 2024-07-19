package com.mergeteam;

import org.hashids.Hashids;

public class CryptoTool {

    private static final int MIN_HASH_LENGTH = 10;
    private final Hashids hashids;

    public CryptoTool(String salt) {
        this.hashids = new Hashids(salt, MIN_HASH_LENGTH);
    }

    public String hashOf(long value) {
        return hashids.encode(value);
    }

    public Long idOf(String value) {
        long[] decoded = hashids.decode(value);
        return (decoded != null && decoded.length > 0) ? decoded[0] : null;
    }
}
