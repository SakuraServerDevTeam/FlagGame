/*
 * Copyright (C) 2016 Toyblocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package syam.flaggame.database;

/**
 *
 * @author Toyblocks
 */
public enum RecordType {
    
    DEATH((byte) 1),
    FLAG_PLACE((byte) 2),
    FLAG_BREAK((byte) 3),
    NEXUS_BREAK((byte) 4),
    BANNER_GET((byte) 5),
    BANNER_KEEP((byte) 6),
    BANNER_DEPLOY((byte) 7),
    ;
    
    
    private final byte typeCode;
    
    private RecordType(byte typeCode) {
        this.typeCode = typeCode;
    }

    public byte getTypeCode() {
        return typeCode;
    }
    
}
