// CMS v72のソースを流用
package constants;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Properties;

public class BeansConstants {

    private static BeansConstants instance = null;
    private static boolean CANLOG;
    private Properties itempb_cfg = new Properties();
    private final String pachinkoequip[];
    private final String pachinkosetup[];
    private final String consume[];
    private final int piratehatrate;//（百分比 = 固定值）
    private final String goldendograte[];// = （1,2 = 出的概率）
    private final String smallwhitemob[];// > （1,2 = 出的概率  3,4 = 中奖概率）
    private final String bigwhitemob[];// > （1,2 = 出的概率  3,4 = 中奖概率）
    private final String purplemob[];// > （1,2 = 出的概率  3,4 = 中奖概率）
    private final String pinkmob[];// > （1,2 = 出的概率  3,4 = 中奖概率）
    private final String flyingman[];// > （1,2 = 出的概率  3,4 = 中奖概率）
    private final String pirate[];// > （1,2 = 出的概率  3,4 = 中奖概率）
    private final String magician[];// > （1,2 = 出的概率  3,4 = 中奖概率）
    private final String warrior[];// > （1,2 = 出的概率  3,4 = 中奖概率）
    private final String archer[];// > （1,2 = 出的概率  3,4 = 中奖概率）
    private final String queen[];// > （1,2 = 出的概率  3,4 = 中奖概率）
    private final String whitemobaward[];// > （1,2 经验    3,4药水）
    private final String colormobaward[];// > （1,2 经验    3,4药水  5,6equip）
    private final String job5award[];// > （1,2 经验    3,4药水  5,6equip 7,8setup）
    private final String queenaward[];// > （1,2 经验    3,4药水  5,6equip 7,8setup）
    private final int handlepower;//大概范围在500-1000左右
    private final int pachinkoawardrange;//1-10 * pachinkoaward范围 = 给与award

    public BeansConstants() {
        try (InputStreamReader is = new FileReader("beans.properties")) {
            itempb_cfg.load(is);
        } catch (Exception e) {
        }
        pachinkoequip = itempb_cfg.getProperty("ddzb").split(",");
        pachinkosetup = itempb_cfg.getProperty("ddzq").split(",");
        consume = itempb_cfg.getProperty("xhp").split(",");
        piratehatrate = Integer.parseInt(itempb_cfg.getProperty("hymzjl"));
        goldendograte = itempb_cfg.getProperty("hjgjl").split(",");
        bigwhitemob = itempb_cfg.getProperty("dbg").split(",");
        smallwhitemob = itempb_cfg.getProperty("xbg").split(",");
        purplemob = itempb_cfg.getProperty("zsg").split(",");
        pinkmob = itempb_cfg.getProperty("fsg").split(",");
        flyingman = itempb_cfg.getProperty("fx").split(",");
        pirate = itempb_cfg.getProperty("hd").split(",");
        magician = itempb_cfg.getProperty("fs").split(",");
        warrior = itempb_cfg.getProperty("zs").split(",");
        archer = itempb_cfg.getProperty("gjs").split(",");
        queen = itempb_cfg.getProperty("nh").split(",");
        whitemobaward = itempb_cfg.getProperty("bgjl").split(",");
        colormobaward = itempb_cfg.getProperty("sgjl").split(",");
        job5award = itempb_cfg.getProperty("wzyjl").split(",");
        queenaward = itempb_cfg.getProperty("nhjl").split(",");
        handlepower = Integer.parseInt(itempb_cfg.getProperty("ldgj"));
        pachinkoawardrange = Integer.parseInt(itempb_cfg.getProperty("ddjlfw"));

    }

    public int getpachinkoawardrange() {
        return pachinkoawardrange;
    }

    public int gethandlepower() {
        return handlepower;
    }

    public String[] getwhitemobaward() {
        return whitemobaward;
    }

    public String[] getcolormobaward() {
        return colormobaward;
    }

    public String[] getjob5award() {
        return job5award;
    }

    public String[] getqueenaward() {
        return queenaward;
    }

    public String[] getbigwhitemob() {
        return bigwhitemob;
    }

    public String[] getsmallwhitemob() {
        return smallwhitemob;
    }

    public String[] getpurplemob() {
        return purplemob;
    }

    public String[] getpinkmob() {
        return pinkmob;
    }

    public String[] getflyingman() {
        return flyingman;
    }

    public String[] getpirate() {
        return pirate;
    }

    public String[] getmagician() {
        return magician;
    }

    public String[] getwarrior() {
        return warrior;
    }

    public String[] getarcher() {
        return archer;
    }

    public String[] getqueen() {
        return queen;
    }

    public String[] getpachinkoequip() {
        return pachinkoequip;
    }

    public String[] getpachinkosetup() {
        return pachinkosetup;
    }

    public String[] getconsume() {
        return consume;
    }

    public int getpiratehatrate() {
        return piratehatrate;
    }

    public String[] getgoldendograte() {
        return goldendograte;
    }

    public boolean isCANLOG() {
        return CANLOG;
    }

    public void setCANLOG(boolean CANLOG) {
        BeansConstants.CANLOG = CANLOG;
    }

    public static BeansConstants getInstance() {
        if (instance == null) {
            instance = new BeansConstants();
        }
        return instance;
    }

}
