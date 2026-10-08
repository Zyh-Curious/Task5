import java.io.File;

public class test {
    public static void main(String[] args) {
        String filePath = "doro.jpg"; // Specify the path to the file

        File file = new File(filePath);
        if (file.exists()) {
            System.out.println("文件存在: " + file.getAbsolutePath());
        } else {
            System.out.println("文件不存在: " + file.getAbsolutePath());
        }
    }
    
}
