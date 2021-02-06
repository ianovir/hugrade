package com.ianovir.hugrade.managers.history.ops;

public interface HistoryOp<T> {

    void onDo(T graph);
    void onUndo(T graph);

}
