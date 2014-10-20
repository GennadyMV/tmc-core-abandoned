package fi.helsinki.cs.tmc.client.core.domain;

/**
 * Class that stores the project as a zip. Used when downloading exercises from
 * the server; all exercises are initially zipped.
 */
public class Zip {

    private byte[] bytes;

    public void setBytes(final byte[] bytes) {

        this.bytes = bytes;
    }

    public byte[] getBytes() {

        return bytes;
    }

    public int length() {

        return bytes != null ? bytes.length : 0;
    }
}
