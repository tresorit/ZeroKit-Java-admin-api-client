package ZeroKit.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * IO utilities for ZeroKit admin API client
 * @author 		hami89 (Gergely Hamos, hami89@gmail.com)
 * @copyright	Copyright © Tresorit AG. 2017
 */
public final class IOUtils {
    /**
     * Reads all contents till the end of the given input stream and returns it as a byte array
     * @param input Input stream to read
     * @return Returns the read data as a byte array
     * @throws IOException Throw when an error occurs during the read of the underlying stream
     */
    public static byte[] readAll(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }
}
