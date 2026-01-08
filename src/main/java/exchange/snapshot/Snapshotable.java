package exchange.snapshot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Snapshotable {
    void snapshot(DataOutputStream out) throws IOException;
    void restore(DataInputStream in) throws IOException;
}
