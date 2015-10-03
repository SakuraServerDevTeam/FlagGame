/* 
 * Copyright (c) 2015 SakuraServerDev All rights reserved.
 */
package syam.flaggame.util;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 *
 * @author Toyblocks
 */
public final class UUIDUtil {
    
    private UUIDUtil() {
        throw new RuntimeException();
    }
    
    public static UUID getUUIDFromBytes(byte[] bytes) {
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        return new UUID(buf.getLong(), buf.getLong());
    }
    
    public static byte[] getBytesFromUUID(UUID uuid) {
        if (uuid == null) {
            throw new NullPointerException();
        }
        return ByteBuffer.allocate(16)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits())
                .array();
    }
    
}
