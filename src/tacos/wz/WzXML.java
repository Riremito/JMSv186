/*
 * Copyright (C) 2025 Riremito
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */
package tacos.wz;

import tacos.property.Property_Java;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import tacos.debug.DebugLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import tacos.config.ContentCustom;
import java.util.AbstractMap.SimpleImmutableEntry;

/**
 *
 * @author Riremito
 */
public class WzXML {

    public static final CharacterWz CHARACTER = new CharacterWz();
    // Effect
    public static final EtcWz ETC = new EtcWz();
    public static final ItemWz ITEM = new ItemWz();
    // List
    public static final MapWz MAP = new MapWz();
    public static final MobWz MOB = new MobWz();
    // Morph
    public static final NpcWz NPC = new NpcWz();
    public static final QuestWz QUEST = new QuestWz();
    public static final ReactorWz REACTOR = new ReactorWz();
    public static final SkillWz SKILL = new SkillWz();
    public static final SoundWz SOUND = new SoundWz();
    public static final StringWz STRING = new StringWz();
    // TamingMob
    public static final UIWz UI = new UIWz();

    private static List<SimpleImmutableEntry<String, MapleData>> xml_cache = new ArrayList<>();
    private String root_path = null;
    private File root_dir;
    protected DirectoryEntry rootDirectory;

    public WzXML(String path) {
        this.root_path = path;
        setWzRoot();
    }

    private boolean setWzRoot() {
        File file = new File(Property_Java.getDir_WzXml() + "/" + this.root_path);

        if (!file.exists()) {
            DebugLogger.XmlLog("setWzRoot : path not found, " + this.root_path);
            return false;
        }

        if (!file.isDirectory()) {
            DebugLogger.XmlLog("setWzRoot : path is not a directory, " + this.root_path);
            return false;
        }

        DebugLogger.XmlLog("setWzRoot : " + this.root_path);

        this.root_dir = file;
        this.rootDirectory = new DirectoryEntry(this.root_dir.getName(), 0, 0, null);
        createEntry(this.root_dir, this.rootDirectory);
        return true;
    }

    private void createEntry(File dir, DirectoryEntry entry) {
        for (File file : dir.listFiles()) {
            String fn = file.getName();
            if (fn.endsWith(".img")) {
                DebugLogger.XmlLog("what's this1? " + file.getName());
                continue;
            }
            if (file.isDirectory()) {
                DirectoryEntry sub_entry = new DirectoryEntry(fn, 0, 0, entry);
                entry.addDirectory(sub_entry);
                createEntry(file, sub_entry);
                continue;
            }
            if (fn.endsWith(".xml")) {
                String fn_img = fn.substring(0, fn.length() - 4);
                entry.addFile(new Entry(fn_img, 0, 0, entry));
                continue;
            }
            DebugLogger.XmlLog("what's this2? " + file.getName());
        }
    }

    private static void addXmlCache(String data_path, MapleData md) {
        xml_cache.add(new SimpleImmutableEntry<>(data_path, md));
    }

    private static MapleData getXmlCache(String data_path) {
        for (SimpleImmutableEntry<String, MapleData> pair : xml_cache) {
            if (pair.getKey().equals(data_path)) {
                return pair.getValue();
            }
        }
        return null;
    }

    private FileInputStream getCustomData(String data_path) {
        if (!ContentCustom.CC_WZ_MAP_ADDED.get()) {
            return null;
        }

        int dir_left = data_path.indexOf("/");
        int dir_right = data_path.indexOf("/", dir_left + 1);
        String path_dir = data_path.substring(dir_left, dir_right + 1);
        String pathCustom = data_path;
        pathCustom = pathCustom.replace(path_dir, "/Custom/");
        File dataFileCustom = new File(this.root_dir, pathCustom + ".xml");

        try {
            FileInputStream fis = new FileInputStream(dataFileCustom);
            DebugLogger.XmlLog("custom wz: " + pathCustom + ".xml");
            return fis;

        } catch (FileNotFoundException ex) {

        }
        DebugLogger.XmlLog("custom wz error: " + pathCustom + ".xml");
        return null;
    }

    public MapleData getData(String data_path) {
        String full_path = this.root_path + "/" + data_path;
        // data is already loaded.
        MapleData md_cache = getXmlCache(full_path);
        if (md_cache != null) {
            //DebugLogger.XmlLog("getData : cached, " + path);
            return md_cache;
        }
        // new data.
        DebugLogger.XmlLog("getData : " + full_path);

        File dataFile = new File(this.root_dir, data_path + ".xml");
        if (!dataFile.exists()) {
            DebugLogger.XmlLog("getData : " + data_path + ".xml");
            return null;
        }
        File imageDataDir = new File(this.root_dir, data_path);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(dataFile);
        } catch (FileNotFoundException ex) {
            if (ContentCustom.CC_WZ_MAP_ADDED.get()) {
                fis = getCustomData(data_path);
            }
        }

