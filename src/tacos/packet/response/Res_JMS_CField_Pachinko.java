package tacos.packet.response;

import odin.handling.channel.handler.PachinkoHandler;
import tacos.client.TacosCharacter;
import tacos.packet.ops.OpsPachinkoPlay;
import java.util.List;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;

// CField_Pachinko (JMS), CMS v72から流用
public class Res_JMS_CField_Pachinko {

    // LP_JMS_PachinkoMessage
    public static ServerPacket PachinkoMessage(int cid, int x, String message) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoMessage);

        sp.Encode4(cid);
        // JMS v186.1 fix
        sp.Encode1(x);
        sp.EncodeStr(message);
        return sp;
    }

    // LP_JMS_PachinkoOpen
    public static ServerPacket PachinkoOpen(TacosCharacter player, int type) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoOpen);

        sp.Encode4(player.getTama());
        sp.Encode1(type);
        return sp;
    }

    // LP_JMS_PachinkoPlay : 黄金狗
    public static ServerPacket PachinkoPlay_GoldenDog(byte mode) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(OpsPachinkoPlay.PachinkoPlay_GoldenDog.get());
        sp.Encode1(mode); // 改变模式
        return sp;
    }

    // LP_JMS_PachinkoPlay : 颜色求进洞, 进洞次数 最多有7个
    public static ServerPacket PachinkoPlay_HoleCount(int count, int fast_spin, int blue, int green, int red) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(OpsPachinkoPlay.PachinkoPlay_HoleCount.get());
        sp.Encode1(count);
        sp.Encode1(fast_spin); // 快速转动
        sp.Encode1(blue); // 蓝？
        sp.Encode1(green); // 绿？
        sp.Encode1(red); // 红？
        return sp;
    }

    // LP_JMS_PachinkoPlay : 进洞旋转, 进洞后开始旋转图片
    public static ServerPacket PachinkoPlay_Spin(int count, int row1, int row3, int row2, int mob_effect, int win_rate, int fast_spin, boolean hide_effect_a, boolean hide_effect_b) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(OpsPachinkoPlay.PachinkoPlay_Spin.get());
        sp.Encode1(count);
        sp.Encode1(row1); // 第一排
        sp.Encode1(row3); // 第三排
        sp.Encode1(row2); // 第二排
        sp.Encode1(mob_effect); // 开启情况下出现怪物打框
        if (mob_effect > 0) {
            sp.Encode1(win_rate); // 中奖率？？%
            sp.Encode4(0); // 未知
        }
        sp.Encode1(fast_spin); // 加速旋转
        sp.Encode1(hide_effect_a ? 1 : 0);
        sp.Encode1(hide_effect_b ? 1 : 0);
        return sp;
    }

    // LP_JMS_PachinkoPlay : 奖励豆豆效果, 豆豆进洞后奖励的
    public static ServerPacket PachinkoPlay_Reward(boolean type, int count) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(type ? OpsPachinkoPlay.PachinkoPlay_Reward.get() : OpsPachinkoPlay.PachinkoPlay_RewardB.get()); // 05 08 都是加豆豆
        sp.Encode4(count); // 奖励豆豆的数量
        sp.Encode1(5);
        return sp;
    }

    // LP_JMS_PachinkoPlay : 领奖NPC, %s。请拿到凯瑟琳处确认。
    public static ServerPacket PachinkoPlay_RewardItem(int item_id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(OpsPachinkoPlay.PachinkoPlay_RewardItem.get());
        sp.Encode4(item_id);
        return sp;
    }

    // LP_JMS_PachinkoPlay : 开始打豆豆
    public static ServerPacket PachinkoPlay_Start(List<PachinkoHandler.Bean> beansInfo) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoPlay);

        sp.Encode1(OpsPachinkoPlay.PachinkoPlay_Start.get());
        sp.Encode1(beansInfo.size());
        for (PachinkoHandler.Bean bean : beansInfo) {
            sp.Encode2(bean.getPos());
            sp.Encode1(bean.getType());
            sp.Encode4(bean.getNumber());
        }
        return sp;
    }

    // LP_JMS_PachinkoUpdate, 能量储存器
    public static ServerPacket PachinkoUpdate(int beansCount) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoUpdate);

        sp.Encode4(beansCount);
        return sp;
    }
}
