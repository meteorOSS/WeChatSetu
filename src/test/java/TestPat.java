import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestPat {
    public static void main(String[] args) throws IOException {
        File file = new File(System.getProperty("user.dir"), "video/test.mp4");
        System.out.println(Files.probeContentType(file.toPath()));
    }
}