        // not found.
        if (fis == null) {
            addXmlCache(full_path, null);
            return null;
        }

        XmlDomData domMapleData = new XmlDomData(fis, imageDataDir.getParentFile());

        try {
            fis.close();
        } catch (IOException ex) {
        }

        addXmlCache(full_path, domMapleData);
        return domMapleData;
    }

    public MapleDataDirectoryEntry getRootDirectory() {
        return this.rootDirectory;
    }

    public MapleDataDirectoryEntry getSubDirectory(String path) {
        return this.rootDirectory.getSubDirectory(path);
    }

    public List<MapleDataEntity> getSubDirectoryFiles(String path) {
        return this.rootDirectory.getSubDirectory(path).getFiles();
    }

    public enum DataType {

        NONE,
        IMG_0x00,
        SHORT,
        INT,
        FLOAT,
        DOUBLE,
        STRING,
        EXTENDED,
        PROPERTY,
        CANVAS,
        VECTOR,
        CONVEX,
        SOUND,
        UOL,
        UNKNOWN_TYPE,
        UNKNOWN_EXTENDED_TYPE;
    }

    public static class Entry implements MapleDataEntity {

        private String name;
        private MapleDataEntity parent;

        public Entry(String name, int size, int checksum, MapleDataEntity parent) {
            this.name = name;
            this.parent = parent;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public MapleDataEntity getParent() {
            return parent;
        }
    }

    public static class DirectoryEntry extends Entry implements MapleDataDirectoryEntry {

        private List<MapleDataDirectoryEntry> subdirs = new ArrayList<>();
        private List<MapleDataEntity> files = new ArrayList<>();
        private Map<String, MapleDataEntity> entries = new HashMap<>();

        public DirectoryEntry(String name, int size, int checksum, MapleDataEntity parent) {
            super(name, size, checksum, parent);
        }

        public DirectoryEntry() {
            super(null, 0, 0, null);
        }

        public void addDirectory(MapleDataDirectoryEntry dir) {
            subdirs.add(dir);
            entries.put(dir.getName(), dir);
        }

        public void addFile(MapleDataEntity fileEntry) {
            files.add(fileEntry);
            entries.put(fileEntry.getName(), fileEntry);
        }

        @Override
        public List<MapleDataDirectoryEntry> getSubDirectories() {
            return Collections.unmodifiableList(subdirs);
        }

        @Override
        public MapleDataDirectoryEntry getSubDirectory(String path) {
            for (MapleDataDirectoryEntry mde : this.subdirs) {
                if (mde.getName().equals(path)) {
                    return mde;
                }
            }

            return null;
        }

        @Override
        public List<MapleDataEntity> getFiles() {
            return Collections.unmodifiableList(files);
        }

        @Override
        public MapleDataEntity getEntry(String name) {
            return entries.get(name);
        }
    }

    public static class XmlDomData implements MapleData {

        private Node node;
        private File imageDataDir;

        private XmlDomData(Node node) {
            this.node = node;
        }

        public XmlDomData(FileInputStream fis, File imageDataDir) {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(fis);
                this.node = document.getFirstChild();

            } catch (ParserConfigurationException | IOException e) {
                throw new RuntimeException(e);
            } catch (SAXException e) {
                //throw new RuntimeException(e);
            }
            this.imageDataDir = imageDataDir;
        }

        @Override
        public MapleData getChildByPath(String path) {
            String segments[] = path.split("/");
            if (segments[0].equals("..")) {
                return ((MapleData) getParent()).getChildByPath(path.substring(path.indexOf("/") + 1));
            }

            Node myNode = node;
            for (String segment : segments) {
                NodeList childNodes = myNode.getChildNodes();
                boolean foundChild = false;
                for (int i = 0; i < childNodes.getLength(); i++) {
                    try {
                        Node childNode = childNodes.item(i);
                        if (childNode != null && childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getAttributes().getNamedItem("name").getNodeValue().equals(segment)) {
                            myNode = childNode;
                            foundChild = true;
                            break;
                        }
                    } catch (NullPointerException e) {
                    }
                }
                if (!foundChild) {
                    return null;
                }
            }
            XmlDomData ret = new XmlDomData(myNode);
            ret.imageDataDir = new File(imageDataDir, getName() + "/" + path).getParentFile();
            return ret;
        }

        @Override
        public List<MapleData> getChildren() {
            List<MapleData> ret = new ArrayList<>();
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode != null && childNode.getNodeType() == Node.ELEMENT_NODE) {
                    XmlDomData child = new XmlDomData(childNode);
                    child.imageDataDir = new File(imageDataDir, getName());
                    ret.add(child);
                }
            }
            return ret;
        }

        @Override
        public Object getData() {
            NamedNodeMap attributes = node.getAttributes();
            DataType type = getType();
            switch (type) {
                case DOUBLE: {
                    return Double.valueOf(attributes.getNamedItem("value").getNodeValue());
                }
                case FLOAT: {
                    return Float.valueOf(attributes.getNamedItem("value").getNodeValue());
                }
                case INT: {
                    return Integer.valueOf(attributes.getNamedItem("value").getNodeValue());
                }
                case SHORT: {
                    return Short.valueOf(attributes.getNamedItem("value").getNodeValue());
                }
                case STRING:
                case UOL: {
                    return attributes.getNamedItem("value").getNodeValue();
                }
                case VECTOR: {
                    return new Point(Integer.parseInt(attributes.getNamedItem("x").getNodeValue()), Integer.parseInt(attributes.getNamedItem("y").getNodeValue()));
                }
                default: {
                    break;
                }
            }
            return null;
        }

        @Override
        public DataType getType() {
            String nodeName = node.getNodeName();
            switch (nodeName) {
                case "imgdir":
                    return DataType.PROPERTY;
                case "canvas":
                    return DataType.CANVAS;
                case "convex":
                    return DataType.CONVEX;
                case "sound":
                    return DataType.SOUND;
                case "uol":
                    return DataType.UOL;
                case "double":
                    return DataType.DOUBLE;
                case "float":
                    return DataType.FLOAT;
                case "int":
                    return DataType.INT;
                case "short":
                    return DataType.SHORT;
                case "string":
                    return DataType.STRING;
                case "vector":
                    return DataType.VECTOR;
                case "null":
                    return DataType.IMG_0x00;
                default:
                    break;
            }
            return null;
        }

        @Override
        public MapleDataEntity getParent() {
            Node parentNode = node.getParentNode();
            if (parentNode.getNodeType() == Node.DOCUMENT_NODE) {
                return null; // can't traverse outside the img file - TODO is this a problem?
            }
            XmlDomData parentData = new XmlDomData(parentNode);
            parentData.imageDataDir = imageDataDir.getParentFile();
            return parentData;
        }

        @Override
        public String getName() {
            return node.getAttributes().getNamedItem("name").getNodeValue();
        }

        @Override
        public Iterator<MapleData> iterator() {
            return getChildren().iterator();
        }

        // read data.
        @Override
        public int getIntExpression(String path, int common_level) {
            return getIntExpression(path, common_level, 0);
        }

        @Override
        public int getIntExpression(String path, int common_level, int def) {
            return dataToIntExpression(getChildByPath(path), common_level, def);
        }

        @Override
        public int getInt(String path) {
            return dataToInt(getChildByPath(path), 0);
        }

        @Override
        public int getInt(String path, int def) {
            return dataToInt(getChildByPath(path), def);
        }

        @Override
        public String getString(String path) {
            return dataToString(getChildByPath(path), "");
        }

        @Override
        public String getString(String path, String def) {
            return dataToString(getChildByPath(path), def);
        }

        @Override
        public Float getFloat(String path) {
            return getFloat(path, 0.0f);
        }

        @Override
        public Float getFloat(String path, float def) {
            return dataToFloat(getChildByPath(path), def);
        }

        @Override
        public Double getDouble(String path) {
            return getDouble(path, 0.0);
        }

        @Override
        public Double getDouble(String path, double def) {
            return dataToDouble(getChildByPath(path), def);
        }

        @Override
        public Point getPoint(String path) {
            return dataToPoint(getChildByPath(path));
        }

        @Override
        public long getLong(String path) {
            return getLong(path, 0L);
        }

        @Override
        public long getLong(String path, long def) {
            return dataToLong(getChildByPath(path), def);
        }

        // convert data.
        // PostBB Skill.wz
        private static int dataToIntExpression(MapleData data, int common_level, int def) {
            if (common_level == 0) {
                return dataToInt(data, def);
            }
            if (data == null) {
                //DebugLogger.XmlDataLog(null, "getIntExpression");
                return def;
            }
            if (data.getType() != DataType.STRING) {
                return dataToInt(data, def);
            }
            // post bb
            return evalIntExpression((String) data.getData(), common_level);
        }

        // get data.
        private static int dataToInt(MapleData data, int def) {
            if (data == null) {
                //DebugLogger.XmlDataLog(null, "getInt");
                return def;
            }
            Object ret = data.getData();
            if (ret == null) {
                DebugLogger.XmlDataLog(data, "getInt def");
                return def;
            }

            switch (data.getType()) {
                case SHORT:
                    DebugLogger.XmlDataLog(data, "getInt = short");
                    return (Short) ret;
                case INT: {
                    return (Integer) ret;
                }
                case FLOAT: {
                    return ((Float) ret).intValue();
                }
                case DOUBLE: {
                    return ((Double) ret).intValue();
                }
                case STRING: {
                    DebugLogger.XmlDataLog(data, "getInt = string");
                    return Integer.parseInt((String) ret);
                }
                default: {
                    DebugLogger.XmlDataLog(data, "getInt = others");
                    break;
                }
            }

            return (Integer) ret;
        }

        // for Map.wz, 749050100.img, info/decHP, pinkbean cake map
        private static long dataToLong(MapleData data, long def) {
            if (data == null) {
                DebugLogger.XmlDataLog(null, "getLong");
                return def;
            }
            if (data.getType() != DataType.STRING) {
                return (long) dataToInt(data, (int) def);
            }
            String ret = (String) data.getData();
            if (ret == null) {
                DebugLogger.XmlDataLog(data, "getLong def");
                return def;
            }
            return Long.parseLong(ret);
        }

        // for Map.wz, info/recovery
        private static Float dataToFloat(MapleData data, float def) {
            if (data == null) {
                DebugLogger.XmlDataLog(null, "getFloat");
                return def;
            }
            Float ret = (Float) data.getData();
            if (ret == null) {
                DebugLogger.XmlDataLog(data, "getFloat def");
                return def;
            }
            return (Float) data.getData();
        }

        // for Item.wz, unitPrice star/bullet
        private static Double dataToDouble(MapleData data, double def) {
            if (data == null) {
                DebugLogger.XmlDataLog(null, "getDouble");
                return def;
            }
            Double ret = (Double) data.getData();
            if (ret == null) {
                DebugLogger.XmlDataLog(data, "getDouble def");
                return def;
            }
            return ret;
        }

        private static String dataToString(MapleData data, String def) {
            if (data == null) {
                //DebugLogger.XmlDataLog(null, "getString");
                return def;
            }
            String ret = (String) data.getData();
            if (ret == null) {
                DebugLogger.XmlDataLog(data, "getString def");
                return def;
            }
            return ret;
        }

        // for Reactor.wz, lt, rb
        private static Point dataToPoint(MapleData data) {
            if (data == null) {
                DebugLogger.XmlDataLog(null, "getPoint");
                return null;
            }
            Point ret = (Point) data.getData();
            if (ret == null) {
                DebugLogger.XmlDataLog(data, "getPoint def");
                return null;
            }
            return ret;
        }

        // expression for post BB Skill.wz.
        private static int evalIntExpression(String val, int level) {
            // not an expression.
            if (val.matches("-?\\d+")) {
                return Integer.parseInt(val);
            }
            // expression for post BB Skill.wz. (JMS187+)

            // EMS89 fix.
            if (val.contains("=")) {
                DebugLogger.ErrorLog("TacosWzExpression : = is found. " + val);
                val = val.replace("=", "");
            }

            ExpressionBuilder builder = new ExpressionBuilder(val);
            builder.variable("x"); // x : skill level.
            builder.variable("y"); // y : skill level? (KMS169)

            // thank you teto and chronos.
            Function ceil = new Function("u", 1) {
                @Override
                public double apply(double... doubles) {
                    return Math.ceil(doubles[0]);
                }
            };

            Function floor = new Function("d", 1) {
                @Override
                public double apply(double... doubles) {
                    return Math.floor(doubles[0]);
                }
            };

            // allowed, space, ()*+, -./0123456789, duxy
            if (!val.matches("[\\s\\(-\\+\\--9duxy]+")) {
                DebugLogger.ErrorLog("TacosWzExpression : " + val);
                System.exit(0);
            }

            builder.function(ceil); // u : 切り上げ
            builder.function(floor); // d : 切り捨て
            Expression expression = builder.build();

            expression.setVariable("x", level);
            expression.setVariable("y", level);

            int ret = (int) expression.evaluate();
            return ret;
        }
    }
}
