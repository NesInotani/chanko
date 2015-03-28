package com.github.chanko.vpd.entity;

import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

public class VpdSessionAdaptor extends SessionEventAdapter {

	/**
	 * DB接続取得後に呼び出されるイベントハンドラ。<br/>
	 * deptnoプロパティが設定されていれば、DBのセッションコンテキストにdeptnoを設定する。<br/>
	 * されていない場合はDBのセッションコンテキストをクリアする。（以前のコンテキスト設定が残っていた場合の念のための処置）
	 */
	@Override
	public void postAcquireExclusiveConnection(SessionEvent event) {
		String deptno = (String) event.getSession().getProperty("deptno");
		SQLCall call;
		if (deptno != null) {
			call = new SQLCall("CALL SCOTT.PAC1.PROC2('" + deptno + "')");
		} else {
			call = new SQLCall("CALL SCOTT.PAC1.PROC3()");
		}
		event.getSession().executeNonSelectingCall(call);
	}

	/**
	 * DB接続返却前に呼び出されるイベントハンドラ。<br/>
	 * DBのセッションコンテキストをクリアする。（次の接続利用時への念のための処置）
	 */
	public void preReleaseExclusiveConnection(SessionEvent event) {
		SQLCall call = new SQLCall("CALL SCOTT.PAC1.PROC3()");
		event.getSession().executeNonSelectingCall(call);
	}
}
