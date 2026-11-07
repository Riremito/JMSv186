/*
 * Copyright (C) 2026 Riremito
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
package tacos.script.quest;

/**
 *
 * @author Riremito
 */
// Quest.wz startscript : q3360s (JMS186), 暗証番号の認証, lua の処理を利用
public class Quest_Magatia_VerifyingThePassword extends QuestScriptMan {

    private final int quest_password = 7061; // lua の qr.set( 7061, pass2 ), 秘密の通路のキー
    private final int quest_password_input = 7062; // lua の qr.set( 7062, "00" )
    private final String password_chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final int password_length = 10;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 0) {
            // 翻訳
            say("早く早く。覚える自信がないならペンでも出したまえ！");
            return false;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                say("おお！来たのかい？ちょうどいいところに来た。君のために、このパーウェンが秘密の通路に出入りできるマスターキーを突き止めたんだ！はっはっはっは！すごいだろう？早くすごいと言いたまえ！", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                askAccept("さあ。キーはとても長くて複雑だから、よく覚えておいてくれ。一度しか言わないから、どこかに書いておくといい。準備はいいかい？");
                return true;
            }
            case 2 -> {
                String password = shuffle(this.password_chars).substring(0, this.password_length);
                setQuestData(this.quest_password, password);
                setQuestData(this.quest_password_input, "00");
                // lua の qr.set( 3360, "0" ), qr.setState( 3360, 1 )
                forceStartQuest(getQuestId(), "0");
                // 翻訳
                say("キー番号は#b" + password + "#kだ。忘れていないだろうね？このキーを秘密の通路の入口に入力すれば、秘密の通路を自由に使えるようになるよ。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
