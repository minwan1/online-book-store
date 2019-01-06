package com.book.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
public class BaseControllerTest {

    protected ObjectMapper objectMapper = new ObjectMapper();


    protected <T> T readValue(String path, Class<T> clazz){
        try {
            final InputStream inputStream = new ClassPathResource(path, getClass()).getInputStream();
            return objectMapper.readValue(inputStream, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getAsString(InputStream json) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = json.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }


}
