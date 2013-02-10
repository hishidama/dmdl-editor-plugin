Asakusa Framework DMDLエディター（ひしだま自作版）Eclipseプラグイン
===================================================================

[Eclipseプラグイン開発の勉強](http://www.ne.jp/asahi/hishidama/home/tech/eclipse/plugin/develop/index.html)として、[Asakusa Framework](http://www.ne.jp/asahi/hishidama/home/tech/asakusafw/index.html)のDMDLエディターを作っています。

まだ簡単な部分しか出来ていないしバグもありますが、ひとまず公開してみます。


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
    * Ctrlキーを押しながら“別データモデルから持ってきているプロパティー”をクリックすると、定義元へジャンプする。
* 入力補完が使える。
    * Windowsの場合はCtrl+Space、UNIXの場合はAlt+「/」でキーワードを補完できる。
* ソースの整形が出来る。
    * Ctrl+Shift+Fでソースを整形する。（ただし範囲選択だと挙動が怪しい…）
    * 設定によってインデントのスペース数を変更できる。
* エラーマーカーが表示される。
    * Asakusaプロジェクト内のdmdlファイルであれば、ファイル保存時に構文解析を行ってエラー箇所をマークする。（意味解析はまだ）

今のところ、文字入力される度に全体をパースし直しているので、大きなファイルだと動作が遅くなる可能性があります。

