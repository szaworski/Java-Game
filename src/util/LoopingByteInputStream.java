package util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
    The LoopingByteInputStream is a ByteArrayInputStream that
    loops indefinitly. The looping stops when the close() method
    is called.
    <p>Possible ideas to extend this class:<ul>
    <li>Add an option to only loop a certain number of times.
    </ul>
*/
public class LoopingByteInputStream extends ByteArrayInputStream {

    private boolean closed;

    public LoopingByteInputStream(byte[] buffer) {
        super(buffer);
        closed = false;
    }

    public int read(byte[] buffer, int offset, int length) {
        if (closed) {
            return -1;
        }
        int totalBytesRead = 0;

        while (totalBytesRead < length) {
            int numBytesRead = super.read(buffer,
                offset + totalBytesRead,
                length - totalBytesRead);

            if (numBytesRead > 0) {
                totalBytesRead += numBytesRead;
            }
            else {
                reset();
            }
        }
        return totalBytesRead;
    }

    public void close() throws IOException {
        super.close();
        closed = true;
    }

}
