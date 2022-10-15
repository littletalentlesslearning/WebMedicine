package Test;

import org.junit.jupiter.api.Test;

public class UploadTest {
    @Test
    public void Test1(){
        String fileName = "abc.jpg";
        //String substring = fileName.substring(fileName.lastIndexOf("."));
        String[] split = fileName.split("\\.");
        String substring = "." + split[1];
        System.out.println(substring);
        System.out.println("之后的长度："+split.length);
        for (int i = 0;i<split.length;i++) {
            System.out.println(split[i]);
        }

    }
    @Test
    public void Test2() {
        String s = "a,b,c,d,e";

        String temp[];

        temp = s.split(",");
        System.out.println("之后的长度：" + temp.length);
        for (int i = 0; i < temp.length; i++) {
            System.out.println(temp[i]);
        }
    }
}
