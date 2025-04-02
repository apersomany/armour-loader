package dev.aperso.loader.armour;

import org.joml.Vector3i;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmourReader {
    private static final int FILE_VERSION = 13;

    private static final String TAG_SKIN_HEADER = "AW-SKIN-START";
    private static final String TAG_SKIN_PROPS_HEADER = "PROPS-START";
    private static final String TAG_SKIN_PROPS_FOOTER = "PROPS-END";
    private static final String TAG_SKIN_TYPE_HEADER = "TYPE-START";
    private static final String TAG_SKIN_TYPE_FOOTER = "TYPE-END";
    private static final String TAG_SKIN_PAINT_HEADER = "PAINT-START";
    private static final String TAG_SKIN_PAINT_FOOTER = "PAINT-END";
    private static final String TAG_SKIN_PART_HEADER = "PART-START";
    private static final String TAG_SKIN_PART_FOOTER = "PART-END";
    private static final String TAG_SKIN_FOOTER = "AW-SKIN-END";

    private static final Vector3i[] FACE_NORMALS = new Vector3i[] {
        new Vector3i(0, +1, 0),
        new Vector3i(0, -1, 0),
        new Vector3i(0, 0, -1),
        new Vector3i(0, 0, +1),
        new Vector3i(+1, 0, 0),
        new Vector3i(-1, 0, 0),
    };

    private static Map<String, Object> readProps(DataInputStream stream) throws IOException {
        int length = stream.readInt();
        Map<String, Object> props = new HashMap<>(length);
        for (int i = 0; i < length; i++) {
            String name = stream.readUTF();
            switch (stream.readByte()) {
                case 0: {
                    props.put(name, stream.readUTF());
                    break;
                }
                case 1: {
                    props.put(name, stream.readInt());
                    break;
                }
                case 2: {
                    props.put(name, stream.readDouble());
                    break;
                }
                case 3: {
                    props.put(name, stream.readBoolean());
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown property type");
            }
        }
        return props;
    }

    // todo: optimize this abomination
    private static Map<String, Armour.Quad[][]> readParts(DataInputStream stream) throws IOException {
        byte length = stream.readByte();
        Map<String, Armour.Quad[][]> parts = new HashMap<>(length);
        for (byte i = 0; i < length; i++) {
            if (!stream.readUTF().equals(TAG_SKIN_PART_HEADER)) {
                throw new IllegalArgumentException("Expected part header: " + TAG_SKIN_PART_HEADER);
            }
            String name = stream.readUTF();
            int cubeCount = stream.readInt();
            Armour.Quad[][] cubes = new Armour.Quad[cubeCount][6];
            int[][] binaryCubes = new int[32][128];
            for (int j = 0; j < cubeCount; j++) {
                byte t = stream.readByte();
                byte x = stream.readByte();
                byte y = stream.readByte();
                byte z = stream.readByte();
                binaryCubes[x + 16][y + 64] |= (t & 1 ^ 1) << (z + 16);
                for (int k = 0; k < 6; k++) {
                    cubes[j][k] = new Armour.Quad(t, x, y, z, stream.readInt() >> 8 | 0xFF000000);
                }
            }
            List<List<Armour.Quad>> quads = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                Vector3i normal = FACE_NORMALS[j];
                int x = normal.x + 16;
                int y = normal.y + 64;
                int z = normal.z + 16;
                quads.add(new ArrayList<>());
                for (Armour.Quad[] cube : cubes) {
                    Armour.Quad quad = cube[j];
                    if (((binaryCubes[quad.x() + x][quad.y() + y] >> (quad.z() + z)) & 1) == 0) {
                        quads.get(j).add(quad);
                    }
                }
            }
            Armour.Quad[][] arrayQuads = new Armour.Quad[6][];
            for (int j = 0; j < 6; j++) {
                arrayQuads[j] = quads.get(j).toArray(new Armour.Quad[0]);
            }
            stream.skipBytes(stream.readInt() * 4);
            if (!stream.readUTF().equals(TAG_SKIN_PART_FOOTER)) {
                throw new IllegalArgumentException("Expected part footer: " + TAG_SKIN_PART_FOOTER);
            }
            parts.put(name, arrayQuads);
        }
        return parts;
    }

    public static Armour read(DataInputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("DataInputStream cannot be null");
        }
        if (stream.readInt() != FILE_VERSION) {
            throw new IllegalArgumentException("Unsupported file version");
        }
        if (!stream.readUTF().equals(TAG_SKIN_HEADER)) {
            throw new IllegalArgumentException("Expected header: " + TAG_SKIN_HEADER);
        }
        if (!stream.readUTF().equals(TAG_SKIN_PROPS_HEADER)) {
            throw new IllegalArgumentException("Expected props header: " + TAG_SKIN_PROPS_HEADER);
        }
        Map<String, Object> props = readProps(stream);
        if (!stream.readUTF().equals(TAG_SKIN_PROPS_FOOTER)) {
            throw new IllegalArgumentException("Expected props footer: " + TAG_SKIN_PROPS_FOOTER);
        }
        if (!stream.readUTF().equals(TAG_SKIN_TYPE_HEADER)) {
            throw new IllegalArgumentException("Expected type header: " + TAG_SKIN_TYPE_HEADER);
        }
        String type = stream.readUTF();
        if (!stream.readUTF().equals(TAG_SKIN_TYPE_FOOTER)) {
            throw new IllegalArgumentException("Expected type footer: " + TAG_SKIN_TYPE_FOOTER);
        }
        if (!stream.readUTF().equals(TAG_SKIN_PAINT_HEADER)) {
            throw new IllegalArgumentException("Expected paint header: " + TAG_SKIN_PAINT_HEADER);
        }
        if (stream.readBoolean()) {
            stream.skipBytes(2048 * 4);
        }
        if (!stream.readUTF().equals(TAG_SKIN_PAINT_FOOTER)) {
            throw new IllegalArgumentException("Expected paint footer: " + TAG_SKIN_PAINT_FOOTER);
        }
        Map<String, Armour.Quad[][]> parts = readParts(stream);
        if (!stream.readUTF().equals(TAG_SKIN_FOOTER)) {
            throw new IllegalArgumentException("Expected footer: " + TAG_SKIN_FOOTER);
        }
        return new Armour(type, props, parts);
    }
}