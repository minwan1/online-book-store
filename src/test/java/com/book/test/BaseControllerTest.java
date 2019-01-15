package com.book.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

@RunWith(SpringRunner.class)
@Ignore
public class BaseControllerTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation(); // (1)/

    protected RestDocumentationResultHandler document = buildDocument();
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


    protected MockMvc buildMockMvc(final WebApplicationContext webApplicationContext) {
        return MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(document)
                .build();
    }

    private RestDocumentationResultHandler buildDocument() {
        return document(
                "{class-name}/{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
        );
    }

}
