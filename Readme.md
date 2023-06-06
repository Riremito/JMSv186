# JMSv186
## サーバーを動作させるまでの流れ
### 開発環境
+ Java
    + jre-8u111-windows-x64
        + 適当に入手してください
    + jdk-8u111-nb-8_2-windows-x64
        + 適当に入手してください
    + jce_policy-8
        + AESが動作しないので以下を参考にJDK/JREのファイルを上書きしてください
            + [参考](https://qiita.com/mizuki_takahashi/items/cc26a7fd51aa04396e92)
        + アカウント作成しないとダウンロード出来ないので注意
    + NetBeans IDE 8.2
        + 適当に入手してください
+ DB
    + Wampserver2.4-x64.exe
        + 適当に入手してください
+ MySQL
    + 5.6.12
        + Wampに付属しているバージョン
+ phpMyAdmin
    + 4.0.4
        + Wampに付属しているバージョン

### ビルド
+ このリポジトリをクローン
+ NetBeansを実行して以下の手順でビルド
    + ファイル -> 新規プロジェクト -> カテゴリ = Java, プロジェクト = 既存のソースを使用するJavaプロジェクト
    + プロジェクト名 = v186 (なんでもOK)
    + ソース・パッケージフォルダ = src
    + 一旦プロジェクトが読み込まれるので続ける
    + 右クリックからプロパティ
    + ライブラリを選択 -> JAR/フォルダの追加 = libディレクトリに.jarファイルがあるので全て選択して追加
    + 追加したプロジェクトを右クリックしてビルド

### データとスクリプトの入手
+ jms_wz
    + 以下のリポジトリから取得する方法
        + `git clone https://github.com/Riremito/jms_wz`
    + もしくはHaRepackerで生成する
+ jms_scripts
    + 以下のリポジトリから取得する方法
    + `git clone https://github.com/Riremito/jms_scripts`

### DB設定
+ Wampを実行
+ タスクアイコンクリックするとメニューが出るので以下をする
    + Start all Services
    + phpMyAdminを開く
+ ユーザー名root, パスワード空欄で管理画面へ入る
+ データベースを選択し、`v186`という名前で作成を行う
+ v186を開いた後以下のインポートを行う
    + `v186_empty.sql`を指定して実行ボタンを押す
    + `init_data_set.sql`を指定して実行ボタンを押す

### サーバーの起動
+ JMS v186.1を実行する場合
    + run.batを実行
+ 別バージョンのサーバーを実行する場合
    + 以下のようにrun_any.batにバージョンを引数として渡して実行する
        + `run_any.bat 188 0`

### クライアントの準備
+ ファイル入手
    + https://github.com/Riremito/EmuClient
        + JMSのクライアントを実行可能にするためのツール
    + https://github.com/Riremito/LocalHost
        + クライアントの接続先を127.0.0.1等任意に変更するためのツール
    + https://github.com/Riremito/FixThemida
        + 古いクライアントをWindows 10で実行可能にするためのツール
            + v164前後のバージョンのクライアントはWindows 10に対応しておらずこれを利用しないと実行出来ないので注意

+ 実行
    + RunEmu.exeを実行するとローカルで動作する
        + 初回はRunEmu.exeにMapleStory.exeをドロップする必要があります

## その他
### サーバー仕様
+ DB
    + バージョンごとにDBを持っているのでv186のようにバージョン名のDBを作成すると、自動的にそれが利用されます
+ wz
    + jms_wzフォルダにバージョンごとにwzのxmlを持っているので、バージョンごとに用意が必要です
+ MapleID
    + 自動登録 (Auto Register)
        + 存在しないアカウントを入力した場合は自動的にMapleIDが新規作成されます
    + 2次パスワードは削除済み
        + 内部的に777777固定となっていますが、使うことはないはず
    + MapleIDの末尾に`_`を付けると一時的にアカウントの性別が女となり、女キャラクターの作成画面へ入れる
+ GM
    + DBからaccountのgmに111を書き込み、charactersのgmに111と書き込むとGM状態になる