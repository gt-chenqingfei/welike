package top.zibin.luban;

import java.io.File;

public class WrapSizeFile {

    public File file;
    public int width;
    public int height;

    public WrapSizeFile(String path) {
        file = new File(path);
    }

    public WrapSizeFile(File path) {
        file = path;
    }

    public WrapSizeFile() {
        
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }
}
