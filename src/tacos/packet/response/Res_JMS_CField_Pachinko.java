package tacos.packet.response;

import odin.client.MapleBeans;
import odin.client.MapleCharacter;
import tacos.network.MaplePacket;
import odin.handling.channel.handler.BeanGame;
import java.util.List;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;

// CField_Pachinko
public class Res_JMS_CField_Pachinko {

    // CMS v72から流用
    public static MaplePacket BeansGameMessage(int cid, int x, String laba) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoMessage);

        sp.Encode4(cid);
        // JMS v186.1 fix
        sp.Encode1(x);
        sp.EncodeStr(laba);
        return sp.get();
    }

    public static MaplePacket openBeans(MapleCharacter c, int type) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoOpen);

        sp.Encode4(c.getTama());
        sp.Encode1(type);
        return sp.get();
    }

    public static MaplePacket BeansZJgeiddB(int a) {
        //豆豆进洞后奖励的
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(BeanGame.BeansType.奖励豆豆效果B.getType()); //类型 05   08  都是加豆豆···
        sp.Encode4(a); //奖励豆豆的数量
        sp.Encode1(0); //未知效果
        return sp.get();
    }

    public static MaplePacket BeansHJG(byte type) {
        //黄金狗
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(BeanGame.BeansType.黄金狗.getType()); //类型
        sp.Encode1(type); //改变模式
        return sp.get();
    }

    public static MaplePacket BeansJDCS(int a, int 加速旋转, int 蓝, int 绿, int 红) {
        //进洞次数 最多有7个
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(BeanGame.BeansType.颜色求进洞.getType());
        sp.Encode1(a); //
        sp.Encode1(加速旋转); //快速转动
        sp.Encode1(蓝); // 蓝？
        sp.Encode1(绿); // 绿？
        sp.Encode1(红); // 红？
        return sp.get();
    }

    public static MaplePacket BeansJDXZ(int a, int 第一排, int 第三排, int 第二排, int 启动打怪效果, int 中奖率, int 加速旋转, boolean 关闭打击效果A, boolean 关闭打击效果B) {
        //进洞后开始旋转图片
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(BeanGame.BeansType.进洞旋转.getType()); //类型
        sp.Encode1(a);
        sp.Encode1(第一排); //第一排
        sp.Encode1(第三排); //第三排
        sp.Encode1(第二排); //第二排
        sp.Encode1(启动打怪效果); //开启情况下出现怪物打框

        if (启动打怪效果 > 0) {
            sp.Encode1(中奖率); //中奖率？？%
            sp.Encode4(0); //未知
        }

        sp.Encode1(加速旋转); //加速旋转
        sp.Encode1(关闭打击效果A ? 1 : 0); //boolean
        sp.Encode1(关闭打击效果B ? 1 : 0); //boolean
        return sp.get();
    }

    public static MaplePacket BeansZJgeidd(boolean type, int a) {
        //豆豆进洞后奖励的
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(type ? BeanGame.BeansType.奖励豆豆效果.getType() : BeanGame.BeansType.奖励豆豆效果B.getType()); //类型 05   08  都是加豆豆···
        sp.Encode4(a); //奖励豆豆的数量
        sp.Encode1(5);
        return sp.get();
    }

    public static MaplePacket Beans_why() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(BeanGame.BeansType.未知效果.getType()); //类型
        return sp.get();
    }

    public static MaplePacket BeansUP(int ITEM) {
        //%s。请拿到凯瑟琳处确认。
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(BeanGame.BeansType.领奖NPC.getType()); //类型
        sp.Encode4(ITEM);
        return sp.get();
    }

    public static MaplePacket showBeans(List<MapleBeans> beansInfo) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(BeanGame.BeansType.开始打豆豆.getType());
        sp.Encode1(beansInfo.size());
        for (MapleBeans bean : beansInfo) {
            sp.Encode2(bean.getPos());
            sp.Encode1(bean.getType());
            sp.Encode4(bean.getNumber());
        }
        return sp.get();
    }

    public static MaplePacket updateBeans(int beansCount) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoUpdate);

        sp.Encode4(beansCount);
        return sp.get();
    }

    public static MaplePacket 能量储存器(int beansCount) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoUpdate);

        sp.Encode4(beansCount);
        return sp.get();
    }

}
