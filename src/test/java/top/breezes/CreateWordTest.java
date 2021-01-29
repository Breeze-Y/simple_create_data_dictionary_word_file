package top.breezes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yuchengxin
 * @Date 2020-11-06 16:33
 * @Description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CreateWordTest {

    public static final String SCHEMA_NAME = "schema_name";
    public static final String FILE_PATH = "/xxx/xxx/xxx/data-word.docx";
    @Resource
    private CreateWordService createWordService;

    @Test
    public void should_create_data_word() {
        // 多个表用英文逗号隔开
        createWordService.run(SCHEMA_NAME, "table_name", FILE_PATH);
    }

    @Test
    public void should_create_data_word_from_list() {
        List<String> list = new LinkedList<>();
        list.add("table_name_1");
        list.add("table_name_2");
        createWordService.run(SCHEMA_NAME, list, FILE_PATH);
    }


}
