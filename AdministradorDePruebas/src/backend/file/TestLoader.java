// backend/file/TestLoader.java
package backend.file;

import backend.model.Test;
import java.io.File;
import java.io.IOException;

public interface TestLoader {

    Test loadTest(File file) throws IOException, IllegalArgumentException;
}