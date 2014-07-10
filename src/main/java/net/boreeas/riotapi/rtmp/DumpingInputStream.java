package net.boreeas.riotapi.rtmp;

import lombok.AllArgsConstructor;
import net.boreeas.riotapi.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by malte on 7/5/2014.
 */
@AllArgsConstructor
public class DumpingInputStream extends InputStream {
    private InputStream base;

    @Override
    public int read() throws IOException {
        int i = base.read();
        System.out.println("[>>>] " + Integer.toHexString(i & 0xff));
        return i;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int i = base.read(b);
        for (String line: Util.hexdump(b)) {
            System.out.println("[>>>] " + line);
        }
        System.out.println();

        return i;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int i = base.read(b, off, len);

        byte[] buf = new byte[len];
        System.arraycopy(b, off, buf, 0, len);
        for (String line: Util.hexdump(buf)) {
            System.out.println("[>>>] " + line);
        }
        System.out.println();

        return i;
    }
}
