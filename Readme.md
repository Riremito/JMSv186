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
+ C++
    + Visual Studio 2017
        + 今は不要
    + Windows SDK 10.0.17763.0
        + 今は不要


### ビルド
+ このリポジトリをクローン
+ NetBeansを実行して以下の手順でビルド
    + ファイル -> 新規プロジェクト -> カテゴリ = Java, プロジェクト = 既存のソースを使用するJavaプロジェクト
    + プロジェクト名 = v186 (なんでもOK), プロジェクトフォルダ = v186.sqlがある階層のディレクトリ
    + ソース・パッケージフォルダ = src
    + 一旦プロジェクトが読み込まれるので続ける
    + 右クリックからプロパティ
    + ライブラリを選択 -> JAR/フォルダの追加 = libディレクトリに.jarファイルがあるので全て選択して追加
    + 追加したプロジェクトを右クリックしてビルド

### DB設定
+ Wampを実行
+ タスクアイコンクリックするとメニューが出るので以下をする
    + Start all Services
    + phpMyAdminを開く
+ ユーザー名root, パスワード空欄で管理画面へ入る
+ インポートを選択しv186.sqlを指定して実行ボタンを押す
+ なんかエラーが出るが無視

### サーバーの起動
+ launch_server.batを実行
    + Fully Initializedと出れば問題なし
    + クライアントが接続してくるとIoSession opened /127.0.0.1と表示される

### クライアントの準備
+ ファイル入手
    + v186のクライアントを用意
        + https://msdl.xyz/pages/jms/setups
            + 誰かがアップロードしてくれているのでそれを利用
    + [エミュ鯖へ接続させるためのツール](https://github.com/Riremito/RunEmu/releases)
+ 設置
    + v186クライアントをインストール
    + v186クライアントのフォルダに以下を設置
        + Emu.dll
        + RunEmu.exe
        + Emu.txt
            + 必要に応じてIPを設定する
+ 実行
    + RunEmu.exeを実行するとローカルで動作する

### サーバーの停止
+ CTRL+Cで停止するか聞かれるのでyで終了

## その他
### サーバー仕様
+ デフォルトGMアカウント
    + ID: riremito
    + ぱすわーど: riresaba
    + 2次パス: 777777
    + DB書き換えないとGMとか作れないので元から用意しておきました
+ アカウント登録
    + 自動登録
    + 2次パスは777777固定
        + 2次パス処理削除すれば不要です

### クライアント側ソース
+ https://github.com/Riremito/RunEmu
