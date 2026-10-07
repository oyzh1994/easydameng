package cn.oyzh.easydameng.dameng.data;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 达梦类型文件读取器抽象基类
 *
 * @author oyzh
 * @since 2024-09-03
 */
public abstract class DamengTypeFileReader implements Closeable {

    // public TypeFileReader( String filePath) {
    //     this(new File(filePath), StandardCharsets.UTF_8);
    // }
    //
    // public TypeFileReader( File file) {
    //     this(file, StandardCharsets.UTF_8);
    // }
    //
    // public TypeFileReader( String filePath, Charset charset) {
    //     this(new File(filePath), charset);
    // }
    //
    // public TypeFileReader( File file, Charset charset) {
    // }

    /**
     * 初始化读取器
     *
     * @throws Exception 初始化异常
     */
    protected void init() throws Exception {

    }

    /**
     * 读取单条对象数据
     *
     * @return 对象数据
     * @throws Exception 读取异常
     */
    public abstract Map<String, Object> readObject() throws Exception;

    /**
     * 读取指定数量的对象数据
     *
     * @param count 读取数量
     * @return 对象数据列表
     * @throws Exception 读取异常
     */
    public List<Map<String, Object>> readObjects(int count) throws Exception {
        // 数据列表
        List<Map<String, Object>> records = new ArrayList<>();
        // 读取数据
        while (records.size() < count) {
            Map<String, Object> item = this.readObject();
            if (item == null) {
                break;
            }
            records.add(item);
        }
        return records;
    }

    /**
     * 解析文本行
     *
     * @param line           待解析的文本行
     * @param txtIdentifier  文本识别符号
     * @param fieldSeparator 字段分割符号
     * @return 字段值列表
     * @throws IOException 解析异常
     */
    protected List<String> parseLine(String line, char txtIdentifier, char fieldSeparator) throws IOException {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean txtStart = false;
        try (StringReader reader = new StringReader(line)) {
            while (reader.ready()) {
                int i = reader.read();
                if (i == -1) {
                    break;
                }
                char c = (char) i;
                if (txtStart && c == fieldSeparator) {
                    txtStart = false;
                    continue;
                }
                if (c == txtIdentifier) {
                    if (txtStart) {
                        list.add(sb.toString());
                        sb.delete(0, sb.length());
                    } else {
                        txtStart = true;
                    }
                } else if (txtStart) {
                    sb.append(c);
                }
            }
        }
        return list;
    }

}
