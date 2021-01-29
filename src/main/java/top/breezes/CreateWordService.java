package top.breezes;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import top.breezes.emuns.InfoTableCellEnum;
import top.breezes.emuns.SqlTableTitleEnum;
import top.breezes.model.TableInfo;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yuchengxin
 * @Date 2020-11-06 16:36
 * @Description
 */
@Service
public class CreateWordService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public void run(String schemaName, List<String> tableNames, String filePath) {
        check(tableNames, filePath);
        Map<String, List<TableInfo>> tableInfoMap = tableNames.stream()
                .collect(
                        Collectors.toMap(Function.identity(), tableName -> findTableInfo(schemaName, tableName))
                );
        createWordFile(schemaName, filePath, tableInfoMap);
    }

    public void run(String schemaName, String tableName, String filePath) {
        check(tableName, filePath);
        List<TableInfo> tableInfo = findTableInfo(schemaName, tableName);

        createWordFile(schemaName, filePath, new HashMap<String, List<TableInfo>>() {{
            put(tableName, tableInfo);
        }});
    }

    private void check(List<String> tableNames, String filePath) {
        if (Objects.isNull(tableNames) || StringUtils.isBlank(filePath)) {
            throw new NullPointerException();
        }
        for (String tableName : tableNames) {
            if (StringUtils.isBlank(tableName)) {
                throw new NullPointerException();
            }
        }
    }

    private void check(String tableName, String filePath) {
        if (StringUtils.isAnyBlank(tableName, filePath)) {
            throw new NullPointerException();
        }
    }

    private void createWordFile(String schemaName, String filePath, Map<String, List<TableInfo>> tableInfoMap) {
        try (FileOutputStream out = new FileOutputStream(new File(filePath));
             XWPFDocument document = new XWPFDocument()) {

            for (Map.Entry<String, List<TableInfo>> entry : tableInfoMap.entrySet()) {
                String tableName = entry.getKey();
                List<TableInfo> tableInfoList = entry.getValue();
                //基本信息表格
                XWPFTable baseTable = document.createTable();
                //列宽自动分割
                CTTblWidth baseTableWidth = baseTable.getCTTbl().addNewTblPr().addNewTblW();
                baseTableWidth.setType(STTblWidth.DXA);
                baseTableWidth.setW(BigInteger.valueOf(9072));

                createInfoTable(baseTable, tableName, schemaName);
                createNewRow(document);

                //基本信息表格2
                XWPFTable infoTable = document.createTable();
                //列宽自动分割
                CTTblWidth infoTableWidth = infoTable.getCTTbl().addNewTblPr().addNewTblW();
                infoTableWidth.setType(STTblWidth.DXA);
                infoTableWidth.setW(BigInteger.valueOf(9072));
                // 设置数据表头
                createDataTitle(infoTable, tableInfoList);

                createNewRow(document);
            }

            document.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewRow(XWPFDocument document) {
        //换行
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun paragraphRun1 = paragraph.createRun();
        paragraphRun1.setText("\r");
    }

    private void createInfoTable(XWPFTable baseTable, String tableName, String schemaName) {
        XWPFTableRow row = null;
        for (int i = 0; i < InfoTableCellEnum.values().length; i++) {
            InfoTableCellEnum infoTable = InfoTableCellEnum.values()[i];
            if (i == 0) {
                row = baseTable.getRow(0);
            } else {
                row = baseTable.createRow();
            }
            XWPFTableCell cell = row.getCell(0);
//            setContextCenter(cell);
            cell.setText(" " + infoTable.title);
            if (i == 0) {
                String comment = findBaseTableInfo(schemaName, tableName);
                row.addNewTableCell().setText(StringUtils.isBlank(comment) ? "" : " " + comment);
            } else {
                row.getCell(1).setText(" " + tableName);
            }

            if (i != InfoTableCellEnum.values().length - 1) {
                row.getCtRow().getTcList().forEach(
                        ctTc -> ctTc.addNewTcPr().addNewTcBorders().addNewBottom().setVal(STBorder.Enum.forString("none"))
                );
            }
        }
    }

    private void createDataTitle(XWPFTable infoTable, List<TableInfo> tableInfoList) {
        XWPFTableRow firstRow = infoTable.getRow(0);
        XWPFTableCell cell = null;
        for (int i = 0; i < SqlTableTitleEnum.values().length; i++) {
            SqlTableTitleEnum titleEnum = SqlTableTitleEnum.values()[i];
            if (i == 0) {
                cell = firstRow.getCell(0);
            } else {
                cell = firstRow.addNewTableCell();
            }
            setContextCenter(cell);
            cell.setText(titleEnum.title);
        }
        for (TableInfo tableInfo : tableInfoList) {
            XWPFTableRow row = infoTable.createRow();
            for (int i = 0; i < SqlTableTitleEnum.values().length; i++) {
                XWPFTableCell newCell = row.getCell(i);
                if (i == 0) {
                    newCell.setText(tableInfo.getColumnName());
                }
                if (i == 1) {
                    newCell.setText(tableInfo.getChineseName());
                }
                if (i == 2) {
                    newCell.setText(tableInfo.getColumnType());
                }
                if (i == 3) {
                    newCell.setText(tableInfo.getIsNullable());
                }
                if (i == 4) {
                    newCell.setText(tableInfo.getColumnDefault());
                }
                if (i == 5) {
                    newCell.setText(tableInfo.getColumnComment());
                }
                setContextCenter(newCell);
            }
        }
    }

    private void setContextCenter(XWPFTableCell cell) {
        CTTc ctTc = cell.getCTTc();
        CTTcPr ctPr = ctTc.addNewTcPr();
        ctPr.addNewVAlign().setVal(STVerticalJc.CENTER);
        ctTc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
    }

    public List<TableInfo> findTableInfo(String schemaName, String tableName) {
        String sql = "SELECT column_name AS columnName,\n" +
                "       null as chineseName ,\n" +
                "       column_type AS columnType,\n" +
                "       if(is_nullable = 'NO', 'false', 'true') as isNullable,\n" +
                "       column_default AS columnDefault,\n" +
                "       if(column_comment = '', null, column_comment) as columnComment\n" +
                "FROM information_schema.columns\n" +
                "WHERE table_schema = '" + schemaName + "'\n" +
                "  and table_name = '" + tableName + "'";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TableInfo.class));
    }

    public String findBaseTableInfo(String schemaName, String tableName) {
        String sql = "SELECT table_comment as comment from information_schema.TABLES where table_name = '" + tableName + "' and table_schema = '" + schemaName + "'";
        System.out.println(sql);
        return jdbcTemplate.queryForObject(sql, String.class);
    }


}
