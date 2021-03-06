Asakusa Framework DMDLエディター Eclipseプラグイン
==================================================

[Eclipseプラグイン開発の勉強](http://www.ne.jp/asahi/hishidama/home/tech/eclipse/plugin/develop/index.html)として、[Asakusa Framework](http://www.ne.jp/asahi/hishidama/home/tech/asakusafw/index.html)のDMDLエディターを作っていましたが、
DMDLエディターの機能は[Xtext版DMDLエディター](https://github.com/hishidama/xtext-dmdl-editor)に移行しました。
今後はXtext版DMDLエディターのみメンテナンスしていくつもりです。


対応バージョン
--------------

（Windows版のPleiades All in One Eclipse 3.7＋Java7で開発していますが、[Eclipse3.4の本](http://www.ne.jp/asahi/hishidama/home/book/tech.html#Eclipse3.4plugin)を見ながら作っているので）
Eclipse 3.4以降＋Java6で動くと思います。


インストール方法
----------------

Eclipseの「新規ソフトウェアのインストール」で更新サイトに「[http://hishidama.github.com/dmdl-editor-plugin/site/](http://hishidama.github.com/dmdl-editor-plugin/site/)」を指定して下さい。

※「カテゴリー化された項目がありません」というメッセージが出て先に進めない場合は、「項目をカテゴリー別にグループ化(G)」のチェックを外して下さい。


出来ること
----------------

* キーワードに色が付く。
    * 色は設定で変更できる。
* カーソル位置の括弧に対して、対応する括弧が（うっすらと）強調表示される。
* フォールディング（ソース上のブロックを閉じること）が出来る。
    * フォールディング範囲が更新されるのは、ファイル保存時。
* アウトラインが表示される。
    * アウトライン上でデータモデル名やプロパティー名をクリックするとソース上のその位置へ移動する。
    * アウトラインが更新されるのは、ファイル保存時。
* ハイパーリンクが使える。
    * マウスで“別データモデルから持ってきているプロパティー”をクリックすると、定義元へジャンプする。
    * またはF3キー。
* 入力補完が使える。
    * Windowsの場合はCtrl+Space、UNIXの場合はAlt+「/」でキーワードを補完できる。
* ソースの整形が出来る。
    * Ctrl+Shift+Fでソースを整形する。（ただし範囲選択だと挙動が怪しい…）
    * 設定によってインデントのスペース数を変更できる。
* DMDLのエラーチェックが出来る。
    * コンテキストメニューの「DMDL Editor」→「create index / error check」で構文解析・意味解析を行い、エラー箇所のマークおよびインデックスの構築を行う。
        * メニューを実行しなくても、ハイパーリンクやアウトラインを初めて表示する際にインデックス構築を行う。
        * ファイルは保存されている必要がある。
        * ファイルを指定してエラーチェックをした場合は、Asakusa Frameworkのbuild.propertiesに定義されているディレクトリーを対象とする。
            * 別ファイルにあるデータモデルを使用してる場合は そのファイルも同時にチェックしないとエラーになるので、まとめてチェックする。
    * プロパティーによってbuild.propertiesおよびAsakusa Framework本体のDMDLコンパイルに必要なjarファイルを指定できる。
        * デフォルトではAsakusa Framework0.4～0.5のDirect I/OとWindGateを対象にしている。
            * Asakusa Frameworkのバージョンは、pom.xmlの中を見て判断している。
* DMDLのデータモデルおよびプロパティーをウィザードで新規作成することが出来る。
    * コンテキストメニューの「DMDL Editor」→「New DataModel」でウィザードが開く。あるいはメニューバーの「ファイル」→「新規」やツールバー。
* DMDLの属性（@directio.csvとか）の追加/削除が出来る。
    * コンテキストメニューの「DMDL Editor」→「Add/Remove attribute of DataModel」でウィザードが開く。あるいはメニューバーの「ファイル」→「新規」やツールバー。
* DMDLからJavaソースの生成（コンパイル）が出来る。
    * コンテキストメニューの「DMDL Editor」→「compile」でコンパイルを行う。
    * エラーチェックと同様のプロパティーを使ってコンパイル対象を決定している。
* DMDLからImporter/Exporterの雛形クラスを作成することが出来る。
    * コンテキストメニューの「DMDL Editor」→「New importer/exporter class」でウィザードが開く。あるいはメニューバーの「ファイル」→「新規」やツールバー。
        * DMDLのコンパイルによってスケルトンクラス（AbstractHogeCsvInputDescription等）が作られていることが前提。（無いと、生成されたクラスがコンパイルエラーになる）
* JavaソースからDMDLへのハイパーリンクが使える。
    * データモデルクラスのクラス名・メソッド名をCtrlキーを押しながらクリックすると、定義元のDMDLへジャンプする。
    * またはShift+F3キー。
* テーブル形式でデータモデルを表示することが出来る。（作りかけ機能）

今のところ、文字入力される度に全体をパースし直しているので、大きなファイルだと動作が遅くなる可能性があります。

