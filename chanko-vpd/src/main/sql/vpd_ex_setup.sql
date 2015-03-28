-- 下記を参照
http://www.istudy.ne.jp/training/serial/plsql/101.html
http://www.istudy.ne.jp/training/serial/plsql/102.html

-- 変更分
-- パッケージヘッダ
CREATE OR REPLACE PACKAGE SCOTT.PAC1
IS
   ...
   -- JPAで使う用のコンテキスト設定処理。（追加分）
   PROCEDURE PROC2 (P_DEPTNO IN VARCHAR2);
   -- JPAで使う用のコンテキストクリア処理。
   PROCEDURE PROC3;
   ...
END;
/

-- パッケージ本体
CREATE OR REPLACE PACKAGE BODY SCOTT.PAC1
IS
   ...
   PROCEDURE PROC2 (P_DEPTNO IN VARCHAR2)
   IS
   BEGIN
      -- JPA用なのでdeptnoを引数でもらって設定。
      -- どのユーザが、はJavaアプリ側で判定している。
      DBMS_SESSION.SET_CONTEXT('CTX1','DEPTNO',P_DEPTNO);
   END PROC2;

   PROCEDURE PROC3
   IS
   BEGIN
      -- JPA用に設定したコンテキストをクリアする。
      -- 共有接続プールを使うので念のため。
      DBMS_SESSION.CLEAR_CONTEXT('CTX1','DEPTNO');
   END PROC3;
   ...
END PAC1;
/
