package Q3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;



public class Main {
    public static void main(String[] args) {
        Student student = new Student(1, "doro", 2, "123-456-7890");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("student.data"))) {
            oos.writeObject(student);
            System.out.println("序列化成功");
        } catch (IOException e) {
            throw new RuntimeException("序列化失败", e);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("student.data"))) {
            Student deserializedStudent = (Student) ois.readObject();
            System.out.println(deserializedStudent.toString());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("反序列化失败", e);
        }

    }
}
