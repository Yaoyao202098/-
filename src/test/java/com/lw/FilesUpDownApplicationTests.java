package com.lw;

import com.lw.domain.User;
import com.lw.utils.FileDownloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileCopyUtils;

import java.io.*;

@Slf4j
@SpringBootTest
class FilesUpDownApplicationTests {

    @Test
    void byteStreamDemo1() throws IOException {
        InputStream is = new FileInputStream("D:\\word.txt");
        int b;
        while ((b = is.read()) != -1) {
            System.out.println(((char) b));
        }
        is.close();
    }

    @Test
    void tes() throws IOException {
        File file = new File("D:\\word.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] data = new byte[2];
        while (fileInputStream.read(data) != -1) {
//            System.out.println(((char) data));
//           data=new byte[2];
        }
    }

    @Test
    void byteStreamDemo2() throws IOException {
        InputStream is = new FileInputStream("D:\\word.txt");
        byte[] data = new byte[2];
        int len;
        while ((len = is.read(data)) != -1) {
            System.out.println(new String(data, 0, len));
        }
        is.close();
    }


    @Test
    void fileCopy() throws FileNotFoundException {
        File file = new File("D:\\lw.txt");
        try (
                InputStream is = new FileInputStream(file);
                OutputStream os = new FileOutputStream("D:\\fileCopyTest\\lw.txt");
        ) {
            byte[] data = new byte[1024];
            int len;
            while ((len = is.read(data)) != -1) {
                os.write(data, 0, len);
            }
//            int copy= FileCopyUtils.copy(is,os);
            // FileUtils.copyFile(file,new File("D:\\lw.txt"));
////            log.info("复制成功,文件大小为{}字节",copy);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void charStreamDemo1() throws IOException {
        FileReader fd = new FileReader("D:\\javaProject\\files_Up_down\\file\\lw.txt");
        char[] ch = new char[4];
        int len;
        while ((len = fd.read(ch)) != -1) {
            System.out.print(new String(ch, 0, len));
        }
        fd.close();
    }

    @Test
    void charStreamDemo2() throws IOException {
        FileWriter fw = new FileWriter("D:\\javaProject\\files_Up_down\\file\\lw.txt");
        fw.write("我是隆伟,你好");
        fw.flush();   //强行写进去
        fw.close();
    }

    @Test
    void bufferStreamDemo1() {
        long start = System.currentTimeMillis();
        try (
                InputStream is = new FileInputStream("D:\\E\\friends.rar");
                BufferedInputStream bis = new BufferedInputStream(is);
                OutputStream os = new FileOutputStream("D:\\friends.rar");
                BufferedOutputStream bos = new BufferedOutputStream(os)
        ) {
            System.out.println("开始复制");
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            System.out.println("复制完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("复制时间为:" + (end - start));
    }

    @Test
    void bufferStreamDemo2() {
        try (
                Reader fr = new FileReader("D:\\javaProject\\files_Up_down\\file\\lw.txt");
                BufferedReader br = new BufferedReader(fr);
        ) {
            String line;
            while ((line = br.readLine()) != null) { //直接读取一行
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void objectStreamDemo1() {
        try (
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D:\\javaProject\\files_Up_down\\file\\user.txt"))
        ) {
            User u = new User("lw", "123456", 23);
            oos.writeObject(u);
            log.info("序列化成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void objectStreamDemo2() {
        try (
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream("D:\\javaProject\\files_Up_down\\file\\user.txt"))
        ) {
            User u = ((User) ois.readObject());
            System.out.println(u);
            log.info("反序列化成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void test1() throws IOException {
        File file = new File("D:\\chunjiang1.txt");
        FileReader fr = new FileReader(file);
        char[] buffer = new char[10];
        int len;
        while ((len = fr.read(buffer)) > -1) {
            String msg = new String(buffer, 0, len);
            System.out.println(msg);
        }
        fr.close();       //关闭流?
        file.delete();
    }


    @Test
    void fileTest() throws Exception {
        File file = new File("D:\\FileTest.txt");
        try (
                InputStream is = new FileInputStream(file);
                OutputStream os = new FileOutputStream("E:\\FileTest.txt")
        ) {
            int len;
            byte[] buffer = new byte[4];
            while ((len = is.read(buffer)) != -1) {
                String s = new String(buffer, 0, len);
                System.out.print(s);
                os.write(buffer, 0, len);
            }
        }
    }

    @Test
    void fileDownTest() {
        String downloadUrl = "https://t7.baidu.com/it/u=1951548898,3927145&fm=193&f=GIF";
        String filePath = "D:\\javaProject\\files_Up_down\\file\\";
        String fileName = "download.jpg";
        boolean isDown = FileDownloadUtil.downloadToServer(downloadUrl, filePath, fileName);
        if (isDown) System.out.println("下载成功");
    }


}
