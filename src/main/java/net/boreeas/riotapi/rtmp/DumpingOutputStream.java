package net.boreeas.riotapi.rtmp;

import lombok.AllArgsConstructor;
import net.boreeas.riotapi.Util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by malte on 7/5/2014.
 */
@AllArgsConstructor
public class DumpingOutputStream extends OutputStream {
    private OutputStream base;


    @Override
    public void write(int b) throws IOException {
        System.out.println("[<<<] " + Integer.toHexString(b & 0xff));
        base.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        for (String line: Util.hexdump(b)) {
            System.out.println("[<<<] " + line);
        }
        System.out.println();
        base.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] buf = new byte[len];
        System.arraycopy(b, off, buf, 0, len);
        for (String line: Util.hexdump(buf)) {
            System.out.println("[<<<] " + line);
        }
        System.out.println();

        base.write(b, off, len);
    }
}
