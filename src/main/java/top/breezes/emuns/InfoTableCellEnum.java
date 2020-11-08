package top.breezes.emuns;

/**
 * @author yuchengxin
 * @Date 2020-11-06 17:49
 * @Description
 */
public enum InfoTableCellEnum {

    INFO_TABLE_CELL_ENUM_01("注释"),
    INFO_TABLE_CELL_ENUM_02("名称"),
    INFO_TABLE_CELL_ENUM_03("表名");

    public final String title;

    InfoTableCellEnum(String title) {
        this.title = title;
    }

}
