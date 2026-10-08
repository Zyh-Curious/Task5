
package Q2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Q2 {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("names.txt"), "UTF-8"))   ) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; 
                }
                names.add(line.trim());
            }

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
        Collections.sort(names, Collator.getInstance(Locale.CHINA));
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("names_sorted.txt"), "UTF-8"))) {
            for (String name : names) {
                bw.write(name);
                bw.newLine();
            }
            System.out.println("文件names_sorted.txt已创建并写入排序后的姓名。");
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }
    
}
