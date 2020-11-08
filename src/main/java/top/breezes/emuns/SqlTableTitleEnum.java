package top.breezes.emuns;

/**
 * @author yuchengxin
 * @Date 2020-11-06 17:49
 * @Description
 */
public enum SqlTableTitleEnum {

    SQL_TABLE_TITLE_ENUM_01("字段名"),
    SQL_TABLE_TITLE_ENUM_02("名称"),
    SQL_TABLE_TITLE_ENUM_03("数据类型"),
    SQL_TABLE_TITLE_ENUM_04("是否为空"),
    SQL_TABLE_TITLE_ENUM_05("默认值"),
    SQL_TABLE_TITLE_ENUM_06("注释");

    public final String title;

    SqlTableTitleEnum(String title) {
        this.title = title;
    }
}
