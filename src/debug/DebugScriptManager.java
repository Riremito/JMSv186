/*
    Packet Debug

    パケットのデバッグはNPC会話経由で行うのが簡単だが、特定のUIを開いている状態でパケットを受信しなければならない場合がある
    その場合はNPC会話が不可能なので 獣の肉 (2010001) を利用した場合にスクリプトを呼び出し任意のパケットを受信できるようにしておく
    チャットからスクリプト実行するように作っても良いかもしれない

    リレミト
 */
package debug;

import client.MapleClient;
import config.ServerConfig;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class DebugScriptManager {

    private static final DebugScriptManager instance = new DebugScriptManager();
    private final static ScriptEngineFactory sef = new ScriptEngineManager().getEngineByName("nashorn").getFactory();

    public final static DebugScriptManager getInstance() {
        return instance;
    }

    public boolean executeDebugScript(final MapleClient c) {
        File scriptFile = new File(ServerConfig.script_path + "debug/debug.js");
        FileReader fr = null;

        // ファイルを開く
        try {
            fr = new FileReader(scriptFile);
        } catch (FileNotFoundException ex) {
            ;
        }

        if (fr == null) {
            return false;
        }

        // スクリプト化
        ScriptEngine debug = sef.getScriptEngine();
        CompiledScript compiled = null;

        try {
            compiled = ((Compilable) debug).compile(fr);
            compiled.eval();
        } catch (ScriptException ex) {
            ;
        }

        // ファイルを閉じる
        try {
            fr.close();
        } catch (IOException ex) {
            ;
        }

        if (compiled == null) {
            return false;
        }

        // スクリプトの実行
        DebugScript script = ((Invocable) debug).getInterface(DebugScript.class);

        c.getPlayer().Info("デバッグ中...");
        script.debug(c);

        return true;
    }
}
