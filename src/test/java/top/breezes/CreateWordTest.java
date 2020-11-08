package top.breezes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author yuchengxin
 * @Date 2020-11-06 16:33
 * @Description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CreateWordTest {

    @Resource
    private CreateWordService createWordService;

    @Test
    public void should_create_data_word() {
        // 多个表用英文逗号隔开
        createWordService.run("db_name","example_table_name", "/exampleFilePath/test-word.docx");
    }

}
