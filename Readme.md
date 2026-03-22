# JMS エミュ鯖
## Readme
+ [日本語](./Readme.md)
+ [English](./Readme_EN.md)

## 開発環境
+ OS
    + Windows
+ Java
    + [JDK 25 : jdk-25_windows-x64_bin.exe](https://www.oracle.com/jp/java/technologies/downloads/#jdk25-windows)
+ IDE
    + [NetBeans 29 : Apache-NetBeans-29.exe](https://netbeans.apache.org/front/main/download/nb29/)
    + [日本語化 : org-apache-netbeans-localise-ja-0.0.4.nbm](https://github.com/junichi11/netbeans-translations-ja/releases)
+ Database
    + [Wampserver : wampserver3.4.0_x64.exe](https://wampserver.aviatechno.net/)
        + MySQL 8.4.7
        + Apache 2.4.65
        + PHP 8.3.28
        + phpMyAdmin 5.2.3

## 実行環境
+ OS
    + Windows or Linux
+ Java
    + JDK 25
+ Database
    + MySQL 8

## ビルド準備
## ソースコード
+ このリポジトリをクローンする。
    + developブランチを推奨します。
+ `JMSv186` という名前のフォルダであるものとして以下の手順を進めていく。
### wz_xml
+ 利用するバージョンのXMLを https://github.com/Riremito/wz_xml から取得し、解凍を行う。
+ JMS147の場合は `JMSv186/wz_xml/xml_JMS_v147/` に必要なファイルを設置する。
### scripts
+ Scriptを https://github.com/Riremito/jms_scripts から取得する。
+ `JMSv186/scripts/scripts_jms/` に必要なファイルを設置する。
### データベース
+ Wampserverを実行し、phpmyadminへブラウザからアクセスする。
    + ユーザー名 `root`, パスワード `空欄` で管理画面へ入れます。
+ JMS147の場合は `jms_v147` という名前のデータベースを作成する。
+ 作成したデータベースに `JMSv186/sql` フォルダ内のファイルを以下の順序でインポートする。
    + `jms_v147_empty.sql`
    + `init_data_set.sql`
        + こちらはインポートしなくても問題ない。

## ビルド
+ NetBeansでプロジェクトを開く。
+ プロパティを開き、ライブラリにClassPathに `JMSv186/lib` フォルダに存在する全ての.jarファイルを追加する。
+ ビルドする。

## サーバーの実行方法
### Windows
+ `run_JMS_v147.0.bat` 等実行したいバージョンの `.bat` ファイルを実行する。
    + `run_any.bat JMS 147 0` など `run_any.bat` に引数を渡すことでも実行可能。
### Linux
+ `bash run_any.sh JMS 147 0` など `run_any.sh` に引数を渡すことで実行可能。

## サーバー仕様
### アカウント
+ 自動登録
+ 性別変更方法
    + ログインする時に `MapleID` と`MapleID_` は同じ扱いとなります
    + `MapleID` だと男キャラクターの作成が可能で、 `MapleID_` だと女キャラクターの作成が可能です。
### コマンド
+ TODO

## クライアント
### 入手方法
+ フルクライアントを入手する
    + Web Archiveや[ragezone](https://forum.ragezone.com/threads/maplestory-client-localhost-archive.1101897/)で探す
+ LocalHostクライアントを入手する
    + [ragezone](https://forum.ragezone.com/threads/some-localhost-clients-kms-jms-cms-twms.1225637/)で探す
+ `JMS_v147.0_L.exe` などのLocalHostクライアントをフルクライアントのフォルダに入れる。
### 実行方法
+ LocalHostクライアントを実行する。
### その他
+ [バグ修正と起動高速化ツール](https://github.com/Riremito/iGPUplz)
    + JMS187以下で画面が崩壊する場合は必須
    + JMS188以上で起動速度を高速化したい場合もオススメ