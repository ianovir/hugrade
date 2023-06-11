package com.ianovir.hugrade.fx.history.ops;

public interface HistoryOp<T> {

    void onDo(T graph);
    void onUndo(T graph);

}
